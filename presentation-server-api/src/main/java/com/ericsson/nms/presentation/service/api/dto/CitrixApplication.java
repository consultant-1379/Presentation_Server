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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

/**
 * Citrix Application class
 */
@XmlRootElement(name = "ica")
public class CitrixApplication extends AbstractApplication {

    private static final long serialVersionUID = 1;

    @XmlAttribute
    private String host;
    @XmlAttribute
    private String params;
    @XmlAttribute
    private String paramHelp;

    /**
     * Default constructor
     */
    public CitrixApplication() {
        super();
    }

    /**
     * @param id {String}
     * @param name {String}
     */
    @SuppressWarnings("PMD")
    public CitrixApplication(final String id, final String name) {
        this.name = name;
        this.id = id;
    }

    /**
     * @param id {String}
     * @param name {String}
     * @param host {String}
     */
    public CitrixApplication(final String id,
                             final String name,
                             final String host) {
        this(id, name);
        this.host = host;
    }

    @Override
    public void setHidden(final boolean hidden) {
        this.hidden = hidden;
    }

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public String getParamHelp() {
        return paramHelp;
    }

    public void setParamHelp(final String paramHelp) {
        this.paramHelp = paramHelp;
    }

    public String getParams() {
        return params;
    }

    public void setParams(final String params) {
        this.params = params;
    }

    /**
     * Return the URI of the Citrix ICA File resource
     */
    @Override
    public String getUri() {

        //FIXME change to a properties file
        return "/rest/apps/citrix/" + id + ".ica";
    }

    @Override
    public String toString() {
        return "CitrixApplication - id:" + id +
                ", name:" + name +
                ", acronym:" + this.getAcronym() +
                ", shortInfo:" + this.getShortInfo() +
                ", favorite:" + this.getFavorite() +
                ", uri:" + getUri() +
                ", icaHost:" + host +
                ", params:" + params +
                ", paramHelp:" + paramHelp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CitrixApplication that = (CitrixApplication) o;
        return Objects.equals(host, that.host) &&
                Objects.equals(params, that.params) &&
                Objects.equals(paramHelp, that.paramHelp);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), host, params, paramHelp);
    }
}
