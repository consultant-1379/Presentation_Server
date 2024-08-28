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
package com.ericsson.nms.presentation.service.tests.service.security

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.presentation.service.api.dto.Resource
import com.ericsson.nms.presentation.service.security.SecurityUtil
import com.ericsson.nms.presentation.service.tests.base.AbstractPresentationServerSpec
import spock.lang.Unroll

class SecurityUtilSpec extends AbstractPresentationServerSpec {

    @ObjectUnderTest
    SecurityUtil securityUtil

    def "get the resources the user is authorized"() {

        given: "mock te authorization service to give read permission on resource-01"
            authorizeOnResources([new Resource(name: "resource-01", action: "READ")] as Set)

        when: "from a set of 2 resources, check which ones are allowed to the current user"
            def authorizedResources = securityUtil.getUserResources(["resource-01", "resource-02"] as Set)

        then: "only resource-01 should be returned as the user has no permission on resource-02"
            authorizedResources.size() == 1
            authorizedResources[0] == "resource-01"
    }

    def "when the user has no permission on any resource, getUserResources should return an empty list"() {

        given: "user has no permission"
            authorizeOnResources([] as Set)

        when: "get the resources the user is authorized"
            def authorizedResources = securityUtil.getUserResources(["resource-01"] as Set)

        then: "no resource should be found as the user has no permissions"
            authorizedResources.empty
    }

    def "get the resources and actions the user is authorized"() {

        given: "mock te authorization service to give read permission on resource-01"
            authorizeOnResources([new Resource(name: "resource-01", action: "READ"), new Resource(name: "resource-01", action: "DELETE")] as Set)

        when: "from a set of 2 resources, check which ones are allowed to the current user"
            def authorizedResources = securityUtil.getUserResourcesAndActions(["resource-01", "resource-02"] as Set)

        then: "only resource-01 should be returned as the user has no permission on resource-02"
            authorizedResources.size() == 1
            authorizedResources.keySet()[0] == "resource-01"

        and: "resource-01 should have actions READ and DELETE"
            authorizedResources.values()[0].size() == 2
            authorizedResources.values()[0].containsAll(["READ", "DELETE"])
    }

    @Unroll
    def "check user permissions on any resource and actions: #description"() {

        given: "mock the authorization service to give the user some permissions"
            authorizeOnResources([
                new Resource(name: "resource-01", action: "READ"),
                new Resource(name: "resource-01", action: "DELETE"),
                new Resource(name: "resource-02", action: "CREATE"),
                new Resource(name: "resource-02", action: "UPDATE")
            ] as Set)

        when: "check the user permission"
            def authorized = securityUtil.hasAccessToAnyResourceAndAction(resouces as Set)

        then: "expected outcome: #expectation"
            expectation == authorized

        where:
            description                                                             | resouces                                                                                                   | expectation
            "single authorized resource"                                            | [new Resource(name: "resource-01", action: "DELETE")]                                                      | true
            "single authorized with no action"                                      | [new Resource(name: "resource-01")]                                                                        | true
            "single unauthorized resource"                                          | [new Resource(name: "resource-02", action: "DELETE")]                                                      | false
            "multiple resources when at least one is authorized"                    | [new Resource(name: "resource-02", action: "DELETE"), new Resource(name: "resource-01", action: "READ")]   | true
            "multiple resources when none is authorized"                            | [new Resource(name: "resource-02", action: "DELETE"), new Resource(name: "resource-01", action: "UPDATE")] | false
            "multiple actions in the same resources when none is authorized"        | [new Resource(name: "resource-02", action: "DELETE"), new Resource(name: "resource-02", action: "READ")]   | false

    }

    @Unroll
    def "check user permissions on all resources and actions: #description"() {

        given: "mock the authorization service to give the user some permissions"
            authorizeOnResources([
                    new Resource(name: "resource-01", action: "READ"),
                    new Resource(name: "resource-01", action: "DELETE"),
                    new Resource(name: "resource-02", action: "CREATE"),
                    new Resource(name: "resource-02", action: "UPDATE")
            ] as Set)

        when: "check the user permission"
            def authorized = securityUtil.hasAccessToAllResourcesAndActions(resouces as Set)

        then: "expected outcome: #expectation"
            expectation == authorized

        where:
            description                                                                       | resouces                                                                                                   | expectation
            "single authorized resource"                                                      | [new Resource(name: "resource-01", action: "DELETE")]                                                      | true
            "single authorized with no action"                                                | [new Resource(name: "resource-01")]                                                                        | true
            "single unauthorized resource"                                                    | [new Resource(name: "resource-02", action: "DELETE")]                                                      | false
            "multiple resources but the user don't have permission in all"                    | [new Resource(name: "resource-02", action: "DELETE"), new Resource(name: "resource-01", action: "READ")]   | false
            "multiple resources when none is authorized"                                      | [new Resource(name: "resource-02", action: "DELETE"), new Resource(name: "resource-01", action: "UPDATE")] | false
            "multiple actions in the same resource but the user don't have permission in all" | [new Resource(name: "resource-02", action: "DELETE"), new Resource(name: "resource-02", action: "UPDATE")] | false
            "multiple actions in the same resources when none is authorized"                  | [new Resource(name: "resource-02", action: "DELETE"), new Resource(name: "resource-02", action: "READ")]   | false

    }

    def "when user has no permissions hasAccessToAllResourcesAndActions should return false"() {

        given: "mock security to have no permission"
            authorizeOnResources([] as Set)

        when: "call the method"
           def hasAccess = securityUtil.hasAccessToAllResourcesAndActions([new Resource(name: "resource-01", action: "DELETE")] as Set)

        then: "should return false"
            !hasAccess
    }

    def "when user has no permissions hasAccessToAnyResourceAndAction should return false"() {

        given: "mock security to have no permission"
            authorizeOnResources([] as Set)

        when: "call the method"
            def hasAccess = securityUtil.hasAccessToAnyResourceAndAction([new Resource(name: "resource-01", action: "DELETE")] as Set)

        then: "should return false"
            !hasAccess
    }

}
