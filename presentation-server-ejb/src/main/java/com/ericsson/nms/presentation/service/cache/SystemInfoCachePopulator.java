/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.cache;

import com.ericsson.nms.presentation.service.api.dto.SystemProperty;
import com.ericsson.oss.itpf.sdk.config.ConfigurationEnvironment;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import javax.cache.Cache;
import javax.ejb.Stateless;
import javax.inject.Inject;

import static com.ericsson.nms.presentation.service.cache.PresentationServerCacheProvider.SYSTEM_PROPERTY_CACHE_NAME;
import static com.ericsson.nms.presentation.service.configurator.SystemInfoConfigListener.HOST_NAME;
import static com.ericsson.nms.presentation.service.configurator.SystemInfoConfigListener.NAME;

/**
 * Populator used to copy supported properties from PIB to our local cache.
 */
@Stateless
public class SystemInfoCachePopulator {

    @Inject
    private ConfigurationEnvironment configEnvironment;

    @Inject
    @CacheName(SYSTEM_PROPERTY_CACHE_NAME)
    private Cache<String,SystemProperty> cache;

    @Inject
    private Logger logger;

    public void populate() {

        if (cache.get(NAME) == null) {

            logger.info("ENM host not available. Trying to retrieve from PIB.");
            // Read the hostName value from PIB
            try {
                final String hostName = (String) configEnvironment.getValue(HOST_NAME);
                if (StringUtils.isNotEmpty(hostName)) {
                    logger.info("enmHostName found with the value: {}", hostName);
                    cache.put(NAME, new SystemProperty(NAME, hostName));
                } else {
                    logger.info("enmHostName property not available on PIB. Host Name will be unavailable.");
                }
            } catch (final Exception exception) {
                logger.error("Failed to read property "+ HOST_NAME, exception);
            }
        }
    }

}
