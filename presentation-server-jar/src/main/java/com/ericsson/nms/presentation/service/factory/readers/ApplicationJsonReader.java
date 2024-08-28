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

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.ericsson.nms.presentation.exceptions.JsonParserException;
import com.ericsson.nms.presentation.service.PresentationServerConstants;
import com.ericsson.nms.presentation.service.api.dto.*;
import com.ericsson.nms.presentation.service.factory.MetadataImportWrapper;
import com.ericsson.nms.presentation.service.json.parsers.JsonParser;
import com.ericsson.nms.presentation.service.json.parsers.dto.ApplicationJson;
import com.ericsson.nms.presentation.service.json.parsers.dto.GroupJson;
import com.ericsson.nms.presentation.service.util.*;

/**
 * Reader class responsible to read the applications metadata on JSON files
 */
public class ApplicationJsonReader {

    @Inject
    ApplicationMetadataFinder finder;

    @Inject
    Logger logger;

    @Inject
    ActionCategoryUtil actionCategoryUtil;

    @Inject
    FileHashUtil hashUtil;

    /**
     * Reads all the applications available on the file system
     * @return Metadata object containing all applications, groups and actions
     */
    public MetadataImportWrapper read() {

        final Map<String,Group> groups = new HashMap<>();

        long readFailures = 0;

        final Metadata metadata = new Metadata();
        for (File jsonResource : finder.getApplicationJsonResources()) {
            ApplicationJson appJson;
            try {
                appJson = new JsonParser().parseToObject(jsonResource, ApplicationJson.class);
            } catch (JsonParserException e) {
                logger.error("Failed to read application {}: {}", jsonResource.getName(), e.getMessage());
                readFailures++;
                continue;
            }

            String hash;
            try {
                hash = hashUtil.getHash(jsonResource);
            } catch (final IOException exception) {
                logger.error("Failed to extract the hash from {}: {}", jsonResource.getName(), exception.getMessage());
                readFailures++;
                continue;
            }

            try {
                final AbstractApplication app = parseApplication(appJson);
                app.setHash(hash);
                if (app instanceof CitrixApplication) {
                    metadata.getIca().add((CitrixApplication) app);
                } else {
                    metadata.getWeb().add((WebApplication) app);
                }
            } catch (IllegalArgumentException exception) {
                logger.error(String.format("Error parsing file %s: %s", jsonResource, exception.getMessage()), exception);
                readFailures++;
                continue;
            }

            updateGroups(appJson, groups);
        }

        metadata.setGroups(groups.values());
        return new MetadataImportWrapper(metadata, readFailures);
    }

    private void updateGroups(final ApplicationJson appJson, final Map<String,Group> groups) {

        for (GroupJson groupJson : appJson.getGroups()) {
            Group group = groups.get(groupJson.getId());
            if (group == null) {
                group = new Group(groupJson.getId(), groupJson.getName(),
                    Stream.of(appJson.getId()).collect(Collectors.toSet()));
            } else {
                group.getAppIds().add(appJson.getId());
            }
            groups.put(group.getId(), group);
        }

    }

    private AbstractApplication parseApplication(final ApplicationJson appJson) {

        AbstractApplication app;
        if (appJson.getId() == null) {
            throw new IllegalArgumentException("No ID was provided for the application");
        }

        if (appJson.getPath() == null) {
            throw new IllegalArgumentException("No path was provided for the application");
        }

        if (appJson.getGroups() == null || appJson.getGroups().isEmpty()) {
            throw new IllegalArgumentException("The application must belong to at least one group.");
        }

        if ("web".equalsIgnoreCase(appJson.getType())) {
            app = new WebApplication(appJson.getId(), appJson.getName());
            ((WebApplication)app).setPath(appJson.getPath());
            ((WebApplication)app).setHost(appJson.getHost());
            app.setExternal(appJson.getExternal());
            app.setVersion(appJson.getVersion());
            ((WebApplication)app).setProtocol(appJson.getProtocol());
            ((WebApplication)app).setOpenInNewWindow(appJson.isOpenInNewWindow());
            if (appJson.getPort() != null) {
                ((WebApplication)app).setPort(Long.toString(appJson.getPort()));
            }
            if (appJson.getExternalHost() != null) {
                ((WebApplication)app).setExternalHost(appJson.getExternalHost());
            }
            ((WebApplication)app).setProvidesActions(getValidActions(appJson.getProvideActions()));
            ((WebApplication)app).setConsumesActions(appJson.getConsumeActions());

        } else if ("citrix".equalsIgnoreCase(appJson.getType())) {
            app = new CitrixApplication(appJson.getId(), appJson.getName());
            ((CitrixApplication) app).setHost(appJson.getHost());
            ((CitrixApplication) app).setParams(appJson.getParams());
            ((CitrixApplication) app).setParamHelp(appJson.getParamsHelp());

        } else {
            throw new IllegalArgumentException("The type attribute should be web or citrix!");
        }

        app.setAcronym(appJson.getAcronym());
        app.setHidden(appJson.isHidden());
        app.setResources(join(appJson.getResources(),false));
        app.setShortInfo(appJson.getShortInfo());
        app.setVersion(appJson.getVersion());

        return app;
    }

    /**
     * Validates the actions defined in the metadata file and discard the invalid ones
     * @param actions actions to be evaluated
     * @return only the valid actions
     */
    private List<Action> getValidActions(final List<Action> actions) {

        if (actions == null) {
            return new ArrayList<>();
        }
        final List<Action> validActions = new ArrayList<>();

        for (final Action action : actions) {

            // Name is required
            if (StringUtils.isEmpty(action.getName())) {
                logger.error("There's an action provided by the application {} without a name and was considered invalid. " +
                        "This action will be skipped.\n\t{}", action.getApplicationId(), action);

                continue;
            }

            // Plugin is required
            if (StringUtils.isEmpty(action.getPlugin())) {
                logger.error("There's an action provided by the application {} without a plugin path and was considered invalid. " +
                        "This action will be skipped. \n\t{}", action.getApplicationId(), action);

                continue;
            }

            // Category is required
            if (StringUtils.isEmpty(action.getCategory())) {
                logger.error("There's an action provided by the application {} without a category and was considered invalid. " +
                        "This action will be skipped. \n\t{}", action.getApplicationId(), action);

                continue;
            }

            // Category is supported
            if (!actionCategoryUtil.isCategorySupported(action.getCategory())) {
                logger.error("There's an action provided by the application {} using an unsupported category ({}) " +
                                "and was considered invalid. This action will be skipped. \n\t{}",
                        action.getApplicationId(), action.getCategory(), action);

                continue;
            }

            if (action.getResources() == null) {
                action.setResources(new HashSet<>());
            }

            // Validation of Metadata
            if (null != action.getMetadata() && !(action.getMetadata().isEmpty()) && !(validateActionMetaData(action))){
                   continue;
            }

            validActions.add(action);
        }

        return validActions;

    }

    private boolean validateActionMetaData(final Action  action) {
        boolean valid=true;
        final Set<ActionMetadata> actionMetadataSet = new HashSet<>(action.getMetadata());
        if (actionMetadataSet.size() != action.getMetadata().size()) {
            logger.error("There's an action {} provided by the application {} could not be loaded because duplicate value "+
                    "for name attribute in metadata. This action will be skipped. \n\t{}",
                action.getName(), action.getApplicationId(), action);
            valid = false;
            return valid;
        }else if (action.getMetadata().size() > PresentationServerConstants.ACTION_METADATA_SIZE_LIMIT) {
            logger.error("There's an action {} provided by the application {} could not be loaded because metadata "+
                    "size having more than 16. This action will be skipped. \n\t{}",
                action.getName(), action.getApplicationId(), action);
            valid = false;
            return valid;
        }
        for (ActionMetadata actionMetadata : action.getMetadata()) {
            if (StringUtils.isEmpty(actionMetadata.getName()) || StringUtils.isEmpty(actionMetadata.getValue())) {
                logger.error("There's an action {} provided by the application {} could not be loaded because "+
                        "metadata attributes either name or value or both are not present. This action will be skipped. \n\t{}",
                    action.getName(), action.getApplicationId(), action);
                valid = false;
                return valid;
            }else if (actionMetadata.getName().length() > PresentationServerConstants.ACTION_METADATA_NAME_PROPERTY_LENGTH_LIMIT ||
                actionMetadata.getValue().length() > PresentationServerConstants.ACTION_METADATA_VALUE_PROPERTY_LENGTH_LIMIT) {
                logger.error("There's an action {} provided by the application {} could not be loaded because metadata attributes either "+
                        "name or value or both values having more than 256 characters in length. This action will be skipped. \n\t{}",
                    action.getName(), action.getApplicationId(), action);
                valid = false;
                return valid;
            }
        }
        return valid;
    }

    private String join(final Collection<String> strList, final boolean withQuotes) {

        if (strList == null || strList.isEmpty()) {
            return null;
        }

        final StringJoiner joiner = new StringJoiner(",");
        strList.forEach(str -> joiner.add(withQuotes ? "\""+ str + "\"" : str));

        return joiner.toString();
    }

}
