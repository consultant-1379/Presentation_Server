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

import com.ericsson.nms.presentation.exceptions.SystemPropertyNotFoundException;
import com.ericsson.nms.presentation.service.api.dto.SystemProperty;
import com.ericsson.nms.presentation.service.cache.CacheName;
import com.ericsson.nms.presentation.service.instrumentation.MetricUtil;
import com.ericsson.nms.presentation.service.interceptors.MethodCallTimerInterceptor;

import javax.cache.Cache;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ericsson.nms.presentation.service.cache.PresentationServerCacheProvider.SYSTEM_PROPERTY_CACHE_NAME;
import static com.ericsson.nms.presentation.service.configurator.SystemInfoConfigListener.NAME;
import static com.ericsson.nms.presentation.service.instrumentation.InstrumentableAction.SYSTEM_INFO_HITS;

/**
 * EJB implementation for {@link SystemInfoService}.
 */
@Interceptors({MethodCallTimerInterceptor.class})
public class SystemInfoServiceEjb implements SystemInfoService {

    private static final Set<String> SUPPORTED_PROPERTIES =
            Stream.of(NAME).collect(Collectors.toSet());

    @Inject
    @CacheName(SYSTEM_PROPERTY_CACHE_NAME)
    private Cache<String,SystemProperty> cache;

    @Inject
    private MetricUtil metricUtil;

    /**
     * {@inheritDoc}
     */
    @Override
    public SystemProperty getSystemProperty(final String propertyName) {
        metricUtil.count(SYSTEM_INFO_HITS);
        final SystemProperty property = cache.get(propertyName);
        if (property == null) {
            throw new SystemPropertyNotFoundException(propertyName);
        }
        return property;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<SystemProperty> getAllSystemProperties() {
        metricUtil.count(SYSTEM_INFO_HITS);
        return cache.getAll(SUPPORTED_PROPERTIES).values();
    }

}
