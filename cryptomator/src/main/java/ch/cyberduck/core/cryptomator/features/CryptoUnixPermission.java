package ch.cyberduck.core.cryptomator.features;

/*
 * Copyright (c) 2002-2017 iterate GmbH. All rights reserved.
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

import ch.cyberduck.core.Path;
import ch.cyberduck.core.Permission;
import ch.cyberduck.core.Session;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.features.UnixPermission;
import ch.cyberduck.core.features.Vault;
import ch.cyberduck.core.transfer.TransferStatus;

import java.util.EnumSet;

public class CryptoUnixPermission implements UnixPermission {

    private final Session<?> session;
    private final UnixPermission delegate;
    private final Vault cryptomator;

    public CryptoUnixPermission(final Session<?> session, final UnixPermission delegate, final Vault cryptomator) {
        this.session = session;
        this.delegate = delegate;
        this.cryptomator = cryptomator;
    }

    @Override
    public void setUnixOwner(final Path file, final String owner) throws BackgroundException {
        delegate.setUnixOwner(cryptomator.encrypt(session, file), owner);
    }

    @Override
    public Permission getDefault(final EnumSet<Path.Type> type) {
        return delegate.getDefault(type);
    }

    @Override
    public void setUnixGroup(final Path file, final String group) throws BackgroundException {
        delegate.setUnixGroup(cryptomator.encrypt(session, file), group);
    }

    @Override
    public Permission getUnixPermission(final Path file) throws BackgroundException {
        return delegate.getUnixPermission(cryptomator.encrypt(session, file));
    }

    @Override
    public void setUnixPermission(final Path file, final TransferStatus status) throws BackgroundException {
        delegate.setUnixPermission(cryptomator.encrypt(session, file), status);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CryptoUnixPermission{");
        sb.append("delegate=").append(delegate);
        sb.append('}');
        return sb.toString();
    }
}
