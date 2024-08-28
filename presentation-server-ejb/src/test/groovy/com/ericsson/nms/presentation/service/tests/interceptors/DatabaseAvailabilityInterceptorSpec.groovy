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
package com.ericsson.nms.presentation.service.tests.interceptors

import com.ericsson.nms.presentation.exceptions.database.DatabaseUnavailabilityException
import com.ericsson.nms.presentation.service.database.availability.DBAvailabilityMonitor
import com.ericsson.nms.presentation.service.database.availability.interceptors.DatabaseAvailabilityInterceptor
import com.ericsson.nms.presentation.service.database.availability.interceptors.exception.handling.DatabaseAvailabilityInterceptorExceptionHandler
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder
import org.postgresql.util.PSQLState
import spock.lang.Specification

import javax.interceptor.InvocationContext
import java.lang.reflect.Method
import java.sql.SQLException

import static com.ericsson.nms.presentation.service.database.availability.metrics.counters.DatabaseAvailabilityEventTypes.REQUESTED_WHEN_UNAVAILABLE
import static com.ericsson.nms.presentation.service.database.availability.metrics.counters.DatabaseAvailabilityMetricsCounters.REQUESTS_WHEN_DATABASE_UNAVAILABLE
import static com.ericsson.nms.presentation.service.database.availability.metrics.counters.DatabaseAvailabilityMetricsCounters.USE_CASE
import static org.hamcrest.CoreMatchers.equalTo
import static spock.util.matcher.HamcrestSupport.that

class DatabaseAvailabilityInterceptorSpec extends Specification {
    DatabaseAvailabilityInterceptor databaseAvailabilityInterceptor = new DatabaseAvailabilityInterceptor();

    DBAvailabilityMonitor dbAvailabilityMonitor = Mock(DBAvailabilityMonitor)
    SystemRecorder systemRecorder = Mock(SystemRecorder)

    DatabaseAvailabilityInterceptorExceptionHandler databaseAvailabilityInterceptorExceptionHandler =
        Mock(DatabaseAvailabilityInterceptorExceptionHandler)

    def setup() {
        databaseAvailabilityInterceptor.dbAvailabilityMonitor = dbAvailabilityMonitor
        databaseAvailabilityInterceptor.databaseAvailabilityInterceptorExceptionHandler = databaseAvailabilityInterceptorExceptionHandler
        databaseAvailabilityInterceptor.systemRecorder = systemRecorder
    }

    def "context is allowed to proceed if the database is available"() {
        given: "database is available"
            dbAvailabilityMonitor.databaseAvailable >> true
        and: "an invocation context"
            InvocationContext invocationContext = GroovyMock(InvocationContext, {
                getMethod() >> GroovyMock(Method, {toString() >> "test method"})
            })
        when: "a call is intercepted"
            databaseAvailabilityInterceptor.callRequiringDBConnection(invocationContext)
        then: "context is allowed to proceed"
            1 * invocationContext.proceed()

        and: "the recorder is not invoked"
            0 * systemRecorder.recordEventData(_,_)
    }

    def "exception is thrown if the database is not available"() {
        given: "database is not available"
            dbAvailabilityMonitor.databaseAvailable >> false

        and: "an invocation context"
            InvocationContext invocationContext = GroovyMock(InvocationContext, {
                getMethod() >> GroovyMock(Method, {toString() >> "test method"})
            })

        when: "a call is intercepted"
            databaseAvailabilityInterceptor.callRequiringDBConnection(invocationContext)

        then: "context is not allowed to proceed"
            0 * invocationContext.proceed()

        and: "exception is thrown"
            DatabaseUnavailabilityException databaseUnavailabilityException = thrown()
            that(databaseUnavailabilityException.getMessage(),
                equalTo("Database is unavailable; Database availability monitor is reattempting a connection"))

        and: "the recorder is invoked"
            1 * systemRecorder.recordEventData(REQUESTED_WHEN_UNAVAILABLE, {
                it.get(USE_CASE) == "test usecase"
                it.get(REQUESTS_WHEN_DATABASE_UNAVAILABLE) == 1
            } as Map<String, Object>)
    }

    def "If the context throws SQL exception while proceeding, it's handled and the result is thrown"() {
        given: "database is available"
            dbAvailabilityMonitor.databaseAvailable >> true
            DatabaseUnavailabilityException databaseUnavailabilityException = new DatabaseUnavailabilityException();

        and: "an invocation context"
            InvocationContext invocationContext = GroovyMock(InvocationContext, {
                getMethod() >> GroovyMock(Method, {toString() >> "test method"})
            })

        when: "a call is intercepted"
            databaseAvailabilityInterceptor.callRequiringDBConnection(invocationContext)

        then: "context is allowed to proceed"
            1 * invocationContext.proceed()>> {throw new SQLException("test reason", PSQLState.UNEXPECTED_ERROR.state, new Exception())}

        and: "the exception is handled as SQL Exception"
            1 * databaseAvailabilityInterceptorExceptionHandler.handleSQLException(_, _) >> {databaseUnavailabilityException}

        and: "the exception is thrown"
            DatabaseUnavailabilityException due = thrown()
            that(due, equalTo(databaseUnavailabilityException))

        and: "the recorder is not invoked (should be invoked in the handler)"
            0 * systemRecorder.recordEventData(_,_)
    }

    def "If the context throws generic exception while proceeding, it's handled and the result is thrown"() {
        given: "database is available"
            dbAvailabilityMonitor.databaseAvailable >> true
            DatabaseUnavailabilityException databaseUnavailabilityException = new DatabaseUnavailabilityException();

        and: "an invocation context"
            InvocationContext invocationContext = GroovyMock(InvocationContext, {
                getMethod() >> GroovyMock(Method, {toString() >> "test method"})
            })

        when: "a call is intercepted"
            databaseAvailabilityInterceptor.callRequiringDBConnection(invocationContext)

        then: "context is allowed to proceed"
            1 * invocationContext.proceed() >> {throw new Exception(new SQLException("test reason", PSQLState.UNEXPECTED_ERROR.state, new Exception()))}

        and: "the exception is handled as generic Exception"
            1 * databaseAvailabilityInterceptorExceptionHandler.handleException(_, _) >> {return databaseUnavailabilityException}

        and: "the exception is thrown"
            DatabaseUnavailabilityException due = thrown()
           that(databaseUnavailabilityException, equalTo(due))

        and: "the recorder is not invoked (should be invoked in the handler)"
            0 * systemRecorder.recordEventData(_,_)
    }
}
