/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.factory;

import com.ericsson.nms.presentation.service.api.dto.Action;
import com.ericsson.nms.presentation.service.api.dto.Metadata;
import com.ericsson.nms.presentation.service.api.dto.WebApplication;
import com.ericsson.nms.presentation.service.factory.readers.ActionRulesReader;
import com.ericsson.nms.presentation.service.factory.readers.ApplicationJsonReader;
import org.slf4j.Logger;

import javax.inject.Inject;

/**
 * Class responsible to read the metadata files
 */
public class MetadataFactory {

    @Inject
    ApplicationJsonReader appJsonReader;

    @Inject
    ActionRulesReader rulesReader;

    @Inject
    Logger logger;

    /**
     * Import metadata
     * @return metadata
     */
    public MetadataImportWrapper importMetadata() {

        Metadata metadata = new Metadata();
        long readFailures = 0;

        MetadataImportWrapper jsonMetadataWrapper = new MetadataImportWrapper(new Metadata());
        try {
            jsonMetadataWrapper = appJsonReader.read();
        } catch (final Exception exception) {
            logger.error(exception.getMessage(), exception);
            readFailures++;
        }

        final Metadata jsonMetadata = jsonMetadataWrapper.getMetadata();

        // Reads the actions and rules
        for (WebApplication app : jsonMetadata.getWeb()) {

            if (app.getProvidesActions() != null) {
                for (final Action action : app.getProvidesActions()) {
                    action.setApplicationId(app.getId());
                    action.getRules().addAll(rulesReader.read(app.getId(), action.getName()));
                    metadata.getActions().add(action);
                }
            }
        }

        metadata.getGroups().addAll(jsonMetadata.getGroups());

        // Any application defined in XML and JSON with the same id, will be replaced by the json version.
        metadata.getWeb().removeAll(jsonMetadata.getWeb());
        metadata.getWeb().addAll(jsonMetadata.getWeb());

        metadata.getIca().removeAll(jsonMetadata.getIca());
        metadata.getIca().addAll(jsonMetadata.getIca());

        // Finally we bundle the metadata along with the metadata's metadata
        final MetadataImportWrapper metadataImportWrapper = new MetadataImportWrapper(metadata);
        // Sum up all file read failures
        metadataImportWrapper.setReadFailures(readFailures + jsonMetadataWrapper.getReadFailures());
        return metadataImportWrapper;
    }

}
