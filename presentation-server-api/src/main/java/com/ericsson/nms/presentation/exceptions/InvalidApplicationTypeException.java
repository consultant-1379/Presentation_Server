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
 * Exception thrown when an invalid application type is used.
 */
public class InvalidApplicationTypeException extends PresentationServerException {

    /**
     * @param message {String}
     */
    public InvalidApplicationTypeException(final String message) {
        super(message);
    }

}
