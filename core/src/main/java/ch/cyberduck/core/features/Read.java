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

import ch.cyberduck.core.ConnectionCallback;
import ch.cyberduck.core.LocaleFactory;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.exception.AccessDeniedException;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.transfer.TransferStatus;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.EnumSet;

/**
 * Read file from server
 */
@Required
public interface Read {

    /**
     * @param status   Transfer status holder
     * @param callback Prompt
     * @return Stream to read from to download file
     */
    InputStream read(Path file, TransferStatus status, ConnectionCallback callback) throws BackgroundException;

    /**
     * @param file File
     * @return True if read with offset is supported
     */
    default boolean offset(Path file) throws BackgroundException {
        if(this.features(file).contains(Flags.offset)) {
            return true;
        }
        return false;

    }

    default void preflight(final Path file) throws BackgroundException {
        if(!file.attributes().getPermission().isReadable()) {
            throw new AccessDeniedException(MessageFormat.format(LocaleFactory.localizedString("Download {0} failed", "Error"),
                    file.getName())).withFile(file);
        }
    }

    /**
     * @return Supported features
     */
    default EnumSet<Flags> features(Path file) {
        return EnumSet.of(Flags.offset);
    }

    /**
     * Feature flags
     */
    enum Flags {
        offset
    }
}
