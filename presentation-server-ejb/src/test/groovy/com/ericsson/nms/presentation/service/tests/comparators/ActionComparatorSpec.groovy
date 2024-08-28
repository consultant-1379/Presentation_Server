package com.ericsson.nms.presentation.service.tests.comparators

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.presentation.service.api.dto.Action
import com.ericsson.nms.presentation.service.comparators.ActionComparator

import static com.ericsson.nms.presentation.service.PresentationServerConstants.*
/**
 * Test specification to test the actions comparator.
 */
class ActionComparatorSpec extends CdiSpecification {

    @ObjectUnderTest
    ActionComparator comparator

    def "actions sorting mixing primary, locals and categories"() {

        given: "a predefined unordered actions list"
            def actions = [
                    new Action(name: "action-01", order: 0,  category: SECURITY_ACTION_CATEGORY),
                    new Action(name: "action-02", order: 2,  category: FAULT_MANAGEMENT_ACTION_CATEGORY, primary: true),
                    new Action(name: "action-03", order: 3,  category: MONITORING_ACTION_CATEGORY),
                    new Action(name: "action-04", order: 1,  category: CONFIGURATION_ACTION_CATEGORY),
                    new Action(name: "action-05", order: 2,  category: MONITORING_ACTION_CATEGORY, local: true),
                    new Action(name: "action-06", order: 2,  category: LEGACY_ACTION_CATEGORY, primary: true),
                    new Action(name: "action-07", order: 3,  category: CONFIGURATION_ACTION_CATEGORY),
                    new Action(name: "action-08", order: 1,  category: FAULT_MANAGEMENT_ACTION_CATEGORY),
                    new Action(name: "action-09", order: 3,  category: MONITORING_ACTION_CATEGORY, local: true),
                    new Action(name: "action-10", order: 1,  category: PERFORMANCE_ACTION_CATEGORY),
                    new Action(name: "action-11", order: 1,  category: MONITORING_ACTION_CATEGORY),
                    new Action(name: "action-12", order: -1, category: FAULT_MANAGEMENT_ACTION_CATEGORY),
                    new Action(name: "action-13", order: 1,  category: LEGACY_ACTION_CATEGORY),
                    new Action(name: "action-14", order: null,  category: FAULT_MANAGEMENT_ACTION_CATEGORY),
                    new Action(name: "action-15", order: null,  category: FAULT_MANAGEMENT_ACTION_CATEGORY),
                    new Action(name: "action-16", order: 1,  category: COLLECTION_MODIFICATION_ACTION_CATEGORY),
                    new Action(name: "action-17", order: 1,  category: COLLECTION_ACTION_CATEGORY),
                    new Action(name: "action-18", order: 3,  category: COLLECTION_MODIFICATION_ACTION_CATEGORY),
                    new Action(name: "action-19", order: 2,  category: COLLECTION_MODIFICATION_ACTION_CATEGORY)
            ]

        when: "sort the list using the comparator"
            Collections.sort(actions, comparator)

        then: "the two first actions should be local as we have 2 local actions"
            actions[0..1].name == ["action-05","action-09"]

        and: "Fault Management primary action should come next"
            actions[2].name == "action-02"

        and: "Fault Management actions with order attribute should come next"
            actions[3..4].name == ["action-12","action-08"]

        and: "Remaining Fault Management actions should come in the end of the group as it has no order attribute"
            actions[5..6].name.containsAll(["action-14","action-15"])

        and: "Monitoring category should come next ordered by order attribute (there's no primary)"
            actions[7..8].name == ["action-11", "action-03"]

        and: "Configuration category comes next ordered by order attribute (there's no primary)"
            actions[9..10].name == ["action-04","action-07"]

        and: "Performance category comes next"
            actions[11].name == "action-10"

        and: "Security category comes next"
            actions[12].name == "action-01"

        and: "Collection Action should come next"
            actions[13].name == "action-17"

        and: "three Collection Modification Actions should come next ordered by order attribute"
            actions[14..16].name == ["action-16","action-19","action-18"]

        and: "Legacy category comes next with primary action first"
            actions[17..18].name == ["action-06","action-13"]


    }

    def "sort actions with the same order attribute but in different groups"() {

        given: "actions with the same order attribute but in different groups"
            def actions = [
                    new Action(name: "action-03", order: 1, category: COLLECTION_MODIFICATION_ACTION_CATEGORY),
                    new Action(name: "action-04", order: 1, category: COLLECTION_ACTION_CATEGORY),
                    new Action(name: "action-01", order: 1, category: CONFIGURATION_ACTION_CATEGORY),
                    new Action(name: "action-02", order: 1, category: FAULT_MANAGEMENT_ACTION_CATEGORY)
            ]

        when: "sort the actions"
            Collections.sort(actions, comparator)

        then: "group ordering should take precedence"
            actions*.name == ["action-02","action-01","action-04","action-03"]

    }

    def "sort actions with the different order in the same group"() {

        given: "actions with the different order in the same group"
            def actions = [
                    new Action(name: "action-01", order: 2, category: CONFIGURATION_ACTION_CATEGORY),
                    new Action(name: "action-02", order: 1, category: CONFIGURATION_ACTION_CATEGORY)
            ]

        when: "sort the actions"
            Collections.sort(actions, comparator)

        then: "action with higher order should come last"
        actions*.name == ["action-02", "action-01"]

    }

    def "sort actions with different ordering and same group but one is primary"() {

        given: "actions with different ordering and same group but one is primary"
            def actions = [
                    new Action(name: "action-01", order: 1, category: CONFIGURATION_ACTION_CATEGORY),
                    new Action(name: "action-02", order: 2, category: CONFIGURATION_ACTION_CATEGORY, primary: true)
            ]

        when: "sort the actions"
            Collections.sort(actions, comparator)

        then: "primary actions should take precedence"
            actions*.name == ["action-02", "action-01"]

    }

    def "sort two primary actions with different ordering"() {

        given: "two primary actions with different ordering"
            def actions = [
                    new Action(name: "action-01", order: 2, category: CONFIGURATION_ACTION_CATEGORY, primary: true),
                    new Action(name: "action-02", order: 1, category: CONFIGURATION_ACTION_CATEGORY, primary: true)
            ]

        when: "sort the actions"
            Collections.sort(actions, comparator)

        then: "the action with high order should come last"
            actions*.name == ["action-02", "action-01"]
    }

    def "sort when one action is local action and other is primary action"() {

        given: "one action is local action and other is primary action"
            def actions = [
                    new Action(name: "action-01", order: 1, category: CONFIGURATION_ACTION_CATEGORY, primary: true),
                    new Action(name: "action-02", order: 2, category: CONFIGURATION_ACTION_CATEGORY, primary: true, local: true)
            ]

        when: "sort the actions"
            Collections.sort(actions, comparator)

        then: "local action should come first"
            actions*.name == ["action-02", "action-01"]

    }

    def "sort when one action is local action and other has lower order in the same group"() {

        given: "one action is local action and other has lower order in the same group"
            def actions = [
                    new Action(name: "action-01", order: 1, category: CONFIGURATION_ACTION_CATEGORY),
                    new Action(name: "action-02", order: 2, category: CONFIGURATION_ACTION_CATEGORY, local: true),
            ]

        when: "sort the actions"
            Collections.sort(actions, comparator)

        then: "local action should come first"
            actions*.name == ["action-02", "action-01"]

    }

    def "sort when both are local actions with different order"() {

        given: "two local actions with different order"
            def actions = [
                    new Action(name: "action-01", order: 2, category: CONFIGURATION_ACTION_CATEGORY, local: true),
                    new Action(name: "action-02", order: 1, category: CONFIGURATION_ACTION_CATEGORY, local: true)
            ]

        when: "sort the actions"
            Collections.sort(actions, comparator)

        then: "local action should come first"
            actions*.name == ["action-02", "action-01"]

    }

}
