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
 * Exception raised when there's no system property with the given name
 */
public class SystemPropertyNotFoundException extends PresentationServerException {

    private static final String MESSAGE = "No Property was found with the given name: %s";

    /**
     * Creates the exception for the given name
     * @param name name to be used in the exception message
     */
    public SystemPropertyNotFoundException(final String name) {
        super(String.format(MESSAGE, name));

    }
}
