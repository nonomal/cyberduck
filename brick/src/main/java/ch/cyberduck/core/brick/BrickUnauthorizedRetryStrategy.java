package ch.cyberduck.core.brick;

/*
 * Copyright (c) 2002-2020 iterate GmbH. All rights reserved.
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

import ch.cyberduck.core.Credentials;
import ch.cyberduck.core.HostPasswordStore;
import ch.cyberduck.core.LocaleFactory;
import ch.cyberduck.core.LoginCallback;
import ch.cyberduck.core.PasswordStoreFactory;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.exception.ConnectionCanceledException;
import ch.cyberduck.core.http.DisabledServiceUnavailableRetryStrategy;
import ch.cyberduck.core.threading.BackgroundAction;
import ch.cyberduck.core.threading.BackgroundActionRegistry;
import ch.cyberduck.core.threading.BackgroundActionStateCancelCallback;
import ch.cyberduck.core.threading.CancelCallback;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HttpContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

public class BrickUnauthorizedRetryStrategy extends DisabledServiceUnavailableRetryStrategy implements HttpRequestInterceptor {
    private static final Logger log = LogManager.getLogger(BrickUnauthorizedRetryStrategy.class);

    private final ReentrantLock lock = new ReentrantLock();
    private final HostPasswordStore store = PasswordStoreFactory.get();
    private final BrickSession session;
    private final LoginCallback prompt;
    private final CancelCallback cancel;

    private String apiKey = StringUtils.EMPTY;

    public BrickUnauthorizedRetryStrategy(final BrickSession session, final LoginCallback prompt, final CancelCallback cancel) {
        this.session = session;
        this.prompt = prompt;
        this.cancel = cancel;
    }

    @Override
    public boolean retryRequest(final HttpResponse response, final int executionCount, final HttpContext context) {
        switch(response.getStatusLine().getStatusCode()) {
            case HttpStatus.SC_UNAUTHORIZED:
                log.debug("Try to acquire semaphore for {}", session);
                // Pairing token no longer valid
                if(!lock.tryLock()) {
                    log.warn("Skip pairing because semaphore cannot be aquired for {}", session);
                    return false;
                }
                try {
                    log.warn("Run pairing flow for {}", session);
                    // Blocks until pairing is complete or canceled
                    final Credentials credentials = session.pair(session.getHost(), prompt, prompt, new BackgroundActionRegistryCancelCallback(cancel),
                            LocaleFactory.localizedString("You've been logged out", "Brick"),
                            LocaleFactory.localizedString("Please complete the login process in your browser.", "Brick")
                    );
                    if(credentials.isSaved()) {
                        store.save(session.getHost());
                    }
                    apiKey = credentials.getPassword();
                    return true;
                }
                catch(BackgroundException e) {
                    log.warn("Failure {} trying to refresh pairing after error response {}", e, response);
                }
                finally {
                    log.debug("Release semaphore for {}", session);
                    lock.unlock();
                }
        }
        return false;
    }

    public void setApiKey(final String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
        if(StringUtils.isNotBlank(apiKey)) {
            request.removeHeaders("X-FilesAPI-Key");
            request.addHeader("X-FilesAPI-Key", apiKey);
        }
    }

    private static final class BackgroundActionRegistryCancelCallback implements CancelCallback {
        private final CancelCallback delegate;

        public BackgroundActionRegistryCancelCallback(final CancelCallback delegate) {
            this.delegate = delegate;
        }

        @Override
        public void verify() throws ConnectionCanceledException {
            delegate.verify();
            for(BackgroundAction action : BackgroundActionRegistry.global()) {
                if(null == action) {
                    continue;
                }
                // Fail if any current background action is canceled
                new BackgroundActionStateCancelCallback(action).verify();
            }
        }
    }
}
