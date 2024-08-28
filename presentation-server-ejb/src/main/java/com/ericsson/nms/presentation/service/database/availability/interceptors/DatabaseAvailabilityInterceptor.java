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
package com.ericsson.nms.presentation.service.database.availability.interceptors;

import com.ericsson.nms.presentation.exceptions.database.DatabaseUnavailabilityException;
import com.ericsson.nms.presentation.service.database.availability.DBAvailabilityMonitor;
import com.ericsson.nms.presentation.service.database.availability.interceptors.bindings.RequiresDatabase;
import com.ericsson.nms.presentation.service.database.availability.interceptors.exception.handling.DatabaseAvailabilityInterceptorExceptionHandler;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static com.ericsson.nms.presentation.service.database.availability.metrics.counters.DatabaseAvailabilityEventTypes.REQUESTED_WHEN_UNAVAILABLE;
import static com.ericsson.nms.presentation.service.database.availability.metrics.counters.DatabaseAvailabilityMetricsCounters.REQUESTS_WHEN_DATABASE_UNAVAILABLE;
import static com.ericsson.nms.presentation.service.database.availability.metrics.counters.DatabaseAvailabilityMetricsCounters.USE_CASE;

/**
 * <p>The idea is to annotate all DAO classes with this interceptor so the first DB unavailability event
 * will result in setting the application-global DB unavailability flag.</p>
 *
 * <p>The interceptor will throw DB unavailability exception if the availability flag is false or if the intercepted request is throwing the connection-related exception.</p>
 * <p>The status will reset itself to available if the database will be available.</p>
 */
@RequiresDatabase
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class DatabaseAvailabilityInterceptor {

    @Inject
    private DBAvailabilityMonitor dbAvailabilityMonitor;

    @Inject
    private DatabaseAvailabilityInterceptorExceptionHandler databaseAvailabilityInterceptorExceptionHandler;

    @Inject
    private SystemRecorder systemRecorder;

    /**
     * <p>Intercepts a call that requires a DB connection and throws the exception if the DB availability monitor
     * is indicating that the connection is not available.</p>
     * <p>If the connection is not available after the monitor indicated that it is available, the monitor is notified.</p>
     *
     * @param invocationContext invocation context
     *
     * @return the object returned by the call.
     *
     * @throws Exception -- should throw the DB unavailability exception.
     */
    @AroundInvoke
    public Object callRequiringDBConnection(final InvocationContext invocationContext) throws Exception {
        if (dbAvailabilityMonitor.isDatabaseAvailable()) {
            try {
                return invocationContext.proceed();
            } catch (SQLException sqle) {
                throw databaseAvailabilityInterceptorExceptionHandler.handleSQLException(sqle, invocationContext.getMethod().toString());
            } catch (Exception e) {
                throw databaseAvailabilityInterceptorExceptionHandler.handleException(e, invocationContext.getMethod().toString());
            }
        } else {
            Map<String, Object> counters = new HashMap<>();
            counters.put(REQUESTS_WHEN_DATABASE_UNAVAILABLE, 1);
            counters.put(USE_CASE, invocationContext.getMethod().toString());
            systemRecorder.recordEventData(REQUESTED_WHEN_UNAVAILABLE, counters);

            throw new DatabaseUnavailabilityException("Database is unavailable; Database availability monitor " +
                "is reattempting a connection");
        }
    }
}
