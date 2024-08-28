/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.api.dto;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents the dictionary for a given application encapsulation the localized texts.
 */
public class ApplicationDictionary implements Serializable {

    public ApplicationDictionary(final String application) {
        this.application = application;
    }

    /**
     * Application that owns the dictionary
     */
    private final String application;

    /**
     * Locales available for this dictionary.
     */
    private final Map<String,Localization> locales = new HashMap<>();

    /**
     * Returns the available locales
     * @return available locales
     */
    public Set<String> getSupportedLocales() {
        return locales.keySet();
    }

    /**
     * Get localized texts for the given locale
     * @param locale locale to be requested
     * @return localized texts for the application in the given locale
     */
    public Localization getLocalization(final String locale) {
        return locales.get(StringUtils.lowerCase(locale));
    }

    public String getApplication() {
        return application;
    }

    /**
     * Adds a localization to the given locale. If the locale exists, it will be replaced.
     * @param locale locale to be added
     * @param localization localized texts to be added
     */
    public void addLocalization(final String locale, final Localization localization) {
        this.locales.put(StringUtils.lowerCase(locale), localization);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ApplicationDictionary that = (ApplicationDictionary) o;

        return application != null ? application.equals(that.application) : that.application == null;
    }

    @Override
    public int hashCode() {
        return application != null ? application.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ApplicationDictionary{" +
                "application='" + application + '\'' +
                ", locales=" + locales +
                '}';
    }
}
