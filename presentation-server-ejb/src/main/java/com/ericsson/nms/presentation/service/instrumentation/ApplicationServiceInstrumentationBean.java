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

import static com.ericsson.nms.presentation.service.instrumentation.InstrumentableAction.APPLICATIONS_DEPLOYED;
import static com.ericsson.nms.presentation.service.instrumentation.InstrumentableAction.APPLICATION_LAUNCHES;
import static com.ericsson.nms.presentation.service.instrumentation.InstrumentableAction.APPLICATION_METADATA_READ_FAILURES;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.ericsson.oss.itpf.sdk.instrument.annotation.InstrumentedBean;
import com.ericsson.oss.itpf.sdk.instrument.annotation.MonitoredAttribute;

/**
 * Instrumentation Bean used to gather statistics from Application Service.
 */
@InstrumentedBean(description = "Application Service", displayName = "ApplicationService")
@ApplicationScoped
public class ApplicationServiceInstrumentationBean {

    @Inject
    private MetricUtil metricUtil;

    @MonitoredAttribute(displayName = "countApplicationsDeployed", visibility = MonitoredAttribute.Visibility.ALL,
            units = MonitoredAttribute.Units.NONE, category = MonitoredAttribute.Category.UTILIZATION,
            interval = MonitoredAttribute.Interval.FIVE_MIN, collectionType = MonitoredAttribute.CollectionType.DYNAMIC)
    public long getCountApplicationsDeployed() {
        return metricUtil.getLastValue(APPLICATIONS_DEPLOYED);
    }

    @MonitoredAttribute(displayName = "countApplicationMetadataReadFailures", visibility = MonitoredAttribute.Visibility.ALL,
            units = MonitoredAttribute.Units.NONE, category = MonitoredAttribute.Category.THROUGHPUT,
            interval = MonitoredAttribute.Interval.FIVE_MIN, collectionType = MonitoredAttribute.CollectionType.TRENDSUP)
    public long getCountApplicationMetadataReadFailures() {
        return metricUtil.getCount(APPLICATION_METADATA_READ_FAILURES);
    }

    @MonitoredAttribute(displayName = "countApplicationLaunches", visibility = MonitoredAttribute.Visibility.ALL,
            units = MonitoredAttribute.Units.NONE, category = MonitoredAttribute.Category.THROUGHPUT,
            interval = MonitoredAttribute.Interval.FIVE_MIN, collectionType = MonitoredAttribute.CollectionType.TRENDSUP)
    public long getCountApplicationLaunches() {
        return metricUtil.getCount(APPLICATION_LAUNCHES);
    }

}
