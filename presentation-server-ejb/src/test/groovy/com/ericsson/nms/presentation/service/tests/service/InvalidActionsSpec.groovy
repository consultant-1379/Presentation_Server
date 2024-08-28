package com.ericsson.nms.presentation.service.tests.service

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.presentation.service.cache.ApplicationCachePopulator
import com.ericsson.nms.presentation.service.persistence.dao.impl.ActionDao
import com.ericsson.nms.presentation.service.tests.base.AbstractScenario01Spec
import spock.lang.Unroll

import javax.inject.Inject
/**
 * Test specification to test invalid actions scenarios
 */
class InvalidActionsSpec extends AbstractScenario01Spec {

    @ObjectUnderTest
    ApplicationCachePopulator populator

    @Inject
    ActionDao actionDao

    @Unroll
    def "check invalid action condition: #description"() {

        when:
            populator.populate()

        then:
            actionDao.getAll(actionName).isEmpty()

        where:
            description                                     | actionName
            "action with duplicated order (left side)"      | "two-actions-with-same-order-01"
            "action with duplicated order (right side)"     | "two-actions-with-same-order-02"
    }

}
