/*
 * Copyright (c) 2002-2016 iterate GmbH. All rights reserved.
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

package ch.cyberduck.core.ftp;

import ch.cyberduck.core.Credentials;
import ch.cyberduck.core.LoginOptions;
import ch.cyberduck.core.Protocol;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class FTPProtocolTest {

    @Test
    public void testFeatures() {
        assertEquals(Protocol.Case.sensitive, new FTPProtocol().getCaseSensitivity());
        assertEquals(Protocol.DirectoryTimestamp.implicit, new FTPProtocol().getDirectoryTimestamp());
    }

    @Test
    public void testEquals() {
        assertEquals(new FTPProtocol(), new FTPProtocol());
    }

    @Test
    public void testConfigurable() {
        assertTrue(new FTPProtocol().isHostnameConfigurable());
        assertTrue(new FTPProtocol().isPortConfigurable());
    }

    @Test
    public void testIcons() {
        for(Protocol p : Arrays.asList(new FTPProtocol(), new FTPTLSProtocol())) {
            assertNotNull(p.disk());
            assertNotNull(p.icon());
            assertNotEquals(-1, p.getDefaultPort());
            assertNotNull(p.getDefaultHostname());
            assertNotNull(p.getDescription());
            assertNotNull(p.getIdentifier());
        }
    }

    @Test
    public void testValidateCredentialsEmpty() {
        Credentials c = new Credentials("user", "");
        assertTrue(c.validate(new FTPProtocol(), new LoginOptions(new FTPProtocol())));
    }

    @Test
    public void testValidateCredentialsBlank() {
        Credentials c = new Credentials("user", " ");
        assertTrue(c.validate(new FTPProtocol(), new LoginOptions(new FTPProtocol())));
    }
}
