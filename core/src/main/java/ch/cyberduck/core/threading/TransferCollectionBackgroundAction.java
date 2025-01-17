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
import ch.cyberduck.core.TransferCollection;
import ch.cyberduck.core.pool.SessionPool;
import ch.cyberduck.core.preferences.PreferencesFactory;
import ch.cyberduck.core.transfer.Transfer;
import ch.cyberduck.core.transfer.TransferListener;
import ch.cyberduck.core.transfer.TransferOptions;
import ch.cyberduck.core.transfer.TransferQueue;
import ch.cyberduck.core.transfer.TransferQueueFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TransferCollectionBackgroundAction extends TransferBackgroundAction {
    private static final Logger log = LogManager.getLogger(TransferCollectionBackgroundAction.class);

    private final TransferQueue queue = TransferQueueFactory.get();

    private final SessionPool source;
    private final SessionPool destination;
    private final Transfer transfer;
    private final ProgressListener listener;

    public TransferCollectionBackgroundAction(final Controller controller,
                                              final SessionPool source,
                                              final SessionPool destination,
                                              final TransferListener transferListener,
                                              final ProgressListener listener,
                                              final Transfer transfer,
                                              final TransferOptions options) {
        super(controller, source, destination, transferListener, listener, transfer, options);
        this.source = source;
        this.destination = destination;
        this.transfer = transfer;
        this.listener = listener;
    }

    @Override
    public void prepare() {
        // Update status to running
        super.prepare();
        // Wait for slot in queue
        queue.add(transfer, listener);
    }

    @Override
    public void cancel() {
        log.debug("Cancel background action for transfer {}", transfer);
        super.cancel();
    }

    @Override
    public void finish() {
        log.debug("Finish background action for transfer {}", transfer);
        super.finish();
        queue.remove(transfer);
        source.shutdown();
        destination.shutdown();
    }

    @Override
    public void cleanup() {
        log.debug("Cleanup background action for transfer {}", transfer);
        super.cleanup();
        final TransferCollection collection = TransferCollection.defaultCollection();
        if(PreferencesFactory.get().getBoolean("queue.removeItemWhenComplete")
                && transfer.isReset() && transfer.isComplete()) {
            collection.remove(transfer);
        }
        else {
            collection.collectionItemChanged(transfer);
        }
    }
}
