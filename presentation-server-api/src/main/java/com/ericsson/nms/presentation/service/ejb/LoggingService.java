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

import javax.ejb.Local;

import com.ericsson.nms.presentation.service.api.dto.LogRequest;

/**
 * Service Facade responsible to provide client logging
 */
@Local
public interface LoggingService {

    /**
     * Gets all actions that can be used by the user in the given application
     * @param requests batched logging requests
     * @param userId requesting user
     * @param applicationId requesting application id
     * @throws IllegalArgumentException when the log requests do not match the allowed restrictions
     * @throws SecurityException when the user does not have access to the source or targeted application
     */
    void log(final Collection<LogRequest> requests, String userId, String applicationId);

}
