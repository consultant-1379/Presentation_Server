/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.presentation.service.tests.persistence

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.presentation.service.database.availability.DBAvailabilityMonitor
import com.ericsson.nms.presentation.service.database.availability.DatabaseStatus
import com.ericsson.nms.presentation.service.database.availability.metrics.counters.DatabaseAvailabilityEventTypes
import com.ericsson.nms.presentation.service.tests.base.AbstractPresentationServerSpec
import com.ericsson.oss.itpf.sdk.recording.EventLevel
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder

import javax.inject.Inject

class DatabaseUnavailabilityMonitorSpec extends AbstractPresentationServerSpec {

    @ObjectUnderTest
    DBAvailabilityMonitor monitor

    @Inject
    DatabaseStatus status

    @MockedImplementation
    SystemRecorder systemRecorder

    def "when database is unavailable an event should be raised"() {

        given: "Database is available"
            status.setAsAvailable()

        when: "trigger unavailability"
            monitor.setAsUnavailable()

        then: "Event should be reported"
            1 * systemRecorder.recordEvent(_,_,_,_,_) >> { String eventType, EventLevel eventLevel,
                                                           String source, String resource, String additionalInformation ->

                assert eventType == DatabaseAvailabilityEventTypes.DB_UNAVAILABLE
                assert eventLevel == EventLevel.DETAILED
                assert source == "postgres"
                assert resource == null
                assert additionalInformation == "Database unavailable: starting the unavailability time counting"
            }
    }

}
