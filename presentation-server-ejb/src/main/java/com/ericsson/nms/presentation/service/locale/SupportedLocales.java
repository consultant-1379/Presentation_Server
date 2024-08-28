/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.locale;

import com.ericsson.nms.presentation.service.interceptors.MethodCallTimerInterceptor;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.io.Serializable;
import java.util.Iterator;

/**
 * <p>Represents the supported locales for the current user in preference order.</p>
 * <p>This class is a valid {@link Iterable}, so it can be used to iterate over each locale in preference order.</p>
 */
@Interceptors({MethodCallTimerInterceptor.class})
public class SupportedLocales implements Serializable, Iterable<String> {

    @Inject
    private LocaleService localeService;

    /**
     * {@inheritDoc}
     */
    public Iterator<String> iterator() {
        return localeService.getUserLocales().iterator();
    }


}
