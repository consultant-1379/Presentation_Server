/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Entity representing an Action Rule.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActionRule implements Serializable {

    /**
     * Name of the action that owns this rule
     */
    private String actionName;

    /**
     * Condition for this rule to be applied
     */
    private ActionRuleCondition condition;

    public String getActionName() {
        return actionName;
    }

    public void setActionName(final String actionName) {
        this.actionName = actionName;
    }

    public ActionRuleCondition getCondition() {
        return condition;
    }

    public void setCondition(final ActionRuleCondition condition) {
        this.condition = condition;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ActionRule that = (ActionRule) o;

        if (actionName != null ? !actionName.equals(that.actionName) : that.actionName != null) {
            return false;
        }
        return condition != null ? condition.equals(that.condition) : that.condition == null;

    }

    @Override
    public int hashCode() {
        int result = actionName != null ? actionName.hashCode() : 0;
        result = 31 * result + (condition != null ? condition.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ActionRule{" +
                "actionName='" + actionName + '\'' +
                ", condition=" + condition +
                '}';
    }
}
