/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.rest.resource.v1.response;

import com.ericsson.nms.presentation.service.api.dto.Action;
import com.ericsson.nms.presentation.service.rest.resource.v1.request.ActionMatchesRequest;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Collection;

/**
 * JAXB mapped class used to map <strong>/action-matches</strong> response
 */
public class ActionMatchesResponse implements Serializable {


    @JsonProperty("action-matches")
    private ActionMatchesRequest request;

    private Collection<Action> actions;

    /**
     * Action Matches Response.
     */
    public ActionMatchesResponse() {}

    /**
     * Action Matches Response.
     *
     * @param request {ActionMatchesRequest}
     * @param actions {Collection}
     */
    public ActionMatchesResponse(final ActionMatchesRequest request, final Collection<Action> actions) {
        this.request = request;
        this.actions = actions;
    }

    public ActionMatchesRequest getRequest() {
        return request;
    }

    public void setRequest(final ActionMatchesRequest request) {
        this.request = request;
    }

    public Collection<Action> getActions() {
        return actions;
    }

    public void setActions(final Collection<Action> actions) {
        this.actions = actions;
    }
}
