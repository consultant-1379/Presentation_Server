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
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Created by ejgemro on 11/24/15.
 */
public class GenericExceptionHandler implements ExceptionMapper<Exception> {

    @Inject
    Logger logger;

    @Override
    public Response toResponse(final Exception exception) {

        logger.error("Caught unexpected exception ({}) on the REST layer!", exception.getClass().getName());
        logger.error(exception.getMessage(), exception);
        int indexOfcve = ExceptionUtils.indexOfThrowable(exception, ConstraintViolationException.class);
        if (indexOfcve > -1) {
            ConstraintViolationException cve = (ConstraintViolationException) ExceptionUtils.getThrowables(exception)[indexOfcve];
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ServerMessage(cve.getMessage())).build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ServerMessage(exception.getMessage())).build();
    }
}
