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

import static com.ericsson.nms.presentation.service.instrumentation.InstrumentableAction.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.ericsson.oss.itpf.sdk.instrument.annotation.InstrumentedBean;
import com.ericsson.oss.itpf.sdk.instrument.annotation.MonitoredAttribute;

/**
 * Instrumentation Bean used to gather statistics from UI Settings.
 * Warning: names use bean magic i.e. countGetSettingsRequests => getCountGetSettingsRequests
 */
@InstrumentedBean(description = "UI Settings Service", displayName = "UISettingsService")
@ApplicationScoped
public class UISettingsServiceInstrumentationBean {

    @Inject
    private MetricUtil metricUtil;

    @MonitoredAttribute(displayName = "countGetSettingsRequests", visibility = MonitoredAttribute.Visibility.ALL,
            units = MonitoredAttribute.Units.NONE, category = MonitoredAttribute.Category.THROUGHPUT,
            interval = MonitoredAttribute.Interval.FIVE_MIN, collectionType = MonitoredAttribute.CollectionType.DYNAMIC)
    public long getCountGetSettingsRequests() {
        return metricUtil.getCount(SETTINGS_GET_HITS);
    }

    @MonitoredAttribute(displayName = "countSetSettingsRequests", visibility = MonitoredAttribute.Visibility.ALL,
            units = MonitoredAttribute.Units.NONE, category = MonitoredAttribute.Category.THROUGHPUT,
            interval = MonitoredAttribute.Interval.FIVE_MIN, collectionType = MonitoredAttribute.CollectionType.DYNAMIC)
    public long getCountSetSettingsRequests() {
        return metricUtil.getCount(SETTINGS_SET_HITS);
    }

    @MonitoredAttribute(displayName = "responseTimeGetSettingsRequests", visibility = MonitoredAttribute.Visibility.ALL,
            units = MonitoredAttribute.Units.NONE, category = MonitoredAttribute.Category.PERFORMANCE,
            interval = MonitoredAttribute.Interval.FIVE_MIN, collectionType = MonitoredAttribute.CollectionType.DYNAMIC)
    public long getResponseTimeGetSettingsRequests() {
        return metricUtil.getTimerSum(SETTINGS_GET_TIMES);
    }

    @MonitoredAttribute(displayName = "responseTimeSetSettingsRequests", visibility = MonitoredAttribute.Visibility.ALL,
            units = MonitoredAttribute.Units.NONE, category = MonitoredAttribute.Category.PERFORMANCE,
            interval = MonitoredAttribute.Interval.FIVE_MIN, collectionType = MonitoredAttribute.CollectionType.DYNAMIC)
    public long getResponseTimeSetSettingsRequests() {
        return metricUtil.getTimerSum(SETTINGS_SET_TIMES);
    }

    @MonitoredAttribute(displayName = "currentSettingsDataSize", visibility = MonitoredAttribute.Visibility.ALL,
            units = MonitoredAttribute.Units.BYTES, category = MonitoredAttribute.Category.UTILIZATION,
            interval = MonitoredAttribute.Interval.FIVE_MIN, collectionType = MonitoredAttribute.CollectionType.DYNAMIC)
    public long getCurrentSettingsDataSize() {
        return metricUtil.getLastValue(SETTINGS_DATA_SIZE);
    }
}
