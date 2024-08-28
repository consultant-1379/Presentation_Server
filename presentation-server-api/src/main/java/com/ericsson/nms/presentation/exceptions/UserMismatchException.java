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
package com.ericsson.nms.presentation.exceptions;

/**
 * User mismatch exception for Presentation Server
 */
public class UserMismatchException extends PresentationServerException {

    /**
     * Default constructor
     */
    public UserMismatchException() {
    }

    /**
     * @param cause {Throwable}
     */
    public UserMismatchException(final Throwable cause) {
        super(cause);
    }

    /**
     * @param message {String}
     */
    public UserMismatchException(final String message) {
        super(message);
    }

    /**
     * @param message {String}
     * @param cause {Throwable}
     */
    public UserMismatchException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
