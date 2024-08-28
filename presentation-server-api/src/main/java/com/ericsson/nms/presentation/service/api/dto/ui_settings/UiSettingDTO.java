/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2019
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.presentation.service.api.dto.ui_settings;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Class for the single UI setting
 */
@XmlRootElement(name = "setting")
@XmlAccessorType(XmlAccessType.FIELD)
public class UiSettingDTO {

    @XmlAttribute
    private String id;

    @XmlAttribute
    private String value;

    private Date created;

    private Date lastUpdated;

    /**
     * @return The id (the name) of the setting.
     */
    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    /**
     * @return The value of the setting
     */
    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    /**
     * @return the date when the setting was created.
     */
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
    /**
     * @return the date when the setting was updated for the last time
     */
    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public UiSettingDTO(final String id, final String value) {
        this.id = id;
        this.value = value;
    }

    public UiSettingDTO() {
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final UiSettingDTO that = (UiSettingDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", UiSettingDTO.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("value='" + value + "'")
                .toString();
    }
}
