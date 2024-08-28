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

import com.ericsson.nms.presentation.service.api.dto.ApplicationDictionary;
import com.ericsson.nms.presentation.service.cache.PresentationServerCacheProvider;
import com.ericsson.nms.presentation.service.interceptors.MethodCallTimerInterceptor;
import com.ericsson.nms.presentation.service.persistence.dao.AbstractCacheDao;

import javax.cache.Cache;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

/**
 * Datata integration layer for ApplicationDictionary.
 */
@Interceptors({MethodCallTimerInterceptor.class})
public class ApplicationDictionaryDao extends AbstractCacheDao<String, ApplicationDictionary> {

    @Inject
    private PresentationServerCacheProvider cacheProvider;

    @Override
    protected Cache<String, ApplicationDictionary> getCache() {
        return cacheProvider.getApplicationDictionaryCache();
    }
}
