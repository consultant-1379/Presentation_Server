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
 * The class for storing the event types for database availability DDP
 */
public class DatabaseAvailabilityEventTypes {
    /**
     * Database unavailability event (as recorded by availability monitor)
     */
    public static final String DB_UNAVAILABLE = "PRESENTATION_SERVER.DATABASE.UNAVAILABLE";

    /**
     * Database availability event (as recorded by availability monitor)
     */
    public static final String DB_AVAILABLE = "PRESENTATION_SERVER.DATABASE.AVAILABLE";

    /**
     * Database is requested when it's unavailable (either as per monitor or from exception analysis)
     */
    public static final String REQUESTED_WHEN_UNAVAILABLE = "PRESENTATION_SERVER.DATABASE.REQUESTED_WHEN_UNAVAILABLE";

    private DatabaseAvailabilityEventTypes() {
    }
}
