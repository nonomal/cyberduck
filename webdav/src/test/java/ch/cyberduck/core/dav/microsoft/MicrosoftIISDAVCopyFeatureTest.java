package ch.cyberduck.core.dav.microsoft;

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

import ch.cyberduck.core.AlphanumericRandomStringService;
import ch.cyberduck.core.DisabledConnectionCallback;
import ch.cyberduck.core.DisabledLoginCallback;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.dav.AbstractDAVTest;
import ch.cyberduck.core.dav.DAVAttributesFinderFeature;
import ch.cyberduck.core.dav.DAVCopyFeature;
import ch.cyberduck.core.dav.DAVDeleteFeature;
import ch.cyberduck.core.dav.DAVDirectoryFeature;
import ch.cyberduck.core.dav.DAVLockFeature;
import ch.cyberduck.core.dav.DAVTouchFeature;
import ch.cyberduck.core.exception.ConflictException;
import ch.cyberduck.core.features.Delete;
import ch.cyberduck.core.features.Find;
import ch.cyberduck.core.io.DisabledStreamListener;
import ch.cyberduck.core.shared.DefaultFindFeature;
import ch.cyberduck.core.shared.DefaultHomeFinderService;
import ch.cyberduck.core.transfer.TransferStatus;
import ch.cyberduck.test.IntegrationTest;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;

import static org.junit.Assert.*;

@Category(IntegrationTest.class)
public class MicrosoftIISDAVCopyFeatureTest extends AbstractMicrosoftIISDAVTest {

    @Test
    public void testCopyFile() throws Exception {
        final Path test = new Path(new DefaultHomeFinderService(session).find(), new AlphanumericRandomStringService().random(), EnumSet.of(Path.Type.file));
        new DAVTouchFeature(session).touch(test, new TransferStatus());
        final Path copy = new Path(new DefaultHomeFinderService(session).find(), new AlphanumericRandomStringService().random(), EnumSet.of(Path.Type.file));
        new DAVCopyFeature(session).copy(test, copy, new TransferStatus(), new DisabledConnectionCallback(), new DisabledStreamListener());
        assertEquals(new DAVAttributesFinderFeature(session).find(test), new DAVAttributesFinderFeature(session).find(copy));
        assertTrue(new MicrosoftIISDAVFindFeature(session).find(test));
        assertTrue(new MicrosoftIISDAVFindFeature(session).find(copy));
        new DAVDeleteFeature(session).delete(Collections.<Path>singletonList(test), new DisabledLoginCallback(), new Delete.DisabledCallback());
        new DAVDeleteFeature(session).delete(Collections.<Path>singletonList(copy), new DisabledLoginCallback(), new Delete.DisabledCallback());
    }

    @Test
    public void testCopyWithLock() throws Exception {
        final Path test = new DAVTouchFeature(session).touch(new Path(new DefaultHomeFinderService(session).find(), new AlphanumericRandomStringService().random(), EnumSet.of(Path.Type.file)), new TransferStatus());
        final String lock = new DAVLockFeature(session).lock(test);
        assertEquals(TransferStatus.UNKNOWN_LENGTH, test.attributes().getSize());
        final Path target = new DAVCopyFeature(session).copy(test,
                new Path(new DefaultHomeFinderService(session).find(), new AlphanumericRandomStringService().random(), EnumSet.of(Path.Type.file)), new TransferStatus().withLockId(lock), new DisabledConnectionCallback(), new DisabledStreamListener());
        assertTrue(new MicrosoftIISDAVFindFeature(session).find(test));
        assertTrue(new MicrosoftIISDAVFindFeature(session).find(target));
        assertEquals(test.attributes(), target.attributes());
        new DAVDeleteFeature(session).delete(Collections.singletonMap(test, new TransferStatus().withLockId(lock)), new DisabledLoginCallback(), new Delete.DisabledCallback());
        new DAVDeleteFeature(session).delete(Collections.singletonMap(target, new TransferStatus()), new DisabledLoginCallback(), new Delete.DisabledCallback());
    }

    @Test
    public void testCopyToExistingFile() throws Exception {
        final Path folder = new Path(new DefaultHomeFinderService(session).find(), new AlphanumericRandomStringService().random(), EnumSet.of(Path.Type.directory));
        new DAVDirectoryFeature(session).mkdir(folder, new TransferStatus());
        final Path test = new Path(folder, new AlphanumericRandomStringService().random(), EnumSet.of(Path.Type.file));
        new DAVTouchFeature(session).touch(test, new TransferStatus());
        final Path copy = new Path(folder, new AlphanumericRandomStringService().random(), EnumSet.of(Path.Type.file));
        new DAVTouchFeature(session).touch(copy, new TransferStatus());
        assertThrows(ConflictException.class, () -> new DAVCopyFeature(session).copy(test, copy, new TransferStatus().exists(false), new DisabledConnectionCallback(), new DisabledStreamListener()));
        new DAVCopyFeature(session).copy(test, copy, new TransferStatus().exists(true), new DisabledConnectionCallback(), new DisabledStreamListener());
        final Find find = new DefaultFindFeature(session);
        assertTrue(find.find(test));
        assertTrue(find.find(copy));
        new DAVDeleteFeature(session).delete(Arrays.asList(test, copy), new DisabledLoginCallback(), new Delete.DisabledCallback());
    }

    @Test
    public void testCopyWithLockToExistingFile() throws Exception {
        final Path folder = new Path(new DefaultHomeFinderService(session).find(), new AlphanumericRandomStringService().random(), EnumSet.of(Path.Type.directory));
        new DAVDirectoryFeature(session).mkdir(folder, new TransferStatus());
        final Path test = new Path(folder, new AlphanumericRandomStringService().random(), EnumSet.of(Path.Type.file));
        new DAVTouchFeature(session).touch(test, new TransferStatus());
        final String lock = new DAVLockFeature(session).lock(test);
        final Path copy = new Path(folder, new AlphanumericRandomStringService().random(), EnumSet.of(Path.Type.file));
        new DAVTouchFeature(session).touch(copy, new TransferStatus());
        assertThrows(ConflictException.class, () -> new DAVCopyFeature(session).copy(test, copy, new TransferStatus().exists(false), new DisabledConnectionCallback(), new DisabledStreamListener()));
        new DAVCopyFeature(session).copy(test, copy, new TransferStatus().exists(true).withLockId(lock), new DisabledConnectionCallback(), new DisabledStreamListener());
        final Find find = new DefaultFindFeature(session);
        assertTrue(find.find(test));
        assertTrue(find.find(copy));
        new DAVDeleteFeature(session).delete(Collections.singletonMap(test, new TransferStatus().withLockId(lock)), new DisabledLoginCallback(), new Delete.DisabledCallback());
        new DAVDeleteFeature(session).delete(Collections.singletonMap(copy, new TransferStatus()), new DisabledLoginCallback(), new Delete.DisabledCallback());
    }

    @Test
    public void testCopyDirectory() throws Exception {
        final Path directory = new Path(new DefaultHomeFinderService(session).find(), new AlphanumericRandomStringService().random(), EnumSet.of(Path.Type.directory));
        final String name = new AlphanumericRandomStringService().random();
        final Path file = new Path(directory, name, EnumSet.of(Path.Type.file));
        new DAVDirectoryFeature(session).mkdir(directory, new TransferStatus());
        new DAVTouchFeature(session).touch(file, new TransferStatus());
        final Path copy = new Path(new DefaultHomeFinderService(session).find(), new AlphanumericRandomStringService().random(), EnumSet.of(Path.Type.directory));
        new DAVDirectoryFeature(session).mkdir(copy, new TransferStatus());
        assertThrows(ConflictException.class, () -> new DAVCopyFeature(session).copy(directory, copy, new TransferStatus().exists(false), new DisabledConnectionCallback(), new DisabledStreamListener()));
        new DAVCopyFeature(session).copy(directory, copy, new TransferStatus().exists(true), new DisabledConnectionCallback(), new DisabledStreamListener());
        assertTrue(new MicrosoftIISDAVFindFeature(session).find(file));
        assertTrue(new MicrosoftIISDAVFindFeature(session).find(copy));
        assertTrue(new MicrosoftIISDAVFindFeature(session).find(new Path(copy, name, EnumSet.of(Path.Type.file))));
        new DAVDeleteFeature(session).delete(Arrays.asList(copy, directory), new DisabledLoginCallback(), new Delete.DisabledCallback());
    }
}
