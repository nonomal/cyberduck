package ch.cyberduck.core.transfer.download;

/*
 * Copyright (c) 2002-2013 David Kocher. All rights reserved.
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
 * Bug fixes, suggestions and comments should be sent to feedback@cyberduck.ch
 */

import ch.cyberduck.core.Local;
import ch.cyberduck.core.LocalFactory;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.ProgressListener;
import ch.cyberduck.core.Session;
import ch.cyberduck.core.UserDateFormatterFactory;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.features.AttributesFinder;
import ch.cyberduck.core.preferences.PreferencesFactory;
import ch.cyberduck.core.transfer.TransferStatus;
import ch.cyberduck.core.transfer.symlink.SymlinkResolver;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;

public class RenameExistingFilter extends AbstractDownloadFilter {
    private static final Logger log = LogManager.getLogger(RenameExistingFilter.class);

    public RenameExistingFilter(final SymlinkResolver<Path> symlinkResolver, final Session<?> session) {
        this(symlinkResolver, session, session.getFeature(AttributesFinder.class), new DownloadFilterOptions(session.getHost()));
    }

    public RenameExistingFilter(final SymlinkResolver<Path> symlinkResolver, final Session<?> session, final DownloadFilterOptions options) {
        this(symlinkResolver, session, session.getFeature(AttributesFinder.class), options);
    }

    public RenameExistingFilter(final SymlinkResolver<Path> symlinkResolver, final Session<?> session, final AttributesFinder attribute, final DownloadFilterOptions options) {
        super(symlinkResolver, session, attribute, options);
    }

    @Override
    public void apply(final Path file, final Local local, final TransferStatus status,
                      final ProgressListener listener) throws BackgroundException {
        if(status.isExists()) {
            if(local.attributes().getSize() == 0) {
                // Placeholder from drag operation
                log.warn("Skip renaming placeholder file {}", local);
            }
            else {
                Local rename;
                do {
                    String proposal = MessageFormat.format(PreferencesFactory.get().getProperty("queue.download.file.rename.format"),
                            FilenameUtils.getBaseName(file.getName()),
                            UserDateFormatterFactory.get().getMediumFormat(System.currentTimeMillis(), false).replace(local.getDelimiter(), '-').replace(':', '-'),
                            StringUtils.isNotBlank(file.getExtension()) ? String.format(".%s", file.getExtension()) : StringUtils.EMPTY);
                    rename = LocalFactory.get(local.getParent().getAbsolute(), proposal);
                }
                while(rename.exists());
                log.info("Rename existing file {} to {}", local, rename);
                LocalFactory.get(local.getAbsolute()).rename(rename);
                log.debug("Clear exist flag for file {}", local);
                status.setExists(false);
            }
        }
        super.apply(file, local, status, listener);
    }
}
