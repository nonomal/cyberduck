package ch.cyberduck.core.date;

/*
 * Copyright (c) 2002-2022 iterate GmbH. All rights reserved.
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

import org.junit.Test;

import java.util.TimeZone;

import static org.junit.Assert.*;

public class ISO8601DateFormatterTest {

    @Test
    public void testParseLocalDate() throws Exception {
        // No time zone indicator
        final ISO8601DateFormatter formatter = new ISO8601DateFormatter();
        assertEquals(2527156760000L, formatter.parse("2050-01-30T12:00:00.000000").getTime(), 0L);
    }

    @Test
    public void testParseWithoutMilliseconds() throws Exception {
        final ISO8601DateFormatter formatter = new ISO8601DateFormatter();
        assertThrows(InvalidDateException.class, () -> formatter.parse("").getTime());
        assertEquals(1667567722000L, formatter.parse("2022-11-04T13:15:22Z").getTime(), 0L);
    }

    @Test
    public void testParseWithMilliseconds() throws Exception {
        final ISO8601DateFormatter formatter = new ISO8601DateFormatter();
        assertEquals(1667567722123L, formatter.parse("2022-11-04T13:15:22.123Z").getTime(), 0L);
        assertEquals("2022-11-04T13:15:22.123Z", formatter.format(1667567722123L, TimeZone.getTimeZone("UTC")));
    }

    @Test
    public void testPrint() {
        assertEquals("2022-11-04T12:43:42.654+01:00", new ISO8601DateFormatter().format(1667562222654L, TimeZone.getTimeZone("Europe/Zurich")));
        assertEquals("2022-11-04T11:43:42.654Z", new ISO8601DateFormatter().format(1667562222654L, TimeZone.getTimeZone("UTC")));
    }
}