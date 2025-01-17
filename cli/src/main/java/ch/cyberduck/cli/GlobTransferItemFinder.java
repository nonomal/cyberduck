package ch.cyberduck.cli;

/*
 * Copyright (c) 2002-2014 David Kocher. All rights reserved.
 * http://cyberduck.io/
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
 * feedback@cyberduck.io
 */

import ch.cyberduck.core.Local;
import ch.cyberduck.core.LocalFactory;
import ch.cyberduck.core.NullFilter;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.PathNormalizer;
import ch.cyberduck.core.exception.AccessDeniedException;
import ch.cyberduck.core.transfer.TransferItem;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class GlobTransferItemFinder implements TransferItemFinder {
    private static final Logger log = LogManager.getLogger(GlobTransferItemFinder.class);

    @Override
    public Set<TransferItem> find(final CommandLine input, final TerminalAction action, final Path remote) throws AccessDeniedException {
        if(input.getOptionValues(action.name()).length == 2) {
            final String path = input.getOptionValues(action.name())[1];
            // This only applies to a shell where the glob is not already expanded into multiple arguments
            if(StringUtils.containsAny(path, '*', '?')) {
                final Local directory = LocalFactory.get(FilenameUtils.getFullPath(path));
                if(directory.isDirectory()) {
                    log.debug("Resolve files in {} matching {}", directory, path);
                    final Set<TransferItem> items = new HashSet<>();
                    for(Local file : directory.list(new NullFilter<String>() {
                        @Override
                        public boolean accept(final String file) {
                            return FilenameUtils.wildcardMatch(file, PathNormalizer.name(path));
                        }
                    })) {
                        items.add(new TransferItem(new Path(remote, file.getName(), EnumSet.of(Path.Type.file)), file));
                    }
                    return items;
                }
            }
        }
        return new SingleTransferItemFinder().find(input, action, remote);
    }
}
