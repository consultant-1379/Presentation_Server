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
package com.ericsson.nms.presentation.service.security.cache.interceptors;

import com.ericsson.nms.presentation.service.security.cache.SecurityCache;
import com.ericsson.nms.presentation.service.security.cache.interceptors.bindings.CachedSecurity;
import org.slf4j.Logger;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 * <p>This interceptor guarantees that by the end of each intercepted {@link InvocationContext}
 * the caches are cleared.</p>
 * <p>This is required because the application server uses thread pools, so the threads can be reused and as
 * our cache is thread based this could cause the user to have permissions created in a previous thread usage.</p>
 */
@Interceptor
@CachedSecurity
@Priority(Interceptor.Priority.APPLICATION)
public class SecurityCacheCleaner {

    @Inject
    private SecurityCache securityCache;

    @Inject
    private Logger logger;

    @AroundInvoke
    public Object cleanup(final InvocationContext context) throws Exception {
        try {
            return context.proceed();
        } finally {
            securityCache.flushCache();
            logger.debug("Flushing cache for thread {}", Thread.currentThread().getName());
        }
    }

}
