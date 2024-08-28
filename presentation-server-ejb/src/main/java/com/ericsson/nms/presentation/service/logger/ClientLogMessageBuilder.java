/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.logger;

import com.ericsson.nms.presentation.service.api.dto.LogRequest;
import org.apache.commons.lang3.StringUtils;

/**
 * Build the log message based on the provided info
 */
public class ClientLogMessageBuilder {

    private static final String NEW_LINE = "\n";

    private LogRequest logRequest;

    private String userId;

    private String application;

    private ClientLogMessageBuilder() {
    }

    /**
     * @return client log message builder
     */
    public static ClientLogMessageBuilder get() {
        return new ClientLogMessageBuilder();
    }

    /**
     * @param request {LogRequest}
     * @return request
     */
    public ClientLogMessageBuilder withLog(final LogRequest request) {
        this.logRequest = request;
        return this;
    }

    /**
     * @param user {String}
     * @return user
     */
    public ClientLogMessageBuilder withUser(final String user) {
        this.userId = user;
        return this;
    }

    /**
     * @param application {String}
     * @return application
     */
    public ClientLogMessageBuilder withApplication(final String application) {
        this.application = application;
        return this;
    }

    /**
     * @return logRequest message
     */
    public String build() {

        if (logRequest == null) {
            throw new IllegalArgumentException("No Log information was found");
        }

        final StringBuilder builder = new StringBuilder();

        builder.append("Name: ").append(logRequest.getName()).append(NEW_LINE);
        builder.append("Message: ").append(StringUtils.abbreviate(logRequest.getMessage(), 200)).append(NEW_LINE);
        builder.append("Log Time: ").append(logRequest.getLogTime()).append(NEW_LINE);
        builder.append("Application: ").append(application).append(NEW_LINE);
        builder.append("User: ").append(userId).append(NEW_LINE);
        builder.append("Url: ").append(logRequest.getUrl()).append(NEW_LINE);
        builder.append("Browser: ").append(logRequest.getBrowser()).append(NEW_LINE);
        builder.append("Stacktrace: ").append(StringUtils.abbreviate(logRequest.getStacktrace(), 2000));


        return builder.toString();
    }

}

