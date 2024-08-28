/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.rest.response;

import com.ericsson.nms.presentation.service.api.dto.AbstractApplication;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * REST Response object for Group
 */
public class GroupResponse {

    private String id;

    private String name;

    private Set<AbstractApplication> apps;

    /**
     * @param id {String}
     * @param name {String}
     * @param apps {AbstractApplication...}
     */
    public GroupResponse(final String id, final String name, final AbstractApplication... apps) {
        this.apps = new HashSet<>(Arrays.asList(apps));
        this.id = id;
        this.name = name;
    }

    /**
     * Default constructor
     */
    public GroupResponse() {
    }

    public Set<AbstractApplication> getApps() {
        return apps;
    }

    public void setApps(final Set<AbstractApplication> apps) {
        this.apps = apps;
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final GroupResponse that = (GroupResponse) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        return !(apps != null ? !apps.equals(that.apps) : that.apps != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (apps != null ? apps.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "GroupResponse{" +
                "apps=" + apps +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
