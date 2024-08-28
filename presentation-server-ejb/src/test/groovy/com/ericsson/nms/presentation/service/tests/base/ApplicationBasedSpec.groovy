package com.ericsson.nms.presentation.service.tests.base


import com.ericsson.nms.presentation.service.cache.PresentationServerCacheProvider
import com.ericsson.nms.presentation.service.cache.scheduler.CacheLoaderScheduler

import javax.inject.Inject

/**
 * Base specirfication for test cases that depend on the application, actions and locales caches.
 */
class ApplicationBasedSpec extends AbstractCacheSpec {

    @Inject
    PresentationServerCacheProvider cacheProvider

    @Inject
    CacheLoaderScheduler scheduler

    def setup() {
        scheduler.onTimeout()
    }

}
