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
package com.ericsson.nms.presentation.service.database.exceptions;

import com.ericsson.nms.presentation.exceptions.database.DatabaseException;
import com.ericsson.nms.presentation.exceptions.error_code.ErrorCode;

/**
 * Exception raised when there's a schema upgrade failure.
 */
public class SchemaUpgradeException extends DatabaseException {

    public SchemaUpgradeException(final String message, final Throwable cause) {
        super(message, cause);
        errorCode = ErrorCode.DATABASE_MIGRATION_FAILED;
    }
}
