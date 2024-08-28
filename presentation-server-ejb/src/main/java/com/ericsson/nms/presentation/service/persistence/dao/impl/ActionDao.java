/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.persistence.dao.impl;

import com.ericsson.nms.presentation.service.api.dto.Action;
import com.ericsson.nms.presentation.service.cache.CacheName;
import com.ericsson.nms.presentation.service.interceptors.MethodCallTimerInterceptor;
import com.ericsson.nms.presentation.service.persistence.dao.AbstractCacheDao;

import javax.cache.Cache;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import static com.ericsson.nms.presentation.service.cache.PresentationServerCacheProvider.ACTIONS_CACHE_NAME;

/**
 * Cache DAO implemenattion for the Actions Cache
 */
@Interceptors({MethodCallTimerInterceptor.class})
public class ActionDao extends AbstractCacheDao<String, Action> {

    @Inject
    @CacheName(ACTIONS_CACHE_NAME)
    private Cache<String,Action> cache;


    @Override
    protected Cache<String, Action> getCache() {
        return cache;
    }

}
