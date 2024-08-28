/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.ejb;

import com.ericsson.nms.presentation.service.api.dto.ServerDateTimeMessage;

/**
 * Service responsible to provide ENM system time.
 */
public interface TimeService {

    /**
     * <p>Gets the current ENM system time &amp; locale info for UI/REST applications </p>
     * @return ENM Server Time
     */
    ServerDateTimeMessage now();
}
