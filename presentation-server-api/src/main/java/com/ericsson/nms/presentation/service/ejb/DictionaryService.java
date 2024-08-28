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

import com.ericsson.nms.presentation.service.api.dto.ApplicationDictionary;
import com.ericsson.nms.presentation.service.api.dto.Localization;

import javax.ejb.Local;
import java.util.Set;

/**
 * Service responsible to manage application dictionaries
 */
@Local
public interface DictionaryService {

    /**
     * Gets the Localization for the given application and locale
     * @param application application to be searched
     * @return localization instance found, or null if there's no localization with the given arguments.
     */
    Localization getLocalization(final String application);

    /**
     * Adds a new dictionary. If the dictionary application already exists, it will be replaced.
     * @param applicationDictionary dictionary to be added.
     */
    void addDictionary(final ApplicationDictionary applicationDictionary);

    /**
     * Adds a new localization to a previously added dictionary.
     * @param application application to be searched
     * @param localization localization to be added
     */
    void addLocalization(final String application, Localization localization);

    /**
     * Get all supported locales for a given application
     * @param application Application to be queried
     * @return a Set with all supported locales
     */
    Set<String> getSupportedLocales(final String application);

}
