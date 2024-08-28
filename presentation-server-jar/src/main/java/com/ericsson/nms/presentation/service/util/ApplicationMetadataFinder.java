/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This class is responsible to retrieve the metadata files available on the file system.
 */
public class ApplicationMetadataFinder {

    private static final String METADATA_DIR = "/ericsson/tor/data";
    private static final String XML_FILES_LOCATION_PROPERTY = "presentation.server.applications.xml.directory";

    @Inject
    protected Logger logger;

    /**
     * Gets all the application json files
     * @return collection with all application files on the file system
     */
    public Collection<File> getApplicationJsonResources() {

        final Collection<File> result = new ArrayList<>();

        final File[] directories = getAppsDirectories();
        if (directories != null) {
            for (final File appDir : directories) {
                if (!Files.isReadable(appDir.toPath())) {
                    logger.error("Directory {} cannot be read by {}", appDir.getAbsolutePath(), this.getClass().getName());
                    continue;
                }
                final File[] jsonFiles = appDir.listFiles(filePath -> filePath.getName().endsWith("json") && filePath.isFile());
                if(jsonFiles == null) {
                    logger.error("Abstract pathname {} does not denote a directory in {}", appDir.getName(), this.getClass().getName());
                    continue;
                }
                for (final File jsonFile : jsonFiles) {
                    logger.info("Found application file: {}", jsonFile.getName());
                    result.add(jsonFile);
                }
            }
        }

        return result;
    }

    /**
     * Gets all the action rules files
     *
     * @param appId {String}
     * @return collection with all action rules for all applications on the file system
     */
    public Collection<File> getRulesResources(final String appId) {

        final Collection<File> result = new ArrayList<>();
        for (final File appDir : getAppsDirectories()) {

            if (StringUtils.equals(appDir.getName(), appId)) {

                final File rulesDir = new File(appDir.getAbsolutePath() + "/actions/rules");
                final File[] jsonFiles = rulesDir.listFiles(filePath -> filePath.getName().endsWith("json") && filePath.isFile());
                if (jsonFiles != null) {
                    for (final File jsonFile : jsonFiles) {
                        logger.info("Found action rule file: {}", jsonFile.getName());
                        result.add(jsonFile);
                    }
                }
            }
        }

        return result;
    }

    /**
     * Retrieves the root metadata directory path
     * @return path to the root metadata path
     */
    public String getMetadataDirectory() {
        final String directory = System.getProperty(XML_FILES_LOCATION_PROPERTY);
        if (StringUtils.isEmpty(directory)) {
            return METADATA_DIR;
        } else {
            return directory;
        }
    }

    /**
     * Retrieves all application directories on the file system (folders under /apps).
     * @return All applications sub directories
     */
    public File[] getAppsDirectories() {
        final String root = getMetadataDirectory() + "/apps";
        final File[] directories = new File(root).listFiles(File::isDirectory);
        logger.info("Found {} application directories under {}: \n{}", directories == null ? 0 : directories.length, root, directories);

        return directories != null ? directories : new File[]{};
    }

    /**
     * Get the locales files (application and actions) for a given application in all available locales
     * @param application application to be queried
     * @return all the locale files for the application
     */
    public File[] getLocalesDirectories(final String application) {
        final String root = getMetadataDirectory() + "/apps/"+ application +"/locales";
        final File[] directories = new File(root).listFiles(File::isDirectory);
        logger.info("Found {} locales for the application {}: \n{}", directories == null ? 0 : directories.length, root, directories);
        return directories != null ? directories : new File[]{};
    }
}
