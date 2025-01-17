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

import ch.cyberduck.core.ProgressListener;
import ch.cyberduck.core.TranscriptListener;
import ch.cyberduck.core.exception.BackgroundException;

/**
 * Command interface to the server
 */
@Optional
public interface Command {

    /**
     * Sends an arbitrary command to the server
     *
     * @param command    Command to send
     * @param transcript Listener
     */
    void send(String command, ProgressListener listener, TranscriptListener transcript) throws BackgroundException;
}
