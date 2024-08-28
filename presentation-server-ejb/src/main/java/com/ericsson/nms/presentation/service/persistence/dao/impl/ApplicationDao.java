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

import com.ericsson.nms.presentation.service.api.dto.AbstractApplication;
import com.ericsson.nms.presentation.service.cache.PresentationServerCacheProvider;
import com.ericsson.nms.presentation.service.interceptors.MethodCallTimerInterceptor;
import com.ericsson.nms.presentation.service.persistence.dao.AbstractCacheDao;

import javax.cache.Cache;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

/**
 * Cache DAO implemenattion for the Application Cache
 */
@ApplicationScoped
@Interceptors({MethodCallTimerInterceptor.class})
public class ApplicationDao extends AbstractCacheDao<String, AbstractApplication> {

    @Inject
    private PresentationServerCacheProvider cacheProvider;

    @Override
    protected Cache<String, AbstractApplication> getCache() {
        return cacheProvider.getApplicationsCache();
    }

    /**
     * Retrieves the number of applications in the cache
     * @return number of cached applications
     */
    public long getApplicationCount() {
        long count = 0;
        for (final Cache.Entry entry : cacheProvider.getApplicationsCache()) {
            if (entry != null) {
                count++;
            }
        }
        return count;
    }

}
