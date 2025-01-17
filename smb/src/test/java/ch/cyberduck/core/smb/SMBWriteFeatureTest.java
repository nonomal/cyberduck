package ch.cyberduck.core.smb;

/*
 * Copyright (c) 2002-2023 iterate GmbH. All rights reserved.
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

import ch.cyberduck.core.AlphanumericRandomStringService;
import ch.cyberduck.core.DisabledConnectionCallback;
import ch.cyberduck.core.DisabledListProgressListener;
import ch.cyberduck.core.DisabledLoginCallback;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.features.Delete;
import ch.cyberduck.core.features.Write;
import ch.cyberduck.core.io.StreamCopier;
import ch.cyberduck.core.shared.DefaultHomeFinderService;
import ch.cyberduck.core.transfer.TransferStatus;
import ch.cyberduck.test.TestcontainerTest;

import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.EnumSet;

import static org.junit.Assert.*;

@Category(TestcontainerTest.class)
public class SMBWriteFeatureTest extends AbstractSMBTest {

    @Test
    public void testWrite() throws Exception {
        final Path home = new DefaultHomeFinderService(session).find();
        final Path test = new Path(home, new AlphanumericRandomStringService().random(), EnumSet.of(Path.Type.file));
        {
            final TransferStatus status = new TransferStatus();
            final int length = 1048576;
            final byte[] content = RandomUtils.nextBytes(length);
            status.setLength(content.length);
            final Write writer = new SMBWriteFeature(session);
            status.setChecksum(writer.checksum(test, status).compute(new ByteArrayInputStream(content), status));
            final OutputStream out = writer.write(test, status, new DisabledConnectionCallback());
            assertNotNull(out);
            new StreamCopier(status, status).transfer(new ByteArrayInputStream(content), out);
            assertTrue(new SMBFindFeature(session).find(test));
            assertEquals(content.length, new SMBListService(session).list(test.getParent(), new DisabledListProgressListener()).get(test).attributes().getSize());
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream(content.length);
            final InputStream in = new SMBReadFeature(session).read(test, new TransferStatus().withLength(content.length), new DisabledConnectionCallback());
            new StreamCopier(status, status).transfer(in, buffer);
            assertArrayEquals(content, buffer.toByteArray());
        }
        {
            final TransferStatus status = new TransferStatus().exists(true);
            final int length = 548576;
            final byte[] content = RandomUtils.nextBytes(length);
            status.setLength(content.length);
            final Write writer = new SMBWriteFeature(session);
            status.setChecksum(writer.checksum(test, status).compute(new ByteArrayInputStream(content), status));
            final OutputStream out = writer.write(test, status, new DisabledConnectionCallback());
            assertNotNull(out);
            new StreamCopier(status, status).transfer(new ByteArrayInputStream(content), out);
            assertTrue(new SMBFindFeature(session).find(test));
            assertEquals(content.length, new SMBListService(session).list(test.getParent(), new DisabledListProgressListener()).get(test).attributes().getSize());
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream(content.length);
            final InputStream in = new SMBReadFeature(session).read(test, new TransferStatus().withLength(content.length), new DisabledConnectionCallback());
            new StreamCopier(status, status).transfer(in, buffer);
            assertArrayEquals(content, buffer.toByteArray());
        }
        new SMBDeleteFeature(session).delete(Collections.singletonList(test), new DisabledLoginCallback(), new Delete.DisabledCallback());
    }
}
