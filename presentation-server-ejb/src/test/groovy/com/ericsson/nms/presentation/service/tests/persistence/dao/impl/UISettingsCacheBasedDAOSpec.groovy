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
package com.ericsson.nms.presentation.service.tests.persistence.dao.impl

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.presentation.service.persistence.dao.impl.UISettingsCacheBasedDAO
import com.ericsson.nms.presentation.service.persistence.entities.v1.ui_settings.UiSettingEntity
import com.ericsson.nms.presentation.service.persistence.entities.v1.ui_settings.UiSettingGroupEntity
import com.ericsson.nms.presentation.service.tests.base.AbstractPresentationServerSpec
import com.ericsson.oss.itpf.sdk.security.accesscontrol.EAccessControl
import org.slf4j.Logger

import javax.inject.Inject
import java.time.Instant

class UISettingsCacheBasedDAOSpec extends AbstractPresentationServerSpec  {
    @ObjectUnderTest
    UISettingsCacheBasedDAO cacheBasedDAO

    @Inject
    EAccessControl accessControl

    @Inject
    Logger logger

    def "save and read UI setting group"() {
        given: "a sample UI setting group"
            def entity = createSettingGroup()

        when: "save the UI setting group to the database"
            def savedEntity = cacheBasedDAO.save(entity)

        and: "read the entity from the database using the natural ID"
            savedEntity = cacheBasedDAO.findByApplicationAndNameAndUsername("app", "name", "username")

        then: "Created is set, lastUpdated is not"
            savedEntity != null
            savedEntity.get().settings.every({
                it.created
                !it.lastUpdated
            })

        then: "the UI setting group should be found and have the same parameters as the original entity"
            savedEntity != null
            savedEntity.get().name == entity.name
            savedEntity.get().application == entity.application
            savedEntity.get().username == entity.username
            savedEntity.get().settings.size() == 2
            savedEntity.get().settings.any{it.name == "setting1Name" && it.value == "setting1Value"}
            savedEntity.get().settings.any{it.name == "setting2Name" && it.value == "setting2Value"}
    }

    def "update UI setting group value only on modified entities and leaves time empty"() {
        given: "a sample UI setting group"
            def entity = createSettingGroup()
            def startTime = Instant.now()

        when: "save the UI setting group to the database"
            def savedEntity = cacheBasedDAO.save(entity)

        and: "update the UI setting group"
            savedEntity.settings.get(
                savedEntity.settings.findLastIndexOf {it.name == "setting1Name"}).value = "setting1ValueUpdated"
            def updateTime = Instant.now()
            cacheBasedDAO.save(savedEntity)

        and: "read the entity from the database using the natural ID"
            def selectTime = Instant.now()
            savedEntity = cacheBasedDAO.findByApplicationAndNameAndUsername("app", "name", "username")
            savedEntity = savedEntity.get()

        then: "Created and lastUpdated are empty"
            savedEntity.settings.every{!(it.created && it.lastUpdated)}

        and: "the settings size was not changed"
            savedEntity.settings.size() == 2

        and: "Modified values were persisted, unmodified ones were unchanged"
            savedEntity.name == entity.name
            savedEntity.application == entity.application
            savedEntity.username == entity.username

            savedEntity.settings.any {
                it.name == "setting1Name" &&
                    it.value == "setting1ValueUpdated"
            }

            savedEntity.settings.any {
                it.name == "setting2Name" &&
                    it.value == "setting2Value"
            }
    }

    def "delete UI setting group deletes the whole setting group"() {
        given: "a sample UI setting group"
            def entity = createSettingGroup()
        when: "save the UI setting group to the database"
            def savedEntity = cacheBasedDAO.save(entity)
            def id = savedEntity.id

        and: "delete the setting group"
            cacheBasedDAO.remove(savedEntity)

        and: "try to get entity by natural id"
            def foundByNaturalId = cacheBasedDAO.findByApplicationAndNameAndUsername("app", "name", "username")

        then: "the setting group is not found"
            !foundByNaturalId.isPresent()
    }

    def "an attempt to find the setting group that doesn't exist is returning the empty optional"() {
        when: "try to get entity by the natural id"
            def foundEntity = cacheBasedDAO.findByApplicationAndNameAndUsername("nonExistingApp", "nonExistingName", "nonExistingUsername")

        then: "the setting group is not found"
            !foundEntity.isPresent()
    }

    def "updating the setting group to delete the setting deletes the setting"() {
        given: "a sample UI setting group"
            def entity = createSettingGroup()
            entity.settings.add(new UiSettingEntity(name: "setting3Name", value: "setting3Value"))

        when: "save the UI setting group to the database"
            def savedEntity = cacheBasedDAO.save(entity)

        and: "delete the setting 3 from the entity"
            savedEntity.settings.remove(entity.settings[2])
            savedEntity = cacheBasedDAO.save(savedEntity)
        then: "the entity 3 is deleted"
            savedEntity.settings.size() == 2
            savedEntity.settings.every({
                it.name == "setting1Name" || it.name == "setting2Name"
                it.value == "setting1Value" || it.value == "setting2Value"
            })
    }

    def "containsServiceGroup returns true if the group is there"() {
        given: "a sample UI setting group"
            def entity = createSettingGroup()

        when: "save the UI setting group to the database"
            cacheBasedDAO.save(entity)

        then: "cache contains the service group which was set"
            cacheBasedDAO.containsSettingGroup(entity.username, entity.application, entity.name)
    }

    def "containsServiceGroup returns false if the group is not there"() {
        given: "a sample UI setting group"
            def entity = createSettingGroup()

        when: "save the UI setting group to the database"
            cacheBasedDAO.save(entity)

        then: "cache does not contain the group which was not saved"
            !cacheBasedDAO.containsSettingGroup("otherUsername", "otherApp", "otherName")
    }

    def createSettingGroup() {
        def entity = new UiSettingGroupEntity(application: "app", name: "name", username: "username")
        def uiSettingEntity1 = new UiSettingEntity(name: "setting1Name", value: "setting1Value")
        def uiSettingEntity2 = new UiSettingEntity(name: "setting2Name", value: "setting2Value")
        entity.settings = [uiSettingEntity1, uiSettingEntity2]
        return entity
    }

    def createSettingGroup(String application, String name, String username, String settingName, String settingValue) {
        def entity = new UiSettingGroupEntity(application: application, name: name, username: username)
        def uiSettingEntity1 = new UiSettingEntity(name: settingName, value: settingValue)
        entity.settings = [uiSettingEntity1]
        return entity
    }
}
