package com.ericsson.nms.presentation.service.tests.service

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.presentation.exceptions.SystemPropertyNotFoundException
import com.ericsson.nms.presentation.service.cache.SystemInfoCachePopulator
import com.ericsson.nms.presentation.service.configurator.SystemInfoConfigListener
import com.ericsson.nms.presentation.service.ejb.SystemInfoServiceEjb
import com.ericsson.nms.presentation.service.tests.base.AbstractScenario03Spec
import com.ericsson.oss.itpf.sdk.config.ConfigurationEnvironment

import javax.inject.Inject
/**
 * Test specification for SystemInfo
 */
class SystemInfoSpec extends AbstractScenario03Spec {

    @ObjectUnderTest
    SystemInfoServiceEjb ejb

    @Inject
    SystemInfoConfigListener listener

    @Inject
    SystemInfoCachePopulator populator

    @MockedImplementation
    ConfigurationEnvironment configEnvironment

    def "enm host should be read from PIB using the populator at startup"() {

        when: "simulates the startup bean calling the populator"
            populator.populate()

        and: "retrieve the property from the cache"
            def property = ejb.getSystemProperty("name")

        then: "the property should be found with the value available in PIB"
            property.name == "name"
            property.value == "my-enm-host"

        and: "mock the config API to return my-enm-host for enmHostName and assert that we are calling this method once"
            1 * configEnvironment.getValue("enmHostName") >> "my-enm-host"

    }

    def "enm host should not read PIB again if the property is already on the cache"() {

        when: "simulates the listener event to pre-populate the property"
            listener.onHostNameUpdate("enm-host.ericsson.com")

        and: "simulates the scheduler calling the populator"
            populator.populate()

        and: "retrieve the property from the cache"
            def property = ejb.getSystemProperty("name")

        then: "the property should be found"
            property.name == "name"
            property.value == "enm-host.ericsson.com"

        and: "make sure the property is never retrieved from PIB from other method than the listener"
            0 * configEnvironment.getValue("enmHostName")

    }

    def "get valid system property"() {

        when: "pre populate the cache with the sample property"
            listener.onHostNameUpdate("enm-host.ericsson.com")

        and: "retrieve the property from the cache"
            def property = ejb.getSystemProperty("name")

        then: "the property should be found with the same name and value"
            property.name == "name"
            property.value == "enm-host.ericsson.com"

    }

    def "get invalid system property"() {

        when: "try to retrive a non existent property"
            ejb.getSystemProperty("non-existent-property")

        then: "an exception is expected"
            def exception = thrown(SystemPropertyNotFoundException)
            exception.message == "No Property was found with the given name: non-existent-property"
    }

    def "get all system properties"() {

        when: "pre populate the cache with the sample property"
            listener.onHostNameUpdate("enm-host.ericsson.com")

        and: "retrieve all the properties from the cache"
            def properties = ejb.getAllSystemProperties()

        then: "the property should be found with the same name and value"
            properties.size() == 1
            properties[0].name == "name"
            properties[0].value == "enm-host.ericsson.com"

    }

}
