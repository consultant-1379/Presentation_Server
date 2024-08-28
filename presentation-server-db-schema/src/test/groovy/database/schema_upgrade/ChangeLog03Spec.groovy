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

import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.everyItem
import static spock.util.matcher.HamcrestSupport.that

class ChangeLog03Spec extends DatabaseSchemaUpgradeSpecification {

    def setup() {
        upgradeManager.upgrade()
    }

    def "changelog-3.0 should have been applied"() {

        when: "get the changelog table contents"
            def rows = psdbSqlClient.rows("select id, exectype, filename from databasechangelog")
            def changelogRow = rows.find { it.filename == 'db-changelog/prod/db.changelog-3.0.xml'}

        then: "the changelog version 3.0 must have been executed"
            changelogRow != null
            changelogRow.exectype == "EXECUTED"
    }

    def "table configuration should exist"() {

        given: "configuration table to be checked"
            def table = "configuration"

        when: "executes the query"
            def rows = psdbSqlClient.rows("SELECT to_regclass('" + table + "');")

        then: "table configuration should exist"
            !rows.empty
            that rows.get(0).to_regclass as String, equalTo(table)
    }

    def "configuration \"cache.migration.delay.hours\" should exist with the value 24"() {

        when: "queries the configuration"
            def rows = psdbSqlClient.rows("select config_value from \"configuration\" where config_key = 'cache.migration.delay.hours' ")

        then: "config_value should have the value 24"
            !rows.empty
            that rows[0].config_value as String, equalTo("24")
    }

    def "configuration \"cache.migration.enabled\" should exist with the value true"() {

        when: "queries the configuration"
            def rows = psdbSqlClient.rows("select config_value from \"configuration\" where config_key = 'cache.migration.enabled' ")

        then: "config_value should have the value true"
            !rows.empty
            that rows[0].config_value as String, equalTo("true")
    }


    def "ui settings table should have a new field \"migration_date\""() {

        when: "queries the ui_setting_group table schema"
            def rows = psdbSqlClient.rows("""
                select column_name
                    from information_schema.columns
                    where table_schema = 'public'
                    and table_name = 'ui_setting_group'
                    and column_name = 'migration_date' 
            """)

        then: "a column named \"migration_date\" should be found"
            !rows.empty
            that rows[0].column_name as String, equalTo("migration_date")
    }

    def "all entities should be owned by psuser"() {

        when: "executes the query"
            def rows = psdbSqlClient.rows(OWNERSHIP_QUERY)

        then: "all rows should have username set to psuser"
            !rows.empty
            that rows*.username, everyItem(equalTo("psuser"))
    }

}
