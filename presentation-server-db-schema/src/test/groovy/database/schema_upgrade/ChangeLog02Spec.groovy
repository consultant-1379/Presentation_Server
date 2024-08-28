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
 *----------------------------------------------------------------------------
*/
package database.schema_upgrade


import org.postgresql.util.PSQLException
import spock.lang.Ignore
import spock.lang.Unroll

import static org.hamcrest.Matchers.equalTo
import static spock.util.matcher.HamcrestSupport.that

class ChangeLog02Spec extends DatabaseSchemaUpgradeSpecification {

    def insertToSettingGroup = "INSERT INTO ui_setting_group (name, username, application) VALUES (?, ?, ?);"

    def insertToSetting = "INSERT INTO ui_setting (name, value, setting_group_id, created) VALUES(?, ?, ?, now())"

    def setup() {
        upgradeManager.upgrade()
    }

    def "changelog-2.0 should have been applied"() {

        when: "get the changelog table contents"
            def rows = psdbSqlClient.rows("select id, exectype, filename from databasechangelog")
            def changelogRow = rows.find { it.filename == 'db-changelog/prod/db.changelog-2.0.xml'}

        then: "the changelog version 2.0 must have been executed"
            changelogRow != null
            changelogRow.exectype == "EXECUTED"
    }

    @Unroll
    def "table #table should exist"() {

        when: "executes the query"
            def rows = psdbSqlClient.rows(query)

        then: "table #table should exist"
            !rows.empty
            that rows.get(0).to_regclass as String, equalTo(table)

        where:
            table                   | query
            "ui_setting"            | "SELECT to_regclass('" + table + "');"
            "ui_setting_group"      | "SELECT to_regclass('" + table + "');"

    }

    def "ui_setting_group should have unique natural ID "() {

        when: "Try to insert the object with the same natural id twice"
            psdbSqlClient.execute(insertToSettingGroup, "name1", "user1", "app1")
            psdbSqlClient.execute(insertToSettingGroup, "name1", "user1", "app1")

        then: "unique constraint should be violated"
            final PSQLException exception = thrown()
            exception.message.contains("duplicate key value violates unique constraint")
    }

    /**
     *  This test cannot be broken up because setting group is linked to setting
     */
    def "ui_setting and ui_setting_group should have primary key generated upon insert "() {

        when: "executes the insert query for ui_setting_group"
            psdbSqlClient.execute(insertToSettingGroup, "name2", "user2", "app2")

            def group_id = psdbSqlClient.rows("SELECT id from ui_setting_group").get(0).id

        then: "settings group id should be generated"
            group_id != null

        when: "execute the insert query for ui_setting"
            psdbSqlClient.execute(insertToSetting, "name3", "value3", group_id)

            def setting_id = psdbSqlClient.rows("SELECT id from ui_setting WHERE name = 'name3' AND value = 'value3';")

        then: "setting id should be generated"
            setting_id != null
    }

    @Unroll
    def "ui_setting_group should have natural ID part #missingField required"() {
        when: "executes the insert with the missing field #missingField"
            psdbSqlClient.execute(query)

        then: "non-null constraint should be violated"
            final PSQLException exception = thrown()
            exception.message.contains("null value in column \"" + missingField + "\" violates not-null constraint")

        where:
            query                                                                           | missingField
            "INSERT INTO ui_setting_group (name, application) VALUES ('name1', 'app1');"    |   "username"
            "INSERT INTO ui_setting_group (name, username) VALUES ('name1', 'user1');"      |   "application"
            "INSERT INTO ui_setting_group (username, application) VALUES ('user1', 'app1');"|   "name"
    }

    def "ui_setting should have unique natural ID "() {

        when: "there is a setting group"
            psdbSqlClient.execute(insertToSettingGroup,
                    "name_settings_group_id1",
                    "user_settings_group_id1",
                    "app_settings_group_id1")

            def group_id = psdbSqlClient.rows("SELECT id from ui_setting_group WHERE name='name_settings_group_id1' AND application='app_settings_group_id1'").get(0).id

        and: "inserting a setting belonging to the group twice with the same natural ID "
            psdbSqlClient.execute(insertToSetting, "name1", "value2", group_id)
            psdbSqlClient.execute(insertToSetting, "name1", "value3", group_id)

        then: "unique constraint should be violated"
            final PSQLException exception = thrown()
            exception.message.contains("duplicate key value violates unique constraint")
    }

    @Unroll
    def "ui_setting should have field #missingField required"() {
        when: "executes the query with the #missingField missing"
            psdbSqlClient.execute(query)

        then: "non-null constraint should be violated"
            final PSQLException exception = thrown()
            exception.message.contains("null value in column \"" + missingField + "\" violates not-null constraint")

        where:
            query                                                                           | missingField
            "INSERT INTO ui_setting (value, setting_group_id, created)" +
                    "VALUES ('value1', '123', now());"                                      |   "name"
            "INSERT INTO ui_setting (name, value, created)" +
                    "VALUES ('name1', 'value1', now());"                                    |   "setting_group_id"
            "INSERT INTO ui_setting (name, value, setting_group_id)" +
                    "VALUES ('name1', 'value1', '123');"                                    |   "created"
            "INSERT INTO ui_setting (name, setting_group_id, created)" +
                    "VALUES ('name1', '123', now());"                                       |   "value"
    }

    def "ui_setting should have a foreign key to setting group "() {

        when: "inserting a setting belonging to the nonexisting group "
            def nonExistentSettingGroupId = 100500
            psdbSqlClient.execute(insertToSetting, "name1", "value1", nonExistentSettingGroupId)

        then: "foreign key constraint should be violated"
            final PSQLException exception = thrown()
            exception.message.contains("insert or update on table \"ui_setting\" violates foreign key constraint")
    }

    def "should be able to update schema with new value and lastUpdated after creation "() {

        when: "there is a setting group"
            psdbSqlClient.execute(insertToSettingGroup, "name_settings_group_id4", "user_settings_group_id4", "app_settings_group_id4")
            def group_id = psdbSqlClient.rows("SELECT id from ui_setting_group WHERE name='name_settings_group_id4' AND application='app_settings_group_id4'").get(0).id

        and: "inserting a setting belonging to the group "
            psdbSqlClient.execute(insertToSetting, "name1", "value1", group_id)

        and: " updating the setting with new value and lastUpdated"
            def updateQuery = "UPDATE ui_setting SET lastUpdated=now(), value='someExampleValue'" +
                    " WHERE name='name1' AND setting_group_id=" + group_id + ";"
            psdbSqlClient.execute(updateQuery)

        and: "selecting the updated row"
            def row = psdbSqlClient.rows("SELECT lastUpdated, value FROM ui_setting " +
                    " WHERE name='name1' AND setting_group_id=" + group_id + ";").get(0)
            def lastUpdated = row.lastUpdated
            def value = row.value

        then: " lastUpdated is updated, "
            lastUpdated != null
        and: "value is updated"
            value == "someExampleValue"
    }

    def "deleting the setting group should cascade to the setting "() {

        when: "there is a setting group"
            psdbSqlClient.execute(insertToSettingGroup, "name_settings_group_id6", "user_settings_group_id6", "app_settings_group_id6")
            def group_id = psdbSqlClient.rows("SELECT id from ui_setting_group WHERE name='name_settings_group_id6' AND application='app_settings_group_id6'").get(0).id

        and: "inserting a setting belonging to the group "
            psdbSqlClient.execute(insertToSetting, "name1", "value1", group_id)

        and: " deleting the setting group"
            def deleteQuery = "DELETE from ui_setting_group" +
                    " WHERE id=" + group_id + ";"
            psdbSqlClient.execute(deleteQuery)

        and: "selecting the associated setting row"
            def rows = psdbSqlClient.rows("SELECT * FROM ui_setting " +
                    " WHERE name='name1' AND setting_group_id=" + group_id + ";")

        then: " row with the associated setting is empty "
            rows.isEmpty()
    }

    def "updating the setting group should cascade to the setting "() {

        when: "there is a setting group"
            psdbSqlClient.execute(insertToSettingGroup, "name_settings_group_id6", "user_settings_group_id6", "app_settings_group_id6")
            def group_id = psdbSqlClient.rows("SELECT id from ui_setting_group WHERE name='name_settings_group_id6' AND application='app_settings_group_id6'").get(0).id

        and: "inserting a setting belonging to the group "
            psdbSqlClient.execute(insertToSetting, "name1", "value1", group_id)

        and: " updating the setting group"
            def deleteQuery = "UPDATE ui_setting_group SET id=1005001 " +
                    " WHERE id=" + group_id + ";"
            psdbSqlClient.execute(deleteQuery)

        and: "selecting the associated setting row by the new setting group key"
            def rows = psdbSqlClient.rows("SELECT * FROM ui_setting " +
                    " WHERE name='name1' AND setting_group_id=1005001;")

        then: " row with the associated setting is not empty "
            !rows.isEmpty()
    }

    @Ignore("Could be the behavior for the DB schema but is handled on Hibernate level")
    def " 'created' field should not be changeable after creation "() {

        when: "there is a setting group"
            psdbSqlClient.execute(insertToSettingGroup, "name_settings_group_id5", "user_settings_group_id5", "app_settings_group_id5")
            def group_id = psdbSqlClient.rows("SELECT id from ui_setting_group WHERE name='name_settings_group_id5' AND application='app_settings_group_id5'").get(0).id

        and: "inserting a setting belonging to the group "
            psdbSqlClient.execute(insertToSetting, "name1", "value1", group_id)

        and: " updating the setting with new value and created"
            def updateQuery = "UPDATE ui_setting SET created=now(), value='someExampleValue'" +
                    " WHERE name='name1' AND setting_group_id=" + group_id + ";"
            psdbSqlClient.execute(updateQuery)

        then: " exception is thrown because 'created' field is not updateable "
            final PSQLException exception = thrown()
    }
}