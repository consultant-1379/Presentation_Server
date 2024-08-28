package com.ericsson.nms.presentation.service.tests.base

import com.ericsson.cds.cdi.support.providers.stubs.InMemoryCache
import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.nms.presentation.service.cache.CacheFactory
import com.ericsson.nms.presentation.service.tests.context.InMemoryContext
import com.ericsson.oss.itpf.sdk.context.ContextService

import javax.cache.Cache
/**
 * Base test class containing the common configuration for cache based tests.
 */
abstract class AbstractCacheSpec extends AbstractPresentationServerSpec {

    /**
     * Overrides the CacheFactory.create method to return an In Memory cache implementation
     */
    @ImplementationInstance
    private CacheFactory cacheFactory = new CacheFactory() {
        @Override
        Cache create(final String cacheName) {
            return new InMemoryCache()
        }
    }

    /**
     * Uses the In-Memory context as a context implementation
     */
    @ImplementationInstance
    private ContextService contextService = new InMemoryContext()

}
