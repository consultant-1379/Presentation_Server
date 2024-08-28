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
 * Exception thrown when the Json Parser fails
 */
public class JsonParserException extends  PresentationServerException {

    /**
     * Default constructor
     */
    public JsonParserException() {
    }

    /**
     * @param cause {Throwable}
     */
    public JsonParserException(final Throwable cause) {
        super(cause);
    }

    /**
     * @param message {String}
     */
    public JsonParserException(final String message) {
        super(message);
    }

    /**
     * @param message {String}
     * @param cause {Throwable}
     */
    public JsonParserException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
