/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.rest.exceptions;

import com.ericsson.nms.presentation.service.rest.response.ServerMessage;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Created by ecrcont on 30/11/2015.
 */
public class SecurityExceptionHandler implements ExceptionMapper<SecurityException> {

    @Inject
    Logger logger;


    @Override
    public Response toResponse(final SecurityException e) {
        logger.error(e.getMessage(), e);
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(new ServerMessage(e.getMessage()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
