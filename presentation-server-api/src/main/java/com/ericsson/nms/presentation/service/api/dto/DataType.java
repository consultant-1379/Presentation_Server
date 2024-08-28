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
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

/**
 * Created by ecrcont on 18/11/2015.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class DataType implements Serializable {

    @XmlElement(name = "property")
    private Collection<Property> properties;

    @XmlAttribute
    private String name;

    @XmlAttribute(name = "multiple-selection-enabled")
    private boolean multipleSelectionEnabled; // default value = false

    /**
     * @param other {DataType}
     * @param multipleSelection {boolean}
     * @return validatingDataTypeDTO {boolean}
     */
    public boolean validatingDataTypeDTO(final DataType other, final boolean multipleSelection) {
        if (!other.getName().equals(this.getName())) {
            return false;
        }
        if (this.getProperties() == null) {
            this.setProperties(new HashSet<Property>());
        }
        if (other.getProperties() == null) {
            other.setProperties(new HashSet<Property>());
        }
        if (!this.isMultipleSelectionEnabled() && multipleSelection) {
            return false;
        }

        // The metadata can have more properties than the filter, so we match all the
        // applications that have all the required properties (setted on the filter)
        for (final Property requiredProperty : this.getProperties()) {
            // If the query doesn't have a required property, should not be matched
            if (other.getProperties() == null || !other.getProperties().contains(requiredProperty)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return "DataType{" +
                "properties=" + properties +
                ", name='" + name + '\'' +
                ", multipleSelectionEnabled=" + multipleSelectionEnabled +
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

        final DataType that = (DataType) o;

        if (multipleSelectionEnabled != that.multipleSelectionEnabled) {
            return false;
        }
        if (properties != null ? !properties.equals(that.properties) : that.properties != null) {
            return false;
        }
        return !(name != null ? !name.equals(that.name) : that.name != null);

    }

    @Override
    public int hashCode() {
        int result = properties != null ? properties.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (multipleSelectionEnabled ? 1 : 0);
        return result;
    }

    public Collection<Property> getProperties() {

        return properties;
    }

    public void setProperties(final Collection<Property> properties) {
        this.properties = properties;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean isMultipleSelectionEnabled() {
        return multipleSelectionEnabled;
    }

    public void setMultipleSelectionEnabled(final boolean multipleSelectionEnabled) {
        this.multipleSelectionEnabled = multipleSelectionEnabled;
    }
}
