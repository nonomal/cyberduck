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

import ch.cyberduck.core.Serializable;
import ch.cyberduck.core.serializer.Serializer;

import java.util.Collection;
import java.util.Map;

import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;

public class PlistSerializer implements Serializer<NSDictionary> {

    private final NSDictionary dict;

    public PlistSerializer() {
        this(new NSDictionary());
    }

    public PlistSerializer(NSDictionary dict) {
        this.dict = dict;
    }

    @Override
    public void setStringForKey(final String value, final String key) {
        dict.put(key, value);
    }

    @Override
    public void setObjectForKey(final Serializable value, final String key) {
        dict.put(key, value.serialize(new PlistSerializer()));
    }

    @Override
    public <O extends Serializable> void setListForKey(final Collection<O> value, final String key) {
        final NSArray list = new NSArray(value.size());
        int i = 0;
        for(Serializable serializable : value) {
            list.setValue(i, serializable.serialize(new PlistSerializer()));
            i++;
        }
        dict.put(key, list);
    }

    @Override
    public void setStringListForKey(final Collection<String> value, final String key) {
        final NSArray list = new NSArray(value.size());
        int i = 0;
        for(String serializable : value) {
            list.setValue(i, serializable);
            i++;
        }
        dict.put(key, list);
    }

    @Override
    public void setMapForKey(final Map<String, String> value, final String key) {
        final NSDictionary list = new NSDictionary();
        for(Map.Entry<String, String> entry : value.entrySet()) {
            list.put(entry.getKey(), entry.getValue());
        }
        dict.put(key, list);
    }

    @Override
    public void setBooleanForKey(final boolean value, final String key) {
        dict.put(key, value);
    }

    @Override
    public NSDictionary getSerialized() {
        return dict;
    }
}
