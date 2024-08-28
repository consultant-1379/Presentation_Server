/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.cache.scheduler;

import com.ericsson.nms.presentation.service.cache.ApplicationCachePopulator;
import com.ericsson.nms.presentation.service.cache.LocaleCachePopulator;
import com.ericsson.nms.presentation.service.cache.SystemInfoCachePopulator;
import org.slf4j.Logger;

import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Date;

/**
 * Timer service re-populates cache so that if new application metadata is
 * added Launcher can display it without jboss restart.
 */
@Stateless
public class CacheLoaderScheduler {

    @Inject
    ApplicationCachePopulator applicationPopulator;

    @Inject
    LocaleCachePopulator localePopulator;

    @Inject
    SystemInfoCachePopulator systemInfoCachePopulator;

    @Inject
    Logger logger;

    /**
     * Method called every minute to update the cache with the latest files available in the file system
     */
    @Schedule(minute="*", hour="*", persistent=false)
    public void onTimeout() {
        if(logger.isInfoEnabled()) {
            logger.info("Executing timer at {}.",
                    String.format("%1$te/%1$tm/%1$tY %1$tT", new Date()));
        }
        localePopulator.populate();
        applicationPopulator.populate();
        systemInfoCachePopulator.populate();

    }

}

