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

import com.ericsson.nms.presentation.exceptions.PresentationServerException;
import com.ericsson.nms.presentation.service.rest.response.ServerMessage;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Created by ejgemro on 11/24/15.
 */
public class PresentationServerExceptionHandler implements ExceptionMapper<PresentationServerException> {

    @Inject
    Logger logger;

    @Override
    public Response toResponse(final PresentationServerException exception) {
        logger.debug("Caught exception ({}) on the REST layer!", exception.getClass().getName());
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ServerMessage(exception.getMessage())).build();
    }
}
