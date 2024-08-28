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
 * Exception thrown when the requested dictionary does not exist.
 */
public class DictionaryUnavailableException extends PresentationServerException {

    private static final String MESSAGE_TEMPLATE = "There's no dictionary for the application '%s'!";

    public DictionaryUnavailableException(final String application) {
        super(String.format(MESSAGE_TEMPLATE, application));
    }

}
