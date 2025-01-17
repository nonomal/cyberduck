package ch.cyberduck.core.http;
/*
 * Copyright (c) 2002-2024 iterate GmbH. All rights reserved.
 * https://cyberduck.io/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

import ch.cyberduck.core.Host;

import org.apache.http.HttpResponse;
import org.apache.http.client.ConnectionBackoffStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

public class CustomConnectionBackoffStrategy extends DefaultServiceUnavailableRetryStrategy implements ConnectionBackoffStrategy {
    private static final Logger log = LogManager.getLogger(CustomConnectionBackoffStrategy.class);

    public CustomConnectionBackoffStrategy(final Host host) {
        super(host);
    }

    @Override
    public boolean shouldBackoff(final Throwable t) {
        if(t instanceof SocketTimeoutException) {
            log.warn("Backoff for timeout failure {}", t.getMessage());
            return true;
        }
        if(t instanceof ConnectException) {
            log.warn("Backoff for connect failure {}", t.getMessage());
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldBackoff(final HttpResponse response) {
        if(this.evaluate(response)) {
            log.warn("Backoff for reply {}", response);
            return true;
        }
        return false;
    }
}
