package performance

import base.AbstractBaseSpec
import com.ericsson.nms.presentation.service.api.dto.ActionRuleCondition
import com.ericsson.nms.presentation.service.api.dto.Property
import com.jayway.restassured.http.ContentType
import com.jayway.restassured.response.Header

import static com.jayway.restassured.RestAssured.given

/**
 * Test specification for performance around ActionService.
 */
class ActionServicePerformanceSpec extends AbstractBaseSpec {

    def "get actions for large selection (action-matches)"() {

        given: "a selection with 1000 conditions against 100 rules"
            def matches = [application: "my-app-01", multipleSelection: false,
                   conditions: (1..1000).collect {
                       new ActionRuleCondition(dataType: "ManagedObject", properties:
                            [new Property(name: "moType", value: "MeContext")])
                   }
            ]

        when: "execute a POST to the endpoint"
            def currentTime = System.currentTimeMillis()
            def response = given().header(new Header("X-Tor-UserId", "administrator"))
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .log().ifValidationFails()
                    .body(matches)
                    .post("/rest/v1/apps/action-matches/")

            def responseTime = System.currentTimeMillis() - currentTime
            response.then().log().ifValidationFails()

        then: "status code should be 200 (Ok)"
            response.then().statusCode(200)

        and: "the response should take less than 6 seconds to finish"
            responseTime < 6000
    }

}
