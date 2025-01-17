package ch.cyberduck.core.preferences;

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

import ch.cyberduck.binding.foundation.NSBundle;
import ch.cyberduck.core.Local;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.exception.NotfoundException;
import ch.cyberduck.core.local.FinderLocal;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BundleApplicationResourcesFinder implements ApplicationResourcesFinder {
    private static final Logger log = LogManager.getLogger(BundleApplicationResourcesFinder.class);

    private NSBundle cached;

    @Override
    public Local find() {
        final NSBundle b = this.bundle();
        if(null == b) {
            log.warn("No main bundle found");
            return new TemporarySupportDirectoryFinder().find();
        }
        final Local folder = new FinderLocal(b.resourcePath());
        log.debug("Use folder {} for application resources directory", folder);
        return folder;
    }

    public NSBundle bundle() {
        if(cached != null) {
            return cached;
        }
        log.info("Loading application bundle resources");
        final NSBundle main = NSBundle.mainBundle();
        if(null == main) {
            cached = null;
        }
        else {
            final Local executable = new FinderLocal(main.executablePath());
            cached = this.bundle(main, executable);
        }
        return cached;
    }

    protected NSBundle bundle(final NSBundle main, Local executable) {
        if(!executable.isSymbolicLink()) {
            return main;
        }
        while(executable.isSymbolicLink()) {
            try {
                executable = executable.getSymlinkTarget();
            }
            catch(NotfoundException e) {
                return main;
            }
        }
        Local folder = executable.getParent();
        NSBundle b;
        do {
            b = NSBundle.bundleWithPath(folder.getAbsolute());
            if(null == b) {
                log.error("Loading bundle {} failed", folder);
                break;
            }
            if(StringUtils.equals(String.valueOf(Path.DELIMITER), b.bundlePath())) {
                break;
            }
            folder = folder.getParent();
        }
        while(b.executablePath() == null);
        return b;
    }
}
