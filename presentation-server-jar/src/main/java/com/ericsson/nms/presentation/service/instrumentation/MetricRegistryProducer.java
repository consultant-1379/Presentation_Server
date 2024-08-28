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

import com.codahale.metrics.MetricRegistry;

import javax.enterprise.inject.Produces;

/**
 * CDI producer for MetricRegistry
 */
public class MetricRegistryProducer {

    @Produces
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private MetricRegistry getMetricsRegistry() {
        return new MetricRegistry();
    }
}
