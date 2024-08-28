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
import java.util.HashSet;
import java.util.Set;

/**
 * @author ehanchs
 */
@XmlRootElement(name = "group")
@XmlAccessorType(XmlAccessType.FIELD)
public class Group extends AbstractHeader implements Comparable<Group> {
    private static final long serialVersionUID = 1L;

    @XmlElement(name = "app")
    private Set<String> appIds = new HashSet<>();

    /**
     * Default constructor
     */
    public Group() {}

    /**
     * @param id {@link String}
     * @param name {@link String}
     * @param appIds {@link Set}
     */
    public Group(final String id, final String name, final Set<String> appIds) {
        super(id, name);
        this.appIds = appIds;
    }

    public Set<String> getAppIds() {
        return appIds;
    }

    @Override
    public int compareTo(final Group other) {

        if ( other == null || this.getName() == null || other.getName() == null) {
            return 0;
        }

        final String myName = this.getName().toLowerCase();
        final String otherName = other.getName().toLowerCase();
        final Integer comparison = myName.compareTo(otherName);

        if (comparison == 0) {
            // If we have groups with the same name but different IDs, we compare the ids
            return this.getId().compareTo(other.getId());
        }

        return comparison;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id="+ getId() +", " +
                "name="+ getName() +", " +
                "appIds=" + appIds +
                '}';
    }
}
