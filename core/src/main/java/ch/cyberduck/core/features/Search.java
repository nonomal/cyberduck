package ch.cyberduck.core.features;

/*
 * Copyright (c) 2002-2016 iterate GmbH. All rights reserved.
 * https://cyberduck.io/
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
 */

import ch.cyberduck.core.AttributedList;
import ch.cyberduck.core.Filter;
import ch.cyberduck.core.ListProgressListener;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.exception.BackgroundException;

import java.util.EnumSet;

@Optional
public interface Search {

    /**
     * @param workdir  Current working directory in browser
     * @param regex    Search string
     * @param listener Notification listener
     * @return List of files found or empty list
     */
    AttributedList<Path> search(Path workdir, Filter<Path> regex, ListProgressListener listener) throws BackgroundException;

    /**
     * @return True if search is capable of recursively searching in folders
     */
    default boolean isRecursive() {
        return this.features().contains(Flags.recursive);
    }

    /**
     * @return Supported features
     */
    default EnumSet<Flags> features() {
        return EnumSet.noneOf(Flags.class);
    }

    /**
     * Feature flags
     */
    enum Flags {
        /**
         * Support deleting directories recursively
         */
        recursive
    }
}
