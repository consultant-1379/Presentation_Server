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
package com.ericsson.nms.presentation.service.persistence.dao.configuration;

import com.ericsson.nms.presentation.service.persistence.entities.v1.configuration.ConfigurationEntity;
import com.ericsson.nms.presentation.service.validation.interceptors.annotations.IgnoreValidation;
import com.ericsson.nms.presentation.service.validation.interceptors.bindings.Validate;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import java.util.Optional;

/**
 * Repository class for Configuration entity
 */
@Repository(forEntity = ConfigurationEntity.class)
@Validate
public interface ConfigurationRepository extends EntityRepository<ConfigurationEntity, Integer> {

    /**
     * Gets the configuation which matches with the given key in the database
     * @param key key to be queried
     * @return an Optional instance reflecting the search result.
     */
    Optional<ConfigurationEntity> findByKey(@IgnoreValidation final String key);

}
