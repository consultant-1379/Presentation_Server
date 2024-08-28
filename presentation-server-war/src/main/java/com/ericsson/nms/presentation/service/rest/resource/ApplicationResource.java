/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.rest.resource;


import com.ericsson.nms.presentation.exceptions.InvalidApplicationTypeException;
import com.ericsson.nms.presentation.exceptions.NotFoundException;
import com.ericsson.nms.presentation.exceptions.ServiceRemovedException;
import com.ericsson.nms.presentation.exceptions.UserNotFoundException;
import com.ericsson.nms.presentation.service.api.dto.AbstractApplication;
import com.ericsson.nms.presentation.service.api.dto.Group;
import com.ericsson.nms.presentation.service.api.dto.LogRequest;
import com.ericsson.nms.presentation.service.api.dto.WebApplication;
import com.ericsson.nms.presentation.service.api.dto.ui_settings.UiSettingDTO;
import com.ericsson.nms.presentation.service.ejb.ApplicationService;
import com.ericsson.nms.presentation.service.ejb.LoggingService;
import com.ericsson.nms.presentation.service.ejb.ui_settings.UiSettingsService;
import com.ericsson.nms.presentation.service.instrumentation.MetricUtil;
import com.ericsson.nms.presentation.service.locale.LocaleService;
import com.ericsson.nms.presentation.service.rest.request.ConsumesRequest;
import com.ericsson.nms.presentation.service.rest.response.GroupResponse;
import com.ericsson.nms.presentation.service.util.ApplicationCopyUtil;
import com.ericsson.nms.presentation.service.util.FavoriteAppsUtil;
import com.ericsson.nms.presentation.service.util.PresentationServiceConfig;
import com.ericsson.oss.itpf.sdk.recording.EventLevel;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.itpf.sdk.tracing.annotation.Traceable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.ericsson.nms.presentation.service.instrumentation.InstrumentableAction.APPLICATION_LAUNCHES;

/**
 * Application resource class
 */
@RequestScoped
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Traceable
public class ApplicationResource {

    public static final String EVENT_TYPE_REQUEST_WEB_OK = "PRESENTATION_SERVER.REQUESTED.WEB_APP.SUCCESSFUL";
    public static final String EVENT_TYPE_REQUEST_CITRIX_OK = "PRESENTATION_SERVER.REQUESTED.CITRIX.SUCCESSFUL";
    public static final String EVENT_TYPE_REQUEST_WEB_FAIL = "PRESENTATION_SERVER.REQUESTED.WEB_APP.FAILED";
    public static final String EVENT_TYPE_REQUEST_CITRIX_FAIL = "PRESENTATION_SERVER.REQUESTED.CITRIX.FAILED";
    public static final String EVENT_TYPE_REQUEST_CITRIX_OK_NO_PW = "PRESENTATION_SERVER.REQUESTED.CITRIX.SUCCESSFUL.WITHOUT.SSO_PASSWORD";

    @Inject
    ApplicationService applicationService;

    @Inject
    UiSettingsService uiSettingsService;

    @Inject
    LoggingService loggingService;

    @Inject
    PresentationServiceConfig launcherConfig;

    @Inject
    SystemRecorder systemRecorder;

    @Inject
    FavoriteAppsUtil favoriteAppsUtil;

    @Inject
    ApplicationCopyUtil applicationCopyUtil;

    @Inject
    Logger logger;

    @Inject
    LocaleService localeService;

    @Inject
    MetricUtil metricsUtil;

    private String userId;

    private String logApplication;

    /**
     * JAXRS annotated method that GETs all the Applications the Launcher supports. The userId should be present in the request header.
     *
     * @return Response 200 - A Collection of Launchable objects represented as JSON in entity Response 401 - Error message explaining User Not Found
     * Response 500 - Server Error
     */
    @GET
    @Path("apps")
    @Produces("application/json")
    public Response getApps() {
        checkUser();

        return Response.status(200).entity(
                favoriteAppsUtil.markedApps()
                        .stream()
                        .filter(app -> !app.isHidden())
                        .collect(Collectors.toSet()))
                .build();
    }

    /**
     * @param consumesRequest {ConsumesRequest}
     * @return response status
     */
    @GET
    @Path("apps")
    @Produces("application/json;version=2.0.0")
    public Response getAppsV2(@QueryParam("consumes") final ConsumesRequest consumesRequest) {

        checkUser();

        if (consumesRequest == null) {
            return this.getApps();
        }

        throw new ServiceRemovedException("Please use '/rest/groups' without any query parameters.");
    }

    /**
     * JAXRS annotated method that GETs all the Groups the Launcher supports. The userId should be present in the request header. The Groups are
     * populated with their applications enclosed.
     *
     * @return Response 200 - A Collection of Groups objects represented as JSON in entity Response 401 - Error message explaining User Not Found
     * Response 500 - Server Error
     */
    @GET
    @Path("groups")
    public Response getGroups() {

        checkUser();

        final List<GroupResponse> gs = new ArrayList<>();
        for (final Group g : applicationService.getGroups()) {
            if (g.getAppIds().isEmpty()) {
                gs.add(new GroupResponse(g.getId(), g.getName()));
            } else {

                final Collection<AbstractApplication> groupApps = favoriteAppsUtil.markedApps(g)
                        .stream()
                        .filter(app -> !app.isHidden())
                        .collect(Collectors.toSet());

                gs.add(new GroupResponse(g.getId(), g.getName(), groupApps.toArray(new AbstractApplication[]{})));
            }
        }
        return Response.status(200).entity(gs).build();
    }

    /**
     * JAXRS annotated method that GETs all the Favorites for a particular user. The userId should be present in the request header.
     *
     * @return Response 200 - A Collection of Launchable objects represented as JSON in entity Response 401 - Error message explaining User Not Found
     */
    @GET
    @Path("favorites")
    public Response getFavorites() {

        checkUser();

        final Collection<UiSettingDTO> settings = uiSettingsService.getSettingsGroupByKey("launcher", "favorites").getSettings();

        final Collection<AbstractApplication> as = new ArrayList<>();
        for (final AbstractApplication app : applicationService.getApps()) {
            final String id = app.getId();
            for (final UiSettingDTO setting : settings) {
                if (setting.getId().equals(id)) {
                    as.add(app);
                    break;
                }
            }
        }
        return Response.status(200).entity(favoriteAppsUtil.markedApps(as)).build();
    }

    /**
     * Returns an a redirect url to the client so the web application identified by the passed {appID} can be launched. The userId should be present
     * in the request header to authenticate to the SecurityService to get SSO password The method also produces an event log to say that the user has
     * attempted to launch an application. A failure message will be logged if the method fails.
     *
     * @param appId The web application to launch
     * @return Response 301 - A redirection URL to the Web Application to be launched Response 401 - Error message explaining User Not Found Response
     * 404 - Error message explaining Application Not Found
     */
    @GET
    @Path("apps/web/{appID}")
    public Response launchWebApplication(@PathParam("appID") final String appId) {

        metricsUtil.count(APPLICATION_LAUNCHES);
        checkUser();
        final String id = "unknown";

        try {
            final AbstractApplication app = applicationService.getApplication(appId);
            if ( !(app instanceof WebApplication)) {
                throw new InvalidApplicationTypeException("The application with ID "+ appId+ " is not an Web Application");
            }
            final WebApplication webApp = applicationCopyUtil.copy(false, (WebApplication)app);

            recordAppEvent(EVENT_TYPE_REQUEST_WEB_OK, id, appId);

            final String targetUri = webApp.getTargetUri();
            return Response.status(301).header("Location", targetUri).build();

        } catch (final NotFoundException e) {
            recordAppEvent(EVENT_TYPE_REQUEST_WEB_FAIL, id, appId);
            throw e;
        }
    }

    /**
     * @param requests {Collection<LogRequest>}
     * @return response status
     */
    @POST
    @Path("service/log")
    public Response sendPresentationLogger(final Collection<LogRequest> requests) {
        loggingService.log(requests, userId, logApplication);
        return Response.status(Response.Status.CREATED).build();
    }

    @HeaderParam("X-Tor-UserID")
    public void setUserId(final String userId) {
        this.userId = userId;
    }

    @HeaderParam("X-Tor-Application")
    public void setLogApplication(final String application) {
        this.logApplication = application;
    }

    @HeaderParam("Accept-Language")
    public void setLocale(final String localeHeader) {
        if (StringUtils.isNoneEmpty(localeHeader)) {
            final LinkedHashSet<String> userLocales = new LinkedHashSet<>();
            for (final String locale : StringUtils.split(localeHeader, ",")) {
                final int semiColonIndex = locale.indexOf(';');

                // Remove anything coming after a semicolon in the locale header.
                // e.g: "en-GB,en;q=0.5"
                if (semiColonIndex == -1) {
                    userLocales.add(locale);
                } else {
                    userLocales.add(StringUtils.substring(locale, 0, semiColonIndex));
                }
            }
            localeService.setUserLocale(userLocales);
        }
    }

    /**
     * Record that an application has attempted to be launched
     *
     * @param eventType Static String describing event type
     * @param userId
     * @param appId
     */
    private void recordAppEvent(final String eventType, final String userId, final String appId) {
        this.systemRecorder.recordEvent(eventType, EventLevel.DETAILED, "requested by userId : " + userId + ", application name : " + appId,
                null, null);
    }

    private void checkUser() {
        if (StringUtils.isEmpty(userId)) {
            throw new UserNotFoundException("No user found!");
        }
    }

}
