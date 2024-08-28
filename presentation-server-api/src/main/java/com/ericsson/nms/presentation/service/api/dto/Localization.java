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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates the supported localized texts for the application and their actions.
 */
public class Localization implements Serializable {

    /**
     * Locale of this localization
     */
    private String locale;

    /**
     * Localized application title
     */
    private String title;

    /**
     * Localized description
     */
    private String description;

    /**
     * Localized acronym
     */
    private String acronym;

    /**
     * Localized action names
     */
    private final Map<String, String> actionNames = new HashMap<>();

    public Localization(final String locale) {
        this.locale = locale;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void addActionName(final String actionId, final String localizedText) {
        this.actionNames.put(actionId, localizedText);
    }

    public String getActionName(final String actionId) {
        return this.actionNames.get(actionId);
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(final String acronym) {
        this.acronym = acronym;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(final String locale) {
        this.locale = locale;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Localization that = (Localization) o;

        if (title != null ? !title.equals(that.title) : that.title != null) {
            return false;
        }
        if (description != null ? !description.equals(that.description) : that.description != null) {
            return false;
        }
        return actionNames.equals(that.actionNames);
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + actionNames.hashCode();
        return result;
    }

}
