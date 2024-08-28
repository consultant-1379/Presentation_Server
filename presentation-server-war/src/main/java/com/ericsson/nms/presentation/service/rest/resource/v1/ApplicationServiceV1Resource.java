/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.rest.resource.v1;

import com.ericsson.nms.presentation.service.api.dto.Action;
import com.ericsson.nms.presentation.service.ejb.ActionService;
import com.ericsson.nms.presentation.service.locale.LocaleService;
import com.ericsson.nms.presentation.service.rest.resource.v1.request.ActionMatchesRequest;
import com.ericsson.nms.presentation.service.rest.resource.v1.response.ActionMatchesResponse;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * Rest endpoint for Application Service (versioned at v1).
 */
@RequestScoped
@Path("/v1/apps")
@Produces(MediaType.APPLICATION_JSON)
public class ApplicationServiceV1Resource {

    @Inject
    private ActionService actionService;

    @Inject
    LocaleService localeService;

    /**
     * Rest endpoint used to retrieve the actions for a given selection
     *
     * @param input {ActionMatchesRequest}
     * @return HTTP response
     */
    @POST
    @Path("/action-matches")
    public Response actionMatches(final ActionMatchesRequest input) {

        final Collection<Action> actions = actionService.getActionsBySelection(
                input.getApplication(), input.isMultipleSelection(), input.getConditions());

        return Response.status(Response.Status.OK).entity(new ActionMatchesResponse(input, actions)).build();
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


}
