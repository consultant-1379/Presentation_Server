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

import com.ericsson.nms.presentation.service.cache.scheduler.CacheLoaderScheduler;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

/**
 * Startup Singleton bean responsible to configure the service.
 */
@Singleton
@Startup
@DependsOn("PresentationServerCacheProvider")
public class StartupBean {

    @Inject
    CacheLoaderScheduler scheduler;

    @PostConstruct
    public void onStart() {
        // populate the cache for the first time
        scheduler.onTimeout();
    }

}
