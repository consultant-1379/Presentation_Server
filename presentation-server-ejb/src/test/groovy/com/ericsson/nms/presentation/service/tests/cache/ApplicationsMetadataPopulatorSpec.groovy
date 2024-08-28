package com.ericsson.nms.presentation.service.tests.cache

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.presentation.service.api.dto.CitrixApplication
import com.ericsson.nms.presentation.service.api.dto.WebApplication
import com.ericsson.nms.presentation.service.cache.ApplicationCachePopulator
import com.ericsson.nms.presentation.service.persistence.dao.impl.ApplicationDao
import com.ericsson.nms.presentation.service.tests.base.AbstractScenario01Spec
import spock.lang.Unroll

import javax.inject.Inject

/**
 * Test specification for the applications cache
 */
class ApplicationsMetadataPopulatorSpec extends AbstractScenario01Spec {

    @ObjectUnderTest
    ApplicationCachePopulator cachePopulator

    @Inject
    ApplicationDao appDao

    def "app01 defined in XML should be replaced by json file"() {

        given:
            def appId = "app01"
            def name = "My Application 01"
            def implClass = WebApplication.class

        when: "retrieve the application from the cache"
            def app = appDao.get(appId)

        then: "assert the application attributes"
            app?.name == name
            app?.class == implClass

    }

    @Unroll
    def "Every application on valid json files should be imported"() {

        when: "retrieve the application from the cache"
            def app = appDao.get(appId)

        then: "assert the application attributes"
            app?.name == name
            app?.class == implClass

        where:
            appId   | name                | implClass
            "app01" | "My Application 01" | WebApplication.class
            "app02" | "My Application 02" | WebApplication.class
            "app03" | "My Application 03" | WebApplication.class
            "app04" | "My Application 04" | CitrixApplication.class
    }

    @Unroll
    def "Every application with invalid json files should not be imported"() {

        when: "retrieve the application from the cache"
            def app = appDao.get(appId)

        then: "app does not exist"
            app == null

        where:
            appId << ["app97", "app98", "app99"]
    }

    def "Application with no version declared should have default version 1"() {

        given:
            def appId = "app02"

        when:
            def app = appDao.get(appId)

        then:
            app.version == 1

    }

    def "Application with version declared should have specified version"() {

        given:
            def appId = "app03"

        when:
            def app = appDao.get(appId)

        then:
            app.version == 2

    }

    def "Application with externalHost should return correct targetUri"() {

        given:
        def appId = "app01"

        when:
        def app = appDao.get(appId)

        then:
        ((WebApplication)app).getTargetUri().contains("://www.sample.com/")

    }

}
