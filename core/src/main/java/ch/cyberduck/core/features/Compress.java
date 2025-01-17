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

import ch.cyberduck.core.Archive;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.ProgressListener;
import ch.cyberduck.core.TranscriptListener;
import ch.cyberduck.core.exception.BackgroundException;

import java.util.List;

/**
 * Server side support for compressing files or folders
 */
@Optional
public interface Compress {

    /**
     * Compress files
     *
     * @param archive    Compression format
     * @param workdir    Working directory
     * @param files      Selected files or folders
     * @param listener   Progress callback
     * @param transcript Log callback
     */
    void archive(Archive archive, Path workdir, List<Path> files, ProgressListener listener, TranscriptListener transcript) throws BackgroundException;

    /**
     * Uncompress file
     *
     * @param archive    Compression format
     * @param file       Selected file
     * @param listener   Progress callback
     * @param transcript Log callback
     */
    void unarchive(Archive archive, Path file, ProgressListener listener, TranscriptListener transcript) throws BackgroundException;
}
