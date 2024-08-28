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
package com.ericsson.nms.presentation.service.tests.interceptors.exception.handling

import com.ericsson.nms.presentation.exceptions.database.DatabaseUnavailabilityException
import com.ericsson.nms.presentation.service.database.availability.DBAvailabilityMonitor
import com.ericsson.nms.presentation.service.database.availability.interceptors.exception.handling.DatabaseAvailabilityInterceptorExceptionHandler
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder
import org.hibernate.exception.GenericJDBCException
import org.postgresql.util.PSQLState
import spock.lang.Specification

import javax.persistence.PersistenceException
import java.sql.SQLException

import static com.ericsson.nms.presentation.service.database.availability.metrics.counters.DatabaseAvailabilityEventTypes.REQUESTED_WHEN_UNAVAILABLE
import static com.ericsson.nms.presentation.service.database.availability.metrics.counters.DatabaseAvailabilityMetricsCounters.REQUESTS_WHEN_DATABASE_UNAVAILABLE
import static com.ericsson.nms.presentation.service.database.availability.metrics.counters.DatabaseAvailabilityMetricsCounters.USE_CASE
import static org.hamcrest.CoreMatchers.equalTo
import static spock.util.matcher.HamcrestSupport.that

class DatabaseAvailabilityInterceptorExceptionHandlerSpec extends Specification {
    DatabaseAvailabilityInterceptorExceptionHandler databaseAvailabilityInterceptorExceptionWrapper = new DatabaseAvailabilityInterceptorExceptionHandler();

    DBAvailabilityMonitor dbAvailabilityMonitor = Mock(DBAvailabilityMonitor)
    SystemRecorder systemRecorder = Mock(SystemRecorder)

    def setup() {
        databaseAvailabilityInterceptorExceptionWrapper.dbAvailabilityMonitor = dbAvailabilityMonitor
        databaseAvailabilityInterceptorExceptionWrapper.systemRecorder = systemRecorder
    }

    def "when handling SQL exception while proceeding, monitor is notified"() {
        when: "an SQL exception handled"
            DatabaseUnavailabilityException databaseUnavailabilityException = databaseAvailabilityInterceptorExceptionWrapper.handleSQLException(
                new SQLException("test reason", PSQLState.CONNECTION_UNABLE_TO_CONNECT.state, new Exception()), "test usecase"
            )

        then: "the monitor is notified"
            1 * dbAvailabilityMonitor.setAsUnavailable()

        and: "the exception is returned"
            that(databaseUnavailabilityException.getMessage(),
                equalTo("Database is unavailable despite availability monitor showing it as available." +
                    " Availability monitor was notified."))
        and: "the recorder is invoked"
            1 * systemRecorder.recordEventData(REQUESTED_WHEN_UNAVAILABLE, {
                it.get(USE_CASE) == "test usecase"
                it.get(REQUESTS_WHEN_DATABASE_UNAVAILABLE) == 1
            } as Map<String, Object>)
    }

    def "when handling connection-unrelated SQL exception while proceeding, original exception is returned"() {
        when: "an SQL exception handled"
            Exception exception = databaseAvailabilityInterceptorExceptionWrapper.handleSQLException(
                new SQLException("test reason", PSQLState.UNEXPECTED_ERROR.state, new Exception()), "test usecase"
            )

        then: "the monitor is not notified"
            0 * dbAvailabilityMonitor.setAsUnavailable()

        and: "the original exception is returned"
            exception instanceof SQLException
        and: "the recorder is not invoked"
            0 * systemRecorder.recordEventData(_,_)
    }

    def "when handling PersistenceException caused by GenericJDBCException->SQLException related to connection error while proceeding, monitor is notified"() {
        when: "an exception handled"
            DatabaseUnavailabilityException databaseUnavailabilityException = databaseAvailabilityInterceptorExceptionWrapper.handleException(
                new PersistenceException("", new GenericJDBCException(
                    "",
                    new SQLException("test reason", PSQLState.CONNECTION_UNABLE_TO_CONNECT.getState(), new Exception()
                    ))), "test usecase"
            )

        then: "the monitor is notified"
            1 * dbAvailabilityMonitor.setAsUnavailable()

        and: "the exception is returned"
            that(databaseUnavailabilityException.getMessage(),
                equalTo("Database is unavailable despite availability monitor showing it as available." +
                    " Availability monitor was notified."))
        and: "the recorder is invoked"
            1 * systemRecorder.recordEventData(REQUESTED_WHEN_UNAVAILABLE, {
                it.get(USE_CASE) == "test usecase"
                it.get(REQUESTS_WHEN_DATABASE_UNAVAILABLE) == 1
            } as Map<String, Object>)
    }

    def "when handling  PersistenceException caused by GenericJDBCException->SQLException unrelated to connection error while proceeding, original exception is returned"() {
        when: "an exception handled"
            Exception exception = databaseAvailabilityInterceptorExceptionWrapper.handleException(
                new PersistenceException("", new GenericJDBCException(
                    "",
                    new SQLException("test reason", PSQLState.SYNTAX_ERROR.getState(), new Exception()
                    ))), "test usecase")

        then: "the monitor is not notified"
            0 * dbAvailabilityMonitor.setAsUnavailable()

        and: "the original exception is returned"
            exception instanceof PersistenceException
        and: "the recorder is not invoked"
            0 * systemRecorder.recordEventData(_,_)
    }

    def "when handling Exception unrelated to SQLException while proceeding, original exception is returned"() {
        when: "an exception handled"
            Exception exception = databaseAvailabilityInterceptorExceptionWrapper.handleException(
                new Exception(), "test usecase")

        then: "the monitor is not notified"
            0 * dbAvailabilityMonitor.setAsUnavailable()

        and: "the original exception is returned"
            exception instanceof Exception
        and: "the recorder is not invoked"
            0 * systemRecorder.recordEventData(_,_)
    }
}
