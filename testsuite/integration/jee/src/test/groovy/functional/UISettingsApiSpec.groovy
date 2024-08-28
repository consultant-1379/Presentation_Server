package functional

import base.AbstractBaseSpec

import static org.hamcrest.Matchers.*
import static org.hamcrest.core.IsCollectionContaining.hasItem

/**
 * Test specification for UI settings.
 * This class is responsible for testing /rest/ui/settings/ rest-endpoint.
 */
class UISettingsApiSpec extends AbstractBaseSpec {

    def "publish single UI Setting from a not existing user"() {

        given: "sample JSON log payload"
            def json = """{"id":"myKey","value":"myValue"}"""

        and: "using the default user request"
            def request = defaultRequest("")

        and: "use the provided JSON as POST body"
            request.body(json)

        when: "execute a PUT call on /rest/ui/settings/myApp/key01"
            def response = request.with().put("/rest/ui/settings/myApp/key01")

        then: "the status code should be equal to 401 (Not authorised)"
            response.then().statusCode(401)
                .log().all()
    }

    def "publish single UI Setting"() {

        given: "sample JSON log payload"
            def json = """{"id":"myKey","value":"myValue"}"""

        and: "using the default user request"
            def request = defaultRequest()

        and: "use the provided JSON as POST body"
            request.body(json)

        when: "execute a PUT call on /rest/ui/settings/myApp/key01"
            def response = request.with().put("/rest/ui/settings/myApp/key01")

        then: "the status code should be equal to 204 (No Content)"
            response.then().statusCode(204)
                    .log().all()
    }

    def "Publish multiple UI Settings in a single request"() {

        def uiSettingsJson = """[
                {"id":"AAAA","value":"true"},
                {"id":"BBBB","value":"true"},
                {"id":"CCCC","value":"true"},
                {"id":"DDDD","value":"true"},
                {"id":"EEEE","value":""}]"""

        given: "the default user request"
            def request = defaultRequest()
            request.body(uiSettingsJson)

        when: "publish the list of settings"
            addUiSettings("netex1","favorites", uiSettingsJson)

        and: "execute a GET call on /rest/ui/settings/netex1/favorites"
            def response = request.with().get("/rest/ui/settings/netex1/favorites")
            response.then().log().all()

        then: "should get a 200 response"
            response.then().statusCode(200)

        and: "the response should match the uiSettings and exclude any empty settings"
            response.then().body("id", hasSize(4))
            response.then().body("id", hasItems("AAAA","BBBB","CCCC", "DDDD"))
            response.then().body("id", not(hasItem("EEEE")))
            response.then().body("value", everyItem(equalTo("true")))
    }

    def "Publish multiple UI Settings in a single request from a not existing user"() {

        def uiSettingsJson = """[
                {"id":"AAAA","value":"true"},
                {"id":"BBBB","value":"true"},
                {"id":"CCCC","value":"true"},
                {"id":"DDDD","value":"true"},
                {"id":"EEEE","value":""}]"""

        given: "the default user request"
            def request = defaultRequest("")
            request.body(uiSettingsJson)

        when: "publish the list of settings"
            addUiSettings("netex1", "favorites", uiSettingsJson)

        and: "execute a GET call on /rest/ui/settings/netex1/favorites"
            def response = request.with().get("/rest/ui/settings/netex1/favorites")
            response.then().log().all()

        then: "should get a 401 response"
            response.then().statusCode(401)
    }

    def "get UI Setting"() {

        given: "the default user request"
            def request = defaultRequest()

        when: "add a sample setting"
            addUiSetting("myApp","key02","myKey","myValue")

        and: "execute a GET call on /rest/ui/settings/myApp/key02"
            def response = request.with().get("/rest/ui/settings/myApp/key02")
            response.then().log().all()

        then: "the status code should be equal to 200 (Ok)"
            response.then().statusCode(200)

        and: "assert the body response"
            response.then().body("id", hasItem("myKey"))
            response.then().body("value", hasItem("myValue"))
    }

    def "get UI Setting from a non-existing user"() {

        given: "the non-existing user request"
            def request = defaultRequest("")

        when: "add a sample setting"
            addUiSetting("myApp", "key02", "myKey", "myValue")

        and: "execute a GET call on /rest/ui/settings/myApp/key02"
            def response = request.with().get("/rest/ui/settings/myApp/key02")
            response.then().log().all()

        then: "the status code should be equal to 401 (Not Authorized)"
            response.then().statusCode(401)
    }

    def "get non-existing UI Setting from existing app"() {

        given: "the default user request"
            def request = defaultRequest()

        when: "add a sample setting"
            addUiSetting("myApp","key02","myKey","myValue")

        and: "execute a GET call on /rest/ui/settings/myApp/keyThatDoesNotExist"
            def response = request.with().get("/rest/ui/settings/myApp/keyThatDoesNotExist")
            response.then().log().all()

        then: "the status code should be equal to 200 (Ok)"
            response.then().statusCode(200)

        and: "assert the body response"
            response.then().body("", hasSize(0))
    }

    def "get non-existing UI Setting from non-existing app"() {

        given: "the default user request"
            def request = defaultRequest()

        when: "add a sample setting"
            addUiSetting("myApp","key02","myKey","myValue")

        and: "execute a GET call on /rest/ui/settings/myAppThatDoesNotExist/key05"
            def response = request.with().get("/rest/ui/settings/myAppThatDoesNotExist/key05")
            response.then().log().all()

        then: "the status code should be equal to 200 (Ok)"
            response.then().statusCode(200)

        and: "assert the body response"
            response.then().body("", hasSize(0))
    }

    def "Update multiple UI Settings in a single request"() {

        given: "the default user request and two sets of uiSettings"

            //Starting in non-alphabetical order
            def uiSettingsJson = """[
                {"id":"key03","value":"true"},
                {"id":"key01","value":"true"},
                {"id":"key02","value":"true"},
                {"id":"key04","value":"true"}]"""

            //Updated in a different order
            def uiSettingsJsonUpdated = """[
                {"id":"key01","value":"false"},
                {"id":"key04","value":"true"},
                {"id":"key03","value":"true03"},
                {"id":"key02","value":""}]"""

            def request = defaultRequest()
            request.body(uiSettingsJson)

        when: "publish and then update the list of settings"
            addUiSettings("myAppUpdate","favorites", uiSettingsJson)
            addUiSettings("myAppUpdate","favorites", uiSettingsJsonUpdated)

        and: "execute a GET call on /rest/ui/settings/myAppUpdate/favorites"
            def response = request.with().get("/rest/ui/settings/myAppUpdate/favorites")
            response.then().log().all()

        then: "should get a 200 response"
            response.then().statusCode(200)

        and: "the response should match the uiSettings and exclude any empty settings"
            response.then().body("id", hasSize(3))
            response.then().body("id", hasItems("key03","key01","key04"))

            response.then().body("value", hasItems("true","false","true03"))
    }

    def "delete the last UI Setting in group"() {

        given: "sample JSON log payload for create and delete"
            def createJson = """{"id":"myKey","value":"myValue"}"""
            def deleteJson = """{"id":"myKey"}"""

        and: "the default user request for create and delete"
            def createRequest = defaultRequest()
            def deleteRequest = defaultRequest()

        and: "use the provided JSON as DELETE and PUT body"
            createRequest.body(createJson)
            deleteRequest.body(deleteJson)

        when: "execute a PUT call on /rest/ui/settings/myAppForDeleteLastUISetting/key03 to create a setting"
            def createResponse = createRequest.with().put("/rest/ui/settings/myAppForDeleteLastUISetting/key03")

        and: "execute a DELETE call on /rest/ui/settings/myAppForDeleteLastUISetting/key03"
            def deleteResponse = deleteRequest.with().delete("/rest/ui/settings/myAppForDeleteLastUISetting/key03")

        then: "the status code should be equal to 204 (No Content)"
            deleteResponse.then().statusCode(204)
                .log().all()
    }

    def "delete UI Setting from group that has other settings"() {

        given: "sample JSON log payload for create and delete"
            def createJson = """{"id":"myKey","value":"myValue"}"""
            def createJson2 = """{"id":"myKey2","value":"myValue2"}"""
            def deleteJson = """{"id":"myKey"}"""

        and: "the default user request for create and delete"
            def createRequest = defaultRequest()
            def createRequest2 = defaultRequest()
            def deleteRequest = defaultRequest()

        and: "use the provided JSON as DELETE and PUT body"
            createRequest.body(createJson)
            createRequest2.body(createJson2)
            deleteRequest.body(deleteJson)

        when: "execute a PUT call on /rest/ui/settings/myApp/key03 to create a setting"
            createRequest.with().put("/rest/ui/settings/myApp/key03")
            createRequest2.with().put("/rest/ui/settings/myApp/key03")

        and: "execute a DELETE call on /rest/ui/settings/myApp/key03"
            def deleteResponse = deleteRequest.with().delete("/rest/ui/settings/myApp/key03")

        then: "the status code should be equal to 204 (No Content)"
            deleteResponse.then().statusCode(204)
                    .log().all()
    }

    def "delete UI Setting with a non-existing user"() {

        given: "sample JSON log payload for delete"
            def deleteJson = """{"id":"myKey"}"""

        and: "the non-existent user request for delete"
            def deleteRequest = defaultRequest("")

        and: "use the provided JSON as DELETE"
            deleteRequest.body(deleteJson)

        when: "execute a DELETE call on /rest/ui/settings/myApp/key03"
            def deleteResponse = deleteRequest.with().delete("/rest/ui/settings/myApp/key03")

        then: "the status code should be equal to 401 (Not authorized)"
            deleteResponse.then().statusCode(401)
                .log().all()
    }

    def "delete UI Setting with invalid id"() {

        given: "sample JSON log payload for create and delete with invalid ID"
            def createJson = """{"id":"myKey","value":"myValue"}"""
            def deleteJson = """{"id":"invalid"}"""

        and: "the default user request for create and delete"
            def createRequest = defaultRequest()
            def deleteRequest = defaultRequest()

        and: "use the provided JSON as DELETE and PUT body"
            createRequest.body(createJson)
            deleteRequest.body(deleteJson)

        when: "execute a PUT call on /rest/ui/settings/myApp/key04 to create a setting"
            def createResponse = createRequest.with().put("/rest/ui/settings/myApp/key04")

        and: "execute a DELETE call on /rest/ui/settings/myApp/key04"
            def deleteResponse = deleteRequest.with().delete("/rest/ui/settings/myApp/key04")

        then: "the status code should be equal to 404 (Not Found)"
            deleteResponse.then().statusCode(404)
                    .log().all()
    }

    def "delete UI Setting with internal error"() {

        given: "sample JSON log payload for create and delete with invalid request"
            def createJson = """{"id":"myKey","value":"myValue"}"""
            def deleteJson = """{"invalid":"myKey"}"""

        and: "the default user request for create and delete"
            def createRequest = defaultRequest()
            def deleteRequest = defaultRequest()

        and: "use the provided JSON as DELETE and PUT body"
            createRequest.body(createJson)
            deleteRequest.body(deleteJson)

        when: "execute a PUT call on /rest/ui/settings/myApp/key05 to create a setting"
            def createResponse = createRequest.with().put("/rest/ui/settings/myApp/key05")

        and: "execute a DELETE call on /rest/ui/settings/myApp/key05"
            def deleteResponse = deleteRequest.with().delete("/rest/ui/settings/myApp/key05")

        then: "the status code should be equal to 500 (Internal Error)"
            deleteResponse.then().statusCode(500)
                    .log().all()
    }
}
