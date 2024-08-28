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
package com.ericsson.nms.presentation.service.persistence.database.repository;

import com.ericsson.nms.presentation.service.persistence.entities.v1.ui_settings.UiSettingGroupEntity;
import com.ericsson.nms.presentation.service.validation.interceptors.bindings.Validate;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import java.util.Optional;

/**
 * <p>The repository for UI setting groups.</p>
 * <p>Since the single UI setting is only used in conjunction with the setting group, this repository is used to
 * manage single settings also</p>
 */
@Repository(forEntity = UiSettingGroupEntity.class)
@Validate
public interface UiSettingGroupRepository extends EntityRepository<UiSettingGroupEntity, Integer> {
    /**
     * <p>Save the entity</p>
     *
     * @param entity entity to be saved
     *
     * @return the entity with the ID field set
     */
    @Override
    UiSettingGroupEntity save(final UiSettingGroupEntity entity);

    /**
     * <p>Remove the whole setting group</p>
     *
     * @param entity the group to be removed (the ID should be set)
     */
    @Override
    void remove(UiSettingGroupEntity entity);

    /**
     * <p>Find the setting group by its natural id</p>
     *
     * @param application application which uses UI settigs
     * @param name        the name of the setting group
     * @param username    the user name (it could be any user at this point, not only the logged in one)
     *
     * @return an optional on the setting group, empty if not present
     */
    Optional<UiSettingGroupEntity> findByApplicationAndNameAndUsername(final String application, final String name, final String username);
}
