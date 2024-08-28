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
package com.ericsson.nms.presentation.service.persistence.database.dao;

import com.ericsson.nms.presentation.service.persistence.entities.v1.ui_settings.UiSettingGroupEntity;

import java.util.Optional;

/**
 * The interface that is intended to mimic only
 * those methods of {@link com.ericsson.nms.presentation.service.persistence.database.repository.UiSettingGroupRepository}
 * that are needed for the service, isolating the others.
 * <p>
 * Is also to be implemented by all other data storage types that are not supported by Hibernate, i.e. Infinispan cache.
 * <p>
 * Although the repo is operating Hibernate entities, the validation and constraints are not guaranteed to be respected.
 * Use @{@link com.ericsson.nms.presentation.service.persistence.database.repository.UiSettingGroupRepository} if you want
 * to be absolutely sure
 */
public interface UiSettingGroupDAO {
    /**
     * @param entity the entity to be saved. <b>Entity might not be validated, depending on the implementation</b>
     *
     * @return the saved entity. <b>The id, created and lastUpdated fields might not be set in the returned entity, dependent on implementation</b>
     *
     * @see com.ericsson.nms.presentation.service.persistence.database.repository.UiSettingGroupRepository#save(UiSettingGroupEntity entity)
     * with the following exception: <b>The setting of ID, Created and LastUpdated fields is not guaranteed and
     * depends on the implementation</b>
     */
    UiSettingGroupEntity save(final UiSettingGroupEntity entity);

    /**
     * @param entity entity to be removed. To be compliant with the DB spec the Id field is recommended to be set on this one if known.
     *
     * @see com.ericsson.nms.presentation.service.persistence.database.repository.UiSettingGroupRepository#remove(UiSettingGroupEntity entity)
     */
    void remove(UiSettingGroupEntity entity);

    /**
     * @param application the application which owns the setting group
     * @param name the name of the setting group
     * @param username the group owner name
     *
     * @return the setting group entity <b>The id fields might not be set here, depending on the implementation</b>
     *
     * @see com.ericsson.nms.presentation.service.persistence.database.repository.UiSettingGroupRepository#findByApplicationAndNameAndUsername(String, String, String)
     * with the following exception: the 'id' field is not returned from storages when it's not being used so it's not recommended to rely on it when using this interface.
     * If you absolutely require it consider to rely on {@link com.ericsson.nms.presentation.service.persistence.database.repository.UiSettingGroupRepository instead.}
     */
    Optional<UiSettingGroupEntity> findByApplicationAndNameAndUsername(final String application, final String name, final String username);
}
