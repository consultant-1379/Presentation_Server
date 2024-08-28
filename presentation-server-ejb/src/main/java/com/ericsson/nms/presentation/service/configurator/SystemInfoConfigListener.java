/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.configurator;

import com.ericsson.nms.presentation.service.api.dto.SystemProperty;
import com.ericsson.nms.presentation.service.cache.CacheName;
import com.ericsson.nms.presentation.service.interceptors.MethodCallTimerInterceptor;
import com.ericsson.oss.itpf.sdk.config.annotation.ConfigurationChangeNotification;
import org.slf4j.Logger;

import javax.cache.Cache;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import static com.ericsson.nms.presentation.service.cache.PresentationServerCacheProvider.SYSTEM_PROPERTY_CACHE_NAME;

/**
 * Listener to handle modeled parameters used by system info
 */
@ApplicationScoped
@Interceptors({MethodCallTimerInterceptor.class})
public class SystemInfoConfigListener {

    public static final String HOST_NAME = "enmHostName";
    public static final String NAME = "name";

    @Inject
    private Logger logger;

    @Inject
    @CacheName(SYSTEM_PROPERTY_CACHE_NAME)
    private Cache<String,SystemProperty> cache;

    public void onHostNameUpdate(@Observes @ConfigurationChangeNotification(propertyName = HOST_NAME) final String hostName) {

        logger.info("Updating {} to {}", NAME, hostName);
        cache.put(NAME, new SystemProperty(NAME, hostName));

    }


}
