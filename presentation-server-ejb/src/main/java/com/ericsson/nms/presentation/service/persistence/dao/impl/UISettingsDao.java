/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.presentation.service.persistence.dao.impl;

import static com.ericsson.nms.presentation.service.instrumentation.InstrumentableAction.SETTINGS_DATA_SIZE;

import com.ericsson.nms.presentation.service.instrumentation.MetricUtil;
import com.ericsson.nms.presentation.service.interceptors.MethodCallTimerInterceptor;
import com.ericsson.nms.presentation.service.persistence.dao.AbstractCacheDao;
import com.ericsson.nms.presentation.service.uis.beans.UISettingBean;
import com.ericsson.nms.presentation.service.uis.beans.UISettingGroupDTO;
import com.ericsson.oss.itpf.sdk.cache.annotation.NamedCache;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import javax.cache.Cache;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.util.HashSet;
import java.util.Set;

/**
 * Cache DAO implementation for the UISettings Cache
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Interceptors({MethodCallTimerInterceptor.class})
public class UISettingsDao extends AbstractCacheDao<String, UISettingGroupDTO> {

    @Inject
    Logger logger;

    @Inject
    @NamedCache("Presentation.Server.UISettings")
    private Cache<String, UISettingGroupDTO> cache;

    @Inject
    private MetricUtil metricUtil;

    @Override
    protected Cache<String, UISettingGroupDTO> getCache() {
        return cache;
    }

    /**
     * @param key {String}
     * @return uisetting
     */
    public UISettingGroupDTO getUISetting(final String key) {
        logger.debug("Looking for [{}] on Presentation.Server.UISettings Cache", key);

        UISettingGroupDTO uiSetting = get(key);
        if (uiSetting == null) {
            logger.debug("No entry found for [{}] on Presentation.Server.UISettings Cache. Creating a new entry.", key);
            uiSetting = new UISettingGroupDTO();
        } else {
            logger.debug("Entry found for [{}] on Presentation.Server.UISettings Cache.", key);
            if (logger.isDebugEnabled()) {
                logger.debug("  UISettingGroupDTO:");
                logger.debug("-------------------------------------------");
                for (final UISettingBean setting : uiSetting.getSettings().values()) {
                    logger.debug("  * {} -> {}", setting.getId(), setting.getValue());
                }
            }
        }
        return uiSetting;
    }

    /**
     * @param key {String}
     * @param settingGroup {UISettingGroupDTO}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void setSetting(final String key, final UISettingGroupDTO settingGroup) {

        final Set<String> keysToRemove = new HashSet<>();
        for (final String settingKey : settingGroup.getSettings().keySet()) {
            final String value = settingGroup.getSettings().get(settingKey).getValue();
            if (value == null || StringUtils.isEmpty(value)) {
                keysToRemove.add(settingKey);
            }
        }
        for (final String removeKey : keysToRemove) {
            settingGroup.getSettings().remove(removeKey);
        }

        // Write the updated settingDTO object to the cache
        logger.debug("Updating entry {} on Presentation.Server.UISettings Cache.", key);
        if (logger.isDebugEnabled()) {
            logger.debug("  UISettingGroupDTO:");
            logger.debug("  ----------------------------------------------------");
            for (final UISettingBean setting : settingGroup.getSettings().values()) {
                logger.debug("     *  {} -> {} ", setting.getId(), setting.getValue());
            }
        }
        put(key, settingGroup);
        metricUtil.measure(SETTINGS_DATA_SIZE, getCacheSize());
    }

    public boolean contains(String key) {
        return cache.containsKey(key);
    }

    /**
     * Get the size of the keys and contents of the cache
     * @return cache size in bytes
     */
    private long getCacheSize() {
        long size = 0;
        for (final Cache.Entry<String, UISettingGroupDTO> entry : getCache()) {
            size += entry.getKey().length() * 2;
            size += entry.getValue().calculateSize();
        }
        return size;
    }
}
