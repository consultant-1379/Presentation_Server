/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.instrumentation;

import static com.ericsson.nms.presentation.service.instrumentation.InstrumentableAction.ACTIONS_MATCHED;

import com.ericsson.oss.itpf.sdk.instrument.annotation.InstrumentedBean;
import com.ericsson.oss.itpf.sdk.instrument.annotation.MonitoredAttribute;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Instrumentation Bean used to gather statistics from Actions Service.
 */
@InstrumentedBean(description = "Action Service", displayName = "ActionService")
@ApplicationScoped
public class ActionServiceInstrumentationBean {

    @Inject
    private MetricUtil metricUtil;

    @MonitoredAttribute(displayName = "countActionsMatched", visibility = MonitoredAttribute.Visibility.ALL,
            units = MonitoredAttribute.Units.NONE, category = MonitoredAttribute.Category.THROUGHPUT,
            interval = MonitoredAttribute.Interval.FIVE_MIN, collectionType = MonitoredAttribute.CollectionType.DYNAMIC)
    public long getCountActionsMatched() {
        return metricUtil.getTotal(ACTIONS_MATCHED);
    }

    @MonitoredAttribute(displayName = "averageActionsMatched", visibility = MonitoredAttribute.Visibility.ALL,
            units = MonitoredAttribute.Units.NONE, category = MonitoredAttribute.Category.THROUGHPUT,
            interval = MonitoredAttribute.Interval.FIVE_MIN, collectionType = MonitoredAttribute.CollectionType.DYNAMIC)
    public long getAverageActionsMatched() {
        return metricUtil.getMean(ACTIONS_MATCHED);
    }

}
