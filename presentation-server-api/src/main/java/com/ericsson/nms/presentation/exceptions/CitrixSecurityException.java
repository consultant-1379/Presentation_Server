/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.exceptions;

/**
 * Exception Thrown when a security failure happens when launching a Citrix application.
 */
public class CitrixSecurityException extends PresentationServerException {

    /**
     * @param message {String}
     */
    public CitrixSecurityException(final String message) {
        super(message);
    }

    /**
     * @param message {String}
     * @param cause {Throwable}
     */
    public CitrixSecurityException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
