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
package com.ericsson.nms.presentation.service.database.schema;

import com.ericsson.nms.presentation.exceptions.database.DatabaseException;
import com.ericsson.nms.presentation.service.database.exceptions.SchemaUpgradeException;
import com.ericsson.oss.itpf.sdk.recording.EventLevel;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.slf4j.Logger;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static com.ericsson.nms.presentation.service.database.DatabaseConstants.CHANGELOG_MASTER_DEFAULT_PATH;
import static com.ericsson.nms.presentation.service.database.DatabaseConstants.UPGRADE_DATA_SOURCE_JNDI_PATH;

/**
 * This class is responsible to manage the database upgrade proccess.
 */
public class DatabaseSchemaUpgradeManager {
    @Inject
    private SystemRecorder systemRecorder;

    /**
     * datasource used to connect with the database
     */
    @Resource(lookup = UPGRADE_DATA_SOURCE_JNDI_PATH)
    private DataSource dataSource;

    /**
     * Liquibase changelog file resource path
     */
    private String changeLogPath = CHANGELOG_MASTER_DEFAULT_PATH;

    private static final String UPGRADE_NAMESPACE_PREFIX = "PRESENTATION_SERVER.DATABASE.UPGRADE";
    private static final String UPGRADE_STARTED_EVENT_TYPE = UPGRADE_NAMESPACE_PREFIX + ".STARTED";
    private static final String UPGRADE_SUCCESSFUL_EVENT_TYPE = UPGRADE_NAMESPACE_PREFIX + ".SUCCESSFUL";
    private static final String UPGRADE_FAILED_EVENT_TYPE = UPGRADE_NAMESPACE_PREFIX + ".FAILED";

    private static final String UPGRADE_SOURCE = "liquibase";
    private static final String UPGRADE_RESOURCE = "postgresql_psdb";

    @Inject
    private Logger logger;

    /**
     * Verifies and upgrade the Presentation Server database.
     * This method will use Liquibase to check which version of the database is currently installed and automatically apply in sequence
     * every new change pending.
     * If no change is pending no modification is done.
     *
     * @return true if the DB was actually successfully upgraded (or no changes were detected), false otherwise
     */
    public boolean upgrade() {
        final Contexts contexts = new Contexts("production");
        contexts.add("test");

        return this.upgrade(contexts);
    }

    /**
     * Triggers a database upgrade using the changesets matching the label expression or contexts provided.
     * You can only choose one. If both are used Contexts will have higher priority.
     * @param contexts the liquibase contexts to be used. Currently we support production and test.
     *                 The test context should only be enabled on integration tests environment.
     *
     * @return true if the DB was actually successfully upgraded (or no changes were detected), false otherwise.
     */
    public boolean upgrade(final Contexts contexts) {
        boolean upgraded = false;

        try (Connection connection = dataSource.getConnection()) {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            logger.info("DB UPGRADE: UPDATING DB");
            systemRecorder.recordEvent(UPGRADE_STARTED_EVENT_TYPE, EventLevel.COARSE, UPGRADE_SOURCE, UPGRADE_RESOURCE, "DB UPGRADE: UPDATING DB");
            Liquibase liquibase = new Liquibase(changeLogPath,
                    new ClassLoaderResourceAccessor(this.getClass().getClassLoader()),
                    database);

            liquibase.update(contexts);
            logger.info("DB UPGRADE: UPDATED DB");
            upgraded = true;
            systemRecorder.recordEvent(UPGRADE_SUCCESSFUL_EVENT_TYPE, EventLevel.COARSE, UPGRADE_SOURCE, UPGRADE_RESOURCE, "DB UPGRADE: UPDATED DB");

        } catch(LiquibaseException exception) {
            recordFailureEvent(exception, "schema");
            logger.error(exception.getMessage(), exception);
            throw new SchemaUpgradeException("There was a failure upgrading the database schema.", exception);

        } catch (SQLException exception) {
            recordFailureEvent(exception, "database");
            logger.error(exception.getMessage(), exception);
            throw new DatabaseException("There was a failure executing a database operation.", exception);
        }
        return upgraded;
    }
    private void recordFailureEvent(Exception exception, String exceptionType) {
        Map<String, Object> schemaFailedPropertiesMap = new HashMap<>();
        schemaFailedPropertiesMap.put("exception", exception);
        schemaFailedPropertiesMap.put("exceptionType", exceptionType);
        schemaFailedPropertiesMap.put("errorMessage", exception.getMessage());
        systemRecorder.recordEventData(UPGRADE_FAILED_EVENT_TYPE, schemaFailedPropertiesMap);
    }
}
