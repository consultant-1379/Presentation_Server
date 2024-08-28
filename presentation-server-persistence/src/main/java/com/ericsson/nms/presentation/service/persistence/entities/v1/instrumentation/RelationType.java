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
package com.ericsson.nms.presentation.service.persistence.entities.v1.instrumentation;

/**
 * The types of relations supported by the database
 */
public enum RelationType {

    TABLE,
    MATERIALIZED_VIEW,
    INDEX,
    SEQUENCE,
    VIEW,
    TYPE,
    UNKNOWN

}
