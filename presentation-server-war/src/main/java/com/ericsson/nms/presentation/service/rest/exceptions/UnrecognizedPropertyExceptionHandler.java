/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.presentation.service.rest.exceptions;

import com.ericsson.nms.presentation.service.rest.response.ErrorResponse;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class UnrecognizedPropertyExceptionHandler implements ExceptionMapper<UnrecognizedPropertyException> {
    @Override
    public Response toResponse(final UnrecognizedPropertyException exception) {

        final ErrorResponse errorResponse = new ErrorResponse(exception.getMessage(), Response.Status.NOT_FOUND.getStatusCode());

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errorResponse)
                .build();
    }
}
