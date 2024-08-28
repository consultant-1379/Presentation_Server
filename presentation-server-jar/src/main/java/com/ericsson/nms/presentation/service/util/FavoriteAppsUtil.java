/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.util;

import com.ericsson.nms.presentation.exceptions.PresentationServerException;
import com.ericsson.nms.presentation.exceptions.SettingNotFoundException;
import com.ericsson.nms.presentation.service.api.dto.AbstractApplication;
import com.ericsson.nms.presentation.service.api.dto.Group;
import com.ericsson.nms.presentation.service.api.dto.ui_settings.UiSettingDTO;
import com.ericsson.nms.presentation.service.api.dto.ui_settings.UiSettingGroupDTO;
import com.ericsson.nms.presentation.service.ejb.ApplicationService;
import com.ericsson.nms.presentation.service.ejb.ui_settings.UiSettingsService;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Utility created to handle favorite applications.
 */
public class FavoriteAppsUtil {

    @Inject
    private ApplicationService applicationService;

    @Inject
    private UiSettingsService uiSettingsService;

    @Inject
    private ApplicationCopyUtil applicationCopyUtil;

    @Inject
    private Logger logger;

    /**
     * @return markedApps {Collection}
     */
    public Collection<AbstractApplication> markedApps() {
        return markedApps(applicationService.getApps());
    }

    /**
     * @param group {Group}
     * @return markedApps {Collection}
     */
    public Collection<AbstractApplication> markedApps(final Group group) {
        return markedApps(applicationService.getApps(group.getAppIds().toArray(new String[]{})) );
    }

    /**
     * For a Collection of applications, use the UserId to determine which applications the user has stored as favorites. For applications that are
     * set as favorites, create a CitrixApplication or WebApplication object with the favorite attribute set to true, otherwise false. * The Citrix
     * and WebApplication classes are used to represent the application objects in the browser.
     *
     * @param <A> The expected class of the value
     * @param apps   The list of applications the Launcher lists
     * @return A Collection of Citrix and WebApplication classes (implementing Launchable) are returned and populated with favorite info ready to be
     * used in the UI.
     */
    public <A extends AbstractApplication> Collection<A> markedApps(final Collection<AbstractApplication> apps) {

        final Collection<AbstractApplication> as = new ArrayList<>();
        Collection<UiSettingDTO> settings;
        try {
            UiSettingGroupDTO uiSettingGroupDTO = uiSettingsService.getSettingsGroupByKey("launcher", "favorites");
            settings = uiSettingGroupDTO.getSettings() != null ? uiSettingGroupDTO.getSettings() : new ArrayList<>();
        } catch (SettingNotFoundException snfe) {
            logger.warn("No launcher favorite UI settings were found");
            settings = new ArrayList<>();
        }


        for (final AbstractApplication app : apps) {
            boolean favorite = false;
            final String id = app.getId();
            for (final UiSettingDTO setting : settings) {
                if (setting.getId().equals(id)) {
                    favorite = true;
                    break;
                }
            }
            try {
                final AbstractApplication launchable = applicationCopyUtil.copy(favorite, app);
                as.add(launchable);
            } catch (PresentationServerException exception) {
                logger.error("Failed to read application {}: {}", app.getName(), exception.getMessage());
            }
        }
        return (Collection<A>) as;
    }
}
