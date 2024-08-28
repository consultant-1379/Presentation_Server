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

import com.ericsson.nms.presentation.exceptions.DictionaryUnavailableException;
import com.ericsson.nms.presentation.service.api.dto.ApplicationDictionary;
import com.ericsson.nms.presentation.service.api.dto.Localization;
import com.ericsson.nms.presentation.service.interceptors.MethodCallTimerInterceptor;
import com.ericsson.nms.presentation.service.locale.SupportedLocales;
import com.ericsson.nms.presentation.service.persistence.dao.impl.ApplicationDictionaryDao;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.util.Collections;
import java.util.Set;

/**
 * Implementation class for {@link DictionaryService}
 * @see DictionaryService
 */
@Stateless
@Interceptors({MethodCallTimerInterceptor.class})
public class DictionaryServiceEjb implements DictionaryService {

    @Inject
    private ApplicationDictionaryDao dao;

    @Inject
    private SupportedLocales supportedLocales;

    @Inject
    private Logger logger;

    /**
     * {@inheritDoc}
     */
    @Override
    public Localization getLocalization(final String application) {

        final ApplicationDictionary dictionary = dao.get(application);
        if (dictionary == null) {
            logger.debug("no dictionary was found for the application {}", application);
            return null;
        }

        for (final String locale : supportedLocales) {
            // Iterate over each requested locale in preference order and return the first found.
            final Localization localization = dictionary.getLocalization(locale);
            if (localization != null) {
                return localization;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addDictionary(final ApplicationDictionary applicationDictionary) {
        logger.debug("Adding dictionary to cache: {}", applicationDictionary);
        dao.put(applicationDictionary.getApplication(), applicationDictionary);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addLocalization(final String application, final Localization localization) {
        final ApplicationDictionary dictionary = dao.get(application);

        if (dictionary == null) {
            logger.debug("no dictionary was found for the application {}", application);
            throw new DictionaryUnavailableException(application);
        }

        dictionary.addLocalization(localization.getLocale(), localization);
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getSupportedLocales(final String application) {
        return Collections.unmodifiableSet(dao.get(application).getSupportedLocales());
    }

}
