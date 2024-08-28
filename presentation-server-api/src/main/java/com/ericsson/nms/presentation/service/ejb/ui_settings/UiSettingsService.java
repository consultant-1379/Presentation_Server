/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2019
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.presentation.service.ejb.ui_settings;

import com.ericsson.nms.presentation.service.api.dto.ui_settings.UiSettingGroupDTO;

import javax.ejb.Local;
import java.util.Optional;

/**
 * The settings service that is serving as the main entry point for all operations with the UI settings
 * The interface operates only application and group name parameters.
 * The user is depending on the implementation but the logical choice is the one that is currently logged in.
 */
@Local
public interface UiSettingsService {

    /**
     * Gets the settings for the given application and name.
     * @param application application that has this UI setting
     * @param key the setting group name
     * @return the setting group
     */
    UiSettingGroupDTO getSettingsGroupByKey(String application, String key);

    /**
     * Save the setting group for the application and group name.
     * @param application application
     * @param key key (settingGroupName)
     * @param settingGroupEntity setting group entity
     * @return the setting group that was saved
     */
    UiSettingGroupDTO saveSettings(String application, String key,
                                   UiSettingGroupDTO settingGroupEntity);

    /**
     * Delete the individual setting from the setting group
     * @param application application that owns the setting
     * @param settingGroupKey the name of the setting group
     * @param settingKey the name of the individual setting
     * @return the setting group without the deleted setting, Optional.Empty if the deleted setting was the last one in the group
     */
    Optional<UiSettingGroupDTO> deleteSetting(String application, String settingGroupKey, String settingKey);
}
