package com.ericsson.nms.presentation.service.tests.cache

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.presentation.service.cache.ApplicationCachePopulator
import com.ericsson.nms.presentation.service.persistence.dao.impl.GroupDao
import com.ericsson.nms.presentation.service.tests.base.AbstractScenario01Spec
import spock.lang.Unroll

import javax.inject.Inject

/**
 * Test specification for Groups cache
 */
class GroupsMetadataPopulatorSpec extends AbstractScenario01Spec {

    @ObjectUnderTest
    ApplicationCachePopulator cachePopulator

    @Inject
    GroupDao groupDao;

    @Unroll
    def "Groups defined in application.xml and json files should be combined in the cache"() {

        when: "retrieve the group from the cache"
            def grp = groupDao.get(groupId)

        then: "assert the group attributes"
            grp?.name == name
            grp?.appIds?.size() == appIds.size()
            grp?.appIds.containsAll(appIds)

        where:
            groupId   | name       | appIds
            "group01" | "Group 01" | ["app01-copy-1", "app01-copy-2", "app01", "app03", "app05"]
            "group02" | "Group 02" | ["app02","app03"]
            "group03" | "Group 03" | ["app04"]
            "group04" | "Group 04" | ["app04"]
    }

}
