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
 * Created by ecrcont on 19/11/2015.
 */
public class InvalidConsumesRequestException extends PresentationServerException {

    /**
     * Default constructor
     */
    public InvalidConsumesRequestException() {
    }

    /**
     * @param cause {Throwable}
     */
    public InvalidConsumesRequestException(final Throwable cause) {
        super(cause);
    }

    /**
     * @param message {String}
     */
    public InvalidConsumesRequestException(final String message) {
        super(message);
    }

    /**
     * @param message {String}
     * @param cause {Throwable}
     */
    public InvalidConsumesRequestException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
