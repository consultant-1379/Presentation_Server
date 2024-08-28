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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.Produces;

/**
 * Producer used to create CDI instances of the client side logger
 */
public class LoggerProducer {

    public static final String CLIENT_LOGGER_CATEGORY = "presentation-logger";
    public static final String PERFORMANCE_LOGGER_CATEGORY = "com.ericsson.oss.services.presentationServer.performance";
    public static final String SECURITY_LOGGER_CATEGORY = "presentation.security";

    /**
     * @return Logger
     */
    @Produces
    @ClientLogger
    public Logger produceClientLogger() {
        return LoggerFactory.getLogger(CLIENT_LOGGER_CATEGORY);
    }

    /**
     * Producer for security specific logger
     * @return logger
     */
    @Produces
    @PerformanceLogger
    public Logger producePerformanceLogger() {
        return LoggerFactory.getLogger(PERFORMANCE_LOGGER_CATEGORY);
    }

    /**
     * Producer for security specific logger
     * @return logger
     */
    @Produces
    @SecurityLogger
    public Logger produceSecurityLogger() {
        return LoggerFactory.getLogger(SECURITY_LOGGER_CATEGORY);
    }
}
