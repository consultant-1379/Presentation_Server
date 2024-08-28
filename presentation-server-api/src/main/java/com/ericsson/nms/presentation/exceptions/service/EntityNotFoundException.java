/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2019
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.presentation.exceptions.service;

import com.ericsson.nms.presentation.exceptions.error_code.ErrorCode;

/**
 * Base exception to be raised when an entity is not found on the database
 */
public class EntityNotFoundException extends ServiceException {

    private static final ErrorCode DEFAULT_ERROR_CODE = ErrorCode.ENTITY_NOT_FOUND;

    public EntityNotFoundException(final String entityName, final String id) {
        this(String.format("%s not found with id %s", entityName, id));
    }

    public EntityNotFoundException(final String message) {
        super(message, DEFAULT_ERROR_CODE);
    }

    public EntityNotFoundException(final Throwable cause) {
        super(cause);
        errorCode = DEFAULT_ERROR_CODE;
    }

    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
        errorCode = DEFAULT_ERROR_CODE;
    }

}
