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

class ChangeLog01Spec extends DatabaseSchemaUpgradeSpecification {

    def setup() {
        upgradeManager.upgrade()
    }

    def "changelog-1.0 should have been applied"() {

        when: "get the changelog table contents"
            def rows = psdbSqlClient.rows("select id, exectype, filename from databasechangelog")
            def changelogRow = rows.find { it.filename == 'db-changelog/prod/db.changelog-1.0.xml'}

        then: "the changelog version 1.0 must have been executed"
            changelogRow != null
            changelogRow.exectype == "EXECUTED"
    }

    def "all entities should be owned by psuser"() {

        when: "executes the query"
            def rows = psdbSqlClient.rows(OWNERSHIP_QUERY)

        then: "all rows should have username set to psuser"
            !rows.empty
            that rows*.username, everyItem(equalTo("psuser"))
    }

}
