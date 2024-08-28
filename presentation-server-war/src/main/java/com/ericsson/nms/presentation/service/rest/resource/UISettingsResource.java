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
package com.ericsson.nms.presentation.service.rest.resource;

import com.codahale.metrics.Timer;
import com.ericsson.nms.presentation.exceptions.SettingNotFoundException;
import com.ericsson.nms.presentation.exceptions.UserNotFoundException;
import com.ericsson.nms.presentation.service.api.dto.ui_settings.UiSettingDTO;
import com.ericsson.nms.presentation.service.api.dto.ui_settings.UiSettingGroupDTO;
import com.ericsson.nms.presentation.service.ejb.ui_settings.UiSettingsService;
import com.ericsson.nms.presentation.service.instrumentation.MetricUtil;
import com.ericsson.oss.itpf.sdk.tracing.annotation.Traceable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.ericsson.nms.presentation.service.instrumentation.InstrumentableAction.SETTINGS_GET_HITS;
import static com.ericsson.nms.presentation.service.instrumentation.InstrumentableAction.SETTINGS_GET_TIMES;
import static com.ericsson.nms.presentation.service.instrumentation.InstrumentableAction.SETTINGS_SET_HITS;
import static com.ericsson.nms.presentation.service.instrumentation.InstrumentableAction.SETTINGS_SET_TIMES;

/**
 * UI settings resource class
 */
@RequestScoped
@Path("/ui")
@Traceable
public class UISettingsResource {

    private static final String NO_USER_FOUND = "No user found!";

    @Inject
    private UiSettingsService uiSettingsService;

    @Inject
    private MetricUtil metricUtil;

    @Inject
    private Logger logger;

    private String userId;

    @HeaderParam("X-Tor-UserID")
    public void setUserId(final String userId) {
        this.userId = userId;
    }

    /**
     * Sets a UI Setting
     *
     * @param application The application the setting is used in
     * @param settingType The type of setting for the above application
     * @param setting     The SettingBean that defines the setting.  A SettingBean is basically
     *                    a key/value pair where the key is the unique ID of the Setting and the value is the
     *                    value of the setting.
     * @return Response noContent - If method succeeds
     * Response 401 - If User Not Found
     * Response 500 - General Exception (details will be given)
     */
    @PUT
    @Path("/settings/{appID}/{settingType}/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setSettings(@PathParam("appID") final String application,
                                @PathParam("settingType") final String settingType,
                                final UiSettingDTO setting) {
        metricUtil.count(SETTINGS_SET_HITS);
        if (StringUtils.isEmpty(userId)) {
            throw new UserNotFoundException(NO_USER_FOUND);
        }
        final Timer.Context timer = metricUtil.startTimer(SETTINGS_SET_TIMES);
        try {
            final UiSettingGroupDTO uiSettingGroupDTO = new UiSettingGroupDTO(application, userId, settingType, setting);
            uiSettingsService.saveSettings(application, settingType, uiSettingGroupDTO);
        } finally {
            metricUtil.stopTimer(SETTINGS_SET_TIMES, timer);
        }
        return Response.noContent().build();
    }

    @PUT
    @Path("/settings/v2/{appID}/{settingType}/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setSettings(@PathParam("appID") final String application,
                                @PathParam("settingType") final String settingType,
                                final List<UiSettingDTO> settings) {
        metricUtil.count(SETTINGS_SET_HITS);
        if (StringUtils.isEmpty(userId)) {
            throw new UserNotFoundException(NO_USER_FOUND);
        }
        final Timer.Context timer = metricUtil.startTimer(SETTINGS_SET_TIMES);
        try {
            final UiSettingGroupDTO uiSettingGroupDTO = new UiSettingGroupDTO(application, userId, settingType,
                settings.toArray(new UiSettingDTO[0]));
            uiSettingsService.saveSettings(application, settingType, uiSettingGroupDTO);
        } finally {
            metricUtil.stopTimer(SETTINGS_SET_TIMES, timer);
        }
        return Response.noContent().build();
    }

    /**
     * Gets a UI Setting
     *
     * @param application The application the setting is used in
     * @param settingType The type of setting for the above application
     * @return Response 200 - A Collection of SettingBean objects for the passed application and setting type
     * Response 200 - if the setting type or application is not found
     * Response 401 - If User Not Found
     * Response 500 - General Exception (details will be given)
     */
    @GET
    @Path("/settings/{appID}/{settingType}/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSettings(@PathParam("appID") final String application,
                                @PathParam("settingType") final String settingType) {
        metricUtil.count(SETTINGS_GET_HITS);
        if (StringUtils.isEmpty(userId)) {
            throw new UserNotFoundException(NO_USER_FOUND);
        }

        Collection<UiSettingDTO> settings;
        final Timer.Context timer = metricUtil.startTimer(SETTINGS_GET_TIMES);
        try {
            settings = uiSettingsService.getSettingsGroupByKey(application, settingType).getSettings();
        } catch (SettingNotFoundException snfe) {
            logger.debug(snfe.getMessage(), snfe);
            settings = new ArrayList<>(0);
        } finally {
            metricUtil.stopTimer(SETTINGS_GET_TIMES, timer);
        }
        return Response.status(200).entity(settings).build();
    }

    /**
     * Deletes a UI Setting
     *
     * @param application The application the setting is used in
     * @param settingType The type of setting for the above application
     * @param setting     The SettingBean that defines the setting.  A SettingBean is basically
     *                    a key/value pair where the key is the unique ID of the Setting and the value is the
     *                    value of the setting.
     * @return Response noContent - If method succeeds
     * Response 401 - If User Not Found
     * Response 404 - If Setting to be deleted Not Found
     * Response 500 - General Exception
     */
    @DELETE
    @Path("/settings/{appID}/{settingType}/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeSettings(@PathParam("appID") final String application,
                                   @PathParam("settingType") final String settingType,
                                   final UiSettingDTO setting) {

        if (StringUtils.isEmpty(userId)) {
            throw new UserNotFoundException(NO_USER_FOUND);
        }
        uiSettingsService.deleteSetting(application, settingType, setting.getId());

        return Response.noContent().build();
    }
}
