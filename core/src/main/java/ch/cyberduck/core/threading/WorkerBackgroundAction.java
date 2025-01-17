package ch.cyberduck.core.threading;

/*
 * Copyright (c) 2002-2013 David Kocher. All rights reserved.
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

import ch.cyberduck.core.Controller;
import ch.cyberduck.core.ProgressListener;
import ch.cyberduck.core.Session;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.exception.ConnectionCanceledException;
import ch.cyberduck.core.pool.SessionPool;
import ch.cyberduck.core.worker.Worker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class WorkerBackgroundAction<T> extends RegistryBackgroundAction<T> {
    private static final Logger log = LogManager.getLogger(WorkerBackgroundAction.class);

    protected final Worker<T> worker;

    protected T result;

    public WorkerBackgroundAction(final Controller controller,
                                  final SessionPool session,
                                  final Worker<T> worker) {
        super(controller, session);
        this.worker = worker;
    }

    public WorkerBackgroundAction(final Controller controller,
                                  final SessionPool session,
                                  final Worker<T> worker,
                                  final ProgressListener progress) {
        super(controller, session, progress);
        this.worker = worker;
    }

    public WorkerBackgroundAction(final BackgroundActionListener listener,
                                  final SessionPool session,
                                  final Worker<T> worker,
                                  final ProgressListener progress,
                                  final AlertCallback alert) {
        super(listener, session, progress, alert);
        this.worker = worker;
    }

    @Override
    public T run(final Session<?> session) throws BackgroundException {
        log.debug("Run worker {}", worker);
        try {
            result = worker.run(session);
        }
        catch(ConnectionCanceledException e) {
            worker.cancel();
            throw e;
        }
        return result;
    }

    @Override
    public void cleanup() {
        if(null == result) {
            log.warn("Missing result for worker {}. Use default value.", worker);
            worker.cleanup(worker.initialize());
        }
        else {
            log.debug("Cleanup worker {}", worker);
            worker.cleanup(result);
        }
        super.cleanup();
    }

    @Override
    public void cancel() {
        log.debug("Cancel worker {}", worker);
        worker.cancel();
        super.cancel();
    }

    @Override
    public boolean isCanceled() {
        return worker.isCanceled();
    }

    @Override
    public String getActivity() {
        return worker.getActivity();
    }

    @Override
    public boolean equals(final Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        final WorkerBackgroundAction that = (WorkerBackgroundAction) o;
        if(!Objects.equals(worker, that.worker)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return worker != null ? worker.hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("WorkerBackgroundAction{");
        sb.append("worker=").append(worker);
        sb.append('}');
        return sb.toString();
    }
}
