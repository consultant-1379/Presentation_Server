/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.presentation.exceptions;

import com.ericsson.nms.presentation.exceptions.service.EntityNotFoundException;

/**
 * Setting not found exception for Presentation Server
 */
public class SettingNotFoundException extends EntityNotFoundException {

    /**
     * @param cause
     *            {Throwable}
     */
    public SettingNotFoundException(final Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     *            {String}
     */
    public SettingNotFoundException(final String message) {
        super(message);
    }

    /**
     * @param message
     *            {String}
     * @param cause
     *            {Throwable}
     */
    public SettingNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
