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

import com.ericsson.nms.presentation.service.api.dto.SystemProperty;
import com.ericsson.nms.presentation.service.ejb.SystemInfoService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Rest endpoint for Application Service (versioned at v1).
 */
@RequestScoped
@Path("/system/v1")
@Produces(MediaType.APPLICATION_JSON)
public class SystemInfoServiceV1Resource {

    @Inject
    SystemInfoService service;

    @GET
    @Path("/{property}")
    public Response getSystemInfo(@PathParam("property") final String property) {

        final Map<String,Serializable> entity = new HashMap<>();
        final SystemProperty systemProperty = service.getSystemProperty(property);

        entity.put(systemProperty.getName(), systemProperty.getValue());

        return Response.status(Response.Status.OK).entity(entity).build();
    }

    @GET
    @Path("/")
    public Response getAllSystemInfo() {

        final Map<String,Serializable> entity = new HashMap<>();
        for (final SystemProperty property : service.getAllSystemProperties()) {
            entity.put(property.getName(), property.getValue());
        }

        return Response.status(Response.Status.OK).entity(entity).build();
    }

}
