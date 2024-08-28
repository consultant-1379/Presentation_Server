package com.ericsson.nms.presentation.service.tests

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.presentation.exceptions.InvalidApplicationTypeException
import com.ericsson.nms.presentation.exceptions.NotFoundException
import com.ericsson.nms.presentation.exceptions.SettingNotFoundException
import com.ericsson.nms.presentation.exceptions.UserNotFoundException
import com.ericsson.nms.presentation.service.api.dto.*
import com.ericsson.nms.presentation.service.api.dto.ui_settings.UiSettingDTO
import com.ericsson.nms.presentation.service.api.dto.ui_settings.UiSettingGroupDTO
import com.ericsson.nms.presentation.service.ejb.ApplicationService
import com.ericsson.nms.presentation.service.ejb.LoggingService
import com.ericsson.nms.presentation.service.ejb.ui_settings.UiSettingsService
import com.ericsson.nms.presentation.service.rest.resource.ApplicationResource
import com.ericsson.nms.presentation.service.rest.response.GroupResponse
import com.ericsson.nms.presentation.service.util.PresentationServiceConfig
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder

import javax.inject.Inject

/**
 * Test specification for Application Service endpoint.
 */
class ApplicationResourceSpec extends CdiSpecification {

    @ObjectUnderTest
    ApplicationResource rest

    @Inject
    UiSettingsService uiSettingsService

    @Inject
    ApplicationService applicationService

    @Inject
    PresentationServiceConfig presentationServiceConfig

    @Inject
    SystemRecorder systemRecorder

    @Inject
    LoggingService loggingService

    def "get apps version 1 with empty favorites"() {

        applicationService.getApps() >>
            [new WebApplication(id: "app01", hidden: false), new WebApplication(id: "app02", hidden: false)]

        uiSettingsService.getSettingsGroupByKey("launcher", "favorites") >> new UiSettingGroupDTO(settings: [])

        given: "set the user as anyUser"
            rest.userId = "anyUser"

        when: "execute the rest call"
            def response = rest.getApps()
            def apps = response.entity as Collection<AbstractApplication>

        then: "assert status code and returned entity"
            response.status == 200
            apps.size() == 2
            apps*.id.containsAll("app01","app02")
    }

    def "get apps version 1 with no favorites"() {

        applicationService.getApps() >>
            [new WebApplication(id: "app01", hidden: false), new WebApplication(id: "app02", hidden: false)]

        uiSettingsService.getSettingsGroupByKey("launcher", "favorites") >> {
            throw new SettingNotFoundException("setting not found")
        }

        given: "set the user as anyUser"
            rest.userId = "anyUser"

        when: "execute the rest call"
            def response = rest.getApps()
            def apps = response.entity as Collection<AbstractApplication>

        then: "assert status code and returned entity"
            response.status == 200
            apps.size() == 2
            apps*.id.containsAll("app01","app02")
    }

    def "get apps version 1 with favorites"() {

        applicationService.getApps() >>
                [new WebApplication(id: "app01", hidden: false), new WebApplication(id: "app02", hidden: false)]

        uiSettingsService.getSettingsGroupByKey("launcher", "favorites") >> new UiSettingGroupDTO(
            settings: [new UiSettingDTO(id: "app02")])

        given: "set the user as anyUser"
            rest.userId = "anyUser"

        when: "execute the rest call"
            def response = rest.getApps()
            def apps = response.entity as Collection<AbstractApplication>

        then: "assert status code and returned entity"
            response.status == 200
            apps.size() == 2
            apps*.id.containsAll("app01","app02")
            apps.find{it.id == "app01"}.favorite == "false"
            apps.find{it.id == "app02"}.favorite == "true"
    }

    def "get apps version 1 with hidden apps"() {

        applicationService.getApps() >>
                [new WebApplication(id: "app01", hidden: false), new WebApplication(id: "app02", hidden: true)]

        uiSettingsService.getSettingsGroupByKey("launcher", "favorites") >> new UiSettingGroupDTO(settings: [])

        given: "set the user as anyUser"
            rest.userId = "anyUser"

        when: "execute the rest call"
            def response = rest.getApps()
            def apps = response.entity as Collection<AbstractApplication>

        then: "assert status code and returned entity"
            response.status == 200
            apps.size() == 1
            apps[0].id == "app01"
    }

    def "get apps version 2 with no favorites, hidden apps or consumes"() {

        applicationService.getApps() >>
                [new WebApplication(id: "app01", hidden: false), new WebApplication(id: "app02", hidden: false)]

        uiSettingsService.getSettingsGroupByKey("launcher", "favorites") >> {
            throw new SettingNotFoundException("setting not found")
        }

        given: "set the user as anyUser"
            rest.userId = "anyUser"

        when: "execute the rest call"
            def response = rest.getAppsV2(null)
            def apps = response.entity as Collection<AbstractApplication>

        then: "assert status code and returned entity"
            response.status == 200
            apps.size() == 2
            apps*.id.containsAll("app01","app02")
    }

    def "get groups"() {

        applicationService.getGroups() >>
                [new Group(id: "grp01"), new Group(id: "grp02", appIds: ["app01", "app02"])]

        applicationService.getApps(_ as String[]) >>
                [new WebApplication(id: "app01", hidden: false), new WebApplication(id: "app02", hidden: true)]

        uiSettingsService.getSettingsGroupByKey("launcher", "favorites") >> new UiSettingGroupDTO(settings: [])

        given: "set the user as anyUser"
            rest.userId = "anyUser"

        when: "execute the rest call"
            def response = rest.getGroups()
            def groups = response.entity as Collection<GroupResponse>

        then: "assert status code and returned groups"
            response.status == 200
            groups.size() == 2
            groups*.id.containsAll("grp01","grp02")

        and: "applications set as hidden are not shown in the group"
            groups.find{it.id == "grp02"}.apps.size() == 1
            groups.find{it.id == "grp02"}.apps[0].id == "app01"
    }

    def "get favorites"() {

        applicationService.getApps() >>
                [new WebApplication(id: "app01", hidden: false), new WebApplication(id: "app02", hidden: false)]

        uiSettingsService.getSettingsGroupByKey("launcher", "favorites") >>
            new UiSettingGroupDTO(settings: [new UiSettingDTO(id: "app02")])

        given: "set the user as anyUser"
            rest.userId = "anyUser"

        when: "execute the rest call"
            def response = rest.getFavorites()
            def apps = response.entity as Collection<AbstractApplication>

        then: "assert status code and returned entity"
            response.status == 200
            apps.size() == 1
            apps[0].id == "app02"

    }

    def "Launch Web Application"() {

        applicationService.getApplication(_ as String) >>
                new WebApplication(id: "app01", hidden: false, path: "/some-path")

        given: "set the user as anyUser"
            rest.userId = "anyUser"

        when: "execute the rest call"
            def response = rest.launchWebApplication("app01")

        then: "assert status code and returned entity"
            response.status == 301
            response.metadata["Location"] == ["http://localhost/some-path"]

    }

    def "Launch Web Application when application id is from invalid app type"() {

        applicationService.getApplication(_ as String) >>
                new CitrixApplication(id: "myId")

        given: "set the user as anyUser"
            rest.userId = "anyUser"

        when: "execute the rest call"
            rest.launchWebApplication("myId")

        then: "assert the correct exception was thrown"
            InvalidApplicationTypeException exception = thrown()
            exception.message == "The application with ID myId is not an Web Application"

    }

    def "Launch Web Application when application id is not found"() {

        applicationService.getApplication(_ as String) >> { throw new NotFoundException(); }

        given: "set the user as anyUser"
            rest.userId = "anyUser"

        when: "execute the rest call"
            rest.launchWebApplication("myId")

        then: "assert the correct exception was thrown"
            NotFoundException exception = thrown()
            exception.class == NotFoundException
    }

    def "get apps version 1 when user is not informed"() {

        given: "set the user as NULL"
            rest.userId = null

        when: "execute the rest call"
            rest.getApps()

        then: "assert the correct exception was thrown"
            UserNotFoundException exception = thrown()
            exception.message == "No user found!"
    }

    def "get apps version 2 when user is not informed"() {

        given: "set the user as NULL"
            rest.userId = null

        when: "execute the rest call"
            rest.getAppsV2(null)

        then: "assert the correct exception was thrown"
            UserNotFoundException exception = thrown()
            exception.message == "No user found!"
    }

    def "get groups when user is not informed"() {

        given: "set the user as NULL"
            rest.userId = null

        when: "execute the rest call"
            rest.getGroups()

        then: "assert the correct exception was thrown"
            UserNotFoundException exception = thrown()
            exception.message == "No user found!"
    }

    def "get favorites when user is not informed"() {

        given: "set the user as NULL"
            rest.userId = null

        when: "execute the rest call"
            rest.getFavorites()

        then: "assert the correct exception was thrown"
            UserNotFoundException exception = thrown()
            exception.message == "No user found!"
    }

    def "Launch web application when user is not informed"() {

        given: "set the user as NULL"
            rest.userId = null

        when: "execute the rest call"
            rest.launchWebApplication("anyApp")

        then: "assert the correct exception was thrown"
            UserNotFoundException exception = thrown()
            exception.message == "No user found!"
    }

    def "Log error message"() {

        given: "using semantically correct test data"
            def log = new LogRequest(name: "Delete NE", message: "Delete operation was not completed",
                    browser: "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.130 Safari/537.36",
                    stacktrace: "ReferenceError: abc is not defined (http://localhost:8585/logger-example/regions/main/Main.js:29:6)",
                    url: "http://localhost:8585/#logger-example", logTime: new Date(), severity: LogRequest.LoggerSeverity.ERROR )

        when: "call the rest method"
            def response = rest.sendPresentationLogger([log])

        then: "REST should return status 201 (Created)"
            response.status == 201
    }
}
