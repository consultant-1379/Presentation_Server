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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.io.Serializable;
import java.util.Objects;

/**
 * Defines the base ID/NAME attributes
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({ApplicationDetail.class, Group.class, AbstractApplication.class})
public abstract class AbstractHeader implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlAttribute
    protected String id;

    @XmlAttribute
    protected String name;


    /**
     * Default constructor
     */
    public AbstractHeader() {
    }

    /**
     * @param id {@link String}
     * @param name {@link String}
     */
    public AbstractHeader(final String id, final String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hashCode(this.id);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass())
            return false;

        final AbstractHeader other = (AbstractHeader) obj;
        return Objects.equals(this.id, other.id);
    }

}
