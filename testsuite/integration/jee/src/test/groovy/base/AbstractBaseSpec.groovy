package base

import com.jayway.restassured.http.ContentType
import com.jayway.restassured.response.Header
import com.jayway.restassured.specification.RequestSpecification
import spock.lang.Specification

import static com.jayway.restassured.RestAssured.given
/**
 * Base test class containing the common configuration for rest-endpoint tests.
 */
abstract class AbstractBaseSpec extends Specification {

    protected final Header APPLICATION_HEADER = new Header("X-Tor-Application", "sample-app")

    RequestSpecification defaultRequest(String user = "administrator") {

        def request = given().header(new Header("X-Tor-UserId", user))
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)

        request.log().all()

        request
    }

    def addUiSetting(String app, String key, String id,  String value) {

        defaultRequest()
            .body("{\"id\":\"${id}\",\"value\":\"${value}\"}")
            .put("/rest/ui/settings/${app}/${key}")

    }

    def addUiSettings(String app, String key, String settingsJsonList) {

        defaultRequest()
            .body("${settingsJsonList}")
            .put("/rest/ui/settings/v2/${app}/${key}")
    }

}
