/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.json.parsers.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

import static com.ericsson.nms.presentation.service.locale.LocaleConstants.ATTR_NAME_ACRONYM;
import static com.ericsson.nms.presentation.service.locale.LocaleConstants.ATTR_NAME_DESCRIPTION;
import static com.ericsson.nms.presentation.service.locale.LocaleConstants.ATTR_NAME_TITLE;


/**
 * DTO matching the app.json format used to deserialize the json file.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationLocalizationJson implements Serializable {

    @JsonProperty(value = ATTR_NAME_TITLE)
    private String title;

    @JsonProperty(ATTR_NAME_DESCRIPTION)
    private String description;

    @JsonProperty(ATTR_NAME_ACRONYM)
    private String acronym;

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

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(final String acronym) {
        this.acronym = acronym;
    }

}
