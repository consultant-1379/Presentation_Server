package com.ericsson.nms.presentation.service.tests.service

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.presentation.service.api.dto.LogRequest
import com.ericsson.nms.presentation.service.ejb.LoggingServiceEjb
import com.ericsson.nms.presentation.service.tests.base.AbstractScenario01Spec
import org.slf4j.Logger


/**
 * Test specification for the Logging Service
 */
class LoggingServiceSpec extends AbstractScenario01Spec {

    @ObjectUnderTest
    LoggingServiceEjb loggingServiceEjb

    def setup() {
        loggingServiceEjb.clientLogger = Mock(Logger)
    }

    def "Log valid error message"() {

        given: "using semantically correct test data"
            def log = new LogRequest(name: "Delete NE", message: "Delete operation was not completed",
                    browser: "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.130 Safari/537.36",
                    stacktrace: "ReferenceError: abc is not defined (http://localhost:8585/logger-example/regions/main/Main.js:29:6)",
                    url: "http://localhost:8585/#logger-example", logTime: new Date(), severity: LogRequest.LoggerSeverity.ERROR )

        when: "call the rest method"
            loggingServiceEjb.log([log], "user", "app")

        then: "the clientLogger will call the error() method"
            1* loggingServiceEjb.clientLogger.error(_ as String)
    }

    def "Attempt to log without providing severity"() {
        given: "using test data"
            def log = new LogRequest(name: "Delete NE", message: "Delete operation was not completed",
                    browser: "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.130 Safari/537.36",
                    stacktrace: "ReferenceError: abc is not defined (http://localhost:8585/logger-example/regions/main/Main.js:29:6)",
                    url: "http://localhost:8585/#logger-example", logTime: new Date())

        when: "call the log method"
            loggingServiceEjb.log([log], "user", "app")

        then: "an exception should be thrown"
            def exception = thrown(IllegalArgumentException)
            exception.message == "User: user attempted to submit log for application: app without a severity property."
    }

    def "Attempt to log more than 10 entries in one request"() {
        given: "using test data "
            def logs = (1..11).collect { new LogRequest()}

        when: "call the log method"
            loggingServiceEjb.log(logs, "user", "app")

        then: "an exception should be thrown"
            def exception = thrown(IllegalArgumentException)
            exception.message == "User: user attempted to submit 11 logs in a single request for application: app." +
                    " Maximum permitted number of logs per request is: 10"
    }

}
