package ch.cyberduck.core;

import org.junit.Test;

import java.util.EnumSet;

import static org.junit.Assert.*;

public class PathCacheTest {

    @Test
    public void testIsEmpty() {
        final PathCache cache = new PathCache(1);
        assertTrue(cache.isEmpty());
        cache.put(new Path("/", EnumSet.of(Path.Type.directory)), new AttributedList<>());
        assertFalse(cache.isEmpty());
    }

    @Test
    public void testContainsKey() {
        final PathCache cache = new PathCache(1);
        final Path f = new Path("/", EnumSet.of(Path.Type.directory));
        assertFalse(cache.containsKey(f));
        cache.put(f, new AttributedList<>());
        assertTrue(cache.containsKey(f));
        assertTrue(cache.isCached(f));
    }

    @Test
    public void testInvalidate() {
        final PathCache cache = new PathCache(1);
        final AttributedList<Path> list = new AttributedList<>();
        final Path f = new Path("/t", EnumSet.of(Path.Type.directory));
        cache.put(f, list);
        assertFalse(cache.get(f).attributes().isInvalid());
        cache.invalidate(f);
        assertTrue(cache.get(f).attributes().isInvalid());
        assertTrue(cache.containsKey(f));
        assertTrue(cache.isCached(f));
        assertFalse(cache.isValid(f));
    }

    @Test
    public void testGet() {
        final PathCache cache = new PathCache(1);
        final Path file = new Path("name", EnumSet.of(Path.Type.file));
        assertEquals(AttributedList.<Path>emptyList(), cache.get(file));
    }

    @Test
    public void testGetNull() {
        final PathCache cache = new PathCache(1);
        final Path file = new Path("name", EnumSet.of(Path.Type.file));
        final AttributedList<Path> list = new AttributedList<>();
        list.add(file);
        cache.put(null, list);
        assertEquals(list, cache.get(null));
    }

    @Test
    public void testDisabledCache() {
        PathCache cache = PathCache.empty();
        final Path file = new Path("name", EnumSet.of(Path.Type.file));
        cache.put(file, AttributedList.emptyList());
        assertFalse(cache.containsKey(file));
        assertEquals(0, cache.size());
    }

    @Test
    public void testDirectoryWithFileIdDefaultPredicate() {
        final PathCache cache = new PathCache(1);
        final Path f = new Path("/", EnumSet.of(Path.Type.directory)).withAttributes(new PathAttributes().withFileId("1"));
        assertFalse(cache.containsKey(f));
        cache.put(f, new AttributedList<>());
        assertTrue(cache.containsKey(f));
        assertTrue(cache.isCached(f));
        assertFalse(cache.containsKey(new Path("/", EnumSet.of(Path.Type.directory))));
        assertFalse(cache.isCached(new Path("/", EnumSet.of(Path.Type.directory))));
    }

    @Test
    public void testDirectoryWithFileIdSimplePredicate() {
        final PathCache cache = new PathCache(1) {
            @Override
            public CacheReference<Path> reference(final Path file) {
                return new SimplePathPredicate(file);
            }
        };
        final Path f = new Path("/", EnumSet.of(Path.Type.directory)).withAttributes(new PathAttributes().withFileId("1"));
        assertFalse(cache.containsKey(f));
        cache.put(f, new AttributedList<>());
        assertTrue(cache.containsKey(f));
        assertTrue(cache.isCached(f));
        assertTrue(cache.containsKey(new Path("/", EnumSet.of(Path.Type.directory))));
        assertTrue(cache.isCached(new Path("/", EnumSet.of(Path.Type.directory))));
    }
}
