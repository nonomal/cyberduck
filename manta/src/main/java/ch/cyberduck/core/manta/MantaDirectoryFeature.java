package ch.cyberduck.core.manta;

/*
 * Copyright (c) 2002-2017 iterate GmbH. All rights reserved.
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

import ch.cyberduck.core.DefaultIOExceptionMappingService;
import ch.cyberduck.core.LocaleFactory;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.exception.AccessDeniedException;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.features.Directory;
import ch.cyberduck.core.transfer.TransferStatus;

import java.io.IOException;
import java.text.MessageFormat;

import com.joyent.manta.exception.MantaClientHttpResponseException;
import com.joyent.manta.exception.MantaException;

public class MantaDirectoryFeature implements Directory {

    private final MantaSession session;

    public MantaDirectoryFeature(MantaSession session) {
        this.session = session;
    }

    @Override
    public Path mkdir(final Path folder, final TransferStatus status) throws BackgroundException {
        try {
            session.getClient().putDirectory(folder.getAbsolute());
            return folder;
        }
        catch(MantaException e) {
            throw new MantaExceptionMappingService().map("Cannot create folder {0}", e, folder);
        }
        catch(MantaClientHttpResponseException e) {
            throw new MantaHttpExceptionMappingService().map("Cannot create folder {0}", e, folder);
        }
        catch(IOException e) {
            throw new DefaultIOExceptionMappingService().map("Cannot create folder {0}", e, folder);
        }
    }

    @Override
    public void preflight(final Path workdir, final String filename) throws BackgroundException {
        if(!session.isUserWritable(workdir)) {
            throw new AccessDeniedException(MessageFormat.format(LocaleFactory.localizedString("Cannot create folder {0}", "Error"), filename)).withFile(workdir);
        }
    }
}
