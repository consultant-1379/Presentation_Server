/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.presentation.service.database.upgrade;

import com.ericsson.nms.presentation.exceptions.database.DatabaseUnavailabilityException;
import com.ericsson.nms.presentation.service.database.schema.DatabaseSchemaUpgradeManager;
import com.ericsson.nms.presentation.service.database.availability.DBAvailabilityMonitor;
import com.ericsson.oss.itpf.sdk.core.retry.RetriableCommandException;
import com.ericsson.oss.itpf.sdk.core.retry.RetryManager;
import com.ericsson.oss.itpf.sdk.core.retry.RetryPolicy;
import com.ericsson.oss.itpf.sdk.recording.EventLevel;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

/**
 * Startup bean responsible to trigger the database upgrade event.
 */
@Singleton
@Startup
public class DatabaseUpgraderService {

    private static final int TIMER_STARTUP_DELAY_IN_MILLISECONDS = 3000;
    private static final Integer MAX_ATTEMPTS = 120;
    private static final Integer WAIT_TIME_IN_SECONDS = 2;

    @Inject
    private SystemRecorder systemRecorder;

    @Inject
    private Logger logger;

    @Inject
    private TimerService timerService;

    @Inject
    private RetryManager retryManager;

    @Inject
    private DatabaseSchemaUpgradeManager schemaUpgradeManager;

    @Inject
    private DBAvailabilityMonitor dbAvailabilityMonitor;

    @PostConstruct
    public void scheduleUpgrade() {
        initializeTimer(TIMER_STARTUP_DELAY_IN_MILLISECONDS);
        logger.info("DB UPGRADE SCHEDULED");
        systemRecorder.recordEvent("PRESENTATION_SERVER.DATABASE.UPGRADE.SCHEDULED", EventLevel.COARSE,
            null, "postgresql_psdb", "DB UPGRADE SCHEDULED");
    }

    /**
     * Timer that will run 3 seconds after the deployment to upgrade the database
     * @param timer Timer that will run 3 seconds after the deployment to upgrade the database
     */
    @Timeout
    public void upgradeDatabase(final Timer timer) {
        // Sets the retry manager retry failed attempts after a delay
        final RetryPolicy policy = RetryPolicy.builder()
                .attempts(MAX_ATTEMPTS)
                .waitInterval(WAIT_TIME_IN_SECONDS, TimeUnit.SECONDS)
                .retryOn(DatabaseUnavailabilityException.class)
                .build();

        try {
            retryManager.executeCommand(policy, retryContext -> {
                logger.info("DB UPGRADE STARTED");
                if (schemaUpgradeManager.upgrade()) {
                    logger.info("DB UPGRADE: detected the successful upgrade, starting DB availability monitor");
                    dbAvailabilityMonitor.start();
                }
                return null;
            });

        } catch (final RetriableCommandException rce) {
            logger.error("DPS failed to deploy within at least {} seconds", MAX_ATTEMPTS * WAIT_TIME_IN_SECONDS);
            logger.error(rce.getMessage(), rce);
        } catch (final Exception e) {
            logger.error("An unexpected {} occurred during the database upgrade: {}", e.getClass().getCanonicalName(), e.getMessage());
            logger.error(e.getMessage(), e);
        }
    }

    private void initializeTimer(final long duration) {
        final TimerConfig timerConfig = new TimerConfig();
        timerConfig.setPersistent(false);
        timerService.createSingleActionTimer(duration, timerConfig);
    }
}
