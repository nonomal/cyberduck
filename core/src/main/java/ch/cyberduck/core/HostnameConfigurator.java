package ch.cyberduck.core;

/*
 * Copyright (c) 2012 David Kocher. All rights reserved.
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


/**
 * Configurator for resolving hostname from alias
 */
public interface HostnameConfigurator {

    /**
     * Lookup hostname for alias
     *
     * @param alias Hostname alias
     * @return Actual hostname
     */
    String getHostname(String alias);

    /**
     * @param alias Hostname alias
     * @return -1 to use default port
     */
    int getPort(String alias);

    HostnameConfigurator reload();

    HostnameConfigurator DISABLED = new HostnameConfigurator() {
        @Override
        public String getHostname(final String alias) {
            return alias;
        }

        @Override
        public int getPort(final String alias) {
            return -1;
        }

        @Override
        public HostnameConfigurator reload() {
            return this;
        }
    };
}
