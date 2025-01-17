package ch.cyberduck.core.sftp.putty;

/*
 * Copyright (c) 2002-2014 David Kocher. All rights reserved.
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
 * feedback@cyberduck.ch
 */

import ch.cyberduck.core.sftp.auth.AgentAuthenticator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.jcraft.jsch.agentproxy.AgentProxy;
import com.jcraft.jsch.agentproxy.AgentProxyException;
import com.jcraft.jsch.agentproxy.Identity;
import com.jcraft.jsch.agentproxy.connector.PageantConnector;

/**
 * SSH authentication agent for PuTTY
 */
public class PageantAuthenticator extends AgentAuthenticator {
    private static final Logger log = LogManager.getLogger(PageantAuthenticator.class);

    private AgentProxy proxy;

    public PageantAuthenticator() {
        try {
            this.proxy = new AgentProxy(new PageantConnector());
        }
        catch(AgentProxyException e) {
            log.warn("Agent proxy {} failed with {}", this, e);
        }
    }

    @Override
    public AgentProxy getProxy() {
        return proxy;
    }

    @Override
    public Collection<Identity> getIdentities() {
        if(null == proxy) {
            log.warn("Missing proxy reference");
            return Collections.emptyList();
        }
        log.debug("Retrieve identities from proxy {}", proxy);
        final List<Identity> identities = new ArrayList<Identity>();
        try {
            Collections.addAll(identities, proxy.getIdentities());
            log.debug("Found {} identities", identities.size());
        }
        catch(Exception e) {
            log.warn("Ignore failure reading identities from {}", proxy);
        }
        return identities;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PageantAuthenticator{");
        sb.append("proxy=").append(proxy);
        sb.append('}');
        return sb.toString();
    }
}
