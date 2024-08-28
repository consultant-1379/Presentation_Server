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

import javax.ejb.Local;
import java.util.LinkedHashSet;

/**
 * Service responsible to store and retrieve the user prefered locales from a thread safe context.
 */
@Local
@SuppressWarnings("PMD.LooseCoupling")
public interface LocaleService {

    /**
     * Sets the user requested locales in order of preference.
     * @param locales locales in order of preference (e.g. en-us, en-gb, fr)
     */
    void setUserLocale(final LinkedHashSet<String> locales);

    /**
     * Gets the previously defined prefered locales for the user. If no locale is set, the default locale en-us will be used.
     * @return A list of locales in prefered order
     */
    LinkedHashSet<String> getUserLocales();

}
