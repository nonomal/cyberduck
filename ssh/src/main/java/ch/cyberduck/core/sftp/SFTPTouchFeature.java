package ch.cyberduck.core.sftp;

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

import ch.cyberduck.core.Path;
import ch.cyberduck.core.Permission;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.features.Touch;
import ch.cyberduck.core.transfer.TransferStatus;

import java.io.IOException;
import java.util.EnumSet;

import net.schmizz.sshj.sftp.FileAttributes;
import net.schmizz.sshj.sftp.OpenMode;
import net.schmizz.sshj.sftp.RemoteFile;

public class SFTPTouchFeature implements Touch<Void> {

    private final SFTPSession session;

    public SFTPTouchFeature(final SFTPSession session) {
        this.session = session;
    }

    @Override
    public Path touch(final Path file, final TransferStatus status) throws BackgroundException {
        try {
            final FileAttributes attrs;
            if(Permission.EMPTY != status.getPermission()) {
                attrs = new FileAttributes.Builder().withPermissions(Integer.parseInt(status.getPermission().getMode(), 8)).build();
            }
            else {
                attrs = FileAttributes.EMPTY;
            }
            final RemoteFile handle = session.sftp().open(file.getAbsolute(),
                    EnumSet.of(OpenMode.CREAT, OpenMode.TRUNC, OpenMode.WRITE), attrs);
            handle.close();
        }
        catch(IOException e) {
            throw new SFTPExceptionMappingService().map("Cannot create {0}", e, file);
        }
        return file;
    }
}
