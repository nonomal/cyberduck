package ch.cyberduck.core.ftp;

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

import ch.cyberduck.core.Path;
import ch.cyberduck.core.date.MDTMSecondsDateFormatter;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.features.Timestamp;
import ch.cyberduck.core.transfer.TransferStatus;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Date;
import java.util.TimeZone;

public class FTPUTIMETimestampFeature implements Timestamp {
    private static final Logger log = LogManager.getLogger(FTPUTIMETimestampFeature.class);

    private final FTPSession session;

    private FTPException failure;

    public FTPUTIMETimestampFeature(final FTPSession session) {
        this.session = session;
    }

    @Override
    public void setTimestamp(final Path file, final TransferStatus status) throws BackgroundException {
        if(failure != null) {
            log.debug("Skip setting timestamp for {} due to previous failure {}", file, failure.getMessage());
            throw new FTPExceptionMappingService().map("Cannot change timestamp of {0}", failure, file);
        }
        try {
            if(null != status.getModified()) {
                final MDTMSecondsDateFormatter formatter = new MDTMSecondsDateFormatter();
                // The utime() function sets the access and modification times of the named
                // file from the structures in the argument array timep.
                // The access time is set to the value of the first element,
                // and the modification time is set to the value of the second element
                // Accessed date, modified date, created date
                if(!session.getClient().sendSiteCommand(String.format("UTIME %s %s %s %s UTC",
                        file.getAbsolute(),
                        formatter.format(new Date(System.currentTimeMillis()), TimeZone.getTimeZone("UTC")),
                        formatter.format(new Date(status.getModified()), TimeZone.getTimeZone("UTC")),
                        formatter.format(new Date(status.getModified()), TimeZone.getTimeZone("UTC"))))) {
                    throw failure = new FTPException(session.getClient().getReplyCode(),
                            session.getClient().getReplyString());
                }
            }
        }
        catch(IOException e) {
            throw new FTPExceptionMappingService().map("Cannot change timestamp of {0}", e, file);
        }
    }
}
