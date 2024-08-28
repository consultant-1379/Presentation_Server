/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2019
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.presentation.service.ejb.configuration;

import com.ericsson.nms.presentation.exceptions.service.configuration.ConfigurationNotFoundException;
import com.ericsson.nms.presentation.service.persistence.dao.configuration.ConfigurationRepository;
import com.ericsson.nms.presentation.service.persistence.entities.v1.configuration.ConfigurationEntity;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

/**
 * Service implementation for Configuration Entity
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class ConfigurationService {

    @Inject
    private ConfigurationRepository repository;

    /**
     * gets a configuration with the given key
     * @param key key to be queried
     * @return the configuration that matcher the key provided
     * @throws ConfigurationNotFoundException when the provided key does not exist
     */
    public ConfigurationEntity getConfiguration(final String key) {
        return repository.findByKey(key)
            .orElseThrow(() -> new ConfigurationNotFoundException(key));
    }

    /**
     * Saves (create or update) a configuration in the database
     * @param configuration configuration to be persisted
     * @return the persisted instance of configuration
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ConfigurationEntity saveConfiguration(final ConfigurationEntity configuration) {
        return repository.save(configuration);
    }

}
