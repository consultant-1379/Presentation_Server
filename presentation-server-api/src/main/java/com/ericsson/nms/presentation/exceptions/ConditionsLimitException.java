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

import static com.ericsson.nms.presentation.service.PresentationServerConstants.MAXIMUM_ACTION_CONDITIONS_LIMIT;

/**
 * Exception thrown when conditions exceed limit.
 */
public class ConditionsLimitException extends PresentationServerException {

    /**
     * Conditions limit Exception thrown when conditions exceed limit.
     */
    public ConditionsLimitException() {
        super("Selection conditions must not exceed "+ MAXIMUM_ACTION_CONDITIONS_LIMIT);
    }
}
