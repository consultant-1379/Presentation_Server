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

import com.ericsson.nms.presentation.exceptions.*;
import com.ericsson.nms.presentation.exceptions.SecurityException;
import com.ericsson.nms.presentation.service.action.rules.processors.ActionRulesProcessor;
import com.ericsson.nms.presentation.service.api.dto.*;
import com.ericsson.nms.presentation.service.comparators.ActionComparator;
import com.ericsson.nms.presentation.service.instrumentation.MetricUtil;
import com.ericsson.nms.presentation.service.interceptors.MethodCallTimerInterceptor;
import com.ericsson.nms.presentation.service.persistence.dao.impl.ActionDao;
import com.ericsson.nms.presentation.service.persistence.dao.impl.ApplicationDao;
import com.ericsson.nms.presentation.service.security.SecurityUtil;
import com.ericsson.nms.presentation.service.security.cache.interceptors.bindings.CachedSecurity;
import com.ericsson.oss.itpf.sdk.recording.CommandPhase;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ericsson.nms.presentation.service.PresentationServerConstants.MAXIMUM_ACTION_CONDITIONS_LIMIT;
import static com.ericsson.nms.presentation.service.instrumentation.InstrumentableAction.ACTIONS_MATCHED;

/**
 * Action Service implementation class
 */
@Stateless
@CachedSecurity
@Interceptors({MethodCallTimerInterceptor.class})
public class ActionServiceEjb implements ActionService {

    private static final String RESOURCE_NAME = "ActionService";

    @Inject
    private ActionDao actionDao;

    @Inject
    private ApplicationDao applicationDao;

    @Inject
    private Logger logger;

    @Inject
    private ActionRulesProcessor rulesProcessor;

    @Inject
    private MetricUtil metricUtil;

    @Inject
    private SecurityUtil securityUtil;

    @Inject
    private SystemRecorder systemRecorder;

    @Inject
    private ActionComparator actionComparator;

    @Inject
    private DictionaryService dictionaryService;

    /**
     * {@inheritDoc}
     */
    public Collection<Action> getActionsProvidedByApp(final String appId) {

        try {
            getApplication(appId, true);

            final Collection<Action> result = new ArrayList<>();
            for (final Action action: actionDao.getAll()) {
                if (action.getApplicationId().equals(appId)) {
                    updateActionLocalizedTexts(action);
                    result.add(action);
                }
            }

            logger.info("Actions provided by application {}: {}", appId, result);
            return result;

        } catch (final PresentationServerException exception) {
            logger.error(exception.getMessage(), exception);
            systemRecorder.recordCommand("PresentationServer.getActionsProvidedByApp", CommandPhase.FINISHED_WITH_ERROR,
                    RESOURCE_NAME, appId, securityUtil.getCurrentUser());

            throw exception;

        } finally {
            systemRecorder.recordCommand("PresentationServer.getActionsProvidedByApp", CommandPhase.FINISHED_WITH_SUCCESS,
                    RESOURCE_NAME, appId, securityUtil.getCurrentUser());
        }

    }

    /**
     * {@inheritDoc}
     */
    public Collection<Action> getActionsConsumedByApp(final String appId) {

        try {
            final WebApplication app = getApplication(appId, false);
            cacheActionResources(app);

            final Collection<Action> actions =
                getAuthorizedConsumeActions(app).stream()
                    .filter(a -> (app.getConsumesActions() != null && (app.getConsumesActions().isEmpty() ||
                            app.getConsumesActions().contains(a.getName()))))
                    .distinct()
                    .map(this::updateActionLocalizedTexts)
                    .peek(a ->  {
                        if (a.getApplicationId().equals(appId)) { a.setLocal(true); }
                    })
                    .sorted(actionComparator)
                    .collect(Collectors.toList());

            logger.info("Actions consumed by application {}: {}", appId, actions);
            return actions;

        } catch (final PresentationServerException exception) {
            logger.error(exception.getMessage(), exception);
            systemRecorder.recordCommand("PresentationServer.getActionsConsumedByApp", CommandPhase.FINISHED_WITH_ERROR,
                    RESOURCE_NAME, appId, securityUtil.getCurrentUser());

            throw exception;

        } finally {
            systemRecorder.recordCommand("PresentationServer.getActionsConsumedByApp", CommandPhase.FINISHED_WITH_SUCCESS,
                    RESOURCE_NAME, appId, securityUtil.getCurrentUser());
        }

    }

    /**
     * {@inheritDoc}
     */
    public Collection<Action> getActionsBySelection(final String sourceApp, final boolean multipleSelection,
        final Collection<ActionRuleCondition> conditions) {

        if (conditions.size() > MAXIMUM_ACTION_CONDITIONS_LIMIT) {
            throw new ConditionsLimitException();
        }

        if (conditions.isEmpty()) {
            throw new NoConditionsProvidedException();
        }

        final String params = "sourceApp: "+ sourceApp +
                "; multipleSelection: "+ multipleSelection +
                "; conditions: "+ conditions;
        try {

            final Collection<Action> result = filterActionsBySelection(sourceApp, multipleSelection, conditions);

            logger.info("Actions available for the user {} on app {}: {}",
                    securityUtil.getCurrentUser(), sourceApp, result);
            metricUtil.measure(ACTIONS_MATCHED, result.size());
            return result;

        } catch (final PresentationServerException exception) {
            logger.error(exception.getMessage(), exception);

            systemRecorder.recordCommand("PresentationServer.getActionsBySelection", CommandPhase.FINISHED_WITH_ERROR,
                    RESOURCE_NAME, params, securityUtil.getCurrentUser());

            throw exception;

        } finally {
            systemRecorder.recordCommand("PresentationServer.getActionsBySelection", CommandPhase.FINISHED_WITH_SUCCESS,
                    RESOURCE_NAME, params, securityUtil.getCurrentUser());
        }
    }

    private boolean isActionAuthorized(final Action action) {
        // If action resources are declared, ignore application permissions
        if (!action.getResources().isEmpty()) {
            return securityUtil.hasAccessToAllResourcesAndActions(action.getResources());
        } else {
            return securityUtil.isAuthorisedApp(getApplication(action.getApplicationId(), false));
        }

    }

    private Set<Action> getAuthorizedConsumeActions(final WebApplication app) {
        return actionDao.getAll(app.getConsumesActions().toArray(new String[]{}))
            .stream()
            .filter(this::isActionAuthorized)
            .collect(Collectors.toSet());
    }

    private WebApplication getApplication(final String appId, final boolean validatePermissions) {
        final AbstractApplication app = applicationDao.get(appId);

        if (app == null) {
            throw new NotFoundException("No application was found with the ID: "+ appId);
        }

        if (!(app instanceof WebApplication)) {
            throw new InvalidApplicationTypeException("Only Web Applications can have actions");
        }

        if (validatePermissions && !securityUtil.isAuthorisedApp(app)) {
            throw new SecurityException("User "+securityUtil.getCurrentUser()+" does not have permission on the application "+ appId);
        }

        return (WebApplication)app;
    }

    private Collection<Action> filterActionsBySelection(final String sourceApp, final boolean multipleSelection,
                                                        final Collection<ActionRuleCondition> conditions) {

        final Collection<Action> result = new ArrayList<>();

        for (final Action action : getActionsConsumedByApp(sourceApp)) {
            try {
                // If the action requires single selection but the user selected more then one object
                if (!action.isMultipleSelection() && multipleSelection) {
                    logger.debug("Action {} requires single selection, but the user selected more than one objects. Skipping.",
                            action.getName());
                    continue;
                }

                // Process the available rules
                if (rulesProcessor.isValid(action, conditions)) {
                    // We don't need the rules to be returned
                    action.setRules(null);
                    updateActionLocalizedTexts(action);
                    result.add(action);
                } else {
                    logger.debug("Action {} discarded due rule restriction.", action.getName());
                }

            } catch (final SecurityException exception) {
                logger.debug("User {} does not have access to application {}", securityUtil.getCurrentUser(), action.getApplicationId());
            }
        }

        logger.debug("Filtered actions: {}", result);
        return result;
    }

    private Action updateActionLocalizedTexts(final Action action) {

        logger.info("Checking localization for action {}", action.getName());
        final Localization localization = dictionaryService.getLocalization(action.getApplicationId());

        if (localization != null && StringUtils.isNotEmpty(localization.getActionName(action.getName()))) {
            logger.debug("    -> Using action label from locale. {}", localization.getLocale());
            action.setDefaultLabel(localization.getActionName(action.getName()));
        }

        return action;
    }

    /*
     * Eagerly caches all resources required for actions consumed or provided by this application
     */
    private void cacheActionResources(final WebApplication app) {
        final Set<String> resources = new HashSet<>();
        Stream.concat(
                app.getProvidesActions().stream(),
                actionDao.getAll(app.getConsumesActions().toArray(new String[]{})).stream()
        ).forEach(a -> resources.addAll(a.getResources().stream().map(Resource::getName).collect(Collectors.toSet())));
        securityUtil.hasAccessToAnyResource(resources);
    }

}
