package functional

import base.AbstractBaseSpec
import com.jayway.restassured.response.Header

import java.util.concurrent.Callable
import java.util.concurrent.Executors

import static com.jayway.restassured.config.RedirectConfig.redirectConfig
import static com.jayway.restassured.config.RestAssuredConfig.newConfig

import static org.hamcrest.Matchers.*
import static spock.util.matcher.HamcrestSupport.that


/**
 * Test specification for Applications and Groups API
 */
class ApplicationAndGroupsApiSpec extends AbstractBaseSpec {

    def expectedIds = ["networkexplorercollections", "networkexplorer", "alex", "my-app-02", "my-app-03", "my-app-external", "my-app-01"]

    def "get application (version 1)"() {

        when: "execute a GET call on /rest/apps"
            def response = defaultRequest()
                    .with().get("/rest/apps")

        then: "status code should be 200 (Ok)"
            response.then().statusCode(200)

        and: "assert that all expected ids are returned"
            (response.path("id") as Set).size() == expectedIds.size()
            (response.path("id") as Set).containsAll(expectedIds)
    }

    def "get application (version 2)"() {

        when: "execute a GET call on /rest/apps with the version 2 header"
            def response = defaultRequest()
                .header(new Header("Accept","application/json;version=2.0.0"))
                .with().get("/rest/apps")

        then: "status code should be 200 (Ok)"
            response.then().statusCode(200)

        and: "assert that all expected ids are returned"
            (response.path("id") as Set).size() == expectedIds.size()
            (response.path("id") as Set).containsAll(expectedIds)
    }

    def "launch web application"() {

        when: "execute a GET call on /rest/apps"
            def response = defaultRequest()
                .config(newConfig().redirect(redirectConfig().followRedirects(false)))
                .with().get("/rest/apps/web/my-app-01")

        then: "status code should be 301 (Redirect)"
            response.then().statusCode(301)

    }

    def "get groups"() {
        given:
            def expectedIds = ["Documentation", "group-01", "group-02", "Monitoring", "Performance_and_Optimization", "Provisioning", "Security", "System"]

        when: "execute a GET call on /rest/groups"
            def response = defaultRequest()
                    .with().get("/rest/groups")

        then: "status code should be 200 (Ok)"
            response.then().statusCode(200)

        and: "assert that all expected ids are returned"
            (response.path("id") as Set).size() == expectedIds.size()
            (response.path("id") as Set).containsAll(expectedIds)
    }

    def "get localized application"() {
        given:
            def localizedTexts = [name: "Minha Aplicação 01", shortInfo: "Esta é uma breve descrição da minha aplicação", acronym: "AP01"]

        when: "execute a GET call on /rest/apps"
            def response = defaultRequest()
                .header(new Header("Accept-Language","fr,it,pt-br;q=0.5"))
                .with().get("/rest/apps")

        then: "status code should be 200 (Ok)"
            response.then().statusCode(200)

        and: "assert that the texts for my-app-01 are localized to pt-br"
            (response.path("name") as Set).contains(localizedTexts.name)
            (response.path("shortInfo") as Set).contains(localizedTexts.shortInfo)
            (response.path("acronym") as Set).contains(localizedTexts.acronym)
    }

    def "concurrently getting groups should return consistent results"() {

        given:
            def executors = Executors.newFixedThreadPool(3)

        when: "execute GET calls on /rest/groups"
            def futures = (1..50).collect {
                def user = (it % 2 ==0 ? "administrator" : "user_with_no_role")
                executors.submit(new GetGroupCall(user))
            }

        and:
            def results = futures.collect {
                if (!it.done) {
                    sleep(100)
                }
                return it.get()
            }

        then: "all status codes should be 200 (Ok)"
            that results*.statusCode, everyItem(equalTo(200))

        and: "if user is 'administrator' \"Network Explorer\" should be visible"
            that results.findAll{ it.user == "administrator" }*.apps, everyItem(hasItem("Network Explorer"))

        and: "if user is 'user_with_no_role' \"Network Explorer\" should not be visible"
            that results.findAll{ it.user == "user_with_no_role" }*.apps, everyItem(not(hasItem("Network Explorer")))

    }

    class GetGroupCall implements Callable<Map> {

        String user

        GetGroupCall(String user) {
            this.user = user
        }

        @Override
        Map call() throws Exception {
            def response = defaultRequest(user)
                    .with().get("/rest/groups")

            def statusCode = response.statusCode()
            [thread: Thread.currentThread().name, statusCode: statusCode,
             apps: (statusCode == 200) ? response.then().extract().jsonPath().get("apps.name").flatten() : [],
             user: user]
        }
    }

}
