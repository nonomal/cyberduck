package ch.cyberduck.core.local;

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

import ch.cyberduck.binding.application.NSImage;
import ch.cyberduck.binding.application.NSWorkspace;
import ch.cyberduck.core.Local;
import ch.cyberduck.core.preferences.PreferencesFactory;
import ch.cyberduck.core.resources.IconCacheFactory;
import ch.cyberduck.core.transfer.TransferProgress;
import ch.cyberduck.core.unicode.NFDNormalizer;

import org.rococoa.cocoa.foundation.NSUInteger;

public final class WorkspaceIconService implements IconService {

    private final NSWorkspace workspace = NSWorkspace.sharedWorkspace();

    public boolean update(final Local file, final NSImage icon) {
        synchronized(NSWorkspace.class) {
            // Specify 0 if you want to generate icons in all available icon representation formats
            if(workspace.setIcon_forFile_options(icon, file.getAbsolute(), new NSUInteger(0))) {
                workspace.noteFileSystemChanged(new NFDNormalizer().normalize(file.getAbsolute()).toString());
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean set(final Local file, final TransferProgress status) {
        if(status.getSize() > PreferencesFactory.get().getLong("queue.download.icon.threshold")) {
            int fraction = (int) (status.getTransferred() / (status.getTransferred() + status.getSize()) * 10);
            return this.update(file, IconCacheFactory.<NSImage>get().iconNamed(String.format("download%d.icns", ++fraction)));
        }
        return false;
    }

    @Override
    public boolean remove(final Local file) {
        // The Finder will display the default icon for this file type
        return this.update(file, null);
    }
}
