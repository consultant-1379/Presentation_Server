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
package com.ericsson.nms.presentation.service.database.availability;

import com.ericsson.nms.presentation.service.database.availability.metrics.counters.DatabaseAvailabilityEventTypes;
import com.ericsson.nms.presentation.service.database.availability.metrics.counters.DatabaseAvailabilityMetricsCounters;
import com.ericsson.oss.itpf.sdk.recording.EventLevel;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/**
 * <p>
 * Monitors DB availability by performing regular checks.
 * </p>
 *
 * <p>Should be started by some other component, i.e. no auto-start</p>
 *
 * <p>Can be set up as unavailable manually, e.g. in case of request failure due to connection error.</p>
 */
@ApplicationScoped
public class DBAvailabilityMonitor {

    @Inject
    private Logger logger;

    private static final String DEFAULT_DELAY_PROPERTY = "ps.dbAvailabilityConnectionPollingTimerDelay";
    private static final String DEFAULT_DELAY = "2000";

    private static final String CONNECTION_POLLING_PERIOD_PROPERTY = "ps.dbAvailabilityConnectionPollingPeriod";
    private static final String DEFAULT_CONNECTION_POLLING_PERIOD = "10000";

    @Inject
    private DBAvailabilityChecker dbAvailabilityChecker;

    @Inject
    private DatabaseStatus databaseStatus;

    @Inject
    SystemRecorder systemRecorder;

    private volatile boolean isStarted = false;

    private Timer timer = new Timer();

    private Instant unavailabilityStartTimeMs;

    /**
     * @return true if the DB is available for use and false if not.
     */
    public boolean isDatabaseAvailable() {
        pollForConnection();
        return databaseStatus.getAvailable();
    }

    /**
     * <p>Set the DB as unavailable.</p>
     *
     * <p>The monitor will then perform regular availability checks and set the status back to available</p>
     */
    public void setAsUnavailable() {
        if (databaseStatus.setAsUnavailable().equals(DatabaseStatus.Availability.WAS_CHANGED)) {
            reactToUnavailability();
        }
    }

    /**
     * <p>Starts the check for availability.</p>
     *
     * <p>Before that method all DB calls that use the monitor will not proceed even if the DB is available.</p>
     * <p>This is done because we need some room to finish the DB upgrade.</p>
     */
    public synchronized void start() {
        if (!isStarted) {
            pollForConnection();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    pollForConnection();
                }
            }, getPollingDelay(), getConnectionPollingPeriod());
            isStarted = true;
        } else {
            logger.warn("Attempting to start the DB availability monitor that is already started");
        }
    }

    private void pollForConnection() {
        if (dbAvailabilityChecker.isAvailable()) {
            if (databaseStatus.setAsAvailable().equals(DatabaseStatus.Availability.WAS_CHANGED)) {
                recordAvailabilityEvent();
            }
        } else {
            setAsUnavailable();
        }
    }

    private void reactToUnavailability() {
        unavailabilityStartTimeMs = Instant.now();
        logger.warn("Database unavailable: starting the unavailability time counting");
        this.systemRecorder.recordEvent(DatabaseAvailabilityEventTypes.DB_UNAVAILABLE,
            EventLevel.DETAILED, "postgres",
            null, "Database unavailable: starting the unavailability time counting");
    }

    private long getConnectionPollingPeriod() {
        return Long.parseLong(System.getProperty(CONNECTION_POLLING_PERIOD_PROPERTY, DEFAULT_CONNECTION_POLLING_PERIOD));
    }

    private long getPollingDelay() {
        return Long.parseLong(System.getProperty(DEFAULT_DELAY_PROPERTY, DEFAULT_DELAY));
    }

    private void recordAvailabilityEvent() {
        Duration unavailabilityDuration;
        if (unavailabilityStartTimeMs == null) {
            unavailabilityDuration = Duration.ZERO;
        } else {
            unavailabilityDuration = Duration.between(unavailabilityStartTimeMs, Instant.now());
        }

        logger.info("Database is now available after unavailability or service restart.");

        Map<String, Object> counters = new HashMap<>();
        counters.put(DatabaseAvailabilityMetricsCounters.UNAVAILABILITY_TIME, unavailabilityDuration.toMillis());

        this.systemRecorder.recordEventData(DatabaseAvailabilityEventTypes.DB_AVAILABLE, counters);
    }
}
