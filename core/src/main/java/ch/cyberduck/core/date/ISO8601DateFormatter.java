package ch.cyberduck.core.date;

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

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.TimeZone;

import com.google.gson.internal.bind.util.ISO8601Utils;

public class ISO8601DateFormatter implements DateFormatter {

    @Override
    public String format(final Date input, final TimeZone zone) {
        return ISO8601Utils.format(input, true, zone);
    }

    @Override
    public String format(final long milliseconds, final TimeZone zone) {
        return ISO8601Utils.format(new Date(milliseconds), true, zone);
    }

    @Override
    public Date parse(final String input) throws InvalidDateException {
        try {
            return Date.from(LocalDateTime.parse(input, DateTimeFormatter.ISO_DATE_TIME).toInstant(ZoneOffset.UTC));
        }
        catch(DateTimeParseException e) {
            throw new InvalidDateException(e.getMessage(), e);
        }
    }
}
