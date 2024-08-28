/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.rest.request;


import com.ericsson.nms.presentation.service.api.dto.DataType;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Collection;

/**
 * Created by ejgemro on 11/19/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ConsumesRequest {

    @XmlAttribute(name = "multiple-selection")
    private boolean multipleSelection; // default value = false

    @XmlElement(name = "data-type")
    private Collection<DataType> dataTypes;

    /**
     * Method used by JAX-RS to retrieve this object from a json string
     *
     * @param json {String}
     * @throws IOException {Throwable}
     * @return consumesRequest
     */
    public static ConsumesRequest fromString(final String json) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final String decodedJson = URLDecoder.decode(json, "UTF-8");
        return mapper.readValue(decodedJson, ConsumesRequest.class);
    }

    @Override
    public String toString() {
        return "ConsumesRequest{" +
                "multipleSelection=" + multipleSelection +
                ", dataTypes=" + dataTypes +
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

        final ConsumesRequest that = (ConsumesRequest) o;

        if (multipleSelection != that.multipleSelection) {
            return false;
        }
        return !(dataTypes != null ? !dataTypes.equals(that.dataTypes) : that.dataTypes != null);

    }

    @Override
    public int hashCode() {
        int result = (multipleSelection ? 1 : 0);
        result = 31 * result + (dataTypes != null ? dataTypes.hashCode() : 0);
        return result;
    }

    public boolean isMultipleSelection() {

        return multipleSelection;
    }

    public void setMultipleSelection(final boolean multipleSelection) {
        this.multipleSelection = multipleSelection;
    }

    public Collection<DataType> getDataTypes() {
        return dataTypes;
    }

    public void setDataTypes(final Collection<DataType> dataTypes) {
        this.dataTypes = dataTypes;
    }
}
