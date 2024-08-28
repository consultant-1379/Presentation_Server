/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2019
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.presentation.service.cache.migration.startup;

import com.ericsson.nms.presentation.service.cache.scheduler.CacheMigrationScheduler;
import com.ericsson.nms.presentation.service.database.availability.DBAvailabilityMonitor;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;
import java.time.Duration;

/**
 * Singleton bean that will be triggered at startup to activate the cache migration (if needed)
 */
@Singleton
@Startup
public class MigrationStartupBean {

    public static final long DELAY_BEFORE_SCHEDULING_ATTEMPT_MILLIS = 30000;

    @Inject
    private CacheMigrationScheduler scheduler;

    @Inject
    private DBAvailabilityMonitor dbAvailabilityMonitor;

    @Inject
    private TimerService timerService;

    @Inject
    private Logger logger;

    @PostConstruct
    public void startup() {
        final TimerConfig timerConfig = new TimerConfig();
        timerConfig.setPersistent(false);

        timerService.createSingleActionTimer(DELAY_BEFORE_SCHEDULING_ATTEMPT_MILLIS, timerConfig);
        logger.info("Cache migration scheduling attempt is scheduled in {} ms", DELAY_BEFORE_SCHEDULING_ATTEMPT_MILLIS);
    }

    @Timeout
    public void scheduleMigration() {
        final Duration duration = scheduler.scheduleMigration();
        if (duration != null) {
            logger.warn("Scheduling migration to be taken in {} minutes", duration.toMinutes());
        }
    }
}
