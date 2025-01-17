package ch.cyberduck.core.profiles;

/*
 * Copyright (c) 2002-2021 iterate GmbH. All rights reserved.
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

import ch.cyberduck.core.AttributedList;
import ch.cyberduck.core.DisabledConnectionCallback;
import ch.cyberduck.core.DisabledListProgressListener;
import ch.cyberduck.core.DisabledProgressListener;
import ch.cyberduck.core.Filter;
import ch.cyberduck.core.ListService;
import ch.cyberduck.core.Local;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.PathAttributes;
import ch.cyberduck.core.ProtocolFactory;
import ch.cyberduck.core.Session;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.features.Read;
import ch.cyberduck.core.local.TemporaryFileService;
import ch.cyberduck.core.local.TemporaryFileServiceFactory;
import ch.cyberduck.core.shared.DefaultPathHomeFeature;
import ch.cyberduck.core.shared.DelegatingHomeFeature;
import ch.cyberduck.core.transfer.TransferPathFilter;
import ch.cyberduck.core.transfer.TransferStatus;
import ch.cyberduck.core.transfer.download.CompareFilter;
import ch.cyberduck.core.transfer.symlink.DisabledDownloadSymlinkResolver;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.LazyInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RemoteProfilesFinder implements ProfilesFinder {
    private static final Logger log = LogManager.getLogger(RemoteProfilesFinder.class);

    private final TemporaryFileService temp = TemporaryFileServiceFactory.get();
    private final ProtocolFactory protocols;
    private final Session<?> session;
    private final TransferPathFilter comparison;
    private final Filter<Path> filter;

    public RemoteProfilesFinder(final Session<?> session) {
        this(ProtocolFactory.get(), session);
    }

    public RemoteProfilesFinder(final ProtocolFactory protocols, final Session<?> session) {
        this(protocols, session, new CompareFilter(new DisabledDownloadSymlinkResolver(), session), new ProfileFilter());
    }

    public RemoteProfilesFinder(final Session<?> session,
                                final TransferPathFilter comparison, final Filter<Path> filter) {
        this(ProtocolFactory.get(), session, comparison, filter);
    }

    public RemoteProfilesFinder(final ProtocolFactory protocols, final Session<?> session,
                                final TransferPathFilter comparison, final Filter<Path> filter) {
        this.protocols = protocols;
        this.session = session;
        this.comparison = comparison;
        this.filter = filter;
    }

    @Override
    public Set<ProfileDescription> find(final Visitor visitor) throws BackgroundException {
        log.info("Fetch profiles from {}", session.getHost());
        final AttributedList<Path> list = session.getFeature(ListService.class).list(new DelegatingHomeFeature(
                new DefaultPathHomeFeature(session.getHost())).find(), new DisabledListProgressListener());
        return list.filter(filter).toStream().map(file -> visitor.visit(new RemoteProfileDescription(protocols, file,
                new LazyInitializer<Local>() {
                    @Override
                    protected Local initialize() throws ConcurrentException {
                        try {
                            final Local local = temp.create("profiles", file);
                            if(comparison.accept(file, local, new TransferStatus().exists(true), new DisabledProgressListener())) {
                                final Read read = session.getFeature(Read.class);
                                log.info("Download profile {}", file);
                                // Read latest version
                                try (InputStream in = read.read(file.withAttributes(new PathAttributes(file.attributes())
                                        // Read latest version
                                        .withVersionId(null)), new TransferStatus().withLength(TransferStatus.UNKNOWN_LENGTH), new DisabledConnectionCallback()); OutputStream out = local.getOutputStream(false)) {
                                    IOUtils.copy(in, out);
                                }
                            }
                            return local;
                        }
                        catch(BackgroundException | IOException e) {
                            throw new ConcurrentException(e);
                        }
                    }
                }
        ))).collect(Collectors.toSet());
    }

    private static final class ProfileFilter implements Filter<Path> {
        @Override
        public boolean accept(final Path file) {
            if(file.isFile()) {
                return "cyberduckprofile".equals(Path.getExtension(file.getName()));
            }
            return false;
        }

        @Override
        public Pattern toPattern() {
            return Pattern.compile(".*\\.cyberduckprofile");
        }
    }
}
