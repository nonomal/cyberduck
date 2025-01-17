package ch.cyberduck.core.vault.registry;

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

import ch.cyberduck.core.ConnectionCallback;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.Session;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.features.Write;
import ch.cyberduck.core.io.ChecksumCompute;
import ch.cyberduck.core.io.StatusOutputStream;
import ch.cyberduck.core.transfer.TransferStatus;
import ch.cyberduck.core.vault.VaultRegistry;
import ch.cyberduck.core.vault.VaultUnlockCancelException;

import java.util.EnumSet;

public class VaultRegistryWriteFeature<T> implements Write<T> {

    private final Session<?> session;
    private final Write proxy;
    private final VaultRegistry registry;

    public VaultRegistryWriteFeature(final Session<?> session, final Write proxy, final VaultRegistry registry) {
        this.session = session;
        this.proxy = proxy;
        this.registry = registry;
    }

    @Override
    public StatusOutputStream<T> write(final Path file, final TransferStatus status, final ConnectionCallback callback) throws BackgroundException {
        return registry.find(session, file).getFeature(session, Write.class, proxy).write(file, status, callback);
    }

    @Override
    public EnumSet<Flags> features(final Path file) {
        return proxy.features(file);
    }

    @Override
    public ChecksumCompute checksum(final Path file, final TransferStatus status) {
        try {
            return registry.find(session, file).getFeature(session, Write.class, proxy).checksum(file, status);
        }
        catch(VaultUnlockCancelException e) {
            return proxy.checksum(file, status);
        }
    }

    @Override
    public void preflight(final Path file) throws BackgroundException {
        registry.find(session, file).getFeature(session, Write.class, proxy).preflight(file);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("VaultRegistryWriteFeature{");
        sb.append("proxy=").append(proxy);
        sb.append('}');
        return sb.toString();
    }
}
