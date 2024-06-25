package ch.cyberduck.core.vault;

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

import ch.cyberduck.core.AttributedList;
import ch.cyberduck.core.IndexedListProgressListener;
import ch.cyberduck.core.ListProgressListener;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.Session;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.exception.ConnectionCanceledException;
import ch.cyberduck.core.features.Vault;
import ch.cyberduck.core.preferences.HostPreferences;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class VaultFinderListProgressListener extends IndexedListProgressListener {
    private static final Logger log = LogManager.getLogger(VaultFinderListProgressListener.class);

    private final Session<?> session;
    private final VaultLookupListener lookup;
    private final ListProgressListener proxy;
    private final String config;
    private final String masterkey;
    private final byte[] pepper;
    // Number of files to wait for until proxy is notified of files
    private final int retain = 5;

    public VaultFinderListProgressListener(final Session<?> session, final VaultLookupListener lookup, final ListProgressListener proxy) {
        this.session = session;
        this.lookup = lookup;
        this.proxy = proxy;
        this.config = new HostPreferences(session.getHost()).getProperty("cryptomator.vault.config.filename");
        this.masterkey = new HostPreferences(session.getHost()).getProperty("cryptomator.vault.masterkey.filename");
        this.pepper = new HostPreferences(session.getHost()).getProperty("cryptomator.vault.pepper").getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public VaultFinderListProgressListener reset() throws ConnectionCanceledException {
        super.reset();
        proxy.reset();
        return this;
    }

    @Override
    public void chunk(final Path folder, final AttributedList<Path> list) throws ConnectionCanceledException {
        // Defer notification until we can be sure no vault is found
        if(list.size() < retain) {
            if(log.isDebugEnabled()) {
                log.debug(String.format("Delay chunk notification for file listing of folder %s", folder));
            }
            super.chunk(folder, list);
        }
        else {
            // Delegate
            proxy.chunk(folder, list);
        }
    }

    @Override
    public void visit(final AttributedList<Path> list, final int index, final Path file) throws ConnectionCanceledException {
        final Path directory = file.getParent();
        if(config.equals(file.getName()) || masterkey.equals(file.getName())) {
            if(log.isInfoEnabled()) {
                log.info(String.format("Found vault config or masterkey file %s", file));
            }
            try {
                final Vault vault = lookup.load(session, directory, masterkey, config, pepper);
                if(vault.equals(Vault.DISABLED)) {
                    return;
                }
                throw new VaultFoundListCanceledException(vault, list);
            }
            catch(VaultUnlockCancelException e) {
                // Continue
            }
        }
    }

    @Override
    public void message(final String message) {
        proxy.message(message);
    }

    @Override
    public void finish(final Path directory, final AttributedList<Path> list, final Optional<BackgroundException> e) throws ConnectionCanceledException {
        if(list.size() < retain) {
            proxy.chunk(directory, list);
        }
        proxy.finish(directory, list, e);
    }
}
