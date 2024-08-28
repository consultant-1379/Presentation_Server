/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.cache;

import com.ericsson.oss.itpf.modeling.annotation.cache.CacheMode;
import com.ericsson.oss.itpf.sdk.cache.classic.CacheConfiguration;
import com.ericsson.oss.itpf.sdk.cache.classic.CacheProviderBean;

import javax.cache.Cache;

/**
 * Factory class used to build new cache instances.
 */
public class CacheFactory {

    /**
     * Creates a new cache instance
     * @param cacheName name of the cache
     * @param <K> Key type
     * @param <V> Value type
     * @return a cache instance using K as Key and V as value
     */
    public <K,V> Cache<K,V> create(final String cacheName) {

        final CacheConfiguration cacheConfiguration =
                new CacheConfiguration.Builder()
                        .cacheMode(CacheMode.LOCAL).build();

        final CacheProviderBean bean = new CacheProviderBean();
        return bean.createOrGetCache(cacheName, cacheConfiguration);
    }
}
