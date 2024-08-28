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
 * Service Removed exception for Presentation Server
 */
public class ServiceRemovedException extends PresentationServerException {


    private static final String MESSAGE_TEMPLATE = "This service has been removed. %s";
    /**
     * Default constructor
     */
    public ServiceRemovedException() {
        super();
    }

    /**
     * @param message {String}
     */
    public ServiceRemovedException(final String message) {
        super(String.format(MESSAGE_TEMPLATE, message));
    }
}
