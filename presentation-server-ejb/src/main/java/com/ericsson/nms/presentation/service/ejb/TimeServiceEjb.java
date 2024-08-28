/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.ejb;

import static com.ericsson.nms.presentation.service.instrumentation.InstrumentableAction.SYSTEM_TIME_HITS;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.inject.Inject;
import javax.interceptor.Interceptors;

import com.ericsson.nms.presentation.service.api.dto.ServerDateTimeMessage;
import com.ericsson.nms.presentation.service.instrumentation.MetricUtil;
import com.ericsson.nms.presentation.service.interceptors.MethodCallTimerInterceptor;
import com.ericsson.nms.presentation.service.util.DateTimeUtil;

@Interceptors({MethodCallTimerInterceptor.class})
public class TimeServiceEjb implements TimeService {

    @Inject
    private DateTimeUtil dateTimeUtil;

    @Inject
    private MetricUtil metricUtil;

    @Override
    public ServerDateTimeMessage now() {
        metricUtil.count(SYSTEM_TIME_HITS);
        final TimeZone tz = dateTimeUtil.getDefaultTimeZone();
        final Calendar cal = dateTimeUtil.getCalendar();
        final Date date = cal.getTime();
        final long millis = date.getTime();

        // convert offset from milliseconds to hours and percentage minutes
        final float utcOffset = ((float) tz.getOffset(millis)) / 1000 / 60 / 60;

        final ServerDateTimeMessage serverDateTimeMessage = new ServerDateTimeMessage();
        serverDateTimeMessage.setTimestamp(millis);
        serverDateTimeMessage.setUtcOffset(utcOffset);
        final boolean inDaylightTimeNow = tz.observesDaylightTime() && tz.inDaylightTime(date);
        serverDateTimeMessage.setTimezone(tz.getDisplayName(inDaylightTimeNow, TimeZone.SHORT));
        serverDateTimeMessage.setServerLocation(tz.getID());

        return serverDateTimeMessage;
    }
}
