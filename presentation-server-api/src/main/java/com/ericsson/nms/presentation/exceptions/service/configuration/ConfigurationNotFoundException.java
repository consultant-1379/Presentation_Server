/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2019
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.presentation.exceptions.service.configuration;

import com.ericsson.nms.presentation.exceptions.service.EntityNotFoundException;

/**
 * Exception raised when a configuration with a given key is not found in the database
 */
public class ConfigurationNotFoundException extends EntityNotFoundException {

    public ConfigurationNotFoundException(final String key) {
        super("configuration", key);
    }

}
