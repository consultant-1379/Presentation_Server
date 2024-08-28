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

/**
 * DTO matching the Group object in the application.json file.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupJson {

    private String id;
    private String name;

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

        final GroupJson groupJson = (GroupJson) o;

        if (id != null ? !id.equals(groupJson.id) : groupJson.id != null) {
            return false;
        }
        return name != null ? name.equals(groupJson.name) : groupJson.name == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "GroupJson{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
