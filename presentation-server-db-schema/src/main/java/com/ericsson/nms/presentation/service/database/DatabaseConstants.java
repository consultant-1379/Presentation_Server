/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.presentation.service.database;

/**
 * Constants class used to store database related constants.
 */
public class DatabaseConstants {

    /**
     * The JNDI path on which the data source was defined in JBoss
     */
    public static final String DATA_SOURCE_JNDI_PATH = "java:jboss/datasources/presentation-ds";

    /**
     * The JNDI path on which the data source was defined in JBoss for upgrading the DB
     */
    public static final String UPGRADE_DATA_SOURCE_JNDI_PATH = "java:jboss/datasources/upgrade-presentation-ds";

    /**
     * The resource path to the default changelog master file for liquibase
     */
    public static final String CHANGELOG_MASTER_DEFAULT_PATH = "db-changelog/db.changelog-master.xml";

}
