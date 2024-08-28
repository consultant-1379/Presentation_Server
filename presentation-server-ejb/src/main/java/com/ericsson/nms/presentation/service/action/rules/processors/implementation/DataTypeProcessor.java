/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/
package com.ericsson.nms.presentation.service.action.rules.processors.implementation;

import com.ericsson.nms.presentation.service.action.rules.processors.ActionRulesProcessor;
import com.ericsson.nms.presentation.service.api.dto.Action;
import com.ericsson.nms.presentation.service.api.dto.ActionRule;
import com.ericsson.nms.presentation.service.api.dto.ActionRuleCondition;
import com.ericsson.nms.presentation.service.api.dto.Property;
import com.ericsson.nms.presentation.service.interceptors.MethodCallTimerInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * <p>Default implementation of {@link ActionRulesProcessor}</p>
 * <p>This implementation is a simple matcher between the selection data type and properties and rule</p>
 */
@Interceptors({MethodCallTimerInterceptor.class})
public class DataTypeProcessor implements ActionRulesProcessor {

    @Inject
    private Logger logger;

    private static final String REGEX_PREFIX = "re:";

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(final Action action, final Collection<ActionRuleCondition> conditions) {

        for (final ActionRuleCondition selection : conditions) {
            if (!isSelectionValidAgainstRules(action.getRules(), selection)) {
                logger.debug("Discarding action {} as all the selections must be valid for the action be available.", action);
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if the selection is valid against any of the action rules
     */
    private boolean isSelectionValidAgainstRules(final Collection<ActionRule> rules, final ActionRuleCondition selection) {

        logger.debug("Evaluating selection [{}] against rules [{}]", selection, rules);
        for (final ActionRule rule : rules) {

            logger.debug("Processing rule: {}", rule);
            if (rule.getCondition().getDataType().equalsIgnoreCase(selection.getDataType()) &&
                    rulePropertiesMatch(rule, selection)) {

                return true;
            }
        }

        logger.debug("No rule that matches the given selection was found. Returning FALSE.");
        return false;
    }

    /**
     * Checks if the properties available in the selection match the requirements for the rule.
     * If the rule does not require properties, returns true.
     */
    private boolean rulePropertiesMatch(final ActionRule rule, final ActionRuleCondition selection) {

        if (rule.getCondition().getProperties().isEmpty()) {
            return true;

        }

        if (hasAllRequiredProperties(rule.getCondition().getProperties(), selection.getProperties()) &&
                selection.getProperties().stream().allMatch(getPropertyMatcherPredicate(rule))) {
            return true;

        }

        return false;
    }

    private Predicate<Property> getPropertyMatcherPredicate(final ActionRule rule) {

        // Predicate to find in the selection properties, a match with the current rule (property name and value)
        return selectionProperty -> {

            // The rule condition should have zero-or-one match per property name.
            // This filter will return only the property with name matching the selection
            final Optional<Property> ruleProperty = rule.getCondition().getProperties().stream()
                    .filter(p -> p.getName().equalsIgnoreCase(selectionProperty.getName()))
                    .findFirst();

            if (ruleProperty.isPresent()) {
                // If a matching condition/selection is found and the property values match, then the rule is accepted.
                return ruleProperty.filter(ruleProp -> propertyMatches(ruleProp, selectionProperty)).isPresent();
            }

            return true;
        };
    }

    private boolean hasAllRequiredProperties(final Collection<Property> ruleProperties, final Collection<Property> selectionProperties) {

        return ruleProperties.stream()
            .map(Property::getName)
            .allMatch(
                ruleName -> selectionProperties.stream()
                    .map(Property::getName)
                    .anyMatch( selectionName -> selectionName.equals(ruleName))
            );

    }

    private boolean isRegexMatcher(final Property property) {
        return StringUtils.startsWith(property.getValue(), REGEX_PREFIX);
    }

    private boolean propertyMatches(final Property ruleProperty, final Property selectionProperty) {
        if (isRegexMatcher(ruleProperty)) {
            final String regex = StringUtils.substring(ruleProperty.getValue(), REGEX_PREFIX.length());
            return selectionProperty.getValue().matches(regex);

        } else {
            return ruleProperty.getValue().equals(selectionProperty.getValue());
        }
    }

}