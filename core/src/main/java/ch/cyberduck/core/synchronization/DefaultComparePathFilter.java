package ch.cyberduck.core.synchronization;

/*
 * Copyright (c) 2012 David Kocher. All rights reserved.
 * http://cyberduck.ch/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * Bug fixes, suggestions and comments should be sent to:
 * dkocher@cyberduck.ch
 */

import ch.cyberduck.core.Local;
import ch.cyberduck.core.LocaleFactory;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.PathAttributes;
import ch.cyberduck.core.ProgressListener;
import ch.cyberduck.core.Session;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.features.AttributesFinder;
import ch.cyberduck.core.features.Find;
import ch.cyberduck.core.io.Checksum;
import ch.cyberduck.core.io.ChecksumComputeFactory;
import ch.cyberduck.core.io.HashAlgorithm;
import ch.cyberduck.core.transfer.TransferStatus;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;

public class DefaultComparePathFilter implements ComparePathFilter {
    private static final Logger log = LogManager.getLogger(DefaultComparePathFilter.class);

    private final Find finder;
    private final AttributesFinder attribute;
    private final ComparisonService comparison;

    public DefaultComparePathFilter(final Session<?> session) {
        this(session, session.getFeature(Find.class), session.getFeature(AttributesFinder.class));
    }

    public DefaultComparePathFilter(final Session<?> session, final Find finder, final AttributesFinder attribute) {
        this(finder, attribute, session.getFeature(ComparisonService.class));
    }

    public DefaultComparePathFilter(final Find finder, final AttributesFinder attribute, final ComparisonService comparison) {
        this.finder = finder;
        this.attribute = attribute;
        this.comparison = comparison;
    }

    @Override
    public Comparison compare(final Path file, final Local local, final ProgressListener listener) throws BackgroundException {
        if(local.exists()) {
            if(finder.find(file)) {
                if(file.isDirectory()) {
                    // Do not compare directories
                    return Comparison.equal;
                }
                final PathAttributes remote = attribute.find(file);
                final PathAttributes current = new PathAttributes()
                        .withModificationDate(local.attributes().getModificationDate())
                        .withSize(local.attributes().getSize());
                // We must always compare the size because the download filter will have already created a temporary 0 byte file
                switch(new SizeComparisonService().compare(Path.Type.file, current, remote)) {
                    case remote:
                        return Comparison.remote;
                    case local:
                        return Comparison.local;
                }
                // Equal size
                if(Checksum.NONE.equals(remote.getChecksum())) {
                    log.warn("Missing checksum for {}", file);
                }
                else {
                    listener.message(MessageFormat.format(LocaleFactory.localizedString("Compute MD5 hash of {0}", "Status"), file.getName()));
                    final Checksum checksum = this.checksum(remote.getChecksum().algorithm, local);
                    current.withETag(checksum.hash).withChecksum(checksum);
                }
                final Comparison result = comparison.compare(Path.Type.file, current, remote);
                switch(result) {
                    case unknown:
                    case local:
                    case notequal:
                        return Comparison.local;
                    default:
                        return result;
                }
            }
            // Only the local file exists
            return Comparison.local;
        }
        else {
            if(finder.find(file)) {
                // Only the remote file exists
                return Comparison.remote;
            }
            return Comparison.equal;
        }
    }

    protected Checksum checksum(final HashAlgorithm algorithm, final Local local) throws BackgroundException {
        return ChecksumComputeFactory.get(algorithm).compute(local.getInputStream(), new TransferStatus());
    }
}
