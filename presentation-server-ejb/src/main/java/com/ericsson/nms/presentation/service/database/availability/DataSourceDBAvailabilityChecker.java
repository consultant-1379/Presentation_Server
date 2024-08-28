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

import org.hibernate.exception.GenericJDBCException;
import org.postgresql.util.PSQLState;
import org.slf4j.Logger;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Checks the availability of the DB by obtaining a data source connection.
 */
public class DataSourceDBAvailabilityChecker implements DBAvailabilityChecker {

    public static final String DATA_SOURCE_JNDI_PATH = "java:jboss/datasources/presentation-ds";

    @Resource(lookup = DATA_SOURCE_JNDI_PATH)
    private DataSource dataSource;

    @Inject
    private Logger logger;

    /**
     * Tries to obtain a connection and if the connection-related or JDBC exception is thrown, return false.
     * JDBC exception is handled like that because nothing can throw connection-unrelated exception while just trying to get a connection.
     * @return is DB available
     */
    @Override
    public boolean isAvailable() {
        try (Connection connection = dataSource.getConnection()) {
            return true;
        } catch (SQLException sqle) {
            logger.warn("Exception caught when checking for the data source connection!", sqle);
            return !PSQLState.isConnectionError(sqle.getSQLState()) && (sqle.getSQLState() != null);
        } catch (GenericJDBCException gjbdce) {
            logger.warn("Exception caught when checking for the data source connection!", gjbdce);
            return false;
        }
    }
}
