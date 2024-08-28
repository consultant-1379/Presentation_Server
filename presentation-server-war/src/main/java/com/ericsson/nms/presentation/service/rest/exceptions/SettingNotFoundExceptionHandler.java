/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.presentation.service.rest.exceptions;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;

import com.ericsson.nms.presentation.exceptions.SettingNotFoundException;
import com.ericsson.nms.presentation.service.rest.response.ServerMessage;

/**
 * Exception handler for SettingNotFoundException
 */
public class SettingNotFoundExceptionHandler implements ExceptionMapper<SettingNotFoundException> {

    @Inject
    Logger logger;

    @Override
    public Response toResponse(final SettingNotFoundException e) {
        logger.debug("Caught exception ({}) on the REST layer!", e.getClass().getName());
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ServerMessage(e.getMessage()))
                .build();
    }
}
