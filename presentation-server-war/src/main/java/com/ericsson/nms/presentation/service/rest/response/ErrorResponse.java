/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.rest.response;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Error response.
 */
public class ErrorResponse implements Serializable {

    private String userMessage;

    private String developerMessage;

    private Integer httpStatusCode;

    private String internalErrorCode;

    private String time;

    public ErrorResponse(){}

    public ErrorResponse(final String message, final Integer httpStatusCode) {

        this.userMessage = message;
        this.developerMessage = message;
        this.httpStatusCode = httpStatusCode;
        this.time = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date());
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(final String userMessage) {
        this.userMessage = userMessage;
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(final Integer httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public String getInternalErrorCode() {
        return internalErrorCode;
    }

    public void setInternalErrorCode(final String internalErrorCode) {
        this.internalErrorCode = internalErrorCode;
    }

    public String getTime() {
        return time;
    }

    public void setTime(final String time) {
        this.time = time;
    }

    public String getDeveloperMessage() {
        return developerMessage;
    }

    public void setDeveloperMessage(final String developerMessage) {
        this.developerMessage = developerMessage;
    }
}
