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
package com.ericsson.nms.presentation.service.database.availability.metrics.counters;

/**
 * Counters for DB availability metrics
 */
public class DatabaseAvailabilityMetricsCounters {
    /**
     * Time of the database unavailability
     */
    public static final String UNAVAILABILITY_TIME = "unavailabilityTime";

    /**
     * Requests when the DB was unavailable
     */
    public static final String REQUESTS_WHEN_DATABASE_UNAVAILABLE = "requestsWhenDatabaseUnavailable";

    /**
     * The use case, e.g. the DAO method called which was failed due to the DB unavailability.
     */
    public static final String USE_CASE = "useCase";

    private DatabaseAvailabilityMetricsCounters() {

    }
}
