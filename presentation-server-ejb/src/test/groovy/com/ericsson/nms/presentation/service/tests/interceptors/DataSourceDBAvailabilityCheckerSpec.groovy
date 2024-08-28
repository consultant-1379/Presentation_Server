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

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.presentation.service.database.availability.DataSourceDBAvailabilityChecker
import com.ericsson.nms.presentation.service.tests.base.AbstractPresentationServerSpec
import org.hibernate.exception.GenericJDBCException
import org.postgresql.util.PSQLState
import spock.lang.Unroll

import javax.annotation.Resource
import javax.sql.DataSource
import java.sql.Connection
import java.sql.SQLException


class DataSourceDBAvailabilityCheckerSpec extends AbstractPresentationServerSpec {

    @ObjectUnderTest
    DataSourceDBAvailabilityChecker checker

    @Resource(lookup = DataSourceDBAvailabilityChecker.DATA_SOURCE_JNDI_PATH)
    DataSource dataSource

    @Unroll
    def "isAvailable is #isAvailableExpected when the connection #getConnectionResultDescription "() {
        when: "the connection #getConnectionResultDescription "
            dataSource.getConnection() >> {
                if (expectedException) {
                    throw expectedException
                } else {
                    return getConnectionResponse
                }
            }
            boolean isAvailable = checker.isAvailable()

        then: "isAvailable is #isAvailableExpected"
            isAvailable == isAvailableExpected

        where:
            isAvailableExpected | getConnectionResultDescription                                        | getConnectionResponse  | expectedException
            true                | " can be obtained"                                                    | Mock(Connection.class) | null
            false               | " throws SQLException with null SQL state"                            | null                   | new SQLException()
            false               | " throws SQLException with connection-related SQL state"              | null                   | new SQLException("test reason", PSQLState.CONNECTION_UNABLE_TO_CONNECT.state, new Exception())
            true                | " throws SQLException with SQL state unrelated to connection problem" | null                   | new SQLException("test reason", PSQLState.UNEXPECTED_ERROR.state, new Exception())
            false               | " throws GenericJDBCException"                                        | null                   | new GenericJDBCException("", new SQLException())
    }
}
