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
 * Excdeption raised when a validation fails iin one of the entities.
 */
public class ValidationException extends ServiceException {

    private static final ErrorCode DEFAULT_ERROR_CODE = ErrorCode.VALIDATION_CONSTRAINT_FAILED;

    public ValidationException(final String message) {
        super(message, DEFAULT_ERROR_CODE);
    }
}
