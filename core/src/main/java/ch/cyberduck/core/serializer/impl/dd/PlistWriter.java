package ch.cyberduck.core.serializer.impl.dd;

/*
 * Copyright (c) 2002-2014 David Kocher. All rights reserved.
 * http://cyberduck.io/
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
 * feedback@cyberduck.io
 */

import ch.cyberduck.core.Local;
import ch.cyberduck.core.Serializable;
import ch.cyberduck.core.exception.AccessDeniedException;
import ch.cyberduck.core.serializer.Writer;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import com.dd.plist.NSArray;

public class PlistWriter<S extends Serializable> implements Writer<S> {

    @Override
    public void write(final Collection<S> collection, final Local file) throws AccessDeniedException {
        final NSArray list = new NSArray(collection.size());
        int i = 0;
        for(S bookmark : collection) {
            list.setValue(i, bookmark.serialize(new PlistSerializer()));
            i++;
        }
        final String content = list.toXMLPropertyList();
        try (final OutputStream out = file.getOutputStream(false)) {
            IOUtils.write(content, out, StandardCharsets.UTF_8);
        }
        catch(IOException e) {
            throw new AccessDeniedException(String.format("Cannot create file %s", file.getAbsolute()), e);
        }
    }

    @Override
    public void write(final S item, final Local file) throws AccessDeniedException {
        final String content = item.serialize(new PlistSerializer()).toXMLPropertyList();
        try (final OutputStream out = file.getOutputStream(false)) {
            IOUtils.write(content, out, StandardCharsets.UTF_8);
        }
        catch(IOException e) {
            throw new AccessDeniedException(String.format("Cannot create file %s", file.getAbsolute()), e);
        }
    }
}
