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
package database.schema_upgrade

import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.presentation.service.database.DatabaseConstants
import com.ericsson.nms.presentation.service.database.schema.DatabaseSchemaUpgradeManager
import database.stubs.DataSourceStub
import groovy.sql.Sql
import spock.lang.Shared

import javax.annotation.Resource
import javax.naming.Context
import javax.sql.DataSource

class DatabaseSchemaUpgradeSpecification extends CdiSpecification {

    @ObjectUnderTest
    DatabaseSchemaUpgradeManager upgradeManager

    @Resource(lookup = "java:/app/datasource")
    @ImplementationInstance
    static DataSource dataSource = new DataSourceStub()

    @ImplementationInstance
    protected Context context = Stub {
        lookup(DatabaseConstants.UPGRADE_DATA_SOURCE_JNDI_PATH) >> dataSource
    }

    @Shared
    Properties databaseProperties = new Properties()

    @Shared
    def databaseName = "psdb"

    @Shared
    def url = "jdbc:postgresql://localhost"

    /**
     * Groovy sql client connected to the test database
     */
    @Shared
    Sql psdbSqlClient

    /**
     * Groovy sql client connected to the admin database
     */
    @Shared
    Sql adminSqlClient

    static final OWNERSHIP_QUERY =
        """
            select t.table_name as name, u.usename as username
            from information_schema.tables t
                join pg_catalog.pg_class c on (t.table_name = c.relname)
                join pg_catalog.pg_user u on (c.relowner = u.usesysid)
            where t.table_schema='public'
            union all
            select s.sequence_name, u.usename
            from information_schema.sequences s
                join pg_catalog.pg_class c on (s.sequence_name = c.relname)
                join pg_catalog.pg_user u on (c.relowner = u.usesysid)
            where s.sequence_schema='public'
            union all
            select t.table_name, u.usename
            from information_schema.views t
                join pg_catalog.pg_class c on (t.table_name = c.relname)
                join pg_catalog.pg_user u on (c.relowner = u.usesysid)
            where t.table_schema='public'
        """

    def setupSpec() {
        databaseProperties.setProperty("user","postgres")
        databaseProperties.setProperty("password","P0stgreSQL11")
        databaseProperties.setProperty("ssl","false")

        adminSqlClient = Sql.newInstance("$url/postgres", databaseProperties)

        // Removes the database to make the tests repeatable
        // doing it in the beginning makes more sense as the DB is already populated on startup
        terminateAllConnectionsToDatabase(adminSqlClient)
        adminSqlClient.execute("drop database if exists \"psdb\"")
        adminSqlClient.execute("create database \"psdb\"")
        adminSqlClient.execute("ALTER database psdb OWNER TO psuser;")
        adminSqlClient.close()
        psdbSqlClient = Sql.newInstance("$url/$databaseName", databaseProperties)
    }

    def terminateAllConnectionsToDatabase (Sql client) {
        client.execute("SELECT \n" +
                "   pg_terminate_backend(pg_stat_activity.pid)\n" +
                "FROM pg_stat_activity\n" +
                "WHERE\n" +
                "   pg_stat_activity.datname = 'psdb'\n" +
                "AND pid <> pg_backend_pid()")
    }

    /**
     * Recreates the database after each test to make then independent
     */
    def cleanupSpec() {
        psdbSqlClient.close()
        adminSqlClient.close()
    }
}
