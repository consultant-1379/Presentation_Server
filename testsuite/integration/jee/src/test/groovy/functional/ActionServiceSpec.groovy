package functional

import base.AbstractBaseSpec
import com.ericsson.nms.presentation.service.api.dto.ActionRuleCondition
import com.ericsson.nms.presentation.service.api.dto.Property
import com.jayway.restassured.response.Header
import spock.lang.Unroll

import static org.hamcrest.Matchers.*

/**
 * Test specification for Action Service API
 */
class ActionServiceSpec extends AbstractBaseSpec {

    @Unroll
    def "get Actions for #app with multiSelect #multiSelect on #dataType #propertyType"() {

        given: "a match condition"
            def matches = [application: app, multipleSelection: multiSelect, conditions:
                    [new ActionRuleCondition(dataType: dataType, properties:
                            [new Property(name: propertyType, value: propertyValue)])
                    ]
            ]

        when: "send the POST request"
            def response = defaultRequest()
                    .body(matches)
                    .post("/rest/v1/apps/action-matches/")

            response.then().log().ifValidationFails()

        then: "status code should be 200"
            response.then().statusCode(200)

        and: "validate the returned actions"
            response.then().body("actions", hasSize(size))
            expected.eachWithIndex { it, num ->
                response.then().body("actions[" + num + "].name", equalTo(it.name as Serializable))
                response.then().body("actions[" + num + "].defaultLabel", equalTo(it.defaultLabel as Serializable))
                response.then().body("actions[" + num + "].applicationId", equalTo(it.applicationId as Serializable))
                response.then().body("actions[" + num + "].category", equalTo(it.category as Serializable))
                response.then().body("actions[" + num + "].plugin", equalTo(it.plugin as Serializable))
                response.then().body("actions[" + num + "].primary", equalTo(false))
                response.then().body("actions[" + num + "].multipleSelection", equalTo(it.multiSelect as Serializable))
                response.then().body("actions[" + num + "].order", equalTo(it.order as Serializable))
            }

        and: "the matches filter used should be returned as well"
            response.then().body("action-matches.conditions", hasSize(1))
            response.then().body("action-matches.conditions[0].dataType", equalTo(dataType))
            response.then().body("action-matches.conditions[0].properties", hasSize(1))
            response.then().body("action-matches.conditions[0].properties[0].name", equalTo(propertyType))
            response.then().body("action-matches.conditions[0].properties[0].value", equalTo(propertyValue))

        where:
            app         | multiSelect | dataType        | propertyType | propertyValue | size | expected
            "my-app-01" | false       | "ManagedObject" | "moType"     | "MeContext"   | 3    | [[name: "app01-action-01", defaultLabel: "[App01] Action 01", applicationId: "my-app-01", category: "Configuration Management", plugin: "plugins/app01/action01-plugin.js", multiSelect: false, order: 1000],
                                                                                                 [name: "app02-action-03", defaultLabel: "[App02] Action 03", applicationId: "my-app-02", category: "Configuration Management", plugin: "plugins/app02/action03-plugin.js", multiSelect: false, order: 6000],
                                                                                                 [name: "app02-action-01", defaultLabel: "[App02] Action 01", applicationId: "my-app-02", category: "Legacy Actions", plugin: "plugins/app02/action01-plugin.js", multiSelect: false, order: 3000]]
            "my-app-01" | false       | "ManagedObject" | "targetType" | "ERBS"        | 3    | [[name: "app01-action-01", defaultLabel: "[App01] Action 01", applicationId: "my-app-01", category: "Configuration Management", plugin: "plugins/app01/action01-plugin.js", multiSelect: false, order: 1000],
                                                                                                 [name: "app03-action-02", defaultLabel: "[App03] Action 02", applicationId: "my-app-03", category: "Configuration Management", plugin: "plugins/app03/action02-plugin.js", multiSelect: true, order: 5000],
                                                                                                 [name: "app02-action-01", defaultLabel: "[App02] Action 01", applicationId: "my-app-02", category: "Legacy Actions", plugin: "plugins/app02/action01-plugin.js", multiSelect: false, order: 3000]]
            "my-app-01" | true        | "ManagedObject" | "targetType" | "ERBS"        | 1    | [[name: "app03-action-02", defaultLabel: "[App03] Action 02", applicationId: "my-app-03", category: "Configuration Management", plugin: "plugins/app03/action02-plugin.js", multiSelect: true, order: 5000]]
            "my-app-03" | true        | "ManagedObject" | "targetType" | "ERBS"        | 2    | [[name: "app03-action-02", defaultLabel: "[App03] Action 02", applicationId: "my-app-03", category: "Configuration Management", plugin: "plugins/app03/action02-plugin.js", multiSelect: true, order: 5000],
                                                                                                 [name: "app03-action-01", defaultLabel: "[App03] Action 01", applicationId: "my-app-03", category: "Configuration Management", plugin: "plugins/app03/action01-plugin.js", multiSelect: true, order: 7000]]
            "my-app-01" | false       | "Collection"    | "type"       | "BRANCH"      | 1    | [[name: "app01-action-02", defaultLabel: "[App01] Action 02", applicationId: "my-app-01", category: "Collection Actions", plugin: "plugins/app01/action02-plugin.js", multiSelect: false, order: 1000]]
            "my-app-01" | false       | "Collection"    | "type"       | "LEAF"        | 1    | [[name: "app01-action-03", defaultLabel: "[App01] Action 03", applicationId: "my-app-01", category: "Collection Modification Actions", plugin: "plugins/app01/action03-plugin.js", multiSelect: false, order: 1000]]
    }

    @Unroll
    def "get actions for multiple conditioned actions for #propertyValue of ERBS (action-matches)"() {

        given: "a match condition"
            def matches = [application: "my-app-01", multipleSelection: false, conditions:
                [new ActionRuleCondition(dataType: "ManagedObject", properties:
                    [new Property(name: "moType", value: propertyValue), new Property(name: "targetType", value: "ERBS")])
                ]
            ]

        when: "send the POST request"
            def response = defaultRequest()
                    .body(matches)
                    .post("/rest/v1/apps/action-matches/")

            response.then().log().ifValidationFails()

        then: "status code should be 200"
            response.then().statusCode(200)

        and: "validate the returned actions"
            response.then().body("actions", hasSize(5))
            expected.eachWithIndex { it, num ->
                response.then().body("actions[" + num + "].name", equalTo(it.name as Serializable))
                response.then().body("actions[" + num + "].defaultLabel", equalTo(it.defaultLabel as Serializable))
                response.then().body("actions[" + num + "].applicationId", equalTo(it.applicationId as Serializable))
                response.then().body("actions[" + num + "].category", equalTo(it.category as Serializable))
                response.then().body("actions[" + num + "].plugin", equalTo(it.plugin as Serializable))
                response.then().body("actions[" + num + "].primary", equalTo(false))
                response.then().body("actions[" + num + "].multipleSelection", equalTo(it.multiSelect as Serializable))
                response.then().body("actions[" + num + "].order", equalTo(it.order as Serializable))
            }

        and: "the matches filter used should be returned as well"
            response.then().body("action-matches.conditions", hasSize(1))
            response.then().body("action-matches.conditions[0].dataType", equalTo("ManagedObject"))
            response.then().body("action-matches.conditions[0].properties", hasSize(2))
            response.then().body("action-matches.conditions[0].properties[0].name", equalTo("moType"))
            response.then().body("action-matches.conditions[0].properties[0].value", equalTo(propertyValue))
            response.then().body("action-matches.conditions[0].properties[1].name", equalTo("targetType"))
            response.then().body("action-matches.conditions[0].properties[1].value", equalTo("ERBS"))

        where:
            propertyValue    | expected
            "MeContext"      | [[name: "app01-action-01", defaultLabel: "[App01] Action 01", applicationId: "my-app-01", category: "Configuration Management", plugin: "plugins/app01/action01-plugin.js", multiSelect: false, order: 1000],
                                [name: "app01-action-06", defaultLabel: "[App01] Action 06", applicationId: "my-app-01", category: "Configuration Management", plugin: "plugins/app01/action06-plugin.js", multiSelect: false, order: 4000],
                                [name: "app03-action-02", defaultLabel: "[App03] Action 02", applicationId: "my-app-03", category: "Configuration Management", plugin: "plugins/app03/action02-plugin.js", multiSelect: true, order: 5000],
                                [name: "app02-action-03", defaultLabel: "[App02] Action 03", applicationId: "my-app-02", category: "Configuration Management", plugin: "plugins/app02/action03-plugin.js", multiSelect: false, order: 6000],
                                [name: "app02-action-01", defaultLabel: "[App02] Action 01", applicationId: "my-app-02", category: "Legacy Actions", plugin: "plugins/app02/action01-plugin.js", multiSelect: false, order: 3000]]
            "NetworkElement" | [[name: "app01-action-01", defaultLabel: "[App01] Action 01", applicationId: "my-app-01", category: "Configuration Management", plugin: "plugins/app01/action01-plugin.js", multiSelect: false, order: 1000],
                                [name: "app01-action-05", defaultLabel: "[App01] Action 05", applicationId: "my-app-01", category: "Legacy Actions", plugin: "plugins/app01/action05-plugin.js", multiSelect: false, order: 2000],
                                [name: "app01-action-04", defaultLabel: "[App01] Action 04", applicationId: "my-app-01", category: "Configuration Management", plugin: "plugins/app01/action04-plugin.js", multiSelect: true, order: 2000],
                                [name: "app03-action-02", defaultLabel: "[App03] Action 02", applicationId: "my-app-03", category: "Configuration Management", plugin: "plugins/app03/action02-plugin.js", multiSelect: true, order: 5000],
                                [name: "app02-action-01", defaultLabel: "[App02] Action 01", applicationId: "my-app-02", category: "Legacy Actions", plugin: "plugins/app02/action01-plugin.js", multiSelect: false, order: 3000]]
    }

    @Unroll
    def "get same actions for different properties - level #level (action-matches)"() {

        given: "a custom matches condition"
            def matches = [application: "my-app-02", multipleSelection: false, conditions:
                    [new ActionRuleCondition(dataType: "Collection", properties:
                            [new Property(name: "type", value: "BRANCH"), new Property(name: "level", value: level)])
                    ]
            ]

        when: "execute a POST to the endpoint"
            def response = defaultRequest()
                    .body(matches)
                    .post("/rest/v1/apps/action-matches/")

        then: "validate the returned actions"
            response.then().body("actions", hasSize(1))
            response.then().body("actions[0].name", equalTo("app02-action-02"))
            response.then().body("actions[0].defaultLabel", equalTo("[App02] Action 02"))
            response.then().body("actions[0].applicationId", equalTo("my-app-02"))
            response.then().body("actions[0].category", equalTo("Collection Actions"))
            response.then().body("actions[0].plugin", equalTo("plugins/app02/action02-plugin.js"))
            response.then().body("actions[0].primary", equalTo(false))
            response.then().body("actions[0].multipleSelection", equalTo(false))
            response.then().body("actions[0].order", equalTo(3000))


        and: "the matches filter used should be returned as well"
            response.then().body("action-matches.conditions", hasSize(1))
            response.then().body("action-matches.conditions[0].dataType", equalTo("Collection"))
            response.then().body("action-matches.conditions[0].properties", hasSize(2))
            response.then().body("action-matches.conditions[0].properties[0].name", equalTo("type"))
            response.then().body("action-matches.conditions[0].properties[0].value", equalTo("BRANCH"))
            response.then().body("action-matches.conditions[0].properties[1].name", equalTo("level"))
            response.then().body("action-matches.conditions[0].properties[1].value", equalTo(level.toString()))

        where:
            level << [1, 2, 3]

    }

    def "don't get action if they have same order in same category (action-matches)"() {

        given: "a custom matches condition"
            def matches = [application: "my-app-03", multipleSelection: false, conditions:
                    [new ActionRuleCondition(dataType: "ManagedObject", properties:
                            [new Property(name: "moType", value: "MeContext")])
                    ]
            ]

        when: "execute a POST to the endpoint"
            def response = defaultRequest()
                    .body(matches)
                    .post("/rest/v1/apps/action-matches/")

            response.then().log().ifValidationFails()

        then: "status code should be 200 (Ok)"
            response.then().statusCode(200)

        and: "resulting actions do not contain clashing actions"
            response.then().body("actions", hasSize(greaterThan(0)))
            response.then().body("actions", not(hasItems("app03-action-03", "app03-action-04")))
    }

    def "get actions for non-existent application"() {

        given: "a custom matches condition"
            def matches = [application: "inexistent-app", multipleSelection: false, conditions : [
                                   new ActionRuleCondition(dataType: "ManagedObject", properties:
                                           [new Property(name: "moType", value: "MeContext")])
                           ]
            ]

        when: "execute a POST to the endpoint"
            def response = defaultRequest()
                    .body(matches)
                    .post("/rest/v1/apps/action-matches/")

        then: "status code should be 404 (Not Found)"
            response.then().statusCode(404)

        and: "validates the error message"
            response.then().body("message", equalTo("No application was found with the ID: inexistent-app"))

    }

    def "get actions with no selection"() {

        given: "an empty matches condition"
            def matches = [application: "my-app-01", multipleSelection: false, conditions: []]

        when: "execute a POST to the endpoint"
            def response = defaultRequest()
                    .body(matches)
                    .post("/rest/v1/apps/action-matches/")

        then: "status code should be 400 (Bad Request)"
            response.then().statusCode(400)

        and: "validates the error message"
            response.then().body("message", equalTo("No selection condition  was given to the service."))
    }

    def "get localized actions"() {

        given: "a custom matches condition"
            def matches = [application: "my-app-01", multipleSelection: false, conditions:
                    [new ActionRuleCondition(dataType: "ManagedObject", properties:
                            [new Property(name: "moType", value: "MeContext")])
                    ]
            ]

        when: "execute a POST to the endpoint with languages set"
            def response = defaultRequest()
                    .body(matches)
                    .header(new Header("Accept-Language", "fr,it,pt-br;q=0.5"))
                    .with().post("/rest/v1/apps/action-matches/")

            response.then().log().ifValidationFails()

        then: "status code should be 200 (Ok)"
            response.then().statusCode(200)

        and: "validate the returned actions has successfully translated over to pt-br"
            response.then().body("actions", hasSize(3))
            response.then().body("actions[0].defaultLabel", equalTo("Primeira Ação"))
            response.then().body("actions[0].name", equalTo("app01-action-01"))
            response.then().body("actions[0].plugin", equalTo("plugins/app01/action01-plugin.js"))
            response.then().body("actions[0].applicationId", equalTo("my-app-01"))
            response.then().body("actions[0].category", equalTo("Configuration Management"))
            response.then().body("actions[0].primary", equalTo(false))
            response.then().body("actions[0].multipleSelection", equalTo(false))
            response.then().body("actions[0].icon", equalTo("icon-01"))
            response.then().body("actions[0].order", equalTo(1000))
            response.then().body("actions[0].metadata", hasSize(1))
            response.then().body("actions[0].metadata[0].name", equalTo("url"))
            response.then().body("actions[0].metadata[0].value", equalTo("https://www.webmail.com"))

        and: "the matches filter used should be returned as well"
            response.then().body("action-matches.conditions", hasSize(1))
            response.then().body("action-matches.conditions[0].dataType", equalTo("ManagedObject"))
            response.then().body("action-matches.conditions[0].properties", hasSize(1))
            response.then().body("action-matches.conditions[0].properties[0].name", equalTo("moType"))
            response.then().body("action-matches.conditions[0].properties[0].value", equalTo("MeContext"))
    }
}
