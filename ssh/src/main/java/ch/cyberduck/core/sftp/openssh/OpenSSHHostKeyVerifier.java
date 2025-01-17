package ch.cyberduck.core.sftp.openssh;

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

import ch.cyberduck.core.Host;
import ch.cyberduck.core.Local;
import ch.cyberduck.core.exception.AccessDeniedException;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.exception.ChecksumException;
import ch.cyberduck.core.exception.ConnectionCanceledException;
import ch.cyberduck.core.local.LocalTouchFactory;
import ch.cyberduck.core.preferences.PreferencesFactory;
import ch.cyberduck.core.sftp.PreferencesHostKeyVerifier;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;

import net.schmizz.sshj.common.KeyType;
import net.schmizz.sshj.common.SSHRuntimeException;
import net.schmizz.sshj.transport.verification.OpenSSHKnownHosts;

public abstract class OpenSSHHostKeyVerifier extends PreferencesHostKeyVerifier {
    private static final Logger log = LogManager.getLogger(OpenSSHHostKeyVerifier.class);

    /**
     * It is a thread safe implementation, therefore, you need only to instantiate one
     * <code>KnownHosts</code> for your whole application.
     */
    protected OpenSSHKnownHosts database;

    /**
     * Path to known_hosts file.
     */
    private final Local file;

    public OpenSSHHostKeyVerifier(final Local file) {
        this.file = file;
        InputStream in = null;
        try {
            if(!file.exists()) {
                LocalTouchFactory.get().touch(file);
            }
            in = file.getInputStream();
            database = new OpenSSHKnownHosts(new File(file.getAbsolute()));
        }
        catch(IOException | SSHRuntimeException e) {
            log.error(String.format("Cannot read known hosts file %s", file), e);
        }
        catch(AccessDeniedException e) {
            log.warn("Failure reading {}", file);
        }
        finally {
            IOUtils.closeQuietly(in);
        }
    }

    @Override
    public boolean verify(final Host host, final PublicKey key) throws BackgroundException {
        if(null == database) {
            log.warn("Missing database {}", database);
            return super.verify(host, key);
        }
        final KeyType type = KeyType.fromKey(key);
        if(type == KeyType.UNKNOWN) {
            return false;
        }
        boolean foundApplicableHostEntry = false;
        for(OpenSSHKnownHosts.KnownHostEntry entry : database.entries()) {
            try {
                if(entry.appliesTo(type, format(host))) {
                    foundApplicableHostEntry = true;
                    if(entry.verify(key)) {
                        return true;
                    }
                }
            }
            catch(IOException e) {
                log.error("Failure verifying host key entry {}. {}", entry, e.getMessage());
                return false;
            }
        }
        if(foundApplicableHostEntry) {
            try {
                return this.isChangedKeyAccepted(host, key);
            }
            catch(ConnectionCanceledException | ChecksumException e) {
                return false;
            }
        }
        try {
            return this.isUnknownKeyAccepted(host, key);
        }
        catch(ConnectionCanceledException | ChecksumException e) {
            return false;
        }
    }

    @Override
    public void allow(final Host host, final PublicKey key, final boolean persist) {
        if(null == database) {
            log.warn("Missing database {}", database);
            super.allow(host, key, persist);
        }
        else {
            try {
                // Add the host key to the in-memory database
                final KeyType type = KeyType.fromKey(key);
                switch(type) {
                    case UNKNOWN:
                        log.warn("Unknown key type {}", key);
                        return;
                }
                final OpenSSHKnownHosts.HostEntry entry
                        = new OpenSSHKnownHosts.HostEntry(null, format(host), type, key);
                database.entries().add(entry);
                if(persist) {
                    if(file.attributes().getPermission().isWritable()) {
                        // Also try to add the key to a known_host file
                        database.write(entry);
                    }
                }
            }
            catch(IOException e) {
                log.error("Failure adding host key to database: {}", e.getMessage());
                super.allow(host, key, persist);
            }
        }
    }

    private static String format(final Host host) throws IOException {
        final String hostname = host.getPort() != 22 ? String.format("[%s]:%d", host.getHostname(), host.getPort()) : host.getHostname();
        return PreferencesFactory.get().getBoolean("ssh.knownhosts.hostname.hash") ? hash(hostname) : hostname;
    }

    /**
     * Generate the hashed representation of the given hostname. Useful for adding entries
     * with hashed hostnames to a known_hosts file. (see -H option of OpenSSH key-gen).
     *
     * @return the hashed representation, e.g., "|1|cDhrv7zwEUV3k71CEPHnhHZezhA=|Xo+2y6rUXo2OIWRAYhBOIijbJMA="
     */
    private static String hash(String hostname) throws IOException {
        MessageDigest sha1;
        try {
            sha1 = MessageDigest.getInstance("SHA-1");
        }
        catch(NoSuchAlgorithmException e) {
            throw new IOException(e);
        }
        byte[] salt = new byte[sha1.getDigestLength()];
        new SecureRandom().nextBytes(salt);
        byte[] hash;
        try {
            hash = hmacSha1Hash(salt, hostname);
        }
        catch(IOException e) {
            throw new IOException(e);
        }
        String base64_salt = new String(Base64.encodeBase64(salt));
        String base64_hash = new String(Base64.encodeBase64(hash));

        return String.format("|1|%s|%s", base64_salt, base64_hash);
    }

    private static byte[] hmacSha1Hash(byte[] salt, String hostname) throws IOException {
        try {
            final Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(salt, 0, salt.length, mac.getAlgorithm()));
            mac.update(hostname.getBytes());
            return mac.doFinal();
        }
        catch(GeneralSecurityException e) {
            throw new IOException(e);
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OpenSSHHostKeyVerifier{");
        sb.append("database=").append(database);
        sb.append(", file=").append(file);
        sb.append('}');
        return sb.toString();
    }
}
