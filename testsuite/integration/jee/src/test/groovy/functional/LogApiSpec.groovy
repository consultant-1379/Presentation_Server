package functional

import base.AbstractBaseSpec

/**
 * Test specificatioon for the Log API
 */
class LogApiSpec extends AbstractBaseSpec {

    def "client side logger"() {

        given: "sample JSON log paylog"
            def json = """
                [{
                   "severity":"ERROR",
                   "message":"Delete operation was not completed",
                   "name":"Delete NE",
                   "stacktrace":"ReferenceError: abc is not defined (http://localhost:8585/logger-example/regions/main/Main.js:29:6)",
                   "browser":"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.130 Safari/537.36",
                   "url":"http://localhost:8585/#logger-example",
                   "logTime":"2015-06-22T19:53:41.084Z"

                }]"""

        and: "specify application header"
            def request = defaultRequest().header(APPLICATION_HEADER)

        and: "use the provided JSON as POST body"
            request.body(json)

        when: "execute a POST call on /rest/service/log"
            def response = request.with().post("/rest/service/log")

        then: "the status code should be equal to 201 (Created)"
            response.then().statusCode(201)
                    .log().all()

    }

}
