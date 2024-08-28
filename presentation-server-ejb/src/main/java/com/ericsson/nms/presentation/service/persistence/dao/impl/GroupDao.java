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

import com.ericsson.nms.presentation.service.api.dto.Group;
import com.ericsson.nms.presentation.service.cache.PresentationServerCacheProvider;
import com.ericsson.nms.presentation.service.interceptors.MethodCallTimerInterceptor;
import com.ericsson.nms.presentation.service.persistence.dao.AbstractCacheDao;

import javax.cache.Cache;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

/**
 * Cache DAO implemenattion for the Groups Cache
 */
@ApplicationScoped
@Interceptors({MethodCallTimerInterceptor.class})
public class GroupDao extends AbstractCacheDao<String, Group> {

    @Inject
    private PresentationServerCacheProvider cacheProvider;

    @Override
    protected Cache<String, Group> getCache() {
        return cacheProvider.getGroupsCache();
    }

}
