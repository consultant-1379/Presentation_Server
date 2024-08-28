/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.uis.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @author ehanchs
 * @deprecated because it's replaced by the new DTO
 */
@XmlRootElement(name = "setting")
@XmlAccessorType(XmlAccessType.FIELD)
@Deprecated
public class UISettingBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Id of the for the setting
     */
    @XmlAttribute
    String id;

    /**
     * String representing the value of the setting
     */
    @XmlAttribute
    String value;

    /**
     * Default Constructor for the purpose of JAXB
     */
    public UISettingBean() {
    }

    /**
     * @param id {String}
     * @param value {String}
     */
    public UISettingBean(final String id, final String value) {
        this.id = id;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UISettingBean)) {
            return false;
        }

        final UISettingBean that = (UISettingBean) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (value != null ? !value.equals(that.value) : that.value != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
