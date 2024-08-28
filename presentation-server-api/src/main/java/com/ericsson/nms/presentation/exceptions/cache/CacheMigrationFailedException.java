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
package com.ericsson.nms.presentation.exceptions.cache;

import com.ericsson.nms.presentation.exceptions.error_code.ErrorCode;
import com.ericsson.nms.presentation.exceptions.service.ServiceException;

/**
 * Exception raised when there's a failure migrating an entry from the Infinispan cache to the database
 */
public class CacheMigrationFailedException extends ServiceException {

    public CacheMigrationFailedException(final String message) {
        super(message, ErrorCode.CACHE_MIGRATION_FAILED);
    }
}
