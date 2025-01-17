package ch.cyberduck.core.box;

/*
 * Copyright (c) 2002-2021 iterate GmbH. All rights reserved.
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

import ch.cyberduck.core.AbstractProtocol;
import ch.cyberduck.core.Protocol;
import ch.cyberduck.core.Scheme;
import ch.cyberduck.core.synchronization.ComparisonService;
import ch.cyberduck.core.synchronization.DefaultComparisonService;
import ch.cyberduck.core.synchronization.ETagComparisonService;
import com.google.auto.service.AutoService;

@AutoService(Protocol.class)
public class BoxProtocol extends AbstractProtocol {

    @Override
    public String getIdentifier() {
        return "box";
    }

    @Override
    public String getName() {
        return "Box";
    }

    @Override
    public String getDescription() {
        return "Box";
    }

    @Override
    public Type getType() {
        return Type.box;
    }

    @Override
    public Scheme getScheme() {
        return Scheme.https;
    }

    @Override
    public String getContext() {
        return "/2.0";
    }

    @Override
    public String disk() {
        return String.format("%s.tiff", "ftp");
    }

    @Override
    public DirectoryTimestamp getDirectoryTimestamp() {
        return DirectoryTimestamp.explicit;
    }

    @Override
    public Case getCaseSensitivity() {
        return Case.insensitive;
    }

    @Override
    public VersioningMode getVersioningMode() {
        return VersioningMode.storage;
    }

    @Override
    public <T> T getFeature(final Class<T> type) {
        if(type == ComparisonService.class) {
            return (T) new DefaultComparisonService(new ETagComparisonService(), ComparisonService.disabled);
        }
        return super.getFeature(type);
    }
}
