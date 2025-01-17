package ch.cyberduck.core.features;

/*
 * Copyright (c) 2002-2017 iterate GmbH. All rights reserved.
 * https://cyberduck.io/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

import ch.cyberduck.core.Path;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.transfer.TransferStatus;

import java.util.Map;

@Optional
public interface Metadata {
    Map<String, String> getDefault();

    Map<String, String> getMetadata(Path file) throws BackgroundException;

    default void setMetadata(Path file, Map<String, String> metadata) throws BackgroundException {
        this.setMetadata(file, new TransferStatus().withMetadata(metadata));
    }

    void setMetadata(Path file, TransferStatus status) throws BackgroundException;
}
