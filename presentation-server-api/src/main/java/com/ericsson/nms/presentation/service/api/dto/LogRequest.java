/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.api.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Request DTO used to provide log entries to the REST API.
 */
public class LogRequest implements Serializable {

    private LoggerSeverity severity;

    private String message;

    private String name;

    private String browser;

    private String url;

    private String stacktrace;

    private Date logTime;

    public LoggerSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(final LoggerSeverity severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(final String browser) {
        this.browser = browser;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getStacktrace() {
        return stacktrace;
    }

    public void setStacktrace(final String stacktrace) {
        this.stacktrace = stacktrace;
    }

    public Date getLogTime() {
        return logTime;
    }

    public void setLogTime(final Date logTime) {
        this.logTime = logTime;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final LogRequest that = (LogRequest) o;

        if (severity != that.severity) {
            return false;
        }
        if (message != null ? !message.equals(that.message) : that.message != null) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if (browser != null ? !browser.equals(that.browser) : that.browser != null) {
            return false;
        }
        if (url != null ? !url.equals(that.url) : that.url != null) {
            return false;
        }
        if (stacktrace != null ? !stacktrace.equals(that.stacktrace) : that.stacktrace != null) {
            return false;
        }
        return logTime != null ? logTime.equals(that.logTime) : that.logTime == null;

    }

    @Override
    public int hashCode() {
        int result = severity != null ? severity.hashCode() : 0;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (browser != null ? browser.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (stacktrace != null ? stacktrace.hashCode() : 0);
        result = 31 * result + (logTime != null ? logTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LogRequest{" +
                "severity=" + severity +
                ", message='" + message + '\'' +
                ", name='" + name + '\'' +
                ", browser='" + browser + '\'' +
                ", url='" + url + '\'' +
                ", stacktrace='" + stacktrace + '\'' +
                ", logTime=" + logTime +
                '}';
    }

    /**
     * Logger severity
     */
    public enum LoggerSeverity {
        ERROR;
    }

}
