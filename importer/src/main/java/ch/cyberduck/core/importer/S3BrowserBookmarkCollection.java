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

import ch.cyberduck.core.Host;
import ch.cyberduck.core.Local;
import ch.cyberduck.core.LocalFactory;
import ch.cyberduck.core.ProtocolFactory;
import ch.cyberduck.core.Scheme;
import ch.cyberduck.core.exception.AccessDeniedException;
import ch.cyberduck.core.preferences.PreferencesFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Scanner;

public class S3BrowserBookmarkCollection extends ThirdpartyBookmarkCollection {
    private static final Logger log = LogManager.getLogger(S3BrowserBookmarkCollection.class);

    private static final long serialVersionUID = 2559948549693535359L;

    @Override
    public String getBundleIdentifier() {
        return "com.s3browser";
    }

    @Override
    public String getName() {
        return "S3Browser";
    }

    @Override
    public Local getFile() {
        return LocalFactory.get(PreferencesFactory.get().getProperty("bookmark.import.s3browser.location"));
    }

    @Override
    protected void parse(final ProtocolFactory protocols, final Local file) throws AccessDeniedException {
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            Host current = null;
            String line;
            while((line = in.readLine()) != null) {
                if(line.startsWith("[account_")) {
                    current = new Host(protocols.forScheme(Scheme.s3));
                }
                else if(StringUtils.isBlank(line)) {
                    this.add(current);
                    current = null;
                }
                else {
                    if(null == current) {
                        log.warn("Failed to detect start of bookmark");
                        continue;
                    }
                    Scanner scanner = new Scanner(line);
                    scanner.useDelimiter(" = ");
                    if(!scanner.hasNext()) {
                        log.warn("Missing key in line:{}", line);
                        continue;
                    }
                    String name = scanner.next().toLowerCase(Locale.ROOT);
                    if(!scanner.hasNext()) {
                        log.warn("Missing value in line:{}", line);
                        continue;
                    }
                    String value = scanner.next();
                    switch(name) {
                        case "name":
                            current.setNickname(value);
                            break;
                        case "comment":
                            current.setComment(value);
                            break;
                        case "access_key":
                            current.getCredentials().setUsername(value);
                            break;
                        case "secret_key":
                            current.getCredentials().setPassword(value);
                            break;
                    }
                }
            }
        }
        catch(IOException e) {
            throw new AccessDeniedException(e.getMessage(), e);
        }
    }
}
