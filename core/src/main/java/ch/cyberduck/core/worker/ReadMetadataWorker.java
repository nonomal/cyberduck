package ch.cyberduck.core.worker;

/*
 * Copyright (c) 2002-2010 David Kocher. All rights reserved.
 *
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
 * dkocher@cyberduck.ch
 */

import ch.cyberduck.core.LocaleFactory;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.Session;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.features.Metadata;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReadMetadataWorker extends Worker<Map<String, String>> {
    private static final Logger log = LogManager.getLogger(ReadMetadataWorker.class);

    /**
     * Selected files.
     */
    private final List<Path> files;

    public ReadMetadataWorker(final List<Path> files) {
        this.files = files;
    }

    @Override
    public Map<String, String> run(final Session<?> session) throws BackgroundException {
        final Metadata feature = session.getFeature(Metadata.class);
        log.debug("Run with feature {}", feature);
        // Map for metadata entry key > File & Metadata Values
        final Map<String, Map<Path, String>> graphMetadata = new HashMap<>();
        for(Path next : files) {
            // Read online metadata
            next.attributes().setMetadata(feature.getMetadata(next));
            // take every entry of current metadata and store it in metaGraph
            for(Map.Entry<String, String> entry : next.attributes().getMetadata().entrySet()) {
                if(graphMetadata.containsKey(entry.getKey())) {
                    // if existing, get map, put value
                    graphMetadata.get(entry.getKey()).put(next, entry.getValue());
                }
                else {
                    // if not existent create hashmap and put it back
                    Map<Path, String> map = new HashMap<>();
                    graphMetadata.put(entry.getKey(), map);
                    map.put(next, entry.getValue());
                }
            }
        }
        // Store result metadata in hashmap
        Map<String, String> metadata = new HashMap<>();
        for(Map.Entry<String, Map<Path, String>> entry : graphMetadata.entrySet()) {
            if(entry.getValue().size() != files.size()) {
                metadata.put(entry.getKey(), null);
            }
            else {
                // single use of streams, reason: distinct is easier in Streams than it would be writing it manually
                Stream<String> values = entry.getValue().values().stream().distinct();
                // Use reducing collector, that returns null on non-unique values
                final String value = values.collect(Collectors.reducing((a, v) -> null)).orElse(null);
                // store it
                metadata.put(entry.getKey(), value);
            }
        }
        return metadata;
    }

    @Override
    public String getActivity() {
        return MessageFormat.format(LocaleFactory.localizedString("Reading metadata of {0}", "Status"),
            this.toString(files));
    }

    @Override
    public Map<String, String> initialize() {
        return Collections.emptyMap();
    }

    @Override
    public boolean equals(final Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        final ReadMetadataWorker that = (ReadMetadataWorker) o;
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
        final StringBuilder sb = new StringBuilder("ReadMetadataWorker{");
        sb.append("files=").append(files);
        sb.append('}');
        return sb.toString();
    }
}
