package ch.cyberduck.core;

/*
 *  Copyright (c) 2005 David Kocher. All rights reserved.
 *  http://cyberduck.ch/
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  Bug fixes, suggestions and comments should be sent to:
 *  dkocher@cyberduck.ch
 */

import ch.cyberduck.core.cache.LRUCache;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public abstract class AbstractCache<T extends Referenceable> implements Cache<T> {
    private static final Logger log = LogManager.getLogger(AbstractCache.class);

    private final LRUCache<CacheReference<T>, AttributedList<T>> impl;

    public AbstractCache(int size) {
        if(size == Integer.MAX_VALUE) {
            // Unlimited
            impl = LRUCache.build();
        }
        else {
            // Will inflate to the given size
            impl = LRUCache.build(size);
        }
    }

    @Override
    public T lookup(final CacheReference<T> reference) {
        for(AttributedList<T> entry : impl.asMap().values()) {
            final T f = entry.find(reference);
            if(null == f) {
                continue;
            }
            return f;
        }
        log.warn("Lookup failed for {} in cache", reference);
        return null;
    }

    @Override
    public long size() {
        return impl.size();
    }

    @Override
    public boolean isEmpty() {
        return impl.isEmpty();
    }

    @Override
    public Map<CacheReference<T>, AttributedList<T>> asMap() {
        return impl.asMap();
    }

    /**
     * @param key Absolute path
     * @return True if the directory listing of this path is cached
     */
    public boolean containsKey(final T key) {
        return impl.contains(this.reference(key));
    }

    /**
     * Remove the cached directory listing for this path
     *
     * @param key Path in cache.
     * @return The previously cached directory listing
     */
    public AttributedList<T> remove(final T key) {
        final AttributedList<T> removed = impl.get(this.reference(key));
        impl.remove(this.reference(key));
        if(null == removed) {
            // Not previously in cache
            return AttributedList.emptyList();
        }
        return removed;
    }

    /**
     * @param key Absolute path
     * @return An empty list if no cached file listing is available
     * @throws java.util.ConcurrentModificationException If the caller is iterating of the cache himself
     *                                                   and requests a new filter here.
     */
    public AttributedList<T> get(final T key) {
        final AttributedList<T> children = impl.get(this.reference(key));
        if(null == children) {
            log.debug("No cache for {}", key);
            return AttributedList.emptyList();
        }
        return children;
    }

    /**
     * @param key      Path in cache.
     * @param children Cached directory listing
     * @return Previous cached version
     */
    public AttributedList<T> put(final T key, final AttributedList<T> children) {
        log.debug("Caching {}", key);
        final AttributedList<T> replaced = impl.get(this.reference(key));
        impl.put(this.reference(key), children);
        if(null == replaced) {
            // Not previously in cache
            return AttributedList.emptyList();
        }
        return replaced;
    }

    /**
     * @return True if this path denotes a directory and its file listing is cached for this session
     */
    public boolean isCached(final T reference) {
        return this.containsKey(reference);
    }

    public boolean isValid(final T reference) {
        if(this.isCached(reference)) {
            return !this.get(reference).attributes().isInvalid();
        }
        return false;
    }

    /**
     * @param reference Path reference
     */
    public void invalidate(final T reference) {
        log.info("Invalidate {}", reference);
        if(this.containsKey(reference)) {
            this.get(reference).attributes().setInvalid(true);
        }
        else {
            log.debug("No cache for {}", reference);
        }
    }

    /**
     * Clear all cached directory listings
     */
    public void clear() {
        log.info("Clear cache {}", this);
        impl.clear();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Cache{");
        sb.append("size=").append(impl.size());
        sb.append('}');
        return sb.toString();
    }
}
