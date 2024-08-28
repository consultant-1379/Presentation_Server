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
package dao

import base.PersistenceEnabledSpecification
import com.ericsson.nms.presentation.exceptions.service.ValidationException
import com.ericsson.nms.presentation.service.persistence.dao.configuration.ConfigurationRepository
import com.ericsson.nms.presentation.service.persistence.entities.v1.configuration.ConfigurationEntity
import spock.lang.Unroll

import javax.inject.Inject

class ConfigurationRepositorySpec extends PersistenceEnabledSpecification {

    @Inject
    ConfigurationRepository configurationRepository

    def setup() {
        setDBAvailabilityTo(true)
    }

    def "save and read configuration"() {

        given: "a sample configuration"
            def entity = new ConfigurationEntity(key: "key", value: "my-value")

        when: "save the configuration to the database"
            def savedEntity = configurationRepository.save(entity)

        then: "the saved entity should have a unique id created automatically"
            savedEntity.id != null

        when: "read the entity from the database using the ID"
            savedEntity = configurationRepository.findBy(savedEntity.id)

        then: "the configuration should be found and have the same parameters as the original entity"
            savedEntity != null
            savedEntity.key == entity.key
            savedEntity.value == entity.value
    }

    @Unroll
    def "save configuration with #description should fail"() {

        when: "save the configuration to the database"
            configurationRepository.save(entity)

        then: "a ValidationException should be raised"
            def exception = thrown(ValidationException)
            exception.message == expectedError

        where:
            description                      | entity                                                                      | expectedError
            "key exceeding 150 characters"   | new ConfigurationEntity(key: (1..151).collect {"a"}.join(), value: "valid") | "Configuration key must have at maximum 150 characters."
            "value exceeding 250 characters" | new ConfigurationEntity(key: "valid", value: (1..251).collect {"a"}.join()) | "Configuration value must have at maximum 250 characters."
            "key should not be null"         | new ConfigurationEntity(key: null, value: "valid")                          | "Configuration key is required and can't be empty."
            "key should not be empty"        | new ConfigurationEntity(key: "", value: "valid")                            | "Configuration key is required and can't be empty."
    }

    def "update configuration"() {

        given: "a sample configuration"
            def entity = new ConfigurationEntity(key: "key", value: "my-value")

        when: "save the configuration to the database"
            def savedEntity = configurationRepository.save(entity)

        and: "update the configuration"
            savedEntity.value = "other-value"
            configurationRepository.save(savedEntity)

        and: "read the entity from the database using the ID"
            savedEntity = configurationRepository.findBy(savedEntity.id)

        then: "the configuration should be found in the database with other-value set as value"
            savedEntity != null
            savedEntity.value == "other-value"
    }

    def "delete configuration"() {

        given: "a sample configuration"
            def entity = new ConfigurationEntity(key: "key", value: "my-value")

        when: "save the configuration to the database"
            def savedEntity = configurationRepository.save(entity)
            def id = savedEntity.id

        and: "delete the configuration"
            configurationRepository.remove(savedEntity)

        and: "read the entity from the database using the ID"
            savedEntity = configurationRepository.findBy(id)

        then: "the configuration should not be found"
            savedEntity == null
    }

    def "find configuration by key"() {

        given: "a sample configuration"
            def entity = new ConfigurationEntity(key: "key", value: "my-value")

        when: "save the configuration to the database"
            configurationRepository.save(entity)

        and: "get the collection by key"
            def savedEntity = configurationRepository.findByKey(entity.key)

        then: "the configuration should be found"
            savedEntity != null
    }

}
