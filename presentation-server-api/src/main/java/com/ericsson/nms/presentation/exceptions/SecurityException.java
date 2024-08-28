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
 * Security exception for Presentation Server
 */
public class SecurityException extends PresentationServerException {

    /**
     * Default constructor
     */
    public SecurityException() {
    }

    /**
     * @param cause {Throwable}
     */
    public SecurityException(final Throwable cause) {
        super(cause);
    }

    /**
     * @param message {String}
     */
    public SecurityException(final String message) {
        super(message);
    }

    /**
     * @param message {String}
     * @param cause {Throwable}
     */
    public SecurityException(final String message, final Throwable cause) {
        super(message, cause);
    }
}