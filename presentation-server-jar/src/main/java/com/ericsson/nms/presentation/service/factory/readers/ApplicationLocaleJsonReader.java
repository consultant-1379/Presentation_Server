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
import com.ericsson.nms.presentation.service.api.dto.ApplicationDictionary;
import com.ericsson.nms.presentation.service.api.dto.Localization;
import com.ericsson.nms.presentation.service.json.parsers.JsonParser;
import com.ericsson.nms.presentation.service.json.parsers.dto.ApplicationLocalizationJson;
import com.ericsson.nms.presentation.service.util.ApplicationMetadataFinder;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Reader used to parse the localization files for applications
 */
public class ApplicationLocaleJsonReader {

    @Inject
    private ApplicationMetadataFinder finder;

    @Inject
    private JsonParser jsonParser;

    @Inject
    private Logger logger;

    /**
     * Builds a collection with all application dictionaries extracted from the file system.
     * @return all available dictionaries
     */
    public Collection<ApplicationDictionary> getDictionaries() {

        final Collection<ApplicationDictionary> result = new ArrayList<>();
        for (final String app : getAvailableApplications()) {

            final ApplicationDictionary dictionary = new ApplicationDictionary(app);
            for (final File localeDir : finder.getLocalesDirectories(app)) {

                final String locale = localeDir.getName();

                final File appLocaleFile = new File(localeDir.getAbsolutePath()+ "/app.json");
                final File actionsLocaleFile = new File(localeDir.getAbsolutePath()+ "/app_actions.json");

                final Localization localization = new Localization(locale);
                if (appLocaleFile.exists()) {
                    final ApplicationLocalizationJson appJson;
                    try {
                        appJson = jsonParser.parseToObject(appLocaleFile, ApplicationLocalizationJson.class);
                    } catch (JsonParserException exception) {
                        logger.error("Skipping invalid JSON file {}: \n\t{}", appLocaleFile, exception.getMessage());
                        continue;
                    }
                    localization.setAcronym(appJson.getAcronym());
                    localization.setDescription(appJson.getDescription());
                    localization.setTitle(appJson.getTitle());
                }

                if (actionsLocaleFile.exists()) {
                    final JsonNode jsonActions;
                    try {
                        jsonActions = jsonParser.readNode(actionsLocaleFile);
                    } catch (JsonParserException exception) {
                        logger.error("Skipping invalid JSON file {}: \n\t{}", actionsLocaleFile, exception.getMessage());
                        continue;
                    }
                    final Iterator<Map.Entry<String,JsonNode>> actionsIterator = jsonActions.fields();
                    while (actionsIterator.hasNext()) {
                        final Map.Entry<String,JsonNode> action = actionsIterator.next();
                        localization.addActionName(action.getKey(), action.getValue().path("label").asText());
                    }

                }

                dictionary.addLocalization(locale, localization);
            }
            result.add(dictionary);
        }
        return result;

    }

    /**
     * Extract the available applications from the file system.
     * @return available apps
     */
    private Set<String> getAvailableApplications() {

        File[] appsDirectories = finder.getAppsDirectories();
        if (appsDirectories == null ) {
            return Collections.emptySet();
        } else {
            return Arrays.stream(finder.getAppsDirectories())
                    .map(File::getName)
                    .collect(Collectors.toSet());
        }
    }

}
