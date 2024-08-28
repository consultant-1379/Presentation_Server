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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Entity representing an Action Rule Condition
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActionRuleCondition implements Serializable {

    /**
     * Data type required of this condition (E.G: ManagedObject, Collection, Alarm, etc...)
     */
    private String dataType;

    /**
     * Collection of properties to be checked in this rule.
     * Properties are name/value pairs that can change according to the data type.
     */
    private Collection<Property> properties = new ArrayList<>();

    public Collection<Property> getProperties() {
        return properties;
    }

    public void setProperties(final Collection<Property> properties) {
        this.properties = properties;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(final String dataType) {
        this.dataType = dataType;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ActionRuleCondition that = (ActionRuleCondition) o;

        if (dataType != null ? !dataType.equals(that.dataType) : that.dataType != null) {
            return false;
        }
        return properties.size() == that.properties.size() && properties.containsAll(that.properties);

    }

    @Override
    public int hashCode() {
        int result = dataType != null ? dataType.hashCode() : 0;
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ActionRuleCondition{" +
                "dataType='" + dataType + '\'' +
                ", properties=" + properties +
                '}';
    }
}
