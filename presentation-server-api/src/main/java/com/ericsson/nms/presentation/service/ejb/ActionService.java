/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.ejb;

import com.ericsson.nms.presentation.service.api.dto.Action;
import com.ericsson.nms.presentation.service.api.dto.ActionRuleCondition;

import javax.ejb.Local;
import java.util.Collection;

/**
 * Service Facade responsible to provide actions
 */
@Local
public interface ActionService {

    /**
     * Gets all the actions provided by the given application
     * @param appId id of the application to be searched
     * @return collection with all the actions found
     * @throws com.ericsson.nms.presentation.exceptions.NotFoundException when the application is not found
     * @throws com.ericsson.nms.presentation.exceptions.InvalidApplicationTypeException when the applications is not an web application
     * @throws SecurityException when the user does not have access to the application
     */
    Collection<Action> getActionsProvidedByApp(final String appId);

    /**
     * Gets all the actions that can be used by the given application.
     * @param appId id of the application to be searched
     * @return collection with all the actions found
     * @throws com.ericsson.nms.presentation.exceptions.NotFoundException when the application is not found
     * @throws com.ericsson.nms.presentation.exceptions.InvalidApplicationTypeException when the applications is not an web application
     * @throws SecurityException when the user does not have access to the source application
     */
    Collection<Action> getActionsConsumedByApp(final String appId);

    /**
     * Gets all actions that can be used by the user in the given application
     * @param sourceApp source application
     * @param multipleSelection indicates if more than one object were selected
     * @param conditions conditions characteristics of the selected objects
     * @return collection with all the actions found and allowed to the user.
     * @throws com.ericsson.nms.presentation.exceptions.NotFoundException when the application is not found
     * @throws com.ericsson.nms.presentation.exceptions.InvalidApplicationTypeException when the applications is not an web application
     * @throws SecurityException when the user does not have access to the source or targeted application
     */
    Collection<Action> getActionsBySelection(final String sourceApp, final boolean multipleSelection,
                          final Collection<ActionRuleCondition> conditions);

}
