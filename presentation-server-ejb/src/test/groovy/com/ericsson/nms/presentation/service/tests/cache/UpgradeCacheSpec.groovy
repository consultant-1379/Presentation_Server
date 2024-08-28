package com.ericsson.nms.presentation.service.tests.cache

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.presentation.service.api.dto.Action
import com.ericsson.nms.presentation.service.api.dto.ActionRule
import com.ericsson.nms.presentation.service.api.dto.ActionRuleCondition
import com.ericsson.nms.presentation.service.api.dto.Group
import com.ericsson.nms.presentation.service.api.dto.WebApplication
import com.ericsson.nms.presentation.service.cache.ApplicationCachePopulator
import com.ericsson.nms.presentation.service.persistence.dao.impl.ActionDao
import com.ericsson.nms.presentation.service.persistence.dao.impl.ApplicationDao
import com.ericsson.nms.presentation.service.persistence.dao.impl.GroupDao
import com.ericsson.nms.presentation.service.tests.base.AbstractScenario01Spec
import com.ericsson.nms.presentation.service.util.FileHashUtil

import javax.inject.Inject

/**
 * Test specification for the cache upgrade triggered by the timer.
 */
class UpgradeCacheSpec extends AbstractScenario01Spec {

    @ObjectUnderTest
    ApplicationCachePopulator cachePopulator

    @Inject
    ActionDao actionDao

    @Inject
    GroupDao groupDao

    @MockedImplementation
    FileHashUtil fileHashUtil

    @Inject
    ApplicationDao applicationDao

    def setup() {
        applicationDao.clear()
        actionDao.clear()
        groupDao.clear()
    }

    def "Application with the same hash should not be updated"() {

        given: "assuming the cache has app01 with hash 12345 already"
            def cacheApp = new WebApplication(id: "app01", name: "Current Application", hash: "12345")
            applicationDao.put(cacheApp.id, cacheApp)

        when: "force the populator to read the metadata where app01 again"
            cachePopulator.populate()
            def actualApp = applicationDao.get(cacheApp.id)

        then: "the application name should still be the same"
            actualApp.name == cacheApp.name

        and: "make sure getHash is called ten times (one for each app with a valid json file) and returns the same hash"
            12 * fileHashUtil.getHash(_ as File) >> "12345"

    }

    def "Application with hash different than cache instance should be updated"() {

        given: "assuming the cache has app03 in version 1 already"
            def cacheApp = new WebApplication(id: "app03", name: "Current Application", hash: "12345")
            applicationDao.put(cacheApp.id, cacheApp)

        and: "forces the hash to be different"
            fileHashUtil.getHash(_ as File) >> "any-other-hash"

        when: "force the populator to read the metadata again"
            cachePopulator.populate()
            def actualApp = applicationDao.get(cacheApp.id)

        then: "the application name should be different"
            actualApp.name != cacheApp.name

        and: "the application hash should be any-other-hash"
            actualApp.hash == "any-other-hash"

    }

    def "When json file is not present application should be removed"() {

        given: "assuming the cache has an application id \"anyApp\""
            def cacheApp = new WebApplication(id: "anyApp", name: "Current Application", version: 1)
            applicationDao.put(cacheApp.id, cacheApp)

        when: "force the populator to read the metadata where anyApp does not exist"
            cachePopulator.populate()
            def actualApp = applicationDao.get(cacheApp.id)

        then: "the application should be removed from the cache"
            actualApp == null

    }

    def "Groups should be updated"() {

        given: "assuming the cache has a group \"group01\" with name \"Current Group\""
            def cacheGrp = new Group(id: "group01", name: "Current Group")
            groupDao.put(cacheGrp.id, cacheGrp)

        when: "force the populator to read the metadata where group01 exists with other name"
            cachePopulator.populate()
            def actualGroup = groupDao.get(cacheGrp.id)

        then: "the group name in the cache should be different"
            actualGroup.name != cacheGrp.name

    }

    def "Group should be removed when no application refer to it"() {

        given: "assuming the cache has a group \"anyGroup\""
            def cacheGrp = new Group(id: "anyGroup", name: "Current Group")
            groupDao.put(cacheGrp.id, cacheGrp)

        when: "force the populator to read the metadata where anyGroup does not exist"
            cachePopulator.populate()
            def actualGroup = groupDao.get(cacheGrp.id)

        then: "the actualGroup should be null"
            actualGroup == null

    }

    def "Actions should be updated"() {

        given: "assuming the cache has an action named \"app01-action-01\""
            def cacheAction = new Action(name: "app01-action-01", rules:
                [new ActionRule(actionName:"app01-action-01", condition: new ActionRuleCondition(dataType: "anyDataType") )])

            actionDao.put(cacheAction.name, cacheAction)

        when: "force the populator to read the metadata where an action with same name exists"
            cachePopulator.populate()
            def actualAction = actionDao.get(cacheAction.name)

        then: "the actualAction should have updated rules"
            actualAction.rules.size() == 1
            actualAction.rules[0].condition.dataType != "anyDataType"

    }

    def "Actions not provided anymore should be removed from the cache"() {

        given: "assuming the cache has an action named \"app01-action-01\""
            def cacheAction = new Action(name: "anyAction", rules:
                [new ActionRule(actionName:"anyAction", condition: new ActionRuleCondition(dataType: "anyDataType") )])

            actionDao.put(cacheAction.name, cacheAction)

        when: "force the populator to read the metadata where no action with this name exists"
            cachePopulator.populate()
            def actualAction = actionDao.get(cacheAction.name)

        then: "the actualAction should be null"
            actualAction == null

    }


}
