package ch.cyberduck.core.shared;

/*
 * Copyright (c) 2013 David Kocher. All rights reserved.
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
 * feedback@cyberduck.ch
 */

import ch.cyberduck.core.Host;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.SerializerFactory;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.serializer.PathDictionary;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorkdirHomeFeature extends AbstractHomeFeature {
    private static final Logger log = LogManager.getLogger(WorkdirHomeFeature.class);

    private final Host host;

    public WorkdirHomeFeature(final Host host) {
        this.host = host;
    }

    @Override
    public Path find() throws BackgroundException {
        if(host.getWorkdir() != null) {
            return new PathDictionary<>().deserialize(host.getWorkdir().serialize(SerializerFactory.get()));
        }
        log.debug("No workdir set for bookmark {}", host);
        return null;
    }
}
