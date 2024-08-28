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

import com.ericsson.nms.presentation.service.persistence.database.repository.UiSettingGroupRepository;
import com.ericsson.nms.presentation.service.persistence.entities.v1.ui_settings.UiSettingGroupEntity;

import javax.inject.Inject;
import java.util.Optional;

/**
 * The DAO object that is based on the repository, forwarding its methods that are the same as the
 * ones in the {@link UiSettingGroupDAO} interface but isolating the ones that are not present there
 */
public class UISettingGroupRepositoryBasedDAO implements UiSettingGroupDAO {
    @Inject
    private UiSettingGroupRepository uiSettingGroupRepository;

    /**
     * @see UiSettingGroupRepository#save(UiSettingGroupEntity entity)
     */
    @Override
    public UiSettingGroupEntity save(UiSettingGroupEntity entity) {
        return uiSettingGroupRepository.save(entity);
    }

    /**
     * @see UiSettingGroupRepository#remove(UiSettingGroupEntity entity)
     */
    @Override
    public void remove(UiSettingGroupEntity entity) {
        uiSettingGroupRepository.remove(entity);
    }

    /**
     * @see UiSettingGroupRepository#findByApplicationAndNameAndUsername(String application, String name, String username)
     */
    @Override
    public Optional<UiSettingGroupEntity> findByApplicationAndNameAndUsername(String application, String name, String username) {
        return uiSettingGroupRepository.findByApplicationAndNameAndUsername(application, name, username);
    }
}
