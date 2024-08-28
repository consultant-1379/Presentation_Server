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
 * Exception thrown when a required property is not found in PIB.
 */
public class MissingRequiredPropertyException extends  PresentationServerException {


    private static final String MESSAGE_TEMPLATE = "The key '%s' for '%s' property exists in the " +
            "application metadata file but could not be found in PIB. Please make sure to include this property using PIB.";

    /**
     * Missing Required Property Exception thrown when propert is not found in PIB.
     *
     * @param propertyName {String}
     * @param key {String}
     */
    public MissingRequiredPropertyException(final String propertyName, final String key) {
        super(String.format(MESSAGE_TEMPLATE, key, propertyName));
    }

}
