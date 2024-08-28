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
package com.ericsson.nms.presentation.service.cache.scheduler;

import com.ericsson.nms.presentation.service.cache.migration.CacheMigrator;
import com.ericsson.nms.presentation.service.database.availability.interceptors.bindings.RequiresDatabase;
import com.ericsson.nms.presentation.service.persistence.dao.configuration.ConfigurationRepository;
import com.ericsson.nms.presentation.service.persistence.entities.v1.configuration.ConfigurationEntity;
import com.ericsson.nms.presentation.service.uis.beans.UISettingGroupDTO;
import com.ericsson.oss.itpf.sdk.cache.annotation.NamedCache;
import org.slf4j.Logger;

import javax.cache.Cache;
import javax.ejb.DependsOn;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Scheduler used to schedule the cache migration operation
 */
@Singleton
@DependsOn({"DatabaseUpgraderService"})
@RequiresDatabase
public class CacheMigrationScheduler {

    public static final String SCHEDULE_DELAY_CONFIG_KEY = "cache.migration.delay.hours";
    public static final String MIGRATION_ENABLED_CONFIG = "cache.migration.enabled";
    private static final int RESCHEDULE_IN_HOURS = 6;

    @Inject
    private CacheMigrator cacheMigrator;

    @Inject
    private ConfigurationRepository configRepository;

    @Inject
    @NamedCache("Presentation.Server.UISettings")
    private Cache<String, UISettingGroupDTO> cache;

    @Inject
    private TimerService timerService;

    @Inject
    private Logger logger;

    /**
     * Schedule a cache migration to be executed a day later if there's any cache to be migrated.
     * @return A java.time.Duration representing the delay period for the migration to start.
     */
    public Duration scheduleMigration() {
        return this.scheduleMigration(null);
    }

    /**
     * Schedule a cache migration to be executed a day later if there's any cache to be migrated.
     * @param delay the time in milliseconds to wait to execute the timer.
     * @return A java.time.Duration representing the delay period for the migration to start.
     */
    public Duration scheduleMigration(Long delay) {

        if (isMigrationEnabled()) {
            // If cache is not empty schedule a migration
            if (cache != null && cache.iterator().hasNext()) {

                final Duration duration = getTimerDuration(delay);
                final long timerDelay = duration.toMillis();

                final TimerConfig timerConfig = new TimerConfig();
                timerConfig.setPersistent(false);

                timerService.createSingleActionTimer(timerDelay, timerConfig);
                logger.info("Cache migration is scheduled in {} ms", timerDelay);

                return duration;

            } else {
                logger.warn("UISettings cache is empty. No migration required!");
                return null;
            }
        } else {
            logger.warn("Cache migration is disabled!");
            return null;
        }
    }

    @Timeout
    public void migrateReschedulingOnFailure() {
        try {
            migrate();
        } catch (Exception e) {
            logger.error("Migration failed due to exception, scheduling retry: in {} hours", RESCHEDULE_IN_HOURS, e);
            this.scheduleMigration(TimeUnit.HOURS.toMillis(RESCHEDULE_IN_HOURS));
        }
    }

    /**
     * To be used in case we want to expose the migration call to API since it throws relevant exceptions
     */
    public void migrate() {
        cacheMigrator.migrateAll();
        if (cache.iterator().hasNext()) {
            logger.warn("The migration was completed but the cache is still not empty. Rescheduling to run again in 6 hours");
            this.scheduleMigration(TimeUnit.HOURS.toMillis(6));
        }
    }

    /**
     * Indicates if the service should automatically migrate the cache or not.
     * @return true if the cache should be migrated, false otherwise.
     */
    public boolean isMigrationEnabled() {
        final ConfigurationEntity config = configRepository.findByKey(MIGRATION_ENABLED_CONFIG)
            .orElse(new ConfigurationEntity(MIGRATION_ENABLED_CONFIG, "false"));

        return Boolean.parseBoolean(config.getValue());
    }

    /**
     * gets the Duration instance representing the time between now and the time when the scheduler should be triggered.
     * @param delay delay in milliseconds
     * @return duration until the timer execution
     */
    private Duration getTimerDuration(Long delay) {

        if (delay == null) {
            ConfigurationEntity configuration = configRepository.findByKey(SCHEDULE_DELAY_CONFIG_KEY)
                .orElse(new ConfigurationEntity(SCHEDULE_DELAY_CONFIG_KEY, "24"));

            Integer hoursDelay = Integer.parseInt(configuration.getValue());

            return Duration.between(
                LocalDateTime.now(),
                LocalDateTime.now()
                    .plusHours(hoursDelay)
                    // Add random minutes to have some variation to reduce
                    // the chances of two instances running at the same time
                    .plusMinutes(new Random().nextInt(59))
            );

        } else {
            return Duration.between(
                LocalDateTime.now(),
                LocalDateTime.now().plus(delay, ChronoUnit.MILLIS)
            );
        }
    }

}
