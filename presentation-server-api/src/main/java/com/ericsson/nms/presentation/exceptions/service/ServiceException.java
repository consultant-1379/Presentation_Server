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

import com.ericsson.nms.presentation.exceptions.PresentationServerException;
import com.ericsson.nms.presentation.exceptions.error_code.ErrorCode;

/**
 * Base exception for service related exceptions
 */
public class ServiceException extends PresentationServerException {

    protected ErrorCode errorCode;

    public ServiceException() {
        errorCode = ErrorCode.UNKNOWN_ERROR;
    }

    public ServiceException(final Throwable cause) {
        super(cause);
        errorCode = ErrorCode.UNKNOWN_ERROR;
    }

    public ServiceException(final String message) {
        super(message);
        errorCode = ErrorCode.UNKNOWN_ERROR;
    }

    public ServiceException(final String message, final Throwable cause) {
        super(message, cause);
        errorCode = ErrorCode.UNKNOWN_ERROR;
    }

    public ServiceException(final String message, final ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ServiceException(final String message, final Throwable cause, final ErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * Gets the numeric error code for the exception
     * @return numeric error code
     */
    public Integer getNumericErrorCode() {
        return errorCode.getNumericErrorCode();
    }

    /**
     * Gets the suggested HTTP code to respond with if this exception will surface to the controller
     * @return a suggested HTTP error code
     */
    public Integer getSuggestedHttpErrorCode() {
        return errorCode.getNumericErrorCode();
    }
}
