package com.ericsson.nms.presentation.service.tests.service

import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.presentation.service.ejb.TimeServiceEjb
import com.ericsson.nms.presentation.service.util.DateTimeUtil
import spock.lang.Unroll

class TimeServiceSpec extends CdiSpecification{

    @ObjectUnderTest
    private TimeServiceEjb timeServiceEjb

    @ImplementationInstance
    private DateTimeUtil dateTimeUtil = mock(DateTimeUtil)

    @Unroll
    def "Get system time with timezone of #area_location in standard time"() {

        def tz = TimeZone.getTimeZone(area_location)
        def cal = Calendar.getInstance(tz)
        def dateInStandardTime = new Date(2017, 12, 7)

        cal.setTime(dateInStandardTime)

        dateTimeUtil.getDefaultTimeZone() >> tz
        dateTimeUtil.getCalendar() >> cal

        given: 'a short timezone name'
            def tzDisplayName = tz.getDisplayName(tz.observesDaylightTime() && tz.inDaylightTime(dateInStandardTime), TimeZone.SHORT)

        when: 'we get the time'
            def response = timeServiceEjb.now()

        then: 'the response object matches the timezone and timestamp'
            response.timestamp == cal.getTimeInMillis()
            response.timezone == tzDisplayName
            tzDisplayName == short_name

        where:
            area_location      | short_name
            "Europe/Dublin"    | "GMT"
            "Europe/Zurich"    | "CET"
            "America/New_York" | "EST"
    }

    @Unroll
    def "Get system time with timezone of #area_location in daylight savings time"() {

        def tz = TimeZone.getTimeZone(area_location)
        def cal = Calendar.getInstance(tz)
        def dateInDaylightSavingsTime = new Date(2017, 4, 22)

        cal.setTime(dateInDaylightSavingsTime)

        dateTimeUtil.getDefaultTimeZone() >> tz
        dateTimeUtil.getCalendar() >> cal

        given: 'a short timezone name'
            def tzDisplayName = tz.getDisplayName(tz.observesDaylightTime() && tz.inDaylightTime(dateInDaylightSavingsTime), TimeZone.SHORT)

        when: 'we get the time'
            def response = timeServiceEjb.now()

        then: 'the response object matches the timezone and timestamp'
            response.timestamp == cal.getTimeInMillis()
            response.timezone == tzDisplayName
            tzDisplayName == short_name

        where:
            area_location      | short_name
            "Europe/Dublin"    | "IST"
            "Europe/Zurich"    | "CEST"
            "America/New_York" | "EDT"
    }

    def "Get system time when IllegalArgumentException is thrown"() {

        def tz = mock(TimeZone)
        def cal = Calendar.getInstance()

        dateTimeUtil.getDefaultTimeZone() >> tz
        dateTimeUtil.getCalendar() >> cal

        given: 'a faulty jvm / environment / etc.'
            tz.getOffset(_ as Long) >> { throw new IllegalArgumentException("My Message") }

        when: 'we get the time'
            timeServiceEjb.now()

        then: 'an IllegalArgumentException will be thrown'
            IllegalArgumentException exception = thrown()
            exception.message == "My Message"
    }

}
