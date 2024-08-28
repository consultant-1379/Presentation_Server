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
package com.ericsson.nms.presentation.service.tests.metrics

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.presentation.service.instrumentation.MetricUtil

import static com.ericsson.nms.presentation.service.instrumentation.InstrumentableAction.SETTINGS_SET_TIMES

class MetricsUtilSpec extends CdiSpecification {

    @ObjectUnderTest
    MetricUtil metricUtil

    def "cumulative metrics"() {

        when: "generate a set of metrics"
            (1..10).each {
                def timer = metricUtil.startTimer(SETTINGS_SET_TIMES)
                Thread.sleep(100)
                metricUtil.stopTimer(SETTINGS_SET_TIMES, timer);
            }

        and: "get the cumulative metric"
            def value = metricUtil.getTimerSum(SETTINGS_SET_TIMES)

        then: "the metric should have the sum of all timer durations"
            value >= 1000

        when: "trigger a few more hits"
            (1..5).each {
                def timer = metricUtil.startTimer(SETTINGS_SET_TIMES)
                Thread.sleep(100)
                metricUtil.stopTimer(SETTINGS_SET_TIMES, timer);
            }

        and: "get the cumulative metric"
            value = metricUtil.getTimerSum(SETTINGS_SET_TIMES)

        then: "the metric should have the sum of all timer durations"
            value >= 1500

        when: "getting the cumulative metric again with no updates should maintain the same value "
            value = metricUtil.getTimerSum(SETTINGS_SET_TIMES)

        then: "the metric should have the sum of all timer durations"
            value >= 1500
    }

}
