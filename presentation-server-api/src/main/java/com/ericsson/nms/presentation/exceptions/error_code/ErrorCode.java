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
package com.ericsson.nms.presentation.exceptions.error_code;

/**
 * Error codes supported by Presentation Server
 */
public enum ErrorCode {

    UNKNOWN_ERROR(-1, 500),
    ENTITY_NOT_FOUND(12001, 404),
    VALIDATION_CONSTRAINT_FAILED(12002, 400),
    CACHE_MIGRATION_FAILED(12003, 500),
    DATABASE_NOT_AVAILABLE(12004, 503),
    DATABASE_MIGRATION_FAILED(12005, 500);

    ErrorCode(final Integer code, final Integer httpCode) {
        this.numericErrorCode = code;
        this.suggestedHttpCode = httpCode;
    }

    private Integer numericErrorCode;

    private Integer suggestedHttpCode;

    public Integer getNumericErrorCode() {
        return numericErrorCode;
    }

    public Integer getSuggestedHttpCode() {
        return suggestedHttpCode;
    }
}
