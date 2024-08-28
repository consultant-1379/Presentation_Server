package com.ericsson.nms.presentation.service.tests.service

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.presentation.exceptions.NotFoundException
import com.ericsson.nms.presentation.service.api.dto.Resource
import com.ericsson.nms.presentation.service.cache.ApplicationCachePopulator
import com.ericsson.nms.presentation.service.ejb.ApplicationServiceEjb
import com.ericsson.nms.presentation.service.tests.base.AbstractScenario01Spec
import com.ericsson.oss.itpf.sdk.security.accesscontrol.EAccessControl
import spock.lang.Unroll

import javax.inject.Inject

/**
 * Test specification for ApplicationService.
 */
class ApplicationServiceSpec extends AbstractScenario01Spec {

    @ObjectUnderTest
    ApplicationServiceEjb applicationService

    @Inject
    EAccessControl accessControl

    @Inject
    ApplicationCachePopulator cachePopulator

    @Unroll
    def "getApps method should filter values against RBAC (#resourceId)" () {

        given: "user has access to the required resource"
            authorizeOnResources([new Resource(name: resourceId, action: "READ")] as Set)

        when: "retrieve the available applications when the user has access to #resourceId"
            accessControl.setAuthUserSubject("anyUser")
            def apps = applicationService.getApps()

        then: "checks if the returned applications matches the resources restrictions"
            apps.size() == appIds.size()
            apps*.id.containsAll(appIds)

        where: "are expected the following Application IDs: #appIds"
            appIds                       | resourceId
            ["app01", "app02", "app04"]  | "resource-01"
            ["app01", "app03", "app05"]  | "resource-02"
            ["app02", "app03"]           | "resource-03"
            ["app03"]                    | "resource-04"
    }

    @Unroll
    def "getGroups method should return all groups and associated app ids (#groupId)"() {

        given: "user has access to every resource"
            authorizeAllResources()

        when: "retrieve the group #groupId"
            def groups = applicationService.getGroups(groupId)

        then: "checks if the returned group has all expected applications"
            groups[0].appIds.size() == appIds.size()
            groups[0].appIds == appIds

        where: "#groupId should have the application ids: #appIds"
            appIds                                                                 | groupId
            ["app01", "app01-copy-1", "app01-copy-2", "app03", "app05"] as HashSet | "group01"
            ["app02", "app03"] as HashSet                                          | "group02"
            ["app04"] as HashSet                                                   | "group03"
            ["app04"] as HashSet                                                   | "group04"
    }

    @Unroll
    def "getGroups method should return only applications permitted to the user against RBAC (#groupId)"() {

        given: "user has access to every resource"
            authorizeOnResources(resources as Set)

        when: "retrieve the group #groupId"
        def groups = applicationService.getGroups(groupId)

        then: "checks if only the applications allowed to the user against RBAC are returned"
            groups[0].appIds.size() == appIds.size()
            groups[0].appIds.containsAll(appIds)

        where: "#groupId where the user has the resources #resourceIds should have the applications #appIds"
            appIds      | groupId   | resources
            ["app03"]   | "group01" | [new Resource(name: "resource-03", action: "READ")]
            ["app02"]   | "group02" | [new Resource(name: "resource-01", action: "READ")]
            []          | "group03" | [new Resource(name: "resource-02", action: "READ"), new Resource(name: "resource-03", action: "READ")]
    }

    def "getApps method call with invalid ID should return null"() {

        given: "retrieving application with invalid ID"
            def app = applicationService.getApps("thisIdDontExist")

        expect: "app should be null as no application exists with the given ID"
            !app
    }

    @Unroll
    def "getApps method call with valid and invalid IDs should return only the valid one (#description)"() {

        given: "user has access to every resource"
            authorizeAllResources()

        when: "retrieving application with #description"
            def app = applicationService.getApps(appId)

        then : "Checks if the application is returned for valid IDs or is null for invalid"
            app?.id == expected

        where: "app should be null as no application exists with the given ID"
            description  | appId             | expected
            "valid ID"   | "app01"           | ["app01"]
            "invalid ID" | "thisIdDontExist" | []

    }

    def "getApplication method with valid ID should return application instance found"() {

        given: "user has access to every resource"
            authorizeAllResources()

        when: "retrieving application with valid ID"
            def app = applicationService.getApplication("app01")

        then: "application app01 should be found and an instance returned "
            app.id == "app01"
    }

    def "getApplication method with invalid ID should return exception"() {

        when: "retrieving application with invalid ID"
            applicationService.getApplication("thisIdDontExist")

        then: "the method should throw NotFoundException as no application exists with the given ID"
            NotFoundException exception = thrown()
            exception.message == "Application thisIdDontExist not found"
    }

    def "getApplication method with unauthorized user should return exception"() {

        given: "user has access no permission"
            authorizeOnResources([] as Set)

        when: "retrieving valid application"
            applicationService.getApplication("app01")

        then: "the method should throw SecurityException as the user has no access to the application"
            SecurityException exception = thrown()
            exception.message == "Access denied to application app01"
    }

    def "getGroups method with invalid group ID should return empty collection"() {

        given: "retrieving group with invalid ID"
            def group = applicationService.getGroups("thisIdDontExist")

        expect: "group should be empty as no group exists with the given ID"
            group.isEmpty()

    }

    def "getApps method using a valid id but with invalid access should return empty collection"() {

        given: "user has access to the wrong resource"
            authorizeOnResources([new Resource(name: "other-resource", action: "READ")] as Set)

        when: "retrieving application app01"
            def app = applicationService.getApps("app01")

        then: "app should be empty as the user don't have the required access"
            app.isEmpty()

    }
}
