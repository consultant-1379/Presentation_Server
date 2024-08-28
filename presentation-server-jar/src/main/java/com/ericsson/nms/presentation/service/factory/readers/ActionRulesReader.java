/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.factory.readers;

import com.ericsson.nms.presentation.exceptions.JsonParserException;
import com.ericsson.nms.presentation.service.api.dto.ActionRule;
import com.ericsson.nms.presentation.service.json.parsers.JsonParser;
import com.ericsson.nms.presentation.service.util.ApplicationMetadataFinder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Reader class responsible to read the action rules JSON files.
 */
public class ActionRulesReader {

    @Inject
    private ApplicationMetadataFinder finder;

    @Inject
    private Logger logger;

    /**
     * Reads the JSON files and return action rules instances
     * @param appId filter to read only rules for the given app
     * @param actionName filter to read only rules for the given action
     * @return a collection with all the rules found with the given filter
     */
    public Collection<ActionRule> read(final String appId, final String actionName) {

        final Collection<ActionRule> rules = new ArrayList<>();
        for (File jsonResource : finder.getRulesResources(appId)) {

            try {
                final ActionRule rule = new JsonParser().parseToObject(jsonResource, ActionRule.class);
                if (StringUtils.equals(rule.getActionName(), actionName)) {
                    rules.add(rule);
                }
            } catch (JsonParserException exception) {
                logger.error(String.format("Failed to parse action rule file %s : %s",
                        jsonResource, exception.getMessage()), exception);
            }

        }
        return rules;
    }

}
