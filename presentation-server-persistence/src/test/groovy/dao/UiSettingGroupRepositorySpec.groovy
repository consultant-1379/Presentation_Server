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
import com.ericsson.nms.presentation.service.persistence.database.repository.UiSettingGroupRepository
import com.ericsson.nms.presentation.service.persistence.entities.v1.ui_settings.UiSettingEntity
import com.ericsson.nms.presentation.service.persistence.entities.v1.ui_settings.UiSettingGroupEntity
import spock.lang.Unroll

import javax.inject.Inject
import java.time.Instant

class UiSettingGroupRepositorySpec extends PersistenceEnabledSpecification {

    @Inject
    UiSettingGroupRepository uiSettingGroupRepository

    def setup() {

        setDBAvailabilityTo(true)
    }

    def "save and read UI setting group"() {
        given: "a sample UI setting group"
            def entity = createSettingGroup()

        when: "save the UI setting group to the database"
            def savedEntity = uiSettingGroupRepository.saveAndFlush(entity)

        then: "the saved entity should have a unique id created automatically"
            savedEntity.id != null

        when: "read the entity from the database using the natural ID"
            savedEntity = uiSettingGroupRepository.findByApplicationAndNameAndUsername("app", "name", "username")

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

    def "update UI setting group updates times and value only on modified entities"() {
        given: "a sample UI setting group"
            def entity = createSettingGroup()
            def startTime = Instant.now()

        when: "save the UI setting group to the database"
            def savedEntity = uiSettingGroupRepository.saveAndFlush(entity)

        and: "update the UI setting group"
            savedEntity.settings.get(
                savedEntity.settings.findLastIndexOf {it.name == "setting1Name"}).value = "setting1ValueUpdated"
            def updateTime = Instant.now()
            uiSettingGroupRepository.saveAndFlush(savedEntity)

        and: "read the entity from the database using the natural ID"
            def selectTime = Instant.now()
            savedEntity = uiSettingGroupRepository.findByApplicationAndNameAndUsername("app", "name", "username")
            savedEntity = savedEntity.get()

        then: "Created is set, lastUpdated is also set on changed entity"
            savedEntity.settings.any{it.created && it.lastUpdated}

            savedEntity.settings.any{
                startTime.isBefore(it.created.toInstant()) &&
                updateTime.isAfter(it.created.toInstant()) &&

                updateTime.isBefore(it.lastUpdated.toInstant()) &&
                selectTime.isAfter(it.lastUpdated.toInstant())
            }
        and: "the settings size was not changed"
            savedEntity.settings.size() == 2

        and: "Created and lastUpdated were not changed on the unmodified setting"
            savedEntity.settings.any {it.created && !it.lastUpdated}

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
            def savedEntity = uiSettingGroupRepository.saveAndFlush(entity)
            def id = savedEntity.id

        and: "delete the setting group"
            uiSettingGroupRepository.removeAndFlush(savedEntity)

        and: "try to get entity by id"
            def foundById = uiSettingGroupRepository.findBy(id)

        and: "try to get entity by natural id"
            def foundByNaturalId = uiSettingGroupRepository.findByApplicationAndNameAndUsername("app", "name", "username")

        then: "the setting group is not found"
            foundById == null
            !foundByNaturalId.isPresent()
    }

    def "an attempt to find the setting group that doesn't exist is returning the empty optional"() {
        when: "try to get entity by the natural id"
            def foundEntity = uiSettingGroupRepository.findByApplicationAndNameAndUsername("nonExistingApp", "nonExistingName", "nonExistingUsername")

        then: "the setting group is not found"
            !foundEntity.isPresent()
    }

    def "updating the setting group to delete the setting deletes the setting"() {
        given: "a sample UI setting group"
            def entity = createSettingGroup()
            entity.settings.add(new UiSettingEntity(name: "setting3Name", value: "setting3Value"))

        when: "save the UI setting group to the database"
            def savedEntity = uiSettingGroupRepository.saveAndFlush(entity)

        and: "delete the setting 3 from the entity"
            savedEntity.settings.remove(entity.settings[2])
            savedEntity = uiSettingGroupRepository.saveAndFlush(savedEntity)
        then: "the entity 3 is deleted"
            savedEntity.settings.size() == 2
            savedEntity.settings.every({
                it.name == "setting1Name" || it.name == "setting2Name"
                it.value == "setting1Value" || it.value == "setting2Value"
            })
    }

    @Unroll
    def "save settings group with #description should fail"() {

        when: "save the setting group to the database"
            uiSettingGroupRepository.saveAndFlush(entity)

        then: "a ValidationException should be raised"
            def exception = thrown(ValidationException)
            exception.message == expectedError

        where:
            description                                   | entity                                 | expectedError
            "application exceeding 200 characters"        | settingGroupWithLongApplicationName()  | "The application must have a maximum size of 200 characters."
            "setting name exceeding 100 characters"       | settingGroupWithLongSettingName()      | "The setting name must have a maximum size of 100 characters."
            "setting value exceeding 64K characters"      | settingGroupWithLongSettingValue()     | "The setting value must have a maximum size of 64K characters."
            "username exceeding 250 characters"           | settingGroupWithLongUserName()         | "The username must have a maximum size of 250 characters."
            "setting group name exceeding 100 characters" | settingGroupWithLongGroupName()        | "The setting group name must have a maximum size of 100 characters."

            "application exceeding 200 characters"        | settingGroupWithEmptyApplicationName() | "The setting group must have an application."
            "setting name exceeding 100 characters"       | settingGroupWithEmptySettingName()     | "The setting name is required to create a setting."
            "username exceeding 250 characters"           | settingGroupWithEmptyUserName()        | "The setting group must have a username."
            "setting group name exceeding 100 characters" | settingGroupWithEmptyGroupName()       | "The setting group must have a name."
    }

    def createSettingGroup() {
        def entity = new UiSettingGroupEntity(application: "app", name: "name", username: "username")
        def uiSettingEntity1 = new UiSettingEntity(name: "setting1Name", value: "setting1Value")
        def uiSettingEntity2 = new UiSettingEntity(name: "setting2Name", value: "setting2Value")
        entity.settings = [uiSettingEntity1, uiSettingEntity2]
        return entity
    }

    def settingGroupWithLongSettingName() {
        return createSettingGroup("app", "name", "user", (1..151).collect { "a" }.join(), "settingValue");
    }

    def settingGroupWithLongSettingValue() {
        return createSettingGroup("app", "name", "user", "settingName", (1..640001).collect { "a" }.join());
    }

    def settingGroupWithLongApplicationName() {
        return createSettingGroup((1..201).collect { "a" }.join(), "name", "user", "settingName", "settingValue");
    }

    def settingGroupWithLongUserName() {
        return createSettingGroup("app", "name", (1..251).collect { "a" }.join(), "settingName", "settingValue");
    }

    def settingGroupWithLongGroupName() {
        return createSettingGroup("app", (1..101).collect { "a" }.join(), "user", "settingName", "settingValue");
    }

    def settingGroupWithEmptySettingName() {
        return createSettingGroup("app", "name", "user", "", "settingValue");
    }

    def settingGroupWithEmptySettingValue() {
        return createSettingGroup("app", "name", "user", "settingName", "");
    }

    def settingGroupWithEmptyApplicationName() {
        return createSettingGroup("", "name", "user", "settingName", "settingValue");
    }

    def settingGroupWithEmptyUserName() {
        return createSettingGroup("app", "name", "", "settingName", "settingValue");
    }

    def settingGroupWithEmptyGroupName() {
        return createSettingGroup("app", "", "user", "settingName", "settingValue");
    }

    def createSettingGroup(String application, String name, String username, String settingName, String settingValue) {
        def entity = new UiSettingGroupEntity(application: application, name: name, username: username)
        def uiSettingEntity1 = new UiSettingEntity(name: settingName, value: settingValue)
        entity.settings = [uiSettingEntity1]
        return entity
    }
}
