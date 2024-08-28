/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.uis.beans;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Wraps a HashMap, so we can store key/value pairs
 * in a HashMap structure within the cache.
 * 
 * @author ejonbli
 *
 * @deprecated because it's being replaced by the new DTO
 */
@SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
@Deprecated
public class UISettingGroupDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, UISettingBean> settings = new LinkedHashMap<>();

    /**
     * Default constructor
     */
    public UISettingGroupDTO() {
    }

    /**
     * @param setting {UISettingBean}
     */
    public UISettingGroupDTO(final UISettingBean setting) {
        this.put(setting);
    }

    /**
     * @param settings {Collection}
     */
    public UISettingGroupDTO(final Collection<? extends UISettingBean> settings) {
        if (settings != null) {
            for (final UISettingBean setting : settings) {
                this.settings.put(setting.getId(), setting); // settings key === bean id
            }
        }
    }

    /**
     * Set the setting attribute of this UISettingDTO
     * 
     * @param setting a setting to be stored for an application
     */
    public void put(final UISettingBean setting) {
        settings.put(setting.getId(), setting);  // settings key === bean id
    }

    /**
     * Remove the setting attribute of this UISettingDTO
     * 
     * @param settingId a setting to be removed for an application
     */
    public void remove(final String settingId) {
        settings.remove(settingId);
    }

    public Map<String, UISettingBean> getSettings() {
        return settings;
    }

    public void setSettings(final Map<String, UISettingBean> settings) {
        this.settings = settings;  // settings key ~~~ bean id
    }

    /**
     * Calculate the size of the stored data (compound key + value contents)
     * @return size of stored data in bytes
     */
    public long calculateSize() {
        long size = 0;
        for (final Map.Entry<String, UISettingBean> entry : settings.entrySet()) {
            final String key = entry.getKey(); // Unlike Cache.Entry, Map.Entry keys can be null
            size += calculateStringSize(key);
            final UISettingBean bean = entry.getValue();
            if (bean != null) {
                size += calculateStringSize(bean.getId());
                size += calculateStringSize(bean.getValue());
            }
        }
        return size;
    }

    private int calculateStringSize(final String string) {
        if (string == null) {
            return 0;
        }
        return string.length() * 2; // Internal strings = UTF-16 (2 bytes)
    }

    @Override
    public String toString() {
        return "UISettingGroupDTO{" +
		        "settings=" + settings + '}';
    }
}
