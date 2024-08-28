package com.ericsson.nms.presentation.service.tests.config

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.presentation.exceptions.MissingRequiredPropertyException
import com.ericsson.nms.presentation.service.util.PresentationServiceConfig
import spock.lang.Unroll

/**
 * Test specification for PIB properties.
 */
class PibConfiguratorSpec extends CdiSpecification {

    @ObjectUnderTest
    PresentationServiceConfig config

    @Unroll
    def "web host properties should be parsed (#key)"() {

        when: "intitialize the configuration"
            config.init()

        then: "check if the key/value pair match"
            config.getWebHost(key) == value

        where:
            key           | value
            "web-host-01" | "3.3.3.3"
            "web-host-02" | "4.4.4.4"
            "default"     | "localhost"
    }

    @Unroll
    def "web protocol properties should be parsed (#key)"() {

        when: "intitialize the configuration"
            config.init()

        then: "check if the key/value pair match"
            config.getWebProtocol(key) == value

        where:
            key               | value
            "web-protocol-01" | "http"
            "web-protocol-02" | "https"
            "default"         | "http"
    }

    @Unroll
    def "get value with undefined property (#methodName)"() {

        when: "intitialize the configuration"
            config.init()

        and: "get the config using an undefined property"
            config."$methodName"("undefinedProperty")

        then: "an exception is expected"
            thrown(MissingRequiredPropertyException)

        where:
            methodName << ["getWebHost","getWebProtocol"]

    }

}
