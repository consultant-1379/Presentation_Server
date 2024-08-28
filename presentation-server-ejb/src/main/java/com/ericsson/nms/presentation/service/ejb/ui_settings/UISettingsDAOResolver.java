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

import com.ericsson.nms.presentation.exceptions.UserMismatchException;
import com.ericsson.nms.presentation.service.cache.scheduler.CacheMigrationScheduler;
import com.ericsson.nms.presentation.service.database.availability.interceptors.bindings.RequiresDatabase;
import com.ericsson.nms.presentation.service.persistence.dao.impl.UISettingsCacheBasedDAO;
import com.ericsson.nms.presentation.service.persistence.database.dao.UISettingGroupRepositoryBasedDAO;
import com.ericsson.nms.presentation.service.persistence.database.dao.UiSettingGroupDAO;
import com.ericsson.nms.presentation.service.security.SecurityUtil;

import javax.inject.Inject;
import java.util.Objects;

/**
 * Resolves the cache or DB DAO depending on the data availability in the cache.
 */
@RequiresDatabase
public class UISettingsDAOResolver {

    @Inject
    private UISettingGroupRepositoryBasedDAO uiSettingsRepositoryBasedDAO;

    @Inject
    private UISettingsCacheBasedDAO uiSettingsCacheBasedDAO;

    @Inject
    private CacheMigrationScheduler cacheMigrationScheduler;

    @Inject
    private SecurityUtil securityUtil;

    /**
     * Resolve the DAO judging from the parameters for the setting group
     *
     * @param userId      the user that is owning the data
     * @param appId       the application for which the data are stored
     * @param settingType the setting group key
     *
     * @return the cache-based DAO if the data is present in cache, the DB-based one if not.
     * Also respects the development mode, returning the DB-based DAO only if it is enabled (to be removed after the DB will available)
     */
    public UiSettingGroupDAO resolve(String userId, String appId, String settingType) {
        if (Objects.equals(securityUtil.getCurrentUser(), userId)) {
            return (useOnlyOldFlow() || uiSettingsCacheBasedDAO.containsSettingGroup(userId, appId, settingType))
                ? uiSettingsCacheBasedDAO : uiSettingsRepositoryBasedDAO;
        } else {
            throw new UserMismatchException(
                String.format(
                    "User that is passed to the settings service (%s) should match the currently authenticated one (%s)",
                    userId, securityUtil.getCurrentUser()

                ));
        }
    }

    private boolean useOnlyOldFlow() {
        return !cacheMigrationScheduler.isMigrationEnabled();
    }
}
