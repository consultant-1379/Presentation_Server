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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

/**
 * Holder for the Metadata information
 */
@XmlRootElement(name="apps")
@XmlAccessorType(XmlAccessType.FIELD)
public class Metadata implements Serializable {

    private static final long serialVersionUID = 1L;

    private Collection<CitrixApplication> ica = new HashSet<>();

    private Collection<WebApplication> web = new HashSet<>();

    @XmlElement(name = "group")
    private Collection<Group> groups = new HashSet<>();

    private Collection<Action> actions = new HashSet<>();

    public Collection<Group> getGroups() {
        return groups;
    }

    public void setGroups(final Collection<Group> groups) {
        this.groups = groups;
    }

    public Collection<CitrixApplication> getIca() {
        return ica;
    }

    public void setIca(final Collection<CitrixApplication> ica) {
        this.ica = ica;
    }

    public Collection<WebApplication> getWeb() {
        return web;
    }

    public void setWeb(final Collection<WebApplication> web) {
        this.web = web;
    }

    public Collection<Action> getActions() {
        return actions;
    }

    public void setActions(final Collection<Action> actions) {
        this.actions = actions;
    }
}