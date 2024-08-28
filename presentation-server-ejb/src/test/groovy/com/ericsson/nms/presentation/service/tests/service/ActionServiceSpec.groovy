package com.ericsson.nms.presentation.service.tests.service

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.presentation.exceptions.ConditionsLimitException
import com.ericsson.nms.presentation.service.api.dto.ActionRuleCondition
import com.ericsson.nms.presentation.service.api.dto.Property
import com.ericsson.nms.presentation.service.api.dto.Resource
import com.ericsson.nms.presentation.service.ejb.ActionServiceEjb
import com.ericsson.nms.presentation.service.tests.base.AbstractScenario01Spec
import spock.lang.Unroll

import static com.ericsson.nms.presentation.service.PresentationServerConstants.MAXIMUM_ACTION_CONDITIONS_LIMIT

/**
 * Test specification for the Action Service
 */
class ActionServiceSpec extends AbstractScenario01Spec {

    @ObjectUnderTest
    ActionServiceEjb actionService

    @Unroll
    def "Application resource based filtering: #description"() {

        given: "user has permission on #resources"
            authorizeOnResources(resources as Set)

        when: "using app: #sourceApp, multi selection: #multiSel and conditions: #conditions"
            def actions = actionService.getActionsBySelection(sourceApp, multiSel, conditions)

        then: "assert if the returned actions are #expectedActions"
            actions.size() == expectedActions.size()
            actions*.name.containsAll(expectedActions)

        where:
            sourceApp | multiSel | resources                                           | expectedActions     | conditions                                           | description
            "app01"   | false    | [new Resource(name: "resource-02", action: "READ")] | ["app01-action-01"] | [new ActionRuleCondition(dataType: "ManagedObject")] | "User has no access to whitelisted actions"
            "app01"   | false    | [new Resource(name: "resource-01", action: "READ")] | ["app02-action-01"] | [new ActionRuleCondition(dataType: "Collection")]    | "User has access to whitelisted actions"
    }

    def "User should have access to actions if it's authorized on any of the declared resources"() {

        given: "User "
            authorizeOnResources(new Resource(name:  "resource-01", action: "READ") as Set)

        when: "call action service"
            def actions = actionService.getActionsBySelection("app05", false, [new ActionRuleCondition(dataType: "Collection")])

        then: "assert if the returned actions are #expectedActions"
            actions.size() == 1
            actions*.name[0] == "app05-action-01"

    }
    def "User should not have access to actions if it's authorized on any of the declared resources but with a different security action"() {

        given: "User "
            authorizeOnResources(new Resource(name:  "resource-01", action: "CREATE") as Set)

        when: "call action service"
            def actions = actionService.getActionsBySelection("app05", false, [new ActionRuleCondition(dataType: "Collection")])

        then: "no actions should be found"
            actions.isEmpty()
    }

    @Unroll
    def "Whitelist filtering: #description"() {

        // For this test the user has access to any role
        authorizeAllResources()

        when: "using app: #sourceApp, multi selection: #multiSel and conditions: #conditions"
            def actions = actionService.getActionsBySelection(sourceApp, multiSel, conditions)

        then: "assert if the returned actions are #expectedActions"
            actions.size() == expectedActions.size()
            actions*.name.containsAll(expectedActions)

        where:
            sourceApp | multiSel | expectedActions     | conditions                                           | description
            "app01"   | false    | ["app02-action-01"] | [new ActionRuleCondition(dataType: "Collection")]    | "Whitelisted actions should be returned"
            "app01"   | false    | ["app01-action-01"] | [new ActionRuleCondition(dataType: "ManagedObject")] | "Actions provided by the application should be returned"

    }

    @Unroll
    def "App with empty consumesActions should have access to #description"() {

        // For this test the user has access to any role
        authorizeAllResources()

        when: "using app: #sourceApp, multi selection: #multiSel and conditions: #conditions"
            def actions = actionService.getActionsBySelection(sourceApp, multiSel, conditions)

        then: "assert if the returned actions are #expectedActions"
            actions.size() == expectedActions.size()
            actions*.name.containsAll(expectedActions)

        where:
            sourceApp | multiSel | expectedActions                        | conditions                                           | description
            "app06"   | false    | ["app02-action-01", "app05-action-01"] | [new ActionRuleCondition(dataType: "Collection")]    | "all provided actions that match on Collections"
            "app06"   | false    | ["app01-action-01"]                    | [new ActionRuleCondition(dataType: "ManagedObject")] | "all provided actions that match on ManagedObjects"
    }

    @Unroll
    def "Multiple/Single selection filtering: #description"() {

        // For this test the user has access to any role
       authorizeAllResources()

        when: "using app: #sourceApp, multi selection: #multiSel and conditions: #conditions"
            def actions = actionService.getActionsBySelection(sourceApp, multiSel, conditions)

        then: "assert if the returned actions are #expectedActions"
            actions.size() == expectedActions.size()
            actions*.name.containsAll(expectedActions)

        where:
            sourceApp | multiSel | expectedActions     | conditions                                           | description
            "app01"   | false    | ["app02-action-01"] | [new ActionRuleCondition(dataType: "Collection")]    | "Multiple selection action should be available for single selection"
            "app01"   | true     | ["app02-action-01"] | [new ActionRuleCondition(dataType: "Collection")]    | "Multiple selection action should be available for multiple selection"
            "app03"   | true     | []                  | [new ActionRuleCondition(dataType: "ManagedObject")] | "Single selection action should not be available for multiple selection"
            "app03"   | false    | ["app02-action-01"] | [new ActionRuleCondition(dataType: "Collection")]    | "Single selection action should be available for multiple selection"

    }

    @Unroll
    def "Icon parsed from provideActions #description"() {

        // For this test the user has access to any role
       authorizeAllResources()

        when: "using app: #sourceApp, multi selection: #multiSel and conditions: #conditions"
            def actions = actionService.getActionsBySelection(sourceApp, multiSel, conditions)

        then: "assert if the returned actions contain #icon"
            actions.size() == expectedActions.size()
            actions*.icon.containsAll(icon)

        where:
            sourceApp | multiSel | icon         | expectedActions     | conditions                                           | description
            "app01"   | false    | ["icon-01"]  | ["app01-action-01"] | [new ActionRuleCondition(dataType: "ManagedObject")] | "from app01"
            "app02"   | true     | ["icon-02"]  | ["app02-action-01"] | [new ActionRuleCondition(dataType: "Collection")]    | "from app02"
    }

    def "Only rules applicable to all selections should be available"() {

        // For this test the user has access to any role
       authorizeAllResources()

        given: "using app03"
            def appId = "app03"

        when: "with selection containing a Collection and a ManagedObject"
            def actions = actionService.getActionsBySelection(appId, false,
                [new ActionRuleCondition(dataType: "Collection"),
                 new ActionRuleCondition(dataType: "ManagedObject", properties: [
                         new Property(name: "moType", value: "MeContext"),
                         new Property(name: "neType", value: "ERBS")
             ])])

        then: "only one action is capable to support Collections and ManagedObjects (MeContext at ERBS) at the same time"
            actions.size() == 1
            actions*.name.containsAll(["app02-action-01"])

    }

    def "Properties values should be filtered using action rules"() {

        // For this test the user has access to any role
        authorizeAllResources()

        given: "using app02"
            def appId = "app02"

        when: "with selection containing a MeContext with neType RNC"
            def actions = actionService.getActionsBySelection(appId, false,
                    [new ActionRuleCondition(dataType: "ManagedObject", properties: [
                             new Property(name: "moType", value: "MeContext"),
                             new Property(name: "neType", value: "RNC")
                     ])])

        then: "No action should be returned as this application only supports MeContext on ERBS"
            actions.isEmpty()

    }

    def "if rule does not requires properties the data type should be enough to have a match"() {

        given: "For this test the user has access to any role"
            authorizeAllResources()

        when: "call action service with a selection containing a MeContext with neType RNC"
            def actions = actionService.getActionsBySelection("app01", false,
                    [new ActionRuleCondition(dataType: "ManagedObject", properties: [
                            new Property(name: "moType", value: "MeContext"),
                            new Property(name: "neType", value: "RNC")
                    ])])
        then: "app01-action-01 should be returned as it requires any ManagedObject"
            actions.name.find{it == "app01-action-01"} != null

    }

    def "if the rule expects less properties than what the selection is providing the rule should still match"() {

        given: "For this test the user has access to any role"
            authorizeAllResources()

        when: "call action service with a selection containing more properties than required by the action"
            def actions = actionService.getActionsBySelection("app03", false,
                    [new ActionRuleCondition(dataType: "Collection", properties: [
                            new Property(name: "category", value: "Public"),
                            new Property(name: "extraProperty", value: "anyValue")
                    ])])

        then: "app03-action-02 should be returned as it requires any Public Collection"
            actions.name.find{it == "app03-action-02"} != null

    }

    @Unroll
    def "Regex property values should be filtered using action rules: #neType"() {

        // For this test the user has access to any role
        authorizeAllResources()

        given: "using app02"
            def appId = "app02"

        when: "with selection containing a MeContext with neType RNC"
            def actions = actionService.getActionsBySelection(appId, false,
                    [new ActionRuleCondition(dataType: "ManagedObject", properties: [
                            new Property(name: "moType", value: "MeContext"),
                            new Property(name: "neType", value: neType)
                    ])])

        then: "the number of action should match the expectation"
            actions.size() == actionsExpected

        where:
            neType | actionsExpected
            "A1"   | 0
            "A2"   | 1

    }

    @Unroll
    def "Validate rules with properties: #description"() {

        // For this test the user has access to any role
       authorizeAllResources()

        when: "using app: #sourceApp, multi selection: #multiSel and conditions: #conditions"
            def actions = actionService.getActionsBySelection(sourceApp, multiSel, conditions)

        then: "assert if the returned actions are #expectedActions"
            actions.size() == expectedActions.size()
            actions*.name.containsAll(expectedActions)

        where: "Action app01-action-01 should be available for any ManagedObject. Action app02-action-01 should be available only for MeContext at ERBS node"
            sourceApp | multiSel | expectedActions                        | conditions                                                                                                                                                        | description
            "app01"   | false    | ["app01-action-01"]                    | [new ActionRuleCondition(dataType: "ManagedObject")]                                                                                                              | "Action requires MeContext at ERBS node but no properties were provided"
            "app01"   | false    | ["app01-action-01", "app02-action-01"] | [new ActionRuleCondition(dataType: "ManagedObject", properties: [new Property(name: "moType", value: "MeContext"), new Property(name: "neType", value: "ERBS")])] | "Action requires MeContext at ERBS and the correct properties were provided"
            "app01"   | false    | ["app01-action-01"]                    | [new ActionRuleCondition(dataType: "ManagedObject", properties: [new Property(name: "moType", value: "MeContext")])]                                              | "Action requires MeContext at ERBS and not all required properties were provided"
            "app01"   | false    | ["app01-action-01"]                    | [new ActionRuleCondition(dataType: "ManagedObject", properties: [new Property(name: "moType", value: "MeContext"), new Property(name: "neType", value: "SGSN")])] | "Action requires MeContext at ERBS and all required properties were provided but with different values"

    }

    @Unroll
    def "Validate action with multiple rules: #description"() {

        // For this test the user has access to any role
       authorizeAllResources()

        when: "using app: #sourceApp, multi selection: #multiSel and conditions: #conditions"
            def actions = actionService.getActionsBySelection(sourceApp, multiSel, conditions)

        then: "assert if the returned actions are #expectedActions"
            actions.size() == expectedActions.size()
            actions*.name.containsAll(expectedActions)

        where:
            sourceApp | multiSel | expectedActions                        | conditions                                                                                                                                                                                                         | description
            "app01"   | false    | ["app02-action-01"]                    | [new ActionRuleCondition(dataType: "Collection")]                                                                                                                                                                  | "Only one action should be returned because matches Collection data type"
            "app01"   | false    | ["app01-action-01", "app02-action-01"] | [new ActionRuleCondition(dataType: "ManagedObject", properties: [new Property(name: "moType", value: "MeContext"), new Property(name: "neType", value: "ERBS")])]                                                  | "Two actions should be returned because matches any ManagedObject rule and MeContext at ERBS rule"
            "app01"   | true     | []                                     | [new ActionRuleCondition(dataType: "ManagedObject"), new ActionRuleCondition(dataType: "Collection")]                                                                                                              | "No action should be returned because no rule matches all selected objects"
            "app01"   | true     | ["app02-action-01"]                    | [new ActionRuleCondition(dataType: "ManagedObject", properties: [new Property(name: "moType", value: "MeContext"), new Property(name: "neType", value: "ERBS")]), new ActionRuleCondition(dataType: "Collection")] | "One action should be returned because only one action matches all selected objects"
    }

    def "Local actions defined as primary should have the local attribute equals to true"() {

        // For this test the user has access to any role
       authorizeAllResources()

        given:
        def conditions = [
                new ActionRuleCondition(dataType: "ManagedObject",
                        properties: [
                                new Property(name: "moType", value: "MeContext"),
                                new Property(name: "neType", value: "ERBS")
                        ])
        ]

        when: "get actions for app01"
            def actions = actionService.getActionsBySelection("app02", false, conditions)

        then: "should return one action"
            actions.size() == 1

        and: "action provided by the source application should be marked as local"
            actions[0].local
    }

    def "Local actions not declared as consumed by the application should not be retrieved"() {

        // For this test the user has access to any role
        authorizeAllResources()

        given:
            def conditions = [
                new ActionRuleCondition(dataType: "Collection",
                    properties: [
                        new Property(name: "category", value: "Public")
                    ])
            ]

        when: "get actions for app03"
            def actions = actionService.getActionsBySelection("app03", false, conditions)

        then: "app03-action-01 should not be returned as this action is not declared in the consumes attribute"
            !actions*.name.contains("app03-action-01")

    }

    def "A user should not see any actions whose authorization rules that are not met"() {

        // For this test the user has access to app03, but not other apps or their actions
        authorizeOnResources([new Resource(name: "resource-03", action: "READ")] as Set)

        when: "reading action for app03"
            def actions = actionService.getActionsBySelection("app03", false, [new ActionRuleCondition(dataType: "ManagedObject")])

        then: "no actions should be returned"
            actions.size() == 0
    }

    def "Invalid actions should not be loaded"() {

        // For this test the user has access to any role
       authorizeAllResources()

        when: "reading action for app03"
            def actions = actionService.getActionsBySelection("app03", false, [new ActionRuleCondition(dataType: "ManagedObject")])

        then: "only one action should be returned"
            actions.size() == 1

        and: "none of the invalid actions are returned"
            !["", null, "action-without-category", "action-without-plugin", "action-with-invalid-category",
              "action-without-label"].contains(actions[0].name)

    }

    def "No actions should be found if the user has no access to the application"() {

        given: "the user has no permission to any application"
            authorizeOnResources([] as Set)

        when: "get actions for app01"
            def actions = actionService.getActionsBySelection("app01", false, [new ActionRuleCondition(dataType: "Collection")])
        then:
            actions.size() == 0
    }

    @Unroll
    def "Validate action with duplicate conditions: #description"() {

        // For this test the user has access to any role
       authorizeAllResources()

        when: "using app: #sourceApp, multi selection: #multiSel and conditions: #conditions"
            def actions = actionService.getActionsBySelection(sourceApp, multiSel, conditions)

        then: "assert if the returned actions are #expectedActions"
            actions.size() == expectedActions.size()
            actions*.name.containsAll(expectedActions)

        where:
            sourceApp | multiSel | expectedActions      | conditions                                                                                                                                                                                                                                                                                                                                                                          | description
            "app01"   | false    | ["app02-action-01"]  | [new ActionRuleCondition(dataType: "Collection"), new ActionRuleCondition(dataType: "Collection")]                                                                                                                                                                                                                                                                                  | "Only one action should be returned because matches Collection data type"
            "app01"   | true     | ["app02-action-01"]  | [new ActionRuleCondition(dataType: "ManagedObject", properties: [new Property(name: "moType", value: "MeContext"), new Property(name: "neType", value: "ERBS")]), new ActionRuleCondition(dataType: "Collection"), new ActionRuleCondition(dataType: "ManagedObject", properties: [new Property(name: "moType", value: "MeContext"), new Property(name: "neType", value: "ERBS")])] | "One action should be returned because only one action matches all selected objects"
    }

    def "Validate action with selection greater than 1000"() {

        given: "list of conditions greater than limit"
            def conditionList = (1..(MAXIMUM_ACTION_CONDITIONS_LIMIT+1)).collect{ new ActionRuleCondition(dataType: "Collection") }

        when: "calling get actions by selection"
            actionService.getActionsBySelection("app01", false, conditionList)

        then: "an exception should be thrown"
            def exception = thrown(ConditionsLimitException)
            exception.message == "Selection conditions must not exceed "+ MAXIMUM_ACTION_CONDITIONS_LIMIT

    }

    @Unroll
    def "Metadata values are parsed from the provideActions"() {

        // For this test the user has access to any role
       authorizeAllResources()

        when: "reading action from app96"
            def actions = actionService.getActionsProvidedByApp("app96")

        then: "assert if the returned action has the metadata values"
            actions[0].metadata[0].name == "url"
            actions[0].metadata[0].value == "https://customlaunchurl1.com"

    }

    def "Metadata attribute should return as null if action has no metadata defined"(){

       authorizeAllResources()

        when: "reading action from app02"
            def actions = actionService.getActionsProvidedByApp("app02")

        then: "no metadata values should be returned as there are no metadata values in app02"
            actions[0].metadata == null

    }

    @Unroll
    def "Actions with invalid metadata should not be loaded: #description"() {

       authorizeAllResources()

        when: "reading action from app95"
            def actions = actionService.getActionsProvidedByApp("app95")

        then: "only one action should be returned and be different than #expectation"
            actions.size() == 1
            actions[0].name != expectation

        where:
            expectation                                                   | description
            "action-metadata-without-value"                               | "actions with metadata which does not have value should not be returned"
            "action-metadata-without-name"                                | "actions with metadata which does not have name should not be returned"
            "action-metadata-without-name-and-value"                      | "actions with metadata which does not have both name and value should not be returned"
            "action-with-empty-metadata-values"                           | "actions with empty metadata values should not be returned"
            "action-with-invalid-metadata"                                | "actions with invalid metadata should not be returned"
            "action-with-duplicate-metadata"                              | "actions with duplicate metadata should not be returned"
            "action-with-multiple-metadata-with-invalid-values"           | "actions with multiple metadata with invalid values should not be returned"
            "action-metadata-name-and-values-having-length-more-than-256" | "actions with metadata name and values having more than 256 characters should not be returned"
            "action-metadata-name-having-length-more-than-256"            | "actions with metadata name having more than 256 characters should not be returned"
            "action-metadata-value-having-length-more-than-256"           | "actions with metadata value having more than 256 characters should not be returned"
            "action-metadata-size-having-more-than-16"                    | "actions with metadata size having more than 16 should not be returned"

    }

}
