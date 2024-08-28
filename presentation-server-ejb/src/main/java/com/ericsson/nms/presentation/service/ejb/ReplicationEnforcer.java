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

import com.ericsson.nms.presentation.service.interceptors.MethodCallTimerInterceptor;
import com.ericsson.nms.presentation.service.persistence.dao.impl.UISettingsCacheBasedDAO;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

/**
 * Enforce cache initialization and replication
 * Fix: http://jira-nam.lmera.ericsson.se/browse/TORF-75021
 */
@Singleton
@Startup
@Interceptors({MethodCallTimerInterceptor.class})
public class ReplicationEnforcer {
    @Inject
    private UISettingsCacheBasedDAO uiSettingsCacheBasedDAO;

    /**
     * Initialize cache
     */
    @PostConstruct
    public void initializeCache() {
        //EDALINH: this is a dirty solution for the problem, we should look for alternatives for a persistent storage
        uiSettingsCacheBasedDAO.findByApplicationAndNameAndUsername( "launcher", "favorites", "administrator");
    }
}
