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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * Abstract application class
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({CitrixApplication.class, WebApplication.class})
public abstract class AbstractApplication extends AbstractHeader implements Comparable<AbstractApplication> {

    private static final long serialVersionUID = 1L;

    @XmlAttribute
    boolean hidden;

    @XmlElement
    private String shortInfo;

    @XmlElement
    private String acronym;

    @XmlAttribute
    private String resources;

    @XmlAttribute
    private String roles = "";

    @XmlAttribute
    private String favorite = "false";

    private Integer version;

    private String hash;

    @XmlAttribute
    private boolean external;

    /**
     * Default constructor
     */
    public AbstractApplication() {
        super();
    }

    /**
     * @param id {String}
     * @param name {String}
     */
    public AbstractApplication(final String id, final String name) {
        super(id, name);
    }

    /**
     * Gets the REST Resource URI for the launch mechanism If a web app the REST
     * response will be a URL, if a Citrix App the REST response will be an ICA
     * file
     *
     * @return String - The URI to the REST Resource to launch the app from
     */
    public abstract String getUri();

    public String getFavorite() {
        return favorite;
    }

    public void setFavorite(final String favorite) {
        this.favorite = favorite;
    }

    public String getShortInfo() {
        return shortInfo;
    }

    public void setShortInfo(final String info) {
        this.shortInfo = info;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(final String acronym) {
        this.acronym = acronym;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(final boolean hidden) {
        this.hidden = hidden;
    }

    public String getResources() {
        return resources;
    }

    public void setResources(final String resources) {
        this.resources = resources;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(final String roles) {
        this.roles = roles;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(final Integer version) {
        this.version = version;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(final String hash) {
        this.hash = hash;
    }

    public boolean getExternal() {
        return external;
    }

    public void setExternal(boolean external) {
        this.external = external;
    }

    @Override
    public int compareTo(final AbstractApplication other) {
        if (other == null || this.getName() == null || other.getName() == null) {
            return 0;
        }

        final String myName = this.getName().toLowerCase();
        final String otherName = other.getName().toLowerCase();
        return myName.compareTo(otherName);
    }
}
