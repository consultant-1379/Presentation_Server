/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.util;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Utility class to handle DateTime
 */
public class DateTimeUtil {

    /**
     * Get the default time zone.
     *
     * @return default time zone.
     */
    public TimeZone getDefaultTimeZone() {
        TimeZone.setDefault(null); // Do not use JVM cache
        return TimeZone.getDefault();
    }

    public Calendar getCalendar() {
        return Calendar.getInstance();
    }
}
