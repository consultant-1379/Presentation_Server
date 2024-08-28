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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * JAXB mapping for the appinfo.xml file
 */
@XmlRootElement(name = "apps")
@XmlAccessorType(XmlAccessType.FIELD)
public class Info implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlElement(name = "app")
    private Collection<ApplicationDetail> details = new ArrayList<>();

    public Collection<ApplicationDetail> getDetails() {
        return details;
    }

    public void setDetails(final Collection<ApplicationDetail> details) {
        this.details = details;
    }
}