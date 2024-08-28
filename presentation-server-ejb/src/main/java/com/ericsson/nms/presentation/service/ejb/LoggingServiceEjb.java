/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.ejb;

import java.util.Collection;

import javax.inject.Inject;
import javax.interceptor.Interceptors;

import com.ericsson.nms.presentation.service.interceptors.MethodCallTimerInterceptor;
import org.slf4j.Logger;

import com.ericsson.nms.presentation.service.api.dto.LogRequest;
import com.ericsson.nms.presentation.service.instrumentation.InstrumentableAction;
import com.ericsson.nms.presentation.service.instrumentation.MetricUtil;
import com.ericsson.nms.presentation.service.logger.ClientLogger;
import com.ericsson.nms.presentation.service.logger.ClientLogMessageBuilder;

@Interceptors({MethodCallTimerInterceptor.class})
public class LoggingServiceEjb implements LoggingService {

    public static final Integer MAXIMUM_LOG_BATCH_SIZE = 10;

    private static final String EXCEEDED_MAX_LOG_BATCH_SIZE = "User: %s attempted to submit %d logs in a single request for application: %s." +
            " Maximum permitted number of logs per request is: " + MAXIMUM_LOG_BATCH_SIZE;
    private static final String MISSING_LOG_SEVERITY = "User: %s attempted to submit log for application: %s without a severity property.";
    private static final String UNSUPPORTED_SEVERITY = "User: %s attempted to submit log for application: %s with unsupported severity: %s.";

    @Inject
    @ClientLogger
    private Logger clientLogger;

    @Inject
    private MetricUtil metricUtil;

    /**
     * Log a service client's message.
     * @param request batch of LogRequest objects to process
     * @param userId requesting user
     * @param applicationId requesting application id
     */
    public void log(final Collection<LogRequest> request, final String userId, final String applicationId) {
        if(request != null) {
            if (request.size() > MAXIMUM_LOG_BATCH_SIZE) {
                final String errorMsg = String.format(EXCEEDED_MAX_LOG_BATCH_SIZE, userId, request.size(), applicationId);
                throw new IllegalArgumentException(errorMsg);
            }
            for (final LogRequest log : request) {
                final String message = ClientLogMessageBuilder.get()
                        .withLog(log)
                        .withApplication(applicationId)
                        .withUser(userId)
                        .build();
                if (log.getSeverity() == null) {
                    final String errorMsg = String.format(MISSING_LOG_SEVERITY, userId, applicationId);
                    throw new IllegalArgumentException(errorMsg);
                }
                switch (log.getSeverity()) {
                    case ERROR:
                        clientLogger.error(message);
                        metricUtil.count(InstrumentableAction.LOGS_CREATED);
                        break;
                    default:
                        final String errorMsg = String.format(UNSUPPORTED_SEVERITY, userId, applicationId, log.getSeverity());
                        throw new IllegalArgumentException(errorMsg);
                }
            }
        }
    }
}
