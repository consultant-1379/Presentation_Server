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
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Server DateTime message class
 */
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class ServerDateTimeMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlAttribute
    long timestamp;

    @XmlAttribute
    float utcOffset;

    @XmlAttribute
    String timezone;

    @XmlAttribute
    String serverLocation;

    /**
     * Default constructor
     */
    public ServerDateTimeMessage() {
    }

    /**
     * @param timestamp {long}
     * @param utcOffset {float}
     * @param timezone {String}
     * @param serverLocation {String}
     */
    public ServerDateTimeMessage(final long timestamp, final float utcOffset, final String timezone, final String serverLocation) {

        this.timestamp = timestamp;
        this.utcOffset = utcOffset;
        this.timezone = timezone;
        this.serverLocation = serverLocation;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }

    public float getUtcOffset() {
        return utcOffset;
    }

    public void setUtcOffset(final float utcOffset) {
        this.utcOffset = utcOffset;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(final String timezone) {
        this.timezone = timezone;
    }

    public String getServerLocation() {
        return serverLocation;
    }

    public void setServerLocation(final String serverLocation) {
        this.serverLocation = serverLocation;
    }
}
