package ch.cyberduck.core.dav.microsoft;

/*
 * Copyright (c) 2002-2018 iterate GmbH. All rights reserved.
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
import ch.cyberduck.core.DefaultPathPredicate;
import ch.cyberduck.core.DisabledListProgressListener;
import ch.cyberduck.core.DisabledLoginCallback;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.PathAttributes;
import ch.cyberduck.core.dav.DAVDeleteFeature;
import ch.cyberduck.core.dav.DAVTouchFeature;
import ch.cyberduck.core.features.Delete;
import ch.cyberduck.core.shared.DefaultHomeFinderService;
import ch.cyberduck.core.transfer.TransferStatus;
import ch.cyberduck.test.IntegrationTest;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Collections;
import java.util.EnumSet;

import static org.junit.Assert.*;

@Category(IntegrationTest.class)
public class MicrosoftIISDAVTimestampFeatureTest extends AbstractMicrosoftIISDAVTest {

    @Test
    public void testSetTimestamp() throws Exception {
        final Path file = new DAVTouchFeature(session).touch(new Path(new DefaultHomeFinderService(session).find(), new AlphanumericRandomStringService().random(), EnumSet.of(Path.Type.file)), new TransferStatus());
        assertTrue(new MicrosoftIISDAVFindFeature(session).find(file));
        assertNotSame(PathAttributes.EMPTY, new MicrosoftIISDAVAttributesFinderFeature(session).find(file));
        final TransferStatus status = new TransferStatus();
        new MicrosoftIISDAVTimestampFeature(session).setTimestamp(file, status.withModified(5000L));
        final PathAttributes attr = new MicrosoftIISDAVAttributesFinderFeature(session).find(file);
        assertEquals(5000L, attr.getModificationDate());
        assertEquals(status.getResponse(), attr);
        assertEquals(5000L, new MicrosoftIISDAVListService(session, new MicrosoftIISDAVAttributesFinderFeature(session)).list(file.getParent(),
                new DisabledListProgressListener()).find(new DefaultPathPredicate(file)).attributes().getModificationDate());
        new DAVDeleteFeature(session).delete(Collections.<Path>singletonList(file), new DisabledLoginCallback(), new Delete.DisabledCallback());
        session.close();
    }
}
