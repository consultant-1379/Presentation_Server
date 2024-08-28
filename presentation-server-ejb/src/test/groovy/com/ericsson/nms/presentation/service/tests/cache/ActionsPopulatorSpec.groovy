package com.ericsson.nms.presentation.service.tests.cache

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.presentation.service.api.dto.Action
import com.ericsson.nms.presentation.service.cache.ApplicationCachePopulator
import com.ericsson.nms.presentation.service.persistence.dao.impl.ActionDao
import com.ericsson.nms.presentation.service.tests.base.AbstractScenario01Spec
import spock.lang.Unroll

import javax.inject.Inject

import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.everyItem
import static spock.util.matcher.HamcrestSupport.that

/**
 * Test specification for the Actions cache
 */
class ActionsPopulatorSpec extends AbstractScenario01Spec {

    @ObjectUnderTest
    ApplicationCachePopulator cachePopulator

    @Inject
    ActionDao actionDao

    @Unroll
    def "All actions should be available in the cache: #actionId"() {

        when: "get the action #actionId from the cache"
            def action = actionDao.get(actionId)

        then: "assert the properties"
            action?.applicationId == appId
            action?.defaultLabel == label
            action?.multipleSelection == multipleSelection
            action?.plugin == plugin

        where: "with the given data"
            appId   | actionId          | label               | multipleSelection | plugin
            "app01" | "app01-action-01" | "[App01] Action 01" | false | "plugins/app01/action01-plugin.js"
            "app02" | "app02-action-01" | "[App02] Action 01" | true  | "plugins/app02/action01-plugin.js"
            "app03" | "app03-action-01" | "[App03] Action 01" | false | "plugins/app03/action01-plugin.js"
            "app03" | "app03-action-02" | "[App03] Action 02" | true  | "plugins/app03/action02-plugin.js"
    }

    @Unroll
    def "All action rules should be imported to the cache: #actionId"() {

        when: "get the action #actionId from the cache"
            def action = actionDao.get(actionId)

        then: "assert the action rules"
            action?.rules.size() == ruleCount

        where: "with the given data"
            actionId          | ruleCount
            "app01-action-01" | 1
            "app02-action-01" | 3
            "app03-action-01" | 0
            "app03-action-02" | 1
    }

    def "Populator should read rule with no properties defined"() {

        given:
            def actionId = "app01-action-01"

        when:
            def action = actionDao.get(actionId)

        then:
            action.rules[0].actionName == actionId
            action.rules[0].condition.dataType == "ManagedObject"
            action.rules[0].condition.properties.size() == 0

    }

    def "Populator should read rules declared in different files for the same action"() {

        given:
            def actionId = "app02-action-01"

        when:
            def action = actionDao.get(actionId)
            def ruleCollection = action.rules.find{ it.condition.dataType == "Collection" }
            def rulesMeContext = action.rules.findAll{ it.condition.dataType == "ManagedObject" }

        then: "all rules should have the actionName attribute equals to the actionId"
            action.rules*.actionName == (1..3).collect {actionId}


        and: "rule for collection data type should have no properties defined"
            ruleCollection.condition.dataType == "Collection"
            ruleCollection.condition.properties.size() == 0

        and: "two rules should be found for MeContext"
            rulesMeContext.size() == 2
            that rulesMeContext*.condition.dataType, everyItem(equalTo("ManagedObject"))

    }

    def "Populator should remove actions not present in the new metadata"() {

        given: "any sample action not present in any json file"
            def oldAction = new Action(name: "old-action", applicationId: "my-app-01")

        when: "add the sample action to the cache"
            actionDao.put("old-action", oldAction)

        and: "run the populator to update the cache using the json files"
            cachePopulator.populate()

        and: "try to retrieve the sample action from the cache"
            def oldActionFromCache = actionDao.get("old-action")

        then: "the action should not be found as it was removed"
           oldActionFromCache == null
    }


}
