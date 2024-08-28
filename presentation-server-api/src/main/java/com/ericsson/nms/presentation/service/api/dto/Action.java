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


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * DTO representing an Application Action
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Action implements Serializable {

    /**
     * This field indicates the default label used for this action. This can be replaced on the UI.
     */
    private String defaultLabel;

    /**
     * Unique identifier for this action
     */
    private String name;

    /**
     * Path to the plugin used to execute this action
     */
    private String plugin;

    /**
     * Reference to the application that provides this action
     */
    private String applicationId;

    @JsonSerialize
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<Resource> resources = new HashSet<>();

    private boolean multipleSelection;

    private boolean primary; // initialized as false by default.

    @JsonIgnore
    private boolean local;

    private String category;

    private Integer order = null;

    @JsonSerialize
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty(value="metadata")
    private List<ActionMetadata> metadata = null;

    @JsonSerialize
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String icon;

    @JsonSerialize
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ActionRule> rules = new ArrayList<>();

    public boolean isMultipleSelection() {
        return multipleSelection;
    }

    public void setMultipleSelection(final boolean multipleSelection) {
        this.multipleSelection = multipleSelection;
    }

    public String getDefaultLabel() {
        return defaultLabel;
    }

    public void setDefaultLabel(final String defaultLabel) {
        this.defaultLabel = defaultLabel;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getPlugin() {
        return plugin;
    }

    public void setPlugin(final String plugin) {
        this.plugin = plugin;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(final String applicationId) {
        this.applicationId = applicationId;
    }

    public List<ActionRule> getRules() {
        return rules;
    }

    public void setRules(final List<ActionRule> rules) {
        this.rules = rules;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(final String category) {
        this.category = category;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(final boolean primary) {
        this.primary = primary;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(final String icon) {
        this.icon = icon;
    }

    public boolean isLocal() {
        return local;
    }

    public void setLocal(final boolean local) {
        this.local = local;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(final Integer order) {
        this.order = order;
    }

    public List<ActionMetadata> getMetadata() {
        return metadata;
    }

    public void setMetadata(final List<ActionMetadata> metadata) {
        this.metadata = metadata;
    }

    public Set<Resource> getResources() {
        return resources;
    }

    public void setResources(final Set<Resource> resources) {
        this.resources = resources;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Action action = (Action) o;

        return name != null ? name.equals(action.name) : action.name == null;

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Action{" +
                "applicationId='" + applicationId + '\'' +
                ", defaultLabel='" + defaultLabel + '\'' +
                ", name='" + name + '\'' +
                ", plugin='" + plugin + '\'' +
                ", metadata='" + metadata + '\'' +
                ", multipleSelection=" + multipleSelection + '\'' +
                ", rules=" + rules + '\'' +
                ", icon='" + icon +
                '}';
    }
}
