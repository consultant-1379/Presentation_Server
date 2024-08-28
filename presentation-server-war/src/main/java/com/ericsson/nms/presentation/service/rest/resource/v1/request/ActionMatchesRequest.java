/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.rest.resource.v1.request;

import com.ericsson.nms.presentation.service.api.dto.ActionRuleCondition;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Collection;

/**
 * JAXB mapped class used to map <strong>/action-matches</strong> request
 */
public class ActionMatchesRequest implements Serializable {

    @JsonProperty("conditions")
    private Collection<ActionRuleCondition> conditions;

    private String application;

    private boolean multipleSelection;

    public Collection<ActionRuleCondition> getConditions() {
        return conditions;
    }

    public void setConditions(final Collection<ActionRuleCondition> conditions) {
        this.conditions = conditions;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(final String application) {
        this.application = application;
    }

    public boolean isMultipleSelection() {
        return multipleSelection;
    }

    public void setMultipleSelection(final boolean multipleSelection) {
        this.multipleSelection = multipleSelection;
    }
}
