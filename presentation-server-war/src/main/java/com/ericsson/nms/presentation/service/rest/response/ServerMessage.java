/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.presentation.service.rest.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Server Message class
 */
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class ServerMessage {

    private static final long serialVersionUID = 1L;

    @XmlAttribute
    String message;

    /**
     * Default constructor
     */
    public ServerMessage() {
    }

    /**
     * @param message {String}
     */
    public ServerMessage(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
