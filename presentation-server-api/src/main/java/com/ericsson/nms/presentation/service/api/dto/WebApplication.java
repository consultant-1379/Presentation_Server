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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Web Application class
 */
@XmlRootElement(name = "web")
public class WebApplication extends AbstractApplication {

    private static final long serialVersionUID = 1L;

    @XmlAttribute
    private boolean openInNewWindow;

    @XmlAttribute
    private String path;

    @XmlAttribute
    private String host;

    @XmlAttribute
    private String port;
    
    @XmlAttribute
    private String externalHost;

    @XmlAttribute
    private String protocol;

    /**
    * @deprecated
     */
    @Deprecated
    @XmlElement
    private Consumes consumes;

    private Collection<Action> providesActions = new ArrayList<>();

    private Collection<String> consumesActions = new ArrayList<>() ;

    /**
     * Default constructor
     */
    public WebApplication() {
        super();
    }

    /**
     * @param id {String}
     * @param name {String}
     */
    public WebApplication(final String id, final String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * @param id {String}
     * @param name {String}
     * @param path {String}
     * @param host {String}
     * @param port {String}
     * @param protocol {String}
     * @param openInNewWindow {boolean}
     */
    public WebApplication(final String id,
                          final String name,
                          final String path,
                          final String host,
                          final String port,
                          final String protocol,
                          final boolean openInNewWindow) {
        this(id, name);
        this.path = path;
        this.host = host;
        this.port = port;
        this.protocol = protocol;
        this.openInNewWindow = openInNewWindow;
    }

    public Consumes getConsumes() {
        return consumes;
    }

    public void setConsumes(final Consumes consumes) {
        this.consumes = consumes;
    }

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public String getPort() {
        return port;
    }

    public void setPort(final String port) {
        this.port = port;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(final String protocol) {
        this.protocol = protocol;
    }

    public boolean getOpenInNewWindow() {
        return openInNewWindow;
    }

    public void setOpenInNewWindow(final boolean openInNewWindow) {
        this.openInNewWindow = openInNewWindow;
    }

    public Collection<Action> getProvidesActions() {
        return providesActions;
    }

    public void setProvidesActions(final Collection<Action> providesActions) {
        this.providesActions = providesActions;
    }

    public Collection<String> getConsumesActions() {
        return consumesActions;
    }

    public void setConsumesActions(final Collection<String> consumesActions) {
        this.consumesActions = consumesActions;
    }
    

    public String getExternalHost() {
        return externalHost;
    }


    public void setExternalHost(final String externalHost) {
        this.externalHost = externalHost;
    }

    /**
     * Get the URI for the Web Application
     */
    @Override
    @XmlAttribute(name = "uri")
    public String getUri() {
        //FIXME MOVE TO A PROPERTIES FILE
        return "/rest/apps/web/" + id;
    }

    /**
     * @return target URI
     */
    @XmlAttribute(name = "targetUri")
    public String getTargetUri() {
        if (path == null) {
            path = "";
        }
        if(externalHost != null){
            return protocol + "://" + externalHost + path;
        }
        return protocol + "://" + host +
                port + path;
    }


    @Override
    public String toString() {
        return "WebApplication - id:" + id +
                ", name:" + name +
                ", acronym:" + this.getAcronym() +
                ", shortInfo:" + this.getShortInfo() +
                ", favorite:" + this.getFavorite() +
                ", uri:" + getUri();
    }

}
