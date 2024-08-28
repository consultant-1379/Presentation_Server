package com.ericsson.nms.presentation.service.tests

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.presentation.exceptions.UserNotFoundException
import com.ericsson.nms.presentation.service.api.dto.ui_settings.UiSettingDTO
import com.ericsson.nms.presentation.service.api.dto.ui_settings.UiSettingGroupDTO

import com.ericsson.nms.presentation.service.ejb.ui_settings.UiSettingsService
import com.ericsson.nms.presentation.service.rest.resource.UISettingsResource

import static org.hamcrest.Matchers.equalTo
import static spock.util.matcher.HamcrestSupport.that

import javax.inject.Inject

/**
 * Test specification for UiSettings Service endpoint.
 */
class UiSettingsResourceSpec extends CdiSpecification  {

    @ObjectUnderTest
    UISettingsResource rest

    @Inject
    private UiSettingsService uiSettingsService;

    def "Include new UI Setting"() {

        given:
            def app = "myApp"
            def key = "myKey"
            def userId = "user"
            def setting = new UiSettingDTO(id: "myId", value: "myValue")

        when:
            rest.userId = userId
            def response = rest.setSettings(app, key, setting)

        then:
            response.status == 204
            response.entity == null
            1 * uiSettingsService.saveSettings(app, key, _ as UiSettingGroupDTO)
    }

    def "Include new UI Setting with no user header"() {

        given:
        def app = "myApp"
        def key = "myKey"
        def userId = null
        def setting = new UiSettingDTO(id: "myId", value: "myValue")

        when:
            rest.userId = userId
            rest.setSettings(app, key, setting)

        then:
            UserNotFoundException exception = thrown()
            exception.message == "No user found!"
    }

    def "Include multiple new UI Settings"() {

        given:
            def app = "myApp"
            def key = "myKey"
            def userId = "user"
            def settings = new ArrayList<>([
                    new UiSettingDTO("id1", "true"),
                    new UiSettingDTO("id2", "true"),
                    new UiSettingDTO("id3", "true"),
                    new UiSettingDTO("id4", "true")
            ])

        when:
            rest.userId = userId
            def response = rest.setSettings(app, key, settings)

        then:
            that response.status, equalTo(204)
            that response.entity, equalTo(null)
            1 * uiSettingsService.saveSettings(app, key, _ as UiSettingGroupDTO)
    }

    def "Attempt to include multiple new UI Settings with no user header"() {

        given:
            def app = "myApp"
            def key = "myKey"
            def userId = null
            def settings = new ArrayList<>([
                    new UiSettingDTO("id1", "true"),
                    new UiSettingDTO("id2", "true"),
                    new UiSettingDTO("id3", "true"),
                    new UiSettingDTO("id4", "true")
            ])

        when:
            rest.userId = userId
            rest.setSettings(app, key, settings)

        then:
            UserNotFoundException exception = thrown()
            that exception.message, equalTo("No user found!")
    }

    def "Get UI Setting"() {

        uiSettingsService.getSettingsGroupByKey(_ as String, _ as String) >>
                new UiSettingGroupDTO("anyApp", "anyUser", "anyKey",
                    [new UiSettingDTO(id: "myId", value: "myValue")] as UiSettingDTO[])

        given:
            def app = "myApp"
            def key = "myKey"
            def userId = "user"

        when:
            rest.userId = userId
            def response = rest.getSettings(app, key)
            def settings = response.entity as Collection<UiSettingDTO>

        then:
            response.status == 200
            settings.size() == 1
            settings[0].id == "myId"
    }

    def "Get UI Setting with no user header"() {

        given:
            def app = "myApp"
            def key = "myKey"
            def userId = null

        when:
            rest.userId = userId
            rest.getSettings(app, key)

        then:
            UserNotFoundException exception = thrown()
            exception.message == "No user found!"
    }

    def "Delete UI Setting"() {
        def uiSetting = new UiSettingDTO(id: "myId", value: "myValue")
        uiSettingsService.getSettingsGroupByKey(_ as String, _ as String) >>
                [uiSetting]

        given:
            def app = "myApp"
            def key = "myKey"
            def userId = "user"

        when:
            rest.userId = userId
            def response = rest.removeSettings(app, key, uiSetting)

        then:
            response.status == 204
    }

    def "Delete UI Setting with no user header"() {

        given:
            def app = "myApp"
            def key = "myKey"
            def userId = null

        when:
            rest.userId = userId
            rest.removeSettings(app, key, new UiSettingDTO(id: "myId", value: "myValue"))

        then:
            UserNotFoundException exception = thrown()
            exception.message == "No user found!"
    }

}
