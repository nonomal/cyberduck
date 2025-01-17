package ch.cyberduck.core.transfer;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TransferAdapter implements TransferListener {
    private static final Logger log = LogManager.getLogger(TransferAdapter.class);

    @Override
    public void transferDidStart(final Transfer transfer) {
        log.debug("Start transfer {}", transfer);
    }

    @Override
    public void transferDidStop(final Transfer transfer) {
        log.debug("Stop transfer {}", transfer);
    }

    @Override
    public void transferDidProgress(final Transfer transfer, final TransferProgress status) {
        log.debug(status.toString());
    }
}
