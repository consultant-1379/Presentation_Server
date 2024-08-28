package com.ericsson.nms.presentation.service.tests.cache

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.presentation.service.api.dto.WebApplication
import com.ericsson.nms.presentation.service.cache.ApplicationCachePopulator
import com.ericsson.nms.presentation.service.persistence.dao.impl.ApplicationDao
import com.ericsson.nms.presentation.service.tests.base.AbstractScenario03Spec
import spock.lang.Unroll

import javax.inject.Inject
/**
 * Test specification for tests where the XML files are not present in the file system but JSON files are.
 */
class OnlyJsonFilesPopulatorSpec extends AbstractScenario03Spec {

    @ObjectUnderTest
    ApplicationCachePopulator cachePopulator

    @Inject
    ApplicationDao appDao

    @Unroll
    def "All applications on json and JAR metadata files should be imported: #appId"() {

        when: "retrieve the application from the cache"
            def app = appDao.get(appId)

        then: "assert the application attributes"
            app?.name == name
            app?.class == implClass
            app?.shortInfo == shortInfo

        where:
            appId           | name                         | shortInfo               | implClass
            "app01"         | "My Application 01"          | "Brief description..."  | WebApplication.class
    }

}
