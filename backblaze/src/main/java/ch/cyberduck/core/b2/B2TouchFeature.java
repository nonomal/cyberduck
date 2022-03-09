package ch.cyberduck.core.b2;

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

import ch.cyberduck.core.Path;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.shared.DefaultTouchFeature;
import ch.cyberduck.core.transfer.TransferStatus;

import org.apache.commons.io.input.NullInputStream;

import synapticloop.b2.response.BaseB2Response;

public class B2TouchFeature extends DefaultTouchFeature<BaseB2Response> {

    private final B2VersionIdProvider fileid;

    public B2TouchFeature(final B2Session session, final B2VersionIdProvider fileid) {
        super(new B2WriteFeature(session, fileid));
        this.fileid = fileid;
    }

    @Override
    public Path touch(final Path file, final TransferStatus status) throws BackgroundException {
        status.setChecksum(write.checksum(file, status).compute(new NullInputStream(0L), status));
        final Path result = super.touch(file, status);
        fileid.cache(file, status.getResponse().getFileId());
        return result;
    }

    @Override
    public boolean isSupported(final Path workdir, final String filename) {
        // Creating files is only possible inside a bucket.
        return !workdir.isRoot();
    }
}
