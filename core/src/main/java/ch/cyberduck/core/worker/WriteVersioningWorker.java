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
import ch.cyberduck.core.LoginCallback;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.PathContainerService;
import ch.cyberduck.core.Session;
import ch.cyberduck.core.VersioningConfiguration;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.exception.ConnectionCanceledException;
import ch.cyberduck.core.features.Versioning;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;

public class WriteVersioningWorker extends Worker<Boolean> {
    private static final Logger log = LogManager.getLogger(WriteVersioningWorker.class);

    /**
     * Selected files.
     */
    private final List<Path> files;

    private final LoginCallback prompt;

    private final VersioningConfiguration configuration;

    public WriteVersioningWorker(final List<Path> files, final LoginCallback prompt, final VersioningConfiguration configuration) {
        this.files = files;
        this.prompt = prompt;
        this.configuration = configuration;
    }

    @Override
    public Boolean run(final Session<?> session) throws BackgroundException {
        final Versioning feature = session.getFeature(Versioning.class);
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

    private void write(final Versioning feature, final Path file) throws BackgroundException {
        feature.setConfiguration(file, prompt, configuration);
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
        final WriteVersioningWorker that = (WriteVersioningWorker) o;
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
        final StringBuilder sb = new StringBuilder("WriteVersioningWorker{");
        sb.append("files=").append(files);
        sb.append('}');
        return sb.toString();
    }
}
