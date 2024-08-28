/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.locale;

import com.ericsson.nms.presentation.service.interceptors.MethodCallTimerInterceptor;
import com.ericsson.nms.presentation.service.security.SecurityUtil;
import com.ericsson.oss.itpf.sdk.context.ContextService;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.util.LinkedHashSet;

/**
 * EJB implementation for LocaleService.
 */
@Stateless
@SuppressWarnings("PMD.LooseCoupling")
@Interceptors({MethodCallTimerInterceptor.class})
public class LocaleServiceEjb implements LocaleService {

    private static final String LOCALE_CONTEXT_KEY = "ps.locale";

    @Inject
    private ContextService context;

    @Inject
    private Logger logger;

    @Inject
    SecurityUtil securityUtil;

    /**
     * Default locale to be used when no user locale is defined.
     */
    private static final String DEFAULT_LOCALE = "en-us";

    /**
     * {@inheritDoc}
     */
    public void setUserLocale(final LinkedHashSet<String> locales) {
        logger.debug("Setting locales to {} for user {}", locales, securityUtil.getCurrentUser());
        context.setContextValue(LOCALE_CONTEXT_KEY, locales);
    }

    /**
     * {@inheritDoc}
     */
    public LinkedHashSet<String> getUserLocales() {

        LinkedHashSet<String> locales = context.getContextValue(LOCALE_CONTEXT_KEY);
        if (locales == null) {
            logger.debug("No locale was set for user {}. Using default {}.", securityUtil.getCurrentUser(), DEFAULT_LOCALE);
            locales = new LinkedHashSet<>();
        }

        // If the default locale is not in the list, add it to the end
        if (!locales.contains(DEFAULT_LOCALE)) {
            locales.add(DEFAULT_LOCALE);
        }

        logger.debug("Retrieving locales {} for user {}", locales, securityUtil.getCurrentUser());
        return locales;
    }

}
