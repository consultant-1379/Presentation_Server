/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.instrumentation;

/**
 * Enum containing the instrumented actions.
 */
public enum InstrumentableAction {

    /* Logging Service*/
    LOGS_CREATED("logsCreated"),                                // Counter

    /* Application Service */
    APPLICATION_LAUNCHES("applicationLaunches"),                // Counter
    APPLICATIONS_DEPLOYED("applicationsDeployed"),              // Histogram (Last Value)
    APPLICATION_METADATA_READ_FAILURES("metadataReadFailures"), // Counter

    /* Actions Service */
    ACTIONS_MATCHED("actionsMatched"),                          // Histogram (Average, Total)

    /* System Time Service */
    SYSTEM_TIME_HITS("systemTimeHits"),                         // Counter

    /* System Info Service */
    SYSTEM_INFO_HITS("systemInfoHits"),                         // Counter

    /* UI Settings Service */
    SETTINGS_GET_HITS("settingsGetHits"),                       // Counter
    SETTINGS_GET_TIMES("settingsGetTimes"),                     // Timer
    SETTINGS_SET_HITS("settingsSetHits"),                       // Counter
    SETTINGS_SET_TIMES("settingsSetTimes"),                     // Timer
    SETTINGS_DATA_SIZE("settingsDataSize");                     // Histogram (Last Value)

    private String name;

    /**
     * Constructor
     * @param name {String}
     */
    InstrumentableAction(final String name) {
        this.name = name;
    }

    public String getTimerName() {
        return this.name + "Timer";
    }
}
