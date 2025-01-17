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
import ch.cyberduck.core.Session;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.features.Headers;
import ch.cyberduck.core.features.Vault;
import ch.cyberduck.core.transfer.TransferStatus;

import java.util.Map;

public class CryptoHeadersFeature implements Headers {

    private final Session<?> session;
    private final Headers delegate;
    private final Vault vault;

    public CryptoHeadersFeature(final Session<?> session, final Headers delegate, final Vault vault) {
        this.session = session;
        this.delegate = delegate;
        this.vault = vault;
    }

    @Override
    public Map<String, String> getDefault() {
        return delegate.getDefault();
    }

    @Override
    public Map<String, String> getMetadata(final Path file) throws BackgroundException {
        return delegate.getMetadata(vault.encrypt(session, file));

    }

    @Override
    public void setMetadata(final Path file, final TransferStatus status) throws BackgroundException {
        delegate.setMetadata(vault.encrypt(session, file), status);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CryptoHeadersFeature{");
        sb.append("delegate=").append(delegate);
        sb.append('}');
        return sb.toString();
    }
}
