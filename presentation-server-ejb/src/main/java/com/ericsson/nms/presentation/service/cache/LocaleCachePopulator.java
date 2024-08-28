/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.cache;

import com.ericsson.nms.presentation.service.api.dto.ApplicationDictionary;
import com.ericsson.nms.presentation.service.ejb.DictionaryService;
import com.ericsson.nms.presentation.service.factory.readers.ApplicationLocaleJsonReader;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * Populator used to save the data retrieved from the file system to the Locales cache.
 */
@Stateless
public class LocaleCachePopulator {

    @Inject
    private DictionaryService dictionaryService;

    @Inject
    ApplicationLocaleJsonReader reader;

    @Inject
    Logger logger;

    /**
     * Populated the locales cache
     */
    public void populate() {
        for (final ApplicationDictionary dictionary : reader.getDictionaries()) {
            logger.info("Adding dictionary for application {}", dictionary.getApplication());
            dictionaryService.addDictionary(dictionary);
        }
    }

}
