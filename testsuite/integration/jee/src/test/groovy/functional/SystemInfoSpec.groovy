package functional

import base.AbstractBaseSpec

import static com.jayway.restassured.RestAssured.given
import static org.hamcrest.Matchers.*

/**
 * Test specification for System Info.
 */
class SystemInfoSpec extends AbstractBaseSpec {

    def USERNAME = "pibUser"
    def PASSWORD = "pib12345\$"

    def "get hostname when modeled property was defined"() {

        when: "try to check if the host param already exists"
            def response = given()
                    .authentication().basic(USERNAME, PASSWORD)
                    .param("paramName", "enmHostName")
                    .get("/pib/configurationService/getConfigParameter")

            def exists = response.statusCode != 204

        and: "if not, create it"
            if (!exists) {
                 given()
                    .authentication().basic(USERNAME, PASSWORD)
                    .param("paramName", "enmHostName")
                    .param("paramValue", "enm-host.ericsson.com")
                    .param("paramType", "String")
                    .param("paramScopeType", "GLOBAL")
                    .request().log().ifValidationFails()
                    .get("/pib/configurationService/addConfigParameter")
            }

        and: "wait for 2 seconds to give time to process the config change event"
            Thread.sleep(2000)

        then: "the host name property should be found"
            defaultRequest()
                .log().ifValidationFails()
                .get("/rest/system/v1/name")
                .then()
                    .statusCode(200)
                    .body("name", equalTo("enm-host.ericsson.com"))
                    .log().ifValidationFails()

    }

    def "get all properties"() {

        when: "try to check if the host param already exists"
            def response = given()
                .authentication().basic(USERNAME, PASSWORD)
                .param("paramName", "enmHostName")
                .get("/pib/configurationService/getConfigParameter")

            def exists = response.statusCode != 204

        and: "if not, create it"
            if (!exists) {
                given()
                        .authentication().basic(USERNAME, PASSWORD)
                        .param("paramName", "enmHostName")
                        .param("paramValue", "enm-host.ericsson.com")
                        .param("paramType", "String")
                        .param("paramScopeType", "GLOBAL")
                        .request().log().ifValidationFails()
                        .get("/pib/configurationService/addConfigParameter")
            }
        and: "wait for 2 seconds to give time to process the config change event"
            Thread.sleep(2000)

        then: "the host name property should be found"
            defaultRequest()
                .log().ifValidationFails()
                .get("/rest/system/v1")
                .then()
                .statusCode(200)
                .body("name", equalTo("enm-host.ericsson.com"))
                .log().ifValidationFails()

    }

    def "get non existent property"() {

        when:
            def response = defaultRequest()
                    .log().ifValidationFails()
                    .get("/rest/system/v1/anyParameter")
        then:
            response.then().statusCode(404)

        and:
            response.then()
                .body("userMessage", equalTo("No Property was found with the given name: anyParameter"))
                .body("developerMessage", equalTo("No Property was found with the given name: anyParameter"))
                .body("httpStatusCode", equalTo(404))
                .body("time", allOf(notNullValue(), not(equalTo(""))))

    }

}
