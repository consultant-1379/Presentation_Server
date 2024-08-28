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

import java.io.Serializable;

/**
 * Represents a system property and his value.
 */
public class SystemProperty implements Serializable {

    private String name;

    private Serializable value;

    /**
     * Builds a SystemProperty instance with the given name and value
     * @param name property name
     * @param value property value
     */
    public SystemProperty(final String name, final Serializable value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Default constructor
     */
    public SystemProperty() {
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Serializable getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "SystemProperty{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final SystemProperty that = (SystemProperty) o;

        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
