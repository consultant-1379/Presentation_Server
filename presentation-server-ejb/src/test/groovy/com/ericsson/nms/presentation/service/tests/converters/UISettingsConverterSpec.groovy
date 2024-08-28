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
package com.ericsson.nms.presentation.service.tests.converters

import com.ericsson.nms.presentation.service.api.dto.ui_settings.UiSettingDTO
import com.ericsson.nms.presentation.service.api.dto.ui_settings.UiSettingGroupDTO
import com.ericsson.nms.presentation.service.converters.UISettingsConverter
import com.ericsson.nms.presentation.service.persistence.entities.v1.ui_settings.UiSettingEntity
import com.ericsson.nms.presentation.service.persistence.entities.v1.ui_settings.UiSettingGroupEntity
import spock.lang.Specification

import static org.hamcrest.CoreMatchers.equalTo
import static spock.util.matcher.HamcrestSupport.that

class UISettingsConverterSpec extends Specification {
    UISettingsConverter uiSettingsConverter = Spy(new UISettingsConverter())

    def "Setting DTO is converted to DB entity"() {
        given: "A sample setting DTO"
            UiSettingDTO uiSettingDTO = Spy(new UiSettingDTO("sampleKey", "42"))

        when: "trying to convert the DTO to the DB entity"
            UiSettingEntity uiSettingEntity = uiSettingsConverter.settingToEntity(uiSettingDTO)

        then: "The original object was not changed"
            0*uiSettingDTO.setValue(_)
            0*uiSettingDTO.setCreated(_)
            0*uiSettingDTO.setLastUpdated(_)
            0*uiSettingDTO.setId(_)

        then: "The entity has the required fields from the DTO"
            uiSettingEntity.getName() == (uiSettingDTO.getId())
            uiSettingEntity.getValue() == (uiSettingDTO.getValue())
            uiSettingEntity.getCreated().is(null)
            uiSettingEntity.getLastUpdated().is(null)
    }

    def "Setting DB entity is converted to DTO"() {
        given: "A sample setting DB entity"
            UiSettingEntity uiSettingEntity = Spy(new UiSettingEntity("sampleKey", "42"))
            uiSettingEntity.setCreated(new Date())
            uiSettingEntity.setLastUpdated(new Date())

        when: "trying to convert the DB Entity to DTO"
            UiSettingDTO uiSettingDTO = uiSettingsConverter.settingToDTO(uiSettingEntity)

        then: "The original object was not changed"
            0*uiSettingEntity.setLastUpdated(_)
            0*uiSettingEntity.setCreated(_)
            0*uiSettingEntity.setName(_)
            0*uiSettingEntity.setId(_)
            0*uiSettingEntity.setSettingGroup(_)
            0*uiSettingEntity.setValue(_)

        then: "The DTO has the required fields from the entity"
            uiSettingDTO.getId() == (uiSettingEntity.getName())
            uiSettingDTO.getValue() == (uiSettingEntity.getValue())
            uiSettingDTO.getCreated() == (uiSettingEntity.getCreated())
            uiSettingDTO.getLastUpdated() == (uiSettingEntity.getLastUpdated())
    }

    def "Setting group is converted to DB entity"() {
        given: "A sample setting group DTO"
            UiSettingGroupDTO uiSettingGroupDTO = Spy(new UiSettingGroupDTO())
            uiSettingGroupDTO.setApplication("sampleApp")
            uiSettingGroupDTO.setKey("sampleKey")
            uiSettingGroupDTO.setUser("sampleUser")

        and: "2 settings in the settings group"
            Set<UiSettingDTO> settings = new HashSet<>()
            uiSettingGroupDTO.setSettings(settings)

            UiSettingDTO uiSettingDTO1 = new UiSettingDTO("sampleKey1", "421")
            UiSettingDTO uiSettingDTO2 = new UiSettingDTO("sampleKey2", "422")

            uiSettingGroupDTO.getSettings().add(uiSettingDTO1)
            uiSettingGroupDTO.getSettings().add(uiSettingDTO2)


        when: "trying to convert the DTO to the DB entity"
            UiSettingGroupEntity uiSettingGroupEntity = uiSettingsConverter.settingsGroupToEntity(uiSettingGroupDTO)

        then: "The original object was not changed"
            0*uiSettingGroupDTO.setSettings(_)
            0*uiSettingGroupDTO.setKey(_)
            0*uiSettingGroupDTO.setApplication(_)
            0*uiSettingGroupDTO.setUser(_)

        and: "The entity has the required fields from the DTO"
            uiSettingGroupEntity.getName() == (uiSettingGroupDTO.getKey())
            uiSettingGroupEntity.getApplication() ==(uiSettingGroupDTO.getApplication())
            uiSettingGroupEntity.getUsername() == (uiSettingGroupDTO.getUser())

        and: "The individual settings were converted the right way"
            uiSettingGroupEntity.settings.every {
                (it == uiSettingsConverter
                    .settingToEntity(
                    settings.find {
                    s -> (s.id == it.name)
                    }))
            }
    }


    def "Setting group is converted to DTO"() {
        given: "A sample setting group DB entity"
            UiSettingGroupEntity entity = Spy(new UiSettingGroupEntity())
            entity.setApplication("sampleApp")
            entity.setName("sampleKey")
            entity.setUsername("sampleUser")

            List<UiSettingEntity> settings = new ArrayList<>(2)
            entity.setSettings(settings)

        and: "2 settings in the setting group"
            UiSettingEntity uiSettingEntity1 = new UiSettingEntity("sampleKey1", "421")
            uiSettingEntity1.setCreated(new Date())
            uiSettingEntity1.setLastUpdated(new Date())

            UiSettingEntity uiSettingEntity2 = new UiSettingEntity("sampleKey2", "422")
            uiSettingEntity2.setCreated(new Date())

            entity.getSettings().add(uiSettingEntity1)
            entity.getSettings().add(uiSettingEntity2)


        when: "trying to convert the DTO to the DB entity"
            UiSettingGroupDTO uiSettingGroupDTO = uiSettingsConverter.settingsGroupToDTO(entity)

        then: "The original object was not changed"
            0*entity.setSettings(_)
            0*entity.setName(_)
            0*entity.setApplication(_)
            0*entity.setUsername(_)

        and: "The entity has the required fields from the DTO"
            uiSettingGroupDTO.getKey() == (entity.getName())
            uiSettingGroupDTO.getApplication() ==(entity.getApplication())
            uiSettingGroupDTO.getUser() == (entity.getUsername())

        and: "The individual settings were converted the right way"
            uiSettingGroupDTO.settings.every {
                (it == uiSettingsConverter.settingToDTO(
                    settings.find {
                        s -> s.name == it.id
                    }))
            }
    }

    def "Setting DB entity -- old DTO conversion is symmetrical (when converted without timestamps)"() {
        given: "A sample setting DB entity"
            UiSettingEntity uiSettingEntityReferenceCopy = new UiSettingEntity("sampleKey", "42")
            UiSettingEntity uiSettingEntity = new UiSettingEntity("sampleKey", "42")

        when: "trying to perform a double conversion"
            UiSettingEntity uiSettingEntityDoubleConverted = uiSettingsConverter.settingFromUIBeanToEntity(
                uiSettingsConverter.settingFromEntityToUIBean(uiSettingEntity))

        then: "The result of UI setting entity after double conversion is equal to the reference copy"
            that uiSettingEntityReferenceCopy, equalTo(uiSettingEntityDoubleConverted)

        and: "the original entity is unchanged, e.g. equal to the reference copy"
            that uiSettingEntityReferenceCopy, equalTo(uiSettingEntity)
    }

    def "Setting group entity -- old DTO conversion is symmetrical (given that the right data are supplied on reverse conversion)"() {
        given: "A sample setting group DB entity"
            UiSettingGroupEntity referenceCopy = createSampleUiSettingGroupEntityWithoutTimeStamps()
            UiSettingGroupEntity originalEntity = createSampleUiSettingGroupEntityWithoutTimeStamps()

        when: "trying to convert the DTO to the DB entity"
            UiSettingGroupEntity doubleConvertedEntity = uiSettingsConverter.settingGroupFromOldDTOToEntity(
                uiSettingsConverter.settingGroupFromEntityToOldDTO(originalEntity),
                referenceCopy.getApplication(),
                referenceCopy.getUsername(),
                referenceCopy.getName())

        then: "The original object was not changed"
            that referenceCopy, equalTo(originalEntity)

        and: "The double coverted entity is equal to the reference copy"
            that referenceCopy, equalTo(doubleConvertedEntity)
    }

    def createSampleUiSettingGroupEntityWithoutTimeStamps () {
        UiSettingGroupEntity entity = new UiSettingGroupEntity()
        entity.setApplication("sampleApp")
        entity.setName("sampleKey")
        entity.setUsername("sampleUser")

        List<UiSettingEntity> settings = new ArrayList<>(2)
        entity.setSettings(settings)

        UiSettingEntity uiSettingEntity1 = new UiSettingEntity("sampleKey1", "421")

        UiSettingEntity uiSettingEntity2 = new UiSettingEntity("sampleKey2", "422")

        entity.getSettings().add(uiSettingEntity1)
        entity.getSettings().add(uiSettingEntity2)
        return entity
    }
}
