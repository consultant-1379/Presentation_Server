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
package com.ericsson.nms.presentation.service.tests.service.configuration

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.presentation.exceptions.service.configuration.ConfigurationNotFoundException
import com.ericsson.nms.presentation.service.ejb.configuration.ConfigurationService
import com.ericsson.nms.presentation.service.persistence.dao.configuration.ConfigurationRepository
import com.ericsson.nms.presentation.service.persistence.entities.v1.configuration.ConfigurationEntity
import com.ericsson.nms.presentation.service.tests.base.AbstractPresentationServerSpec

class ConfigurationServiceSpec extends AbstractPresentationServerSpec {

    @ObjectUnderTest
    ConfigurationService service

    @MockedImplementation
    ConfigurationRepository repository

    def "getting a valid configuration"() {

        given: "mock repository to find the requested key"
            repository.findByKey("key") >> Optional.of(new ConfigurationEntity("key", "my-value"))

        when: "get an inexistent configuration"
            def config = service.getConfiguration("key")

        then: "a configuration should be found with value my-value"
            config.value == "my-value"

    }

    def "getting a configuration that do not exist should trigger an exception"() {

        given: "mock repository to not find anything with the provided key"
            repository.findByKey("invalid") >> Optional.ofNullable(null)

        when: "get an inexistent configuration"
            service.getConfiguration("invalid")

        then: "an exception is expected"
            thrown(ConfigurationNotFoundException)
    }



}
