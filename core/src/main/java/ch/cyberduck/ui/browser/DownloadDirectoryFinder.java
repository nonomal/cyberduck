package ch.cyberduck.ui.browser;

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

import ch.cyberduck.core.AbstractHostCollection;
import ch.cyberduck.core.BookmarkCollection;
import ch.cyberduck.core.Host;
import ch.cyberduck.core.Local;
import ch.cyberduck.core.LocalFactory;
import ch.cyberduck.core.preferences.Preferences;
import ch.cyberduck.core.preferences.PreferencesFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DownloadDirectoryFinder implements DirectoryFinder {
    private static final Logger log = LogManager.getLogger(DownloadDirectoryFinder.class);

    private final AbstractHostCollection collection = BookmarkCollection.defaultCollection();

    private final Preferences preferences
            = PreferencesFactory.get();

    @Override
    public Local find(final Host bookmark) {
        if(null != bookmark.getDownloadFolder()) {
            if(bookmark.getDownloadFolder().exists()) {
                return bookmark.getDownloadFolder();
            }
        }
        final Local directory = LocalFactory.get(preferences.getProperty("queue.download.folder")).withBookmark(
                preferences.getProperty("queue.download.folder.bookmark"));
        log.info("Suggest default download folder {} for bookmark {}", directory, bookmark);
        return directory;
    }

    @Override
    public void save(final Host bookmark, final Local directory) {
        if(!directory.exists()) {
            return;
        }
        log.info("Save default download folder {} for bookmark {}", directory, bookmark);
        bookmark.setDownloadFolder(directory);
        if(collection.contains(bookmark)) {
            collection.collectionItemChanged(bookmark);
        }
    }
}
