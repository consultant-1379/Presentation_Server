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
package database.stubs

import javax.sql.DataSource
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.SQLFeatureNotSupportedException
import java.util.logging.Logger

/**
 * Stub created to simulate a JBoss Data Source connecting to a real PostgreSQL instance
 */
class DataSourceStub implements DataSource {

    private Connection produceConnection() {
        def databaseProperties = new Properties()
        databaseProperties.setProperty("user","psuser")
        databaseProperties.setProperty("password","ps123")
        databaseProperties.setProperty("ssl","false")
        DriverManager.getConnection("jdbc:postgresql://localhost/psdb", databaseProperties)
    }

    @Override
    Connection getConnection() throws SQLException {
        return produceConnection()
    }

    @Override
    Connection getConnection(final String username, final String password) throws SQLException {
        return produceConnection()
    }

    @Override
    def <T> T unwrap(final Class<T> iface) throws SQLException {
        return null
    }

    @Override
    boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return false
    }

    @Override
    PrintWriter getLogWriter() throws SQLException {
        return null
    }

    @Override
    void setLogWriter(final PrintWriter out) throws SQLException {

    }

    @Override
    void setLoginTimeout(final int seconds) throws SQLException {

    }

    @Override
    int getLoginTimeout() throws SQLException {
        return 0
    }

    @Override
    Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null
    }
}
