package ch.cyberduck.core.pool;

/*
 * Copyright (c) 2002-2015 David Kocher. All rights reserved.
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
 * Bug fixes, suggestions and comments should be sent to feedback@cyberduck.ch
 */

import ch.cyberduck.core.ConnectionService;
import ch.cyberduck.core.DisabledCancelCallback;
import ch.cyberduck.core.Host;
import ch.cyberduck.core.Session;
import ch.cyberduck.core.SessionFactory;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.ssl.X509KeyManager;
import ch.cyberduck.core.ssl.X509TrustManager;
import ch.cyberduck.core.vault.VaultRegistry;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PooledSessionFactory extends BasePooledObjectFactory<Session> {
    private static final Logger log = LogManager.getLogger(PooledSessionFactory.class);

    private final ConnectionService connect;
    private final X509TrustManager trust;
    private final X509KeyManager key;
    private final Host bookmark;
    private final VaultRegistry registry;

    public PooledSessionFactory(final ConnectionService connect, final X509TrustManager trust, final X509KeyManager key,
                                final Host bookmark, final VaultRegistry registry) {
        this.connect = connect;
        this.trust = trust;
        this.key = key;
        this.bookmark = bookmark;
        this.registry = registry;
    }

    @Override
    public Session create() {
        log.debug("Create new session for host {} in pool", bookmark);
        return SessionFactory.create(bookmark, trust, key).withRegistry(registry);
    }

    @Override
    public PooledObject<Session> wrap(final Session session) {
        return new DefaultPooledObject<>(session);
    }

    @Override
    public void activateObject(final PooledObject<Session> p) throws BackgroundException {
        final Session session = p.getObject();
        log.debug("Activate session {}", session);
        connect.check(session, new DisabledCancelCallback());
    }

    @Override
    public void passivateObject(final PooledObject<Session> p) {
        final Session session = p.getObject();
        log.debug("Pause session {}", session);
    }

    @Override
    public void destroyObject(final PooledObject<Session> p) throws BackgroundException {
        final Session session = p.getObject();
        log.debug("Destroy session {}", session);
        connect.close(session);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PooledSessionFactory{");
        sb.append("bookmark=").append(bookmark);
        sb.append('}');
        return sb.toString();
    }
}
