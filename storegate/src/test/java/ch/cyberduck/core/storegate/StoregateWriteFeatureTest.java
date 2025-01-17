package ch.cyberduck.core.storegate;

/*
 * Copyright (c) 2002-2019 iterate GmbH. All rights reserved.
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
import ch.cyberduck.core.BytecountStreamListener;
import ch.cyberduck.core.DisabledConnectionCallback;
import ch.cyberduck.core.DisabledLoginCallback;
import ch.cyberduck.core.DisabledPasswordCallback;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.PathAttributes;
import ch.cyberduck.core.exception.ConnectionCanceledException;
import ch.cyberduck.core.exception.LockedException;
import ch.cyberduck.core.exception.TransferStatusCanceledException;
import ch.cyberduck.core.features.Delete;
import ch.cyberduck.core.http.HttpResponseOutputStream;
import ch.cyberduck.core.io.StreamCopier;
import ch.cyberduck.core.shared.DefaultFindFeature;
import ch.cyberduck.core.storegate.io.swagger.client.model.File;
import ch.cyberduck.core.transfer.TransferStatus;
import ch.cyberduck.test.IntegrationTest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.EnumSet;

import static org.junit.Assert.*;

@Category(IntegrationTest.class)
public class StoregateWriteFeatureTest extends AbstractStoregateTest {

    @Test
    public void testReadWrite() throws Exception {
        final StoregateIdProvider nodeid = new StoregateIdProvider(session);
        final Path room = new StoregateDirectoryFeature(session, nodeid).mkdir(
                new Path(String.format("/My files/%s", new AlphanumericRandomStringService().random()),
                        EnumSet.of(Path.Type.directory, Path.Type.volume)), new TransferStatus());
        final long folderTimestamp = new StoregateAttributesFinderFeature(session, nodeid).find(room).getModificationDate();
        final byte[] content = RandomUtils.nextBytes(32769);
        final Path test = new Path(room, String.format("%s", new AlphanumericRandomStringService().random()), EnumSet.of(Path.Type.file));
        final File version;
        {
            final TransferStatus status = new TransferStatus();
            status.setLength(content.length);
            status.setModified(1620113107725L);
            status.setCreated(1695160857860L);
            final StoregateWriteFeature writer = new StoregateWriteFeature(session, nodeid);
            final HttpResponseOutputStream<File> out = writer.write(test, status, new DisabledConnectionCallback());
            assertNotNull(out);
            new StreamCopier(status, status).transfer(new ByteArrayInputStream(content), out);
            version = out.getStatus();
        }
        assertNotNull(version);
        assertTrue(new DefaultFindFeature(session).find(test));
        assertEquals(folderTimestamp, new StoregateAttributesFinderFeature(session, nodeid).find(room).getModificationDate());
        PathAttributes attributes = new StoregateAttributesFinderFeature(session, nodeid).find(test);
        assertEquals(1695160857860L, attributes.getCreationDate());
        assertEquals(1620113107725L, attributes.getModificationDate());
        final String versionId = attributes.getVersionId();
        assertNull(versionId);
        final String nodeId = attributes.getFileId();
        assertNotNull(nodeId);
        assertEquals(new StoregateAttributesFinderFeature(session, nodeid).toAttributes(version), attributes);
        final byte[] compare = new byte[content.length];
        final InputStream stream = new StoregateReadFeature(session, nodeid).read(test, new TransferStatus().withLength(content.length), new DisabledConnectionCallback());
        IOUtils.readFully(stream, compare);
        stream.close();
        assertArrayEquals(content, compare);
        // Overwrite
        {
            final byte[] change = RandomUtils.nextBytes(256);
            final TransferStatus status = new TransferStatus();
            status.setLength(change.length);
            final StoregateWriteFeature writer = new StoregateWriteFeature(session, nodeid);
            final HttpResponseOutputStream<File> out = writer.write(test, status.exists(true), new DisabledConnectionCallback());
            assertNotNull(out);
            new StreamCopier(status, status).transfer(new ByteArrayInputStream(change), out);
            assertEquals(nodeId, out.getStatus().getId());
        }
        test.attributes().setCustom(Collections.emptyMap());
        attributes = new StoregateAttributesFinderFeature(session, nodeid).find(test);
        assertNotNull(attributes.getFileId());
        assertEquals(nodeId, new StoregateIdProvider(session).getFileId(test));
        new StoregateDeleteFeature(session, nodeid).delete(Collections.singletonList(room), new DisabledLoginCallback(), new Delete.DisabledCallback());
    }

    @Test
    public void testWriteSingleByte() throws Exception {
        final StoregateIdProvider nodeid = new StoregateIdProvider(session);
        final StoregateWriteFeature feature = new StoregateWriteFeature(session, nodeid);
        final Path room = new StoregateDirectoryFeature(session, nodeid).mkdir(
                new Path(String.format("/My files/%s", new AlphanumericRandomStringService().random()),
                        EnumSet.of(Path.Type.directory, Path.Type.volume)), new TransferStatus());
        final byte[] content = RandomUtils.nextBytes(1);
        final TransferStatus status = new TransferStatus();
        status.setLength(content.length);
        final Path file = new Path(room, new AlphanumericRandomStringService().random(), EnumSet.of(Path.Type.file));
        final HttpResponseOutputStream<File> out = feature.write(file, status, new DisabledConnectionCallback());
        final ByteArrayInputStream in = new ByteArrayInputStream(content);
        assertEquals(content.length, IOUtils.copyLarge(in, out));
        in.close();
        out.close();
        assertNotNull(out.getStatus());
        assertTrue(new DefaultFindFeature(session).find(file));
        final byte[] compare = new byte[content.length];
        final InputStream stream = new StoregateReadFeature(session, nodeid).read(file, new TransferStatus().withLength(content.length), new DisabledConnectionCallback());
        IOUtils.readFully(stream, compare);
        stream.close();
        assertArrayEquals(content, compare);
        new StoregateDeleteFeature(session, nodeid).delete(Collections.singletonList(file), new DisabledLoginCallback(), new Delete.DisabledCallback());
    }

    @Test
    public void testWriteWithLock() throws Exception {
        final StoregateIdProvider nodeid = new StoregateIdProvider(session);
        final Path room = new StoregateDirectoryFeature(session, nodeid).mkdir(
                new Path(String.format("/My files/%s", new AlphanumericRandomStringService().random()),
                        EnumSet.of(Path.Type.directory, Path.Type.volume)), new TransferStatus());
        final byte[] content = RandomUtils.nextBytes(32769);
        final Path test = new StoregateTouchFeature(session, nodeid).touch(
                new Path(room, String.format("%s", new AlphanumericRandomStringService().random()), EnumSet.of(Path.Type.file)), new TransferStatus());
        final String lockId = new StoregateLockFeature(session, nodeid).lock(test);
        final TransferStatus status = new TransferStatus();
        status.setLength(content.length);
        final StoregateWriteFeature writer = new StoregateWriteFeature(session, nodeid);
        try {
            final HttpResponseOutputStream<File> out = writer.write(test, status, new DisabledConnectionCallback());
            out.close();
            fail();
        }
        catch(IOException e) {
            assertTrue(e.getCause() instanceof LockedException);
        }
        status.setLockId(lockId);
        final HttpResponseOutputStream<File> out = writer.write(test, status, new DisabledConnectionCallback());
        assertNotNull(out);
        new StreamCopier(status, status).transfer(new ByteArrayInputStream(content), out);
        out.close();
        new StoregateLockFeature(session, nodeid).unlock(test, lockId);
        new StoregateDeleteFeature(session, nodeid).delete(Collections.singletonList(room), new DisabledLoginCallback(), new Delete.DisabledCallback());
    }

    @Test
    public void testWriteWithLockAlreadyReleased() throws Exception {
        final StoregateIdProvider nodeid = new StoregateIdProvider(session);
        final Path room = new StoregateDirectoryFeature(session, nodeid).mkdir(
                new Path(String.format("/My files/%s", new AlphanumericRandomStringService().random()),
                        EnumSet.of(Path.Type.directory, Path.Type.volume)), new TransferStatus());
        final byte[] content = RandomUtils.nextBytes(32769);
        final Path test = new StoregateTouchFeature(session, nodeid).touch(
                new Path(room, String.format("%s", new AlphanumericRandomStringService().random()), EnumSet.of(Path.Type.file)), new TransferStatus());
        final TransferStatus status = new TransferStatus();
        status.setLength(content.length);
        final String lockId = new StoregateLockFeature(session, nodeid).lock(test);
        new StoregateLockFeature(session, nodeid).unlock(test, lockId);
        final StoregateWriteFeature writer = new StoregateWriteFeature(session, nodeid);
        status.setLockId(lockId);
        final HttpResponseOutputStream<File> out = writer.write(test, status, new DisabledConnectionCallback());
        assertNotNull(out);
        new StreamCopier(status, status).transfer(new ByteArrayInputStream(content), out);
        out.close();
        new StoregateDeleteFeature(session, nodeid).delete(Collections.singletonList(room), new DisabledLoginCallback(), new Delete.DisabledCallback());
    }

    @Test(expected = TransferStatusCanceledException.class)
    public void testWriteCancel() throws Exception {
        final StoregateIdProvider nodeid = new StoregateIdProvider(session);
        final Path room = new StoregateDirectoryFeature(session, nodeid).mkdir(
                new Path(String.format("/My files/%s", new AlphanumericRandomStringService().random()),
                        EnumSet.of(Path.Type.directory, Path.Type.volume)), new TransferStatus());

        final byte[] content = RandomUtils.nextBytes(32769);
        final Path test = new Path(room, String.format("{%s", new AlphanumericRandomStringService().random()), EnumSet.of(Path.Type.file));
        final BytecountStreamListener listener = new BytecountStreamListener();
        final TransferStatus status = new TransferStatus() {
            @Override
            public void validate() throws ConnectionCanceledException {
                if(listener.getSent() >= 32768) {
                    throw new TransferStatusCanceledException();
                }
                super.validate();
            }
        };
        status.setLength(content.length);
        final StoregateWriteFeature writer = new StoregateWriteFeature(session, nodeid);
        final HttpResponseOutputStream<File> out = writer.write(test, status, new DisabledConnectionCallback());
        assertNotNull(out);
        new StreamCopier(status, status).withListener(listener).transfer(new ByteArrayInputStream(content), out);
        assertFalse(new DefaultFindFeature(session).find(test));
        out.getStatus();
        new StoregateDeleteFeature(session, nodeid).delete(Collections.singletonList(room), new DisabledPasswordCallback(), new Delete.DisabledCallback());
    }
}
