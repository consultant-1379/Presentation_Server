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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Application detail class
 */
@XmlType(name = "app")
public class ApplicationDetail extends AbstractHeader {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "short_info")
    private String shortInfo;

    @XmlElement(name = "long_info")
    private String longInfo;

    @XmlElement(name = "acronym")
    private String acronym;

    @XmlElement(name = "param_help")
    private String paramHelp;

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(final String acronym) {
        this.acronym = acronym;
    }

    public String getLongInfo() {
        return longInfo;
    }

    public void setLongInfo(final String longInfo) {
        this.longInfo = longInfo;
    }

    public String getParamHelp() {
        return paramHelp;
    }

    public void setParamHelp(final String paramHelp) {
        this.paramHelp = paramHelp;
    }

    public String getShortInfo() {
        return shortInfo;
    }

    public void setShortInfo(final String shortInfo) {
        this.shortInfo = shortInfo;
    }
}
