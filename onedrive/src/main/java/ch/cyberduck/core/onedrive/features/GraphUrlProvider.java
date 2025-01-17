package ch.cyberduck.core.onedrive.features;

/*
 * Copyright (c) 2002-2022 iterate GmbH. All rights reserved.
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

import ch.cyberduck.core.DescriptiveUrl;
import ch.cyberduck.core.DescriptiveUrlBag;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.UrlProvider;

import java.util.EnumSet;

public class GraphUrlProvider implements UrlProvider {

    @Override
    public DescriptiveUrlBag toUrl(final Path file, final EnumSet<DescriptiveUrl.Type> types) {
        final DescriptiveUrlBag list = new DescriptiveUrlBag();
        if(file.attributes().getLink() != DescriptiveUrl.EMPTY) {
            list.add(file.attributes().getLink());
        }
        return list;
    }
}
