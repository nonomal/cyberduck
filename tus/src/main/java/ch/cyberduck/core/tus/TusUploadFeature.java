package ch.cyberduck.core.tus;

/*
 * Copyright (c) 2002-2024 iterate GmbH. All rights reserved.
 * https://cyberduck.io/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

import ch.cyberduck.core.ConnectionCallback;
import ch.cyberduck.core.DefaultIOExceptionMappingService;
import ch.cyberduck.core.DescriptiveUrl;
import ch.cyberduck.core.Host;
import ch.cyberduck.core.Local;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.ProgressListener;
import ch.cyberduck.core.StringAppender;
import ch.cyberduck.core.concurrency.Interruptibles;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.exception.InteroperabilityException;
import ch.cyberduck.core.exception.NotfoundException;
import ch.cyberduck.core.features.Upload;
import ch.cyberduck.core.features.Write;
import ch.cyberduck.core.http.DefaultHttpResponseExceptionMappingService;
import ch.cyberduck.core.http.HttpUploadFeature;
import ch.cyberduck.core.io.BandwidthThrottle;
import ch.cyberduck.core.io.Checksum;
import ch.cyberduck.core.io.StreamListener;
import ch.cyberduck.core.preferences.Preferences;
import ch.cyberduck.core.preferences.PreferencesFactory;
import ch.cyberduck.core.shared.DefaultUrlProvider;
import ch.cyberduck.core.threading.BackgroundExceptionCallable;
import ch.cyberduck.core.threading.DefaultRetryCallable;
import ch.cyberduck.core.transfer.TransferStatus;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.ConcurrentUtils;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import static ch.cyberduck.core.tus.TusCapabilities.*;

public class TusUploadFeature extends HttpUploadFeature<Void, MessageDigest> {
    private static final Logger log = LogManager.getLogger(TusUploadFeature.class);

    public static final String UPLOAD_URL = "uploadUrl";

    private final Host host;
    private final HttpClient client;
    private final Preferences preferences = PreferencesFactory.get();

    private Write<Void> writer;
    private final TusCapabilities capabilities;

    public TusUploadFeature(final Host host, final HttpClient client, final Write<Void> writer, final TusCapabilities capabilities) {
        super(writer);
        this.host = host;
        this.client = client;
        this.writer = writer;
        this.capabilities = capabilities;
    }

    @Override
    public Void upload(final Path file, final Local local, final BandwidthThrottle throttle, final ProgressListener progress, final StreamListener streamListener,
                       final TransferStatus status, final ConnectionCallback callback) throws BackgroundException {
        // In order to achieve parallel upload the Concatenation extension MAY be used.
        try {
            final List<Future<Void>> chunks = new ArrayList<>();
            long offset = status.getOffset();
            long remaining = status.getLength();
            final String uploadUrl;
            if(status.isAppend()) {
                uploadUrl = preferences.getProperty(toUploadUrlPropertyKey(host, file, status));
                if(StringUtils.isBlank(uploadUrl)) {
                    log.debug("No previous upload URL for {}", file);
                    throw new NotfoundException(file.getAbsolute());
                }
                log.debug("Resume upload to {} for {} from offset {}", uploadUrl, file, status.getOffset());
            }
            else {
                if(!capabilities.extensions.contains(Extension.creation)) {
                    throw new InteroperabilityException(String.format("No support for %s", Extension.creation));
                }
                // Create an Upload URL
                final HttpPost request = new HttpPost(new DefaultUrlProvider(host).toUrl(file.getParent(),
                        EnumSet.of(DescriptiveUrl.Type.provider)).find(DescriptiveUrl.Type.provider).getUrl());
                request.setHeader(TUS_HEADER_RESUMABLE, TUS_VERSION);
                // The Upload-Length header indicates the size of the entire upload in bytes
                request.setHeader(TUS_HEADER_UPLOAD_LENGTH, String.valueOf(status.getDestinationlength()));
                // The Upload-Metadata request and response header MUST consist of one or more comma-separated key-value pairs
                final StringAppender metadata = new StringAppender(',');
                metadata.append(String.format("filename %s", Base64.encodeBase64String(file.getName().getBytes(StandardCharsets.UTF_8))));
                if(status.getModified() != null) {
                    // Modification time (Unix time format)
                    metadata.append(String.format("mtime %s", status.getModified() / 1000));
                }
                if(status.getMime() != null) {
                    metadata.append(String.format("filetype %s", status.getMime()));
                }
                if(status.getChecksum() != Checksum.NONE) {
                    metadata.append(String.format("checksum %s", String.format("%s %s", status.getChecksum().algorithm, status.getChecksum().hex)));
                }
                request.setHeader(TUS_HEADER_UPLOAD_METADATA, metadata.toString());
                uploadUrl = client.execute(request, new ResponseHandler<String>() {
                    @Override
                    public String handleResponse(final HttpResponse response) throws HttpResponseException {
                        if(response.containsHeader(HttpHeaders.LOCATION)) {
                            return response.getFirstHeader(HttpHeaders.LOCATION).getValue();
                        }
                        throw new HttpResponseException(response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
                    }
                });
                log.debug("Save upload URL {} for {}", uploadUrl, file);
                preferences.setProperty(toUploadUrlPropertyKey(host, file, status), uploadUrl);
            }
            while(remaining > 0) {
                final long length = Math.min(preferences.getInteger("tus.chunk.size"), remaining);
                chunks.add(this.submit(file, local, throttle, streamListener, status,
                        uploadUrl, offset, length, callback));
                remaining -= length;
                offset += length;
            }
            // Await upload of chunks
            Interruptibles.awaitAll(chunks);
            // Mark parent status as complete
            status.setComplete();
            preferences.deleteProperty(toUploadUrlPropertyKey(host, file, status));
            return null;
        }
        catch(HttpResponseException e) {
            throw new DefaultHttpResponseExceptionMappingService().map("Upload {0} failed", e, file);
        }
        catch(IOException e) {
            throw new DefaultIOExceptionMappingService().map("Upload {0} failed", e, file);
        }
    }

    private Future<Void> submit(final Path file, final Local local,
                                final BandwidthThrottle throttle, final StreamListener listener,
                                final TransferStatus overall, final String uploadUrl,
                                final long offset, final long length, final ConnectionCallback callback) throws BackgroundException {
        log.info("Send part of {} with offset {} and length {}", file, offset, length);
        return ConcurrentUtils.constantFuture(new DefaultRetryCallable<>(host, new BackgroundExceptionCallable<Void>() {
            @Override
            public Void call() throws BackgroundException {
                overall.validate();
                final TransferStatus status = new TransferStatus()
                        .segment(true)
                        .withOffset(offset)
                        .withLength(length);
                status.setHeader(overall.getHeader());
                status.setChecksum(writer.checksum(file, status).compute(local.getInputStream(), status));
                final Map<String, String> parameters = new HashMap<>();
                parameters.put(UPLOAD_URL, uploadUrl);
                status.withParameters(parameters);
                final Void response = TusUploadFeature.this.upload(
                        file, local, throttle, listener, status, overall, status, callback);
                log.info("Received response {}", response);
                return null;
            }
        }, overall).call());
    }

    @Override
    public Write.Append append(final Path file, final TransferStatus status) throws BackgroundException {
        // Determine the offset at which the upload should be continued
        final String property = toUploadUrlPropertyKey(host, file, status);
        final String uploadUrl = preferences.getProperty(property);
        if(StringUtils.isBlank(uploadUrl)) {
            log.debug("No previous upload URL for {}", file);
            return Write.override;
        }
        final HttpHead request = new HttpHead(uploadUrl);
        request.setHeader(TUS_HEADER_RESUMABLE, TUS_VERSION);
        try {
            final Long offset = client.execute(request, new ResponseHandler<Long>() {
                @Override
                public Long handleResponse(final HttpResponse response) throws HttpResponseException {
                    switch(response.getStatusLine().getStatusCode()) {
                        case HttpStatus.SC_OK:
                            if(response.containsHeader(TUS_HEADER_UPLOAD_OFFSET)) {
                                final Header header = response.getFirstHeader(TUS_HEADER_UPLOAD_OFFSET);
                                log.debug("Return offset header {}", header);
                                return Long.valueOf(header.getValue());
                            }
                    }
                    // If the resource is not found, the Server SHOULD return either the 404 Not Found,
                    // 410 Gone or 403 Forbidden status without the Upload-Offset header.
                    throw new HttpResponseException(response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
                }
            });
            return new Write.Append(true).withStatus(status).withOffset(offset);
        }
        catch(HttpResponseException e) {
            preferences.deleteProperty(property);
            return Write.override;
        }
        catch(IOException e) {
            throw new DefaultIOExceptionMappingService().map("Upload {0} failed", e, file);
        }
    }

    /**
     * Key to use in preferences to save upload URL for file
     */
    private static String toUploadUrlPropertyKey(final Host host, final Path file, final TransferStatus status) {
        return String.format("tus.url.%s%s",
                Base64.encodeBase64String(new DefaultUrlProvider(host).toUrl(file,
                        EnumSet.of(DescriptiveUrl.Type.provider)).find(DescriptiveUrl.Type.provider).getUrl().getBytes(StandardCharsets.UTF_8)),
                null == status.getChecksum() ? StringUtils.EMPTY : String.format(":%s", status.getChecksum().base64));
    }

    @Override
    public Upload<Void> withWriter(final Write<Void> writer) {
        this.writer = writer;
        return super.withWriter(writer);
    }
}
