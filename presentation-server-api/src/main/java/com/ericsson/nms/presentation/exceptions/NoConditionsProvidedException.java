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
 * Exception thrown when no condition is provided for the action service.
 */
public class NoConditionsProvidedException extends PresentationServerException {

    /**
     * No conditions provided exception is thrown when no condition is provided for the action service.
     */
    public NoConditionsProvidedException() {
        super("No selection condition  was given to the service.");
    }
}
