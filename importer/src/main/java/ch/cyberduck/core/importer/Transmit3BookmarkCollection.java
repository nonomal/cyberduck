package ch.cyberduck.core.importer;

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

import ch.cyberduck.binding.foundation.NSArray;
import ch.cyberduck.binding.foundation.NSData;
import ch.cyberduck.binding.foundation.NSDictionary;
import ch.cyberduck.binding.foundation.NSEnumerator;
import ch.cyberduck.binding.foundation.NSKeyedUnarchiver;
import ch.cyberduck.binding.foundation.NSObject;
import ch.cyberduck.core.Credentials;
import ch.cyberduck.core.Host;
import ch.cyberduck.core.Local;
import ch.cyberduck.core.LocalFactory;
import ch.cyberduck.core.Protocol;
import ch.cyberduck.core.ProtocolFactory;
import ch.cyberduck.core.Scheme;
import ch.cyberduck.core.exception.AccessDeniedException;
import ch.cyberduck.core.exception.LocalAccessDeniedException;
import ch.cyberduck.core.preferences.PreferencesFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rococoa.ObjCClass;
import org.rococoa.Rococoa;

public class Transmit3BookmarkCollection extends ThirdpartyBookmarkCollection {
    private static final Logger log = LogManager.getLogger(Transmit3BookmarkCollection.class);

    private static final long serialVersionUID = 6406786269501430927L;

    @Override
    public Local getFile() {
        return LocalFactory.get(PreferencesFactory.get().getProperty("bookmark.import.transmit3.location"));
    }

    @Override
    public String getBundleIdentifier() {
        return "com.panic.Transmit";
    }

    @Override
    public String getName() {
        return "Transmit 3";
    }

    @Override
    protected void parse(final ProtocolFactory protocols, final Local file) throws AccessDeniedException {
        final NSDictionary serialized = NSDictionary.dictionaryWithContentsOfFile(file.getAbsolute());
        if(null == serialized) {
            throw new LocalAccessDeniedException(String.format("Invalid bookmark file %s", file));
        }
        // Adds a class translation mapping to NSKeyedUnarchiver whereby objects encoded with a given class name
        // are decoded as instances of a given class instead.
        final TransmitFavoriteCollection c = Rococoa.createClass("TransmitFavoriteCollection", TransmitFavoriteCollection.class);
        final TransmitFavorite f = Rococoa.createClass("TransmitFavorite", TransmitFavorite.class);

        final NSData collectionsData = Rococoa.cast(serialized.objectForKey("FavoriteCollections"), NSData.class);
        if(null == collectionsData) {
            throw new LocalAccessDeniedException(String.format("Error unarchiving bookmark file %s", file));
        }
        final NSKeyedUnarchiver reader = NSKeyedUnarchiver.createForReadingWithData(collectionsData);
        reader.setClass_forClassName(c, "FavoriteCollection");
        reader.setClass_forClassName(c, "HistoryCollection");
        reader.setClass_forClassName(f, "Favorite");
        reader.setClass_forClassName(f, "DotMacFavorite");

        if(!reader.containsValueForKey("FavoriteCollection")) {
            log.warn("Missing key FavoriteCollection");
            return;
        }
        final TransmitFavoriteCollection rootCollection
                = Rococoa.cast(reader.decodeObjectForKey("FavoriteCollection"), TransmitFavoriteCollection.class);
        reader.finishDecoding();
        if(null == rootCollection) {
            throw new LocalAccessDeniedException(String.format("Error unarchiving bookmark file %s", file));
        }
        final NSArray collections = rootCollection.favorites(); //The root has collections
        final NSEnumerator collectionsEnumerator = collections.objectEnumerator();
        NSObject next;
        while((next = collectionsEnumerator.nextObject()) != null) {
            final TransmitFavoriteCollection collection = Rococoa.cast(next, TransmitFavoriteCollection.class);
            if("History".equals(collection.name())) {
                continue;
            }
            NSArray favorites = collection.favorites();
            NSEnumerator favoritesEnumerator = favorites.objectEnumerator();
            NSObject favorite;
            while((favorite = favoritesEnumerator.nextObject()) != null) {
                this.parse(protocols, Rococoa.cast(favorite, TransmitFavorite.class));
            }
        }
    }

    private void parse(final ProtocolFactory protocols, final TransmitFavorite favorite) {
        String server = favorite.server();
        if(StringUtils.isBlank(server)) {
            return;
        }
        int port = favorite.port();
        if(0 == port) {
            port = -1;
        }
        String protocolstring = favorite.protocol();
        if(StringUtils.isBlank(protocolstring)) {
            log.warn("Unknown protocol:{}", protocolstring);
            return;
        }
        Protocol protocol;
        switch(protocolstring) {
            case "FTP":
                protocol = protocols.forScheme(Scheme.ftp);
                break;
            case "SFTP":
                protocol = protocols.forScheme(Scheme.sftp);
                break;
            case "FTPTLS":
            case "FTPSSL":
                protocol = protocols.forScheme(Scheme.ftps);
                break;
            case "S3":
                protocol = protocols.forScheme(Scheme.s3);
                break;
            case "WebDAV":
                protocol = protocols.forScheme(Scheme.dav);
                break;
            case "WebDAVS":
                protocol = protocols.forScheme(Scheme.davs);
                break;
            default:
                log.warn("Unknown protocol {}", protocolstring);
                return;
        }
        Host bookmark = new Host(protocol, server, port);
        String nickname = favorite.nickname();
        if(StringUtils.isNotBlank(nickname)) {
            bookmark.setNickname(nickname);
        }
        String user = favorite.username();
        if(StringUtils.isNotBlank(user)) {
            bookmark.setCredentials(new Credentials(user));
        }
        else {
            bookmark.getCredentials().setUsername(
                    PreferencesFactory.get().getProperty("connection.login.anon.name"));
        }
        String path = favorite.path();
        if(StringUtils.isNotBlank(path)) {
            bookmark.setDefaultPath(path);
        }
        this.add(bookmark);
    }

    public interface TransmitFavoriteCollection extends ObjCClass {
        String name();

        NSArray favorites();
    }

    public interface TransmitFavorite extends ObjCClass {
        String server();

        String nickname();

        String username();

        String protocol();

        String path();

        int port();
    }
}
