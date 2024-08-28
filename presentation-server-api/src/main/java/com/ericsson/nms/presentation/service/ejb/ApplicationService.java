/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.presentation.service.ejb;

import com.ericsson.nms.presentation.service.api.dto.AbstractApplication;
import com.ericsson.nms.presentation.service.api.dto.Group;

import javax.ejb.Local;
import java.util.Collection;

/**
 * Application service interface
 */
@Local
public interface ApplicationService {

    /**
     * Gets the applications declared on the XMl file.
     *
     * @param ids filters the applications list to return only the applications with the given ids
     * @return A list of found applications
     */
    Collection<AbstractApplication> getApps(String... ids);

    /**
     * Get all the groups
     *
     * @param keys {String...}
     * @return Collection of groups
     */
    Collection<Group> getGroups(String... keys);

    /**
     * Generates an ICA file to enable the application to launch
     *
     * @param appId  The id of the application to launch
     * @return byte[] representing the launch data
     */
    AbstractApplication getApplication(String appId);

}
