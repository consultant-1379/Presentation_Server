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


import com.fasterxml.jackson.databind.ObjectMapper;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.io.Serializable;
import java.net.URLDecoder;
import java.util.Collection;

/**
 * Created by ejgemro on 11/18/15.
 */
@XmlRootElement(name = "consumes")
@XmlAccessorType(XmlAccessType.FIELD)
public class Consumes implements Serializable {

    @XmlElement(name = "data-type")
    private Collection<DataType> dataTypes;

    /**
     * @param json {String}
     * @return consumes
     * @throws IOException {Throwable}
     */
    public static Consumes fromString(final String json) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final String decodedJson = URLDecoder.decode(json, "UTF-8");
        final Consumes consumes = mapper.readValue(decodedJson, Consumes.class);
        return consumes;
    }

    @Override
    public String toString() {
        return "Consumes{" +
                "dataTypes=" + dataTypes +
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

        final Consumes that = (Consumes) o;

        return !(dataTypes != null ? !dataTypes.equals(that.dataTypes) : that.dataTypes != null);

    }

    @Override
    public int hashCode() {
        return dataTypes != null ? dataTypes.hashCode() : 0;
    }

    public Collection<DataType> getDataTypes() {

        return dataTypes;
    }

    public void setDataTypes(final Collection<DataType> dataTypes) {
        this.dataTypes = dataTypes;
    }
}
