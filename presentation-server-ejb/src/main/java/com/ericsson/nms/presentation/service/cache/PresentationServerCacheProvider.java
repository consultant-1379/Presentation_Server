/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.cache;

import com.ericsson.nms.presentation.service.api.dto.AbstractApplication;
import com.ericsson.nms.presentation.service.api.dto.Action;
import com.ericsson.nms.presentation.service.api.dto.ApplicationDictionary;
import com.ericsson.nms.presentation.service.api.dto.Group;
import com.ericsson.nms.presentation.service.api.dto.SystemProperty;

import javax.annotation.PostConstruct;
import javax.cache.Cache;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;

/**
 * Class responsible to provide Cache implementations used on Presentation Server.
 */
@Singleton(name = "PresentationServerCacheProvider")
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@Startup
public class PresentationServerCacheProvider {

    public static final String APP_CACHE_NAME = "applications.cache";
    public static final String GROUPS_CACHE_NAME = "groups.cache";
    public static final String ACTIONS_CACHE_NAME = "actions.cache";
    public static final String SYSTEM_PROPERTY_CACHE_NAME = "system.property.cache";
    public static final String APP_DICTIONARY_CACHE_NAME = "dictionary.cache";

    @Inject
    CacheFactory cacheFactory;

    private Cache<String, AbstractApplication> applicationCache;

    private Cache<String,Group> groupsCache;

    private Cache<String,Action> actionsCache;

    private Cache<String,SystemProperty> systemPropertyCache;

    private Cache<String,ApplicationDictionary> applicationDictionaryCache;

    /**
     * Method called when this class is instantiated by the container.
     * Starts all the required caches.
     */
    @PostConstruct
    public void startup() {

        applicationCache = cacheFactory.create(APP_CACHE_NAME);
        groupsCache = cacheFactory.create(GROUPS_CACHE_NAME);
        actionsCache = cacheFactory.create(ACTIONS_CACHE_NAME);
        systemPropertyCache = cacheFactory.create(SYSTEM_PROPERTY_CACHE_NAME);
        applicationDictionaryCache = cacheFactory.create(APP_DICTIONARY_CACHE_NAME);

    }

    /**
     * Retrieves the applications cache implementation
     * @return applications cache
     */
    @Produces
    @CacheName(APP_CACHE_NAME)
    public Cache<String,AbstractApplication> getApplicationsCache() {
        return applicationCache;
    }

    /**
     * Retrieves the groups cache implementation
     * @return groups cache
     */
    @Produces
    @CacheName(GROUPS_CACHE_NAME)
    public Cache<String,Group> getGroupsCache() {
        return groupsCache;
    }

    /**
     * Retrieves the actions cache implementation
     * @return groups cache
     */
    @Produces
    @CacheName(ACTIONS_CACHE_NAME)
    public Cache<String,Action> getActionsCache() {
        return actionsCache;
    }

    /**
     * Retrieves the System Property cache implementation
     * @return cache instance
     */
    @Produces
    @CacheName(SYSTEM_PROPERTY_CACHE_NAME)
    public Cache<String,SystemProperty> getSystemPropertyCache() {
        return systemPropertyCache;
    }

    /**
     * Retrieves the Application Dictionary cache implementation
     * @return cache instance
     */
    @Produces
    @CacheName(APP_DICTIONARY_CACHE_NAME)
    public Cache<String,ApplicationDictionary> getApplicationDictionaryCache() {
        return applicationDictionaryCache;
    }

}

