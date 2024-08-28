/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.presentation.exceptions.database;

import com.ericsson.nms.presentation.exceptions.error_code.ErrorCode;

/**
 * Exception raised when the database is unavailable.
 */
public class DatabaseUnavailabilityException extends DatabaseException {

    public DatabaseUnavailabilityException() {
        super();
        errorCode = ErrorCode.DATABASE_NOT_AVAILABLE;
    }

    public DatabaseUnavailabilityException(final Throwable cause) {
        super(cause);
        errorCode = ErrorCode.DATABASE_NOT_AVAILABLE;
    }

    public DatabaseUnavailabilityException(final String message) {
        super(message);
        errorCode = ErrorCode.DATABASE_NOT_AVAILABLE;
    }

    public DatabaseUnavailabilityException(final String message, final Throwable cause) {
        super(message, cause);
        errorCode = ErrorCode.DATABASE_NOT_AVAILABLE;
    }
}
