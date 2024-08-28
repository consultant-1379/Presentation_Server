/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service;

/**
 * General constants used in Presentatiuon Server
 */
public final class PresentationServerConstants {

    public static final String COLLECTION_ACTION_CATEGORY = "Collection Actions";

    public static final String COLLECTION_MODIFICATION_ACTION_CATEGORY = "Collection Modification Actions";

    public static final String FAULT_MANAGEMENT_ACTION_CATEGORY = "Fault Management Actions";

    public static final String MONITORING_ACTION_CATEGORY = "Monitoring & Troubleshooting Actions";

    public static final String CONFIGURATION_ACTION_CATEGORY = "Configuration Management";

    public static final String PERFORMANCE_ACTION_CATEGORY = "Performance Management";

    public static final String SECURITY_ACTION_CATEGORY = "Security Management";

    public static final String LEGACY_ACTION_CATEGORY = "Legacy Actions";

    public static final int MAXIMUM_ACTION_CONDITIONS_LIMIT = 1000;

    public static final int ACTION_METADATA_NAME_PROPERTY_LENGTH_LIMIT = 256;

    public static final int ACTION_METADATA_VALUE_PROPERTY_LENGTH_LIMIT = 256;

    public static final int ACTION_METADATA_SIZE_LIMIT = 16;

    private PresentationServerConstants() {
        // Private constructor to avoid any instance of this class
    }
}
