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
package com.ericsson.nms.presentation.service.database.availability.interceptors.exception.handling;

import com.ericsson.nms.presentation.exceptions.database.DatabaseUnavailabilityException;
import com.ericsson.nms.presentation.service.database.availability.DBAvailabilityMonitor;
import com.ericsson.nms.presentation.service.database.availability.metrics.counters.DatabaseAvailabilityEventTypes;
import com.ericsson.nms.presentation.service.database.availability.metrics.counters.DatabaseAvailabilityMetricsCounters;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.postgresql.util.PSQLState;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Handles the exception by determining whether it is connection-related and notifying the DB availability monitor if so</p>
 * <p>Wraps the exceptions in the {@code DatabaseUnavailabilityException} if they have the underlying {@code SQLException}
 * which is caused by a connection error.</p>
 */
public class DatabaseAvailabilityInterceptorExceptionHandler {
    @Inject
    private DBAvailabilityMonitor dbAvailabilityMonitor;

    @Inject
    private SystemRecorder systemRecorder;

    /**
     * Handles the SQL Exception. This is to avoid checking the stack if the exception thrown is SQLException.
     * @param sqle the SQL exception to be handled
     * @param useCase the use case to log in case of unavailability
     * @return Exception to be thrown by the interceptor
     */
    public Exception handleSQLException(SQLException sqle, String useCase) {
        return processSQLException(sqle, sqle, useCase);
    }

    /**
     * Handles the generic exception by looking for the SQLException causes and reacting to them.
     * @param e the exception to handle
     * @param useCase the use case to log in case of unavailability
     * @return the exception to be thrown by the interceptor
     */
    public Exception handleException(Exception e, String useCase) {
        int sqlExceptionIndexInCauseChain = ExceptionUtils.indexOfType(e, SQLException.class);
        if (sqlExceptionIndexInCauseChain >= 0) {
            SQLException sqle = (SQLException) ExceptionUtils.getThrowables(e)[sqlExceptionIndexInCauseChain];
            return processSQLException(sqle, e, useCase);
        }
        return e;
    }

    private Exception processSQLException(SQLException sqle, Exception originalException, String useCase) {
        if (PSQLState.isConnectionError(sqle.getSQLState())) {
            dbAvailabilityMonitor.setAsUnavailable();

            recordRequestWhenDBIsUnavailable(useCase);

            return new DatabaseUnavailabilityException("Database is unavailable despite availability monitor" +
                " showing it as available. Availability monitor was notified.", originalException);
        } else {
            return originalException;
        }
    }

    private void recordRequestWhenDBIsUnavailable(String useCase) {
        Map<String, Object> counters = new HashMap<>();
        counters.put(DatabaseAvailabilityMetricsCounters.REQUESTS_WHEN_DATABASE_UNAVAILABLE, 1);
        counters.put(DatabaseAvailabilityMetricsCounters.USE_CASE, useCase);
        systemRecorder.recordEventData(DatabaseAvailabilityEventTypes.REQUESTED_WHEN_UNAVAILABLE, counters);
    }
}
