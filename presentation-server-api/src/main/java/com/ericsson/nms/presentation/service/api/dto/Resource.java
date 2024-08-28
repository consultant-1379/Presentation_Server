/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2019
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.presentation.service.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Objects;

/**
 * DTO representing security resource
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Resource implements Serializable {

    /**
     * Name of the security resource
     */
    private String name;

    /**
     * Security action (CREATE, READ, DELETE, UPDATE)
     */
    private String action;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getAction() {
        return action;
    }

    public void setAction(final String action) {
        this.action = action;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Resource resource = (Resource) o;
        return Objects.equals(name, resource.name) &&
                Objects.equals(action, resource.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, action);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Resource{");
        sb.append("name='").append(name).append('\'');
        sb.append(", action='").append(action).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
