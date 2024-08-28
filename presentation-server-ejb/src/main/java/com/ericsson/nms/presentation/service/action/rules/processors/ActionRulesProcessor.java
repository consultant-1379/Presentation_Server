/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.action.rules.processors;

import com.ericsson.nms.presentation.service.api.dto.Action;
import com.ericsson.nms.presentation.service.api.dto.ActionRuleCondition;

import java.util.Collection;

/**
 * <p>Interface used to specify the contract for processor classes.</p>
 * <p>A Processor class implementation is used to implement the logic used to evaluate action rules.</p>
 */
public interface ActionRulesProcessor {

    /**
     * Evaluates if the given action is valid for the given selection
     * @param action action to be evaluated
     * @param conditions conditions representing the user selection
     * @return boolean value indications if the action is valid or not
     */
    boolean isValid(final Action action, final Collection<ActionRuleCondition> conditions);

}
