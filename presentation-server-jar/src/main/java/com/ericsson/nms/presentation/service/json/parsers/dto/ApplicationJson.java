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

import java.io.Serializable;
import java.util.List;
import java.util.Set;


import com.ericsson.nms.presentation.service.api.dto.Action;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO matching the application.json format used to deserialize the json file.
 */
@SuppressWarnings("PMD.TooManyFields")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationJson implements Serializable {

    private String id;
    private String name;
    private String shortInfo;
    private String acronym;
    private String path;
    private String protocol;
    private Integer port;
    private String type;
    private boolean external;
    private String host;
    private String externalHost;
    private String params;
    private String paramsHelp;
    private Integer version = 1;
    private boolean hidden;
    private boolean openInNewWindow; // default value = false
    private Set<String> resources;
    private List<GroupJson> groups;
    private List<Action> provideActions;
    private List<String> consumeActions;

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(final String acronym) {
        this.acronym = acronym;
    }

    public List<Action> getProvideActions() {
        return provideActions;
    }

    public void setProvideActions(final List<Action> provideActions) {
        this.provideActions = provideActions;
    }

    public List<GroupJson> getGroups() {
        return groups;
    }

    public void setGroups(final List<GroupJson> groups) {
        this.groups = groups;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public Integer getPort() {
        return port;
    }

    public String getExternalHost() {
        return externalHost;
    }

    public void setExternalHost(final String externalHost) {
        this.externalHost = externalHost;
    }

    public boolean getExternal() {
        return external;
    }

    public void setExternal(final boolean external) {
        this.external = external;
    }

    public void setPort(final Integer port) {
        this.port = port;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(final String protocol) {
        this.protocol = protocol;
    }

    public String getShortInfo() {
        return shortInfo;
    }

    public void setShortInfo(final String shortInfo) {
        this.shortInfo = shortInfo;
    }

    public Set<String> getResources() {
        return resources;
    }

    public void setResources(final Set<String> resources) {
        this.resources = resources;
    }

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public String getParams() {
        return params;
    }

    public void setParams(final String params) {
        this.params = params;
    }

    public String getParamsHelp() {
        return paramsHelp;
    }

    public void setParamsHelp(final String paramsHelp) {
        this.paramsHelp = paramsHelp;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(final boolean hidden) {
        this.hidden = hidden;
    }

    public List<String> getConsumeActions() {
        return consumeActions;
    }

    public void setConsumeActions(final List<String> consumeActions) {
        this.consumeActions = consumeActions;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(final Integer version) {
        this.version = version;
    }

    public boolean isOpenInNewWindow() {
        return openInNewWindow;
    }

    public void setOpenInNewWindow(final boolean openInNewWindow) {
        this.openInNewWindow = openInNewWindow;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ApplicationJson that = (ApplicationJson) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ApplicationJson{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", shortInfo='" + shortInfo + '\'' +
                ", acronym='" + acronym + '\'' +
                ", path='" + path + '\'' +
                ", externalHost='" + externalHost + '\'' +
                ", protocol='" + protocol + '\'' +
                ", port=" + port +
                ", type='" + type + '\'' +
                ", host='" + host + '\'' +
                ", params='" + params + '\'' +
                ", paramsHelp='" + paramsHelp + '\'' +
                ", version='" + version + '\'' +
                ", hidden=" + hidden +
                ", resources=" + resources +
                ", groups=" + groups +
                ", provideActions=" + provideActions +
                ", consumeActions=" + consumeActions +
                '}';
    }

}
