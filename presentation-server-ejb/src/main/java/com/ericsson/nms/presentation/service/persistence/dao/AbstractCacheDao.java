/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.persistence.dao;

import javax.cache.Cache;
import java.io.Serializable;
import java.util.*;

/**
 * Abstract cache DAO implementation
 *
 * @param <K> {Serializable}
 * @param <V> {Serializable}
 */
public abstract class AbstractCacheDao<K extends Serializable, V extends Serializable> {

    /**
     * Should be overridden by the subclass to provide the cache instance
     * @return cache
     */
    protected abstract Cache<K, V> getCache();

    /**
     * @param key {K}
     * @return cache by key
     */
    public V get(final K key) {
        return getCache().get(key);
    }

    /**
     * @param keys {K...}
     * @return cache as collection
     */
    public Collection<V> getAll(final K... keys) {
        final Collection<V> result = new ArrayList<>();
        final Cache<K, V> cache = getCache();
        if (keys.length > 0) {
            final Set<K> keySet = new HashSet<>();
            keySet.addAll(Arrays.asList(keys));
            result.addAll(cache.getAll(keySet).values());
        } else {
            for (final Cache.Entry<K, V> entry : cache) {
                final Cache.Entry<K, V> e = entry;
                result.add(e.getValue());
            }
        }
        return result;
    }

    /**
     * @param keys {Collection<K>}
     * @return cache as map
     */
    public Map<K, V> getAll(final Collection<K> keys) {
        final Set<K> keySet = new HashSet<>();
        keySet.addAll(keys);
        return getCache().getAll(keySet);
    }

    /**
     * @param key {K}
     * @param value {V}
     */
    public void put(final K key, final V value) {
        getCache().put(key, value);
    }


    /**
     * @param keys {Collection<K>}
     */
    public void retainAll(final Collection<K> keys) {

        final Set<K> toRemove = new HashSet<>();
        for (final Cache.Entry<K, V> entry : getCache()) {
            if (!keys.contains(entry.getKey())) {
                toRemove.add(entry.getKey());
            }
        }
        if (!toRemove.isEmpty()) {
            getCache().removeAll(toRemove);
        }
    }

    public void removeAll(final Collection<K> keys) {
        getCache().removeAll(new HashSet<>(keys));
    }

    public void remove(final K key) {
        getCache().remove(key);
    }

    /**
     * Clear cache
     */
    public void clear() {
        getCache().clear();
    }

}
