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
package com.ericsson.nms.presentation.service.database.availability;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>Bean that represents the DB status.</p>
 *
 * <p>Is thread-safe and always returns whether the status write changed the availability</p>
 */
@ApplicationScoped
public class DatabaseStatus {
    private final AtomicBoolean available;

    public DatabaseStatus() {
        available = new AtomicBoolean(false);
    }

    /**
     * Sets the database status to the 'available' state
     * @return the availability change -- was it changed during this operation?
     */
    public Availability setAsAvailable() {
        boolean wasUnavailable = available.compareAndSet(false, true);
        return wasUnavailable ? Availability.WAS_CHANGED : Availability.WAS_NOT_CHANGED;
    }

    /**
     * Sets the database status to the 'unavailable' state
     * @return the availability change -- was it changed during this operation?
     */
    public Availability setAsUnavailable() {
        boolean wasAvailable = available.compareAndSet(true, false);
        return wasAvailable ? Availability.WAS_CHANGED : Availability.WAS_NOT_CHANGED;
    }

    public boolean getAvailable() {
        return available.get();
    }

    /**
     * Whether the database availability was changed
     */
    public enum Availability {
        WAS_CHANGED,
        WAS_NOT_CHANGED
    }
}
