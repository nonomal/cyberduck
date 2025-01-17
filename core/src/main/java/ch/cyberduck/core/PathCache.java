package ch.cyberduck.core;

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

import ch.cyberduck.core.features.Home;

public class PathCache extends AbstractCache<Path> {

    private static final CacheReference<Path> NULL_KEY = new DefaultPathPredicate(Home.ROOT);

    private static final PathCache EMPTY = new PathCache(0) {
        @Override
        public AttributedList<Path> put(final Path key, final AttributedList<Path> children) {
            return AttributedList.emptyList();
        }
    };

    public static PathCache empty() {
        return EMPTY;
    }

    public PathCache(final int size) {
        super(size);
    }

    @Override
    public CacheReference<Path> reference(final Path file) {
        if(null == file) {
            return NULL_KEY;
        }
        return new DefaultPathPredicate(file);
    }
}
