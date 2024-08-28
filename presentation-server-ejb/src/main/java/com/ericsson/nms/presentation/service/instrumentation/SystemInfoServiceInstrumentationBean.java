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

import static com.ericsson.nms.presentation.service.instrumentation.InstrumentableAction.SYSTEM_INFO_HITS;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.ericsson.oss.itpf.sdk.instrument.annotation.InstrumentedBean;
import com.ericsson.oss.itpf.sdk.instrument.annotation.MonitoredAttribute;

/**
 * Instrumentation Bean used to gather statistics from System Info.
 */
@InstrumentedBean(description = "System Info Service", displayName = "SystemInfoService")
@ApplicationScoped
public class SystemInfoServiceInstrumentationBean {

    @Inject
    private MetricUtil metricUtil;

    @MonitoredAttribute(displayName = "countSystemInfoRequests", visibility = MonitoredAttribute.Visibility.ALL,
            units = MonitoredAttribute.Units.NONE, category = MonitoredAttribute.Category.UTILIZATION,
            interval = MonitoredAttribute.Interval.FIVE_MIN, collectionType = MonitoredAttribute.CollectionType.DYNAMIC)
    public long getCountSystemInfoRequests() {
        return metricUtil.getCount(SYSTEM_INFO_HITS);
    }
}
