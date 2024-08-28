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

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;

import com.ericsson.nms.presentation.service.api.dto.ServerDateTimeMessage;
import com.ericsson.nms.presentation.service.ejb.TimeService;

/**
 * System resource class
 */
@RequestScoped
@Path("/system")
@Produces(MediaType.APPLICATION_JSON)
public class SystemResource {

    @Inject
    private TimeService timeService;

    @Inject
    private Logger logger;

    /**
     * JAXRS annotated method that GETs server time, server location, UTC offset and 3 letter timezone
     *
     * @return Response 200 - Server time, server location, timezone and UTC offset represented as JSON entity
     *         Response 500 - Server Error
     */
    @GET
    @Path("time")
    public Response getServerDateTime() {
        try {
            final ServerDateTimeMessage serverDateTimeMessage = timeService.now();
            return Response.status(Response.Status.OK).entity(serverDateTimeMessage).build();
        } catch (final IllegalArgumentException e) {
            logger.error(e.getMessage());
            return Response.serverError()
                    .entity(e.getMessage())
                    .build();
        }
    }
}
