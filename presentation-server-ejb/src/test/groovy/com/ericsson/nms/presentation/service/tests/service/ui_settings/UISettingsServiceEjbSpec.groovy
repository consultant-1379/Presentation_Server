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
package com.ericsson.nms.presentation.service.tests.service.ui_settings

import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.presentation.exceptions.SettingNotFoundException
import com.ericsson.nms.presentation.exceptions.service.EntityNotFoundException
import com.ericsson.nms.presentation.service.api.dto.ui_settings.UiSettingDTO
import com.ericsson.nms.presentation.service.api.dto.ui_settings.UiSettingGroupDTO
import com.ericsson.nms.presentation.service.converters.UISettingsConverter
import com.ericsson.nms.presentation.service.ejb.ui_settings.UISettingsServiceEjb
import com.ericsson.nms.presentation.service.persistence.dao.qualifier.Dispatcher
import com.ericsson.nms.presentation.service.persistence.database.dao.UiSettingGroupDAO
import com.ericsson.nms.presentation.service.persistence.entities.v1.ui_settings.UiSettingEntity
import com.ericsson.nms.presentation.service.persistence.entities.v1.ui_settings.UiSettingGroupEntity
import com.ericsson.nms.presentation.service.security.SecurityUtil
import com.ericsson.nms.presentation.service.tests.base.AbstractPresentationServerSpec
import com.ericsson.oss.itpf.sdk.security.accesscontrol.EAccessControl
import org.slf4j.Logger
import spock.lang.Shared
import spock.lang.Unroll

import javax.inject.Inject

import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.startsWith
import static spock.util.matcher.HamcrestSupport.that

class UISettingsServiceEjbSpec extends AbstractPresentationServerSpec {

    def EXISTING_SETTING1_NAME = "settingName"
    def EXISTING_SETTING1_VALUE_ORIGINAL = "settingValue"

    def EXISTING_SETTING2_NAME = "settingKey2"
    def EXISTING_SETTING2_VALUE_ORIGINAL = "settingValue2Original"
    def EXISTING_SETTING2_VALUE_NEW = "settingValue2New"
    def EXISTING_SETTING2_VALUE_CHANGED = "settingValue2Changed"

    def NEW_SETTING1_NAME = "settingKey1"
    def NEW_SETTING1_VALUE = "settingValue1"


    @Shared def LARGE_SETTING_NAME = "largeSetting"
    @Shared def LARGE_SETTING_VALUE = "x" * 2_001

    @Shared def OVERSIZED_SETTING_NAME = "oversizedSetting"
    @Shared def OVERSIZED_SETTING_VALUE = "x" * 5_001

    public final LinkedHashMap<String, String> DEFAULT_SETTING_GROUP_FIELDS = [userId: "user", appId: "app01", key: "myKey"]

    @ObjectUnderTest
    UISettingsServiceEjb service

    UiSettingGroupEntity uiSettingGroupEntityInDB

    @ImplementationInstance
    @Dispatcher
    private UiSettingGroupDAO settingGroupDAO = Mock {
        findByApplicationAndNameAndUsername(_,_,_) >> {
            def uiSettingGroupEntity = createSettingGroup(DEFAULT_SETTING_GROUP_FIELDS.appId,
                DEFAULT_SETTING_GROUP_FIELDS.key,
                DEFAULT_SETTING_GROUP_FIELDS.userId,
                EXISTING_SETTING1_NAME,
                EXISTING_SETTING1_VALUE_ORIGINAL)
            uiSettingGroupEntity.settings.add(new UiSettingEntity(EXISTING_SETTING2_NAME, EXISTING_SETTING2_VALUE_ORIGINAL))
            return Optional.of(uiSettingGroupEntity)
        }
    }

    @ImplementationInstance
    private UISettingsConverter uiSettingsConverter = Spy(new UISettingsConverter());

    @ImplementationInstance
    private SecurityUtil securityUtil = Mock {
        getCurrentUser() >> "administrator"
    }

    @Inject
    EAccessControl accessControl

    @ImplementationInstance
    Logger log = Mock()

    def "Get the existing setting group gets the setting"() {

        given: "a setting group"
            def args = DEFAULT_SETTING_GROUP_FIELDS

        when: "get the existing setting"
            def dto = service.getSettingsGroupByKey(args.appId, args.key)

        then: "setting group is retrieved"
            that dto.key, equalTo(args.key)

            that dto.user, equalTo(args.userId)

            that dto.application, equalTo(args.appId)

        and: "There are 2 settings with the right id/values"
            that dto.settings.size(), equalTo(2)

            dto.settings*.id == [ EXISTING_SETTING2_NAME , EXISTING_SETTING1_NAME ]

            dto.settings*.value == [ EXISTING_SETTING2_VALUE_ORIGINAL, EXISTING_SETTING1_VALUE_ORIGINAL ]
    }

    def "Get the non-existing setting group throws the exception"() {

        given: "a setting group"
            def args = DEFAULT_SETTING_GROUP_FIELDS

        when: "delete the existing setting"
            def dto = service.getSettingsGroupByKey(args.appId, args.key)

        then: "assert that the exception is thrown"
            SettingNotFoundException snfe = thrown()
            that snfe.message, startsWith("No setting found with key")

            1 * settingGroupDAO.findByApplicationAndNameAndUsername(_,_,_) >> Optional.empty()
    }

    def "Create new UI Setting merges the existing settings with the new settings"() {

        given: "a setting group"
            def args = DEFAULT_SETTING_GROUP_FIELDS
            UiSettingGroupEntity uiSettingsGroup

        when: "create two new settings"
            UiSettingGroupDTO uiSettingGroupDTO = new UiSettingGroupDTO()
            uiSettingGroupDTO.setSettings(new HashSet<UiSettingDTO>(2))

            UiSettingDTO uiSettingDTO1 = new UiSettingDTO(NEW_SETTING1_NAME, NEW_SETTING1_VALUE)
            UiSettingDTO uiSettingDTO2 = new UiSettingDTO(EXISTING_SETTING2_NAME, EXISTING_SETTING2_VALUE_CHANGED)

            uiSettingGroupDTO.getSettings().add(uiSettingDTO1)
            uiSettingGroupDTO.getSettings().add(uiSettingDTO2)
            def savedEntity = service.saveSettings(args.appId, args.key, uiSettingGroupDTO)

        then: "The setting is saved in DAO"
            1*settingGroupDAO.save(_) >> {arguments -> uiSettingsGroup = arguments[0]}

        and: "The setting entity has right properties"
            that savedEntity.key, equalTo(args.key)
            that savedEntity.user, equalTo(args.userId)
            that savedEntity.application, equalTo(args.appId)
            that savedEntity.settings.size(), equalTo(3)

        and: "the original setting group is unchanged"
            that uiSettingsGroup.name, equalTo(args.key)
            that uiSettingsGroup.username, equalTo(args.userId)
            that uiSettingsGroup.application, equalTo(args.appId)
            that uiSettingsGroup.settings.size(), equalTo(3)

        and: "the id/values of the saved/original settings are right"
            uiSettingsGroup.settings*.name.containsAll([ EXISTING_SETTING1_NAME, EXISTING_SETTING2_NAME, NEW_SETTING1_NAME ])

            savedEntity.settings*.id.containsAll([ EXISTING_SETTING1_NAME, EXISTING_SETTING2_NAME, NEW_SETTING1_NAME ])

            uiSettingsGroup.settings*.value.containsAll([ EXISTING_SETTING1_VALUE_ORIGINAL, EXISTING_SETTING2_VALUE_CHANGED, NEW_SETTING1_VALUE ])

            savedEntity.settings*.value.containsAll([ EXISTING_SETTING1_VALUE_ORIGINAL, EXISTING_SETTING2_VALUE_CHANGED, NEW_SETTING1_VALUE ])
    }

    def "Create new UI Setting saves the setting group if there's no existing one"() {

        given: "a setting group"
            def args = DEFAULT_SETTING_GROUP_FIELDS
            UiSettingGroupEntity uiSettingsGroup

        and: "no entity is returned by DAO get"
            settingGroupDAO.findByApplicationAndNameAndUsername(_,_,_) >> Optional.empty()

        when: "create two new settings"
            UiSettingGroupDTO uiSettingGroupDTO = new UiSettingGroupDTO()
            uiSettingGroupDTO.setSettings(new HashSet<UiSettingDTO>(2))

            UiSettingDTO uiSettingDTO1 = new UiSettingDTO(NEW_SETTING1_NAME, NEW_SETTING1_VALUE)
            UiSettingDTO uiSettingDTO2 = new UiSettingDTO(EXISTING_SETTING2_NAME, EXISTING_SETTING2_VALUE_NEW)

        and: "add them to group"
            uiSettingGroupDTO.getSettings().add(uiSettingDTO1)
            uiSettingGroupDTO.getSettings().add(uiSettingDTO2)

        and: "save them"
            def savedEntity = service.saveSettings(args.appId, args.key, uiSettingGroupDTO)

        then: "The settings were saved when no existing settings group was found"
            1*settingGroupDAO.findByApplicationAndNameAndUsername(_,_,_) >> Optional.empty()
            1*settingGroupDAO.save(_) >> {arguments -> uiSettingsGroup = arguments[0]}

        and: "The setting entity has right properties"
            that savedEntity.key, equalTo(args.key)
            that savedEntity.application, equalTo(args.appId)
            that savedEntity.settings.size(), equalTo(2)

        and: "the original setting group is unchanged"
            that uiSettingsGroup.name, equalTo(args.key)
            that uiSettingsGroup.application, equalTo(args.appId)
            that uiSettingsGroup.settings.size(), equalTo(2)

        and: "the id/values of the saved/original settings are right"

            uiSettingsGroup.settings*.name.containsAll([ NEW_SETTING1_NAME, EXISTING_SETTING2_NAME ])

            savedEntity.settings*.id.containsAll([ NEW_SETTING1_NAME, EXISTING_SETTING2_NAME ])

            uiSettingsGroup.settings*.value.containsAll([ NEW_SETTING1_VALUE, EXISTING_SETTING2_VALUE_NEW ])

            savedEntity.settings*.value.containsAll([NEW_SETTING1_VALUE, EXISTING_SETTING2_VALUE_NEW ])

        and: "user is overriden by the security service"
            uiSettingsGroup.username == "administrator"
            savedEntity.user == "administrator"
    }

    @Unroll
    def "Create UI Setting with size larger than #size logs a warning message"() {

        given: "a setting group"
            def args = DEFAULT_SETTING_GROUP_FIELDS
            UiSettingGroupEntity uiSettingsGroup

        and: "no entity is returned by DAO get"
            settingGroupDAO.findByApplicationAndNameAndUsername(_,_,_) >> Optional.empty()

        when: "create new setting"
            UiSettingGroupDTO uiSettingGroupDTO = new UiSettingGroupDTO()
            uiSettingGroupDTO.setSettings(new HashSet<UiSettingDTO>())

            UiSettingDTO uiSetting = new UiSettingDTO("myNewSetting", (1..size+1).collect {"A" }.join(""))

        and: "add them to group"
            uiSettingGroupDTO.getSettings().add(uiSetting)

        and: "save them"
            service.saveSettings(args.appId, args.key, uiSettingGroupDTO)

        then: "The settings were saved when no existing settings group was found"
            1 * settingGroupDAO.findByApplicationAndNameAndUsername(_,_,_) >> Optional.empty()
            1 * settingGroupDAO.save(_) >> {arguments -> uiSettingsGroup = arguments[0]}

        and: "the original setting group is unchanged"
            1 * log.warn(_ as String, _) >> { String message, Object arguments ->
                    assert message.startsWith(expectedMessage)
            }

        where:
            size | expectedMessage
            2000 | "Large setting detected"
            5000 | "Oversized setting detected"

    }

    def "Delete the existing setting deletes the setting (saves the setting group without the setting)"() {

        given: "a setting group"
            def args = DEFAULT_SETTING_GROUP_FIELDS
            UiSettingGroupEntity uiSettingsGroup

        when: "delete the existing setting"
            def savedEntity = service.deleteSetting(args.appId, args.key, EXISTING_SETTING1_NAME)

        then: "save is called"
            1 * settingGroupDAO.save(_) >> {arguments -> uiSettingsGroup = arguments[0]}


        and: "original setting group parameters are unchanged"
            that uiSettingsGroup.name, equalTo(args.key)
            that uiSettingsGroup.username, equalTo(args.userId)
            that uiSettingsGroup.application, equalTo(args.appId)

        and: "saved setting group parameters are unchanged"
            that savedEntity.get().key, equalTo(args.key)
            that savedEntity.get().user, equalTo(args.userId)
            that savedEntity.get().application, equalTo(args.appId)

        and: "Setting is deleted"
            that uiSettingsGroup.settings.size(), equalTo(1)
            that savedEntity.get().settings.size(), equalTo(1)

        and: "Setting that was deleted was the right one"
            uiSettingsGroup.settings*.name == [ EXISTING_SETTING2_NAME ]

            savedEntity.get().settings*.id == [ EXISTING_SETTING2_NAME ]

            uiSettingsGroup.settings*.value == [ EXISTING_SETTING2_VALUE_ORIGINAL ]

            savedEntity.get().settings*.value == [ EXISTING_SETTING2_VALUE_ORIGINAL ]
    }

    def "Delete the non-existing setting from the existing group throws exception"() {

        given: "a setting group"
            def args = DEFAULT_SETTING_GROUP_FIELDS
            UiSettingGroupEntity uiSettingsGroup

        when: "delete the non-existing setting"
            def savedEntity = service.deleteSetting(args.appId, args.key, "settingNameThatDoesNotExist")

        then: "assert that the exception is thrown"
            EntityNotFoundException enfe = thrown()
            enfe.message == "Trying to delete the setting that does not exist"
            0*settingGroupDAO.save(_) >> {arguments -> uiSettingsGroup = arguments[0]}
    }

    def "Delete any setting from the non-existing group throws an exception"() {
        when: "delete the existing setting from the group that does not exist"
            service.deleteSetting("appId", "nonExistingSettingsGroup", EXISTING_SETTING1_NAME)

        then: "assert that the exception is thrown"
            EntityNotFoundException enfe = thrown()
            enfe.message == "Trying to delete entity from nonexisting setting group"
            1 * settingGroupDAO.findByApplicationAndNameAndUsername(_,_,_) >> Optional.empty()
            0 * settingGroupDAO.save(_) >> {arguments -> uiSettingsGroup = arguments[0]}
    }

    def createSettingGroup(String application, String name, String username, String settingName, String settingValue) {
        def entity = new UiSettingGroupEntity(application: application, name: name, username: username)
        def uiSettingEntity1 = new UiSettingEntity(name: settingName, value: settingValue)
        entity.settings = [uiSettingEntity1]
        return entity
    }

    @Unroll
    def "#type setting is saved but the #type setting is logged"() {

        given: "a setting group"
            def args = DEFAULT_SETTING_GROUP_FIELDS
            UiSettingGroupEntity uiSettingsGroup

        and: "no entity is returned by DAO get"
            settingGroupDAO.findByApplicationAndNameAndUsername(_,_,_) >> Optional.empty()

        when: "create two new settings"
            UiSettingGroupDTO uiSettingGroupDTO = new UiSettingGroupDTO()
            uiSettingGroupDTO.setSettings(new HashSet<UiSettingDTO>(1))

            UiSettingDTO largeSettingDTO = new UiSettingDTO(name, value)

        and: "add them to group"
            uiSettingGroupDTO.getSettings().add(largeSettingDTO)

        and: "save them"
            def savedEntity = service.saveSettings(args.appId, args.key, uiSettingGroupDTO)

        then: "The settings were saved when no existing settings group was found"
            1*settingGroupDAO.findByApplicationAndNameAndUsername(_,_,_) >> Optional.empty()
            1*settingGroupDAO.save(_) >> {arguments -> uiSettingsGroup = arguments[0]}

        and: "The setting entity has right properties"
            that savedEntity.key, equalTo(args.key)
            that savedEntity.application, equalTo(args.appId)
            that savedEntity.settings.size(), equalTo(1)

        and: "the warning is logged"
            1*log.warn(_, args.appId, args.key, name, value)

        where:
            type        | name                   | value
            "large"     | LARGE_SETTING_NAME     | LARGE_SETTING_VALUE
            "oversized" | OVERSIZED_SETTING_NAME | OVERSIZED_SETTING_VALUE
    }

    @Unroll
    def "null-named setting is logged as error"() {

        given: "a setting group"
            def args = DEFAULT_SETTING_GROUP_FIELDS
            UiSettingGroupEntity uiSettingsGroup

        and: "no entity is returned by DAO get"
            settingGroupDAO.findByApplicationAndNameAndUsername(_,_,_) >> Optional.empty()

        when: "create two new settings"
            UiSettingGroupDTO uiSettingGroupDTO = new UiSettingGroupDTO()
            uiSettingGroupDTO.setSettings(new HashSet<UiSettingDTO>(1))

            UiSettingDTO largeSettingDTO = new UiSettingDTO(null, NEW_SETTING1_VALUE)

        and: "add them to group"
            uiSettingGroupDTO.getSettings().add(largeSettingDTO)

        and: "save them"
            def savedEntity = service.saveSettings(args.appId, args.key, uiSettingGroupDTO)

        then: "The settings were saved when no existing settings group was found"
            1*settingGroupDAO.findByApplicationAndNameAndUsername(_,_,_) >> Optional.empty()
            1*settingGroupDAO.save(_) >> {arguments -> uiSettingsGroup = arguments[0]}

        and: "The setting entity has right properties"
            that savedEntity.key, equalTo(args.key)
            that savedEntity.application, equalTo(args.appId)
            that savedEntity.settings.size(), equalTo(1)

        and: "the error is logged"
            1*log.error(_)
    }
}
