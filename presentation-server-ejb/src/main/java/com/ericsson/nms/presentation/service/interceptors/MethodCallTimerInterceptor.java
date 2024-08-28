/*******************************************************************************
 * COPYRIGHT Ericsson 2019
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/
package com.ericsson.nms.presentation.service.interceptors;

import com.ericsson.nms.presentation.service.logger.PerformanceLogger;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

public class MethodCallTimerInterceptor {

    private static final String METHOD_EXECUTION_TIME_THRESHHOLD_MS = "misMethodExecutionTimeThreshhold";
    private static final String METHOD_EXECUTION_TIME_THRESHHOLD_MS_DEFAULT = "5000";

    @Inject
    @PerformanceLogger
    private Logger performanceLogger;

    @AroundInvoke
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public Object timeMethodCall(final InvocationContext invocationContext) throws Exception {

        final long startTimeMs = System.currentTimeMillis();
        final Object objectReturned = invocationContext.proceed();
        final long timeTaken = System.currentTimeMillis() - startTimeMs;

        if (timeTaken > getCallTimeThresholdMs()) {
            final String className = invocationContext.getMethod().getDeclaringClass().getName();
            final String methodName = invocationContext.getMethod().getName();
            performanceLogger.warn("Method '{}.{}()' took {}ms to complete.", className, methodName, timeTaken);
        }

        return objectReturned;
    }

    private long getCallTimeThresholdMs() {
        return Long.parseLong(System.getProperty(METHOD_EXECUTION_TIME_THRESHHOLD_MS, METHOD_EXECUTION_TIME_THRESHHOLD_MS_DEFAULT));
    }
}
