/*******************************************************************************
 * COPYRIGHT Ericsson 2019
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.rest.exceptions;

import com.ericsson.nms.presentation.exceptions.ServiceRemovedException;
import com.ericsson.nms.presentation.service.rest.response.ServerMessage;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class ServiceRemovedExceptionHandler implements ExceptionMapper<ServiceRemovedException> {

    @Inject
    Logger logger;

    @Override
    public Response toResponse(ServiceRemovedException e) {
        logger.debug("Caught exception ({}) on the REST layer!", e.getClass().getName());
        return Response.status(Response.Status.GONE)
                .entity(new ServerMessage(e.getMessage()))
                .build();
    }
}
