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

import com.ericsson.nms.presentation.exceptions.service.ServiceException;

/**
 * Parent exception used to group all database related exceptions.
 */
public class DatabaseException extends ServiceException {

    public DatabaseException() {
        super();
    }

    public DatabaseException(final Throwable cause) {
        super(cause);
    }

    public DatabaseException(final String message) {
        super(message);
    }

    public DatabaseException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
