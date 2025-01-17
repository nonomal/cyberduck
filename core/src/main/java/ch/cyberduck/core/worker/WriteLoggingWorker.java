package ch.cyberduck.core.worker;

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

import ch.cyberduck.core.LocaleFactory;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.PathContainerService;
import ch.cyberduck.core.Session;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.exception.ConnectionCanceledException;
import ch.cyberduck.core.features.Logging;
import ch.cyberduck.core.logging.LoggingConfiguration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;

public class WriteLoggingWorker extends Worker<Boolean> {
    private static final Logger log = LogManager.getLogger(WriteLoggingWorker.class);

    /**
     * Selected files.
     */
    private final List<Path> files;

    private final LoggingConfiguration configuration;

    public WriteLoggingWorker(final List<Path> files, final LoggingConfiguration configuration) {
        this.files = files;
        this.configuration = configuration;
    }

    @Override
    public Boolean run(final Session<?> session) throws BackgroundException {
        final Logging feature = session.getFeature(Logging.class);
        if(null == feature) {
            return false;
        }
        log.debug("Run with feature {}", feature);
        final PathContainerService container = session.getFeature(PathContainerService.class);
        for(Path file : this.getContainers(container, files)) {
            if(this.isCanceled()) {
                throw new ConnectionCanceledException();
            }
            this.write(feature, file);
        }
        return true;
    }

    private void write(final Logging feature, final Path file) throws BackgroundException {
        feature.setConfiguration(file, configuration);
    }

    @Override
    public Boolean initialize() {
        return false;
    }

    @Override
    public String getActivity() {
        return MessageFormat.format(LocaleFactory.localizedString("Writing metadata of {0}", "Status"),
                this.toString(files));
    }

    @Override
    public boolean equals(final Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        final WriteLoggingWorker that = (WriteLoggingWorker) o;
        if(!Objects.equals(files, that.files)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return files != null ? files.hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("WriteLoggingConfigurationWorker{");
        sb.append("files=").append(files);
        sb.append('}');
        return sb.toString();
    }
}
