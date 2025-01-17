package ch.cyberduck.core.transfer.copy;

/*
 * Copyright (c) 2002-2015 David Kocher. All rights reserved.
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
import ch.cyberduck.core.Path;
import ch.cyberduck.core.ProgressListener;
import ch.cyberduck.core.Session;
import ch.cyberduck.core.features.AttributesFinder;
import ch.cyberduck.core.features.Find;
import ch.cyberduck.core.transfer.TransferStatus;
import ch.cyberduck.core.transfer.upload.UploadFilterOptions;

import java.util.Map;

public class OverwriteFilter extends AbstractCopyFilter {

    public OverwriteFilter(final Session<?> source, final Session<?> destination, final Map<Path, Path> files) {
        this(source, destination, files, new UploadFilterOptions(destination.getHost()));
    }

    public OverwriteFilter(final Session<?> source, final Session<?> destination, final Map<Path, Path> files, final UploadFilterOptions options) {
        this(source, destination, files, destination.getFeature(Find.class), destination.getFeature(AttributesFinder.class), options);
    }

    public OverwriteFilter(final Session<?> source, final Session<?> destination, final Map<Path, Path> files, final Find find, final AttributesFinder attribute) {
        this(source, destination, files, find, attribute, new UploadFilterOptions(destination.getHost()));
    }

    public OverwriteFilter(final Session<?> source, final Session<?> destination, final Map<Path, Path> files, final Find find, final AttributesFinder attribute, final UploadFilterOptions options) {
        super(source, destination, files, find, attribute, options);
    }

    @Override
    public boolean accept(final Path source, final Local local, final TransferStatus parent, final ProgressListener progress) {
        return true;
    }
}
