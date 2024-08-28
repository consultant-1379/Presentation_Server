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

import com.ericsson.nms.presentation.service.converters.UISettingsConverter;
import com.ericsson.nms.presentation.service.persistence.database.dao.UiSettingGroupDAO;
import com.ericsson.nms.presentation.service.persistence.entities.v1.ui_settings.UiSettingGroupEntity;
import com.ericsson.nms.presentation.service.uis.beans.UISettingBean;
import com.ericsson.nms.presentation.service.uis.beans.UISettingGroupDTO;

import javax.inject.Inject;
import java.util.Optional;

/**
 * The implementation of the {@link UiSettingGroupDAO} that is written with the intent to work as close to the one based
 * on DB as possible. The id generation and validation parts are missing for obvious reasons,
 * as 'created' and 'lastUpdated' are, everything else should work in the same way, as indicated by closely related tests
 * (for this class and {@link com.ericsson.nms.presentation.service.persistence.database.repository.UiSettingGroupRepository})
 */
public class UISettingsCacheBasedDAO implements UiSettingGroupDAO {
    @Inject
    private UISettingsConverter uiSettingsConverter;

    @Inject
    private UISettingsDao uiSettingsDao;

    private static final String SEPARATOR = "_";

    /**
     * Saves the entity to cache, converting it to the old DTO implementation
     * @param entity the entity to be saved. <b>Entity is NOT validated, ID field is not returned, as well as 'created' and 'lastUpdated'</b>
     *
     * @return the entity converted from the old DTO i.e. without 'id', 'created' and 'lastUpdated' fields
     */
    @Override
    public UiSettingGroupEntity save(UiSettingGroupEntity entity) {
        String key = buildKey(entity.getUsername(), entity.getApplication(), entity.getName());
        UISettingGroupDTO existingDTO = uiSettingsDao.getUISetting(key);
        UISettingGroupDTO dtoToSave = uiSettingsConverter.settingGroupFromEntityToOldDTO(entity);

        markDeletedSettingsForDeletion(existingDTO, dtoToSave);

        uiSettingsDao.setSetting(key, dtoToSave);
        return entity;
    }

    /**
     * {@inheritDoc}
     * In this case you could not set the 'id' field as it will be ignored, instead the entity will be deleted using the composite natural key
     */
    @Override
    public void remove(UiSettingGroupEntity entity) {
        String key = buildKey(entity.getUsername(), entity.getApplication(), entity.getName());
        uiSettingsDao.remove(key);
    }

    /**
     * {@inheritDoc}
     * In this case the 'id', 'created' and 'lastUpdated' fields would not be returned as they are not persisted in the cache.
     */
    @Override
    public Optional<UiSettingGroupEntity> findByApplicationAndNameAndUsername(String application, String name, String username) {
        String key = buildKey(username, application, name);
        UISettingGroupDTO uiSettingGroupDTO = uiSettingsDao.getUISetting(key);
        if (uiSettingGroupDTO.getSettings().size() == 0) {
            return Optional.empty();
        } else {
            return Optional.of(uiSettingsConverter.settingGroupFromOldDTOToEntity(uiSettingGroupDTO,
                application, username, name));
        }
    }

    public boolean containsSettingGroup(final String userId, final String appId, final String settingType) {
        return uiSettingsDao.contains(buildKey(userId, appId, settingType));
    }

    private String buildKey(final String userId, final String appId, final String settingType) {
        return userId + SEPARATOR + appId + SEPARATOR + settingType;
    }

    /**
     * As the cache DAO is deleting the settings with null values and not the ones that do not exist,
     * an additional operation is needed, that is to add the missing settings with null values, allowing them to be deleted.
     * @param existingDTO the DTO that exists in the cache
     * @param dtoToSave the DTO that is going to be saved, possibly having the deleted settings removed from the settings map
     */
    private void markDeletedSettingsForDeletion(UISettingGroupDTO existingDTO,
                                                UISettingGroupDTO dtoToSave) {
        for (UISettingBean beanInDB : existingDTO.getSettings().values()){
            dtoToSave.getSettings().putIfAbsent(beanInDB.getId(), new UISettingBean(beanInDB.getId(), null));
        }
    }
}
