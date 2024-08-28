/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.ejb;

import com.ericsson.nms.presentation.service.api.dto.SystemProperty;

import java.util.Collection;

/**
 * Service responsible to provide ENM system information.
 */
public interface SystemInfoService {

    /**
     * Retrieves the system property with the given name
     * @param propertyName name to be searched
     * @return the value of the property
     */
    SystemProperty getSystemProperty(final String propertyName);

    /**
     * Retrieves all available properties
     * @return All properties
     */
    Collection<SystemProperty> getAllSystemProperties();

}
