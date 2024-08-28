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
package com.ericsson.nms.presentation.service.persistence.dao.impl;

import com.ericsson.nms.presentation.service.ejb.ui_settings.UISettingsDAOResolver;
import com.ericsson.nms.presentation.service.persistence.dao.qualifier.Dispatcher;
import com.ericsson.nms.presentation.service.persistence.database.dao.UiSettingGroupDAO;
import com.ericsson.nms.presentation.service.persistence.entities.v1.ui_settings.UiSettingGroupEntity;

import javax.inject.Inject;
import java.util.Optional;

/**
 * Dispatches the calls to the different DAOs using the injected resolver.
 * This is the DAO to be used when you don't want to use the specific data storage
 */
@Dispatcher
public class UISettingsDispatcherDAO implements UiSettingGroupDAO {
    @Inject
    private UISettingsDAOResolver uiSettingsDAOResolver;

    /**
     * {@inheritDoc}
     */
    @Override
    public UiSettingGroupEntity save(UiSettingGroupEntity entity) {
        return resolveByEntity(entity).save(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(UiSettingGroupEntity entity) {
        resolveByEntity(entity).remove(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<UiSettingGroupEntity> findByApplicationAndNameAndUsername(String application, String name, String username) {
        return uiSettingsDAOResolver.resolve(username, application, name)
            .findByApplicationAndNameAndUsername(application, name, username);
    }

    private UiSettingGroupDAO resolveByEntity(UiSettingGroupEntity entity) {
        return uiSettingsDAOResolver.resolve(entity.getUsername(),
            entity.getApplication(),
            entity.getName());
    }
}
