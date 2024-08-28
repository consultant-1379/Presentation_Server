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

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Utility class wrapping the metrics logic used in the instrumentation.
 */
@ApplicationScoped
public class MetricUtil {

    /**
     * Unique registry name for this service group.
     */
    static final String REGISTRY_NAME = "PresentationServerRegistry";

    private final Map<String, Long> lastValue = new HashMap<>();

    @Inject
    private MetricRegistry serviceMetrics;

    /**
     * Gets the duration of a timer in microseconds
     * @param instrumentableAction action that is measured
     * @return time in nanoseconds
     */
    public Long getTimerSum(final InstrumentableAction instrumentableAction) {
        final Timer timer = serviceMetrics.timer(MetricRegistry.name(REGISTRY_NAME, instrumentableAction.getTimerName()));
        return sumAsMicroSeconds(timer.getSnapshot().getValues());
    }

    /**
     * Starts a timer. Use stopTimer when done.
     * @param instrumentableAction action that is measured
     * @return timer context
     */
    public Timer.Context startTimer(final InstrumentableAction instrumentableAction) {
        return serviceMetrics.timer(MetricRegistry.name(REGISTRY_NAME, instrumentableAction.getTimerName())).time();
    }

    /**
     * Stops a timer.
     * @param instrumentableAction action that is measured
     * @param timer timer to stop
     */
    public void stopTimer(final InstrumentableAction instrumentableAction, final Timer.Context timer) {
        final long duration = timer.stop();
        final String metricName = MetricRegistry.name(REGISTRY_NAME, instrumentableAction.name());
        lastValue.put(metricName, duration);
    }

    /**
     * Increments the total number of objects counted by 1.
     * @param instrumentableAction action that is measured
     */
    public void count(final InstrumentableAction instrumentableAction){
        count(instrumentableAction,1);
    }

    /**
     * Increments the total number of objects counted.
     * @param instrumentableAction action that is measured
     * @param quantity  the number by which to increment the object count by.
     */
    public void count(final InstrumentableAction instrumentableAction, final long quantity){
        final String metricName = MetricRegistry.name(REGISTRY_NAME, instrumentableAction.name());
        lastValue.put(metricName, quantity);
        serviceMetrics.counter(metricName).inc(quantity);
    }

    /**
     * Get the total of a countable metric.
     * @param instrumentableAction action that is measured
     * @return the total counted so far.
     */
    public long getCount(final InstrumentableAction instrumentableAction){
        return serviceMetrics.counter(MetricRegistry.name(REGISTRY_NAME, instrumentableAction.name())).getCount();
    }

    /**
     * Increments the total number of objects counted.
     * @param instrumentableAction action that is measured
     * @param quantity  the number by which to increment the object count by.
     */
    public void measure(final InstrumentableAction instrumentableAction, final long quantity){
        final String metricName = MetricRegistry.name(REGISTRY_NAME, instrumentableAction.name());
        lastValue.put(metricName, quantity);
        serviceMetrics.histogram(metricName).update(quantity);
    }

    /**
     * Get the sum of all values in a series.
     * @param instrumentableAction action that is measured
     * @return the sum of all values.
     */
    public long getTotal(final InstrumentableAction instrumentableAction){
        return sumValues(getHistogram(instrumentableAction).getSnapshot().getValues());
    }

    /**
     * Get the mean of all values in a series.
     * @param instrumentableAction action that is measured
     * @return the mean of all values.
     */
    public long getMean(final InstrumentableAction instrumentableAction){
        return (long)(getHistogram(instrumentableAction).getSnapshot().getMean());
    }

    /**
     * Get the maximum of all values in a series.
     * @param instrumentableAction action that is measured
     * @return the maximum of all values.
     */
    public long getMax(final InstrumentableAction instrumentableAction){
        return getHistogram(instrumentableAction).getSnapshot().getMax();
    }

    /**
     * Get the median of all values in a series.
     * @param instrumentableAction action that is measured
     * @return the median of all values.
     */
    public long getMedian(final InstrumentableAction instrumentableAction){
        return (long)(getHistogram(instrumentableAction).getSnapshot().getMedian());
    }

    /**
     * Get the last recorded value in any metric.
     * @param instrumentableAction action that is measured
     * @return the last value.
     */
    public long getLastValue(final InstrumentableAction instrumentableAction){
        final String metricName = MetricRegistry.name(REGISTRY_NAME, instrumentableAction.name());
        final Long last = lastValue.get(metricName);
        return last == null ? 0 : last;
    }

    /**
     * Get a histogram for an action (values are distributed low to high).
     * @param instrumentableAction action that is measured
     * @return a histogram.
     */
    private Histogram getHistogram(final InstrumentableAction instrumentableAction){
        return serviceMetrics.histogram(MetricRegistry.name(REGISTRY_NAME, instrumentableAction.name()));
    }

    /**
     * Get the median of all values in a series.
     * @param values array of long integers
     * @return the median of all values.
     */
    private long sumAsMicroSeconds(final long...values) {
        return TimeUnit.MILLISECONDS.convert(sumValues(values), TimeUnit.NANOSECONDS); // Converts nanos to millis
    }

    /**
     * Sum an array of numbers together
     * @param values array of long integers
     * @return the sum of all longs
     */
    private long sumValues(final long...values) {
        long sum = 0L;
        for (final long value : values) {
            sum += value;
        }
        return sum;
    }
}
