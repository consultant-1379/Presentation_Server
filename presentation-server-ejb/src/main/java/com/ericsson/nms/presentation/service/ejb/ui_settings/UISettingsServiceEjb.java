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
package com.ericsson.nms.presentation.service.ejb.ui_settings;


import com.ericsson.nms.presentation.exceptions.SettingNotFoundException;
import com.ericsson.nms.presentation.service.api.dto.ui_settings.UiSettingDTO;
import com.ericsson.nms.presentation.service.api.dto.ui_settings.UiSettingGroupDTO;
import com.ericsson.nms.presentation.service.converters.UISettingsConverter;
import com.ericsson.nms.presentation.service.persistence.dao.qualifier.Dispatcher;
import com.ericsson.nms.presentation.service.persistence.database.dao.UiSettingGroupDAO;
import com.ericsson.nms.presentation.service.persistence.entities.v1.ui_settings.UiSettingEntity;
import com.ericsson.nms.presentation.service.persistence.entities.v1.ui_settings.UiSettingGroupEntity;
import com.ericsson.nms.presentation.service.security.SecurityUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * The service implementation which uses the dispatcher DAO to access settings
 * and the logged in user as the 'user' field of the service group.
 */
@Stateless
public class UISettingsServiceEjb implements UiSettingsService {
    @Inject
    @Dispatcher
    private UiSettingGroupDAO settingGroupDAO;

    @Inject
    private SecurityUtil securityUtil;

    @Inject
    private UISettingsConverter uiSettingsConverter;

    @Inject
    private Logger log;

    /**
     * {@inheritDoc}
     */
    @Override
    public UiSettingGroupDTO getSettingsGroupByKey(String application, String key) {
        final String user = securityUtil.getCurrentUser();
        return settingGroupDAO.findByApplicationAndNameAndUsername(application, key, user)
                .map(uiSettingsConverter::settingsGroupToDTO)
                .orElseThrow(() ->
                        new SettingNotFoundException(String.format("No setting found with key %s,"
                                + " for application %s, and user %s", key, application, user)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UiSettingGroupDTO saveSettings(String application, String key,
                                          UiSettingGroupDTO settingGroupEntity) {

        final String user = securityUtil.getCurrentUser();
        settingGroupEntity.setUser(user);
        settingGroupEntity.setApplication(application);
        settingGroupEntity.setKey(key);

        Optional<UiSettingGroupEntity> uiSettingGroupEntityInDB =
            settingGroupDAO.findByApplicationAndNameAndUsername(application, key, user);

        UiSettingGroupEntity uiSettingGroupEntityToSave;

        if (uiSettingGroupEntityInDB.isPresent()) {
            uiSettingGroupEntityToSave = applyChangesToExistingGroupEntity(uiSettingGroupEntityInDB.get(), settingGroupEntity);
        } else {
            uiSettingGroupEntityToSave = uiSettingsConverter.settingsGroupToEntity(settingGroupEntity);
        }
        preProcessSettings(uiSettingGroupEntityToSave);

        return uiSettingsConverter.settingsGroupToDTO(settingGroupDAO.save(uiSettingGroupEntityToSave));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<UiSettingGroupDTO> deleteSetting(String application, String settingGroupKey, String settingKey) {
        final String user = securityUtil.getCurrentUser();
        Optional<UiSettingGroupEntity> uiSettingGroupEntityInDB =
            settingGroupDAO.findByApplicationAndNameAndUsername(application, settingGroupKey, user);
        if (uiSettingGroupEntityInDB.isPresent()) {
            final UiSettingGroupEntity uiSettingGroupEntity = uiSettingGroupEntityInDB.get();
            final Optional<UiSettingEntity> setting = uiSettingGroupEntity.getSettings().stream()
                .filter(s -> s.getName().equals(settingKey))
                .findFirst();
            uiSettingGroupEntity.getSettings().remove(setting
                .orElseThrow(() -> new SettingNotFoundException("Trying to delete the setting that does not exist")));
            if (uiSettingGroupEntity.getSettings().isEmpty()) {
                settingGroupDAO.remove(uiSettingGroupEntity);
                return Optional.empty();
            }
            return Optional.of(uiSettingsConverter.settingsGroupToDTO(settingGroupDAO.save(uiSettingGroupEntity)));
        } else {
            throw new SettingNotFoundException("Trying to delete entity from nonexisting setting group");
        }
    }

    private UiSettingGroupEntity applyChangesToExistingGroupEntity(UiSettingGroupEntity uiSettingGroupEntity, UiSettingGroupDTO uiSettingGroupDTO) {
        List<UiSettingEntity> addedSettings = new LinkedList<>();

        for (UiSettingDTO dto : uiSettingGroupDTO.getSettings()) {
            Optional<UiSettingEntity> entityInDB = uiSettingGroupEntity
                    .getSettings()
                    .stream()
                    .filter(uiSettingEntity -> uiSettingEntity.getName().equals(dto.getId()))
                    .findFirst();

            if (entityInDB.isPresent()) {
                entityInDB.get().setValue(dto.getValue());
            } else {
                addedSettings.add(new UiSettingEntity(dto.getId(), dto.getValue()));
            }
        }

        uiSettingGroupEntity.getSettings().addAll(addedSettings);
        return uiSettingGroupEntity;
    }

    private void preProcessSettings(UiSettingGroupEntity original) {
        Set<UiSettingEntity> uiSettingEntitySetToRemove = new LinkedHashSet<>();
        for (final UiSettingEntity entity: original.getSettings()) {
            if (StringUtils.isEmpty(entity.getValue())) {
                uiSettingEntitySetToRemove.add(entity);
            } else {
                logLargeSettingIfNeeded(entity, original);
            }
        }
        for (final UiSettingEntity uiSettingEntity : uiSettingEntitySetToRemove) {
            uiSettingEntity.setSettingGroup(null);
            original.getSettings().remove(uiSettingEntity);
        }
    }

    private void logLargeSettingIfNeeded(UiSettingEntity entity, UiSettingGroupEntity parentSettingGroup) {
        if (entity.getName() == null) {
            log.error("Encountered a setting with null name.");
            return;
        }
        String settingValue = entity.getValue();
        if (settingValue.length() > 5_000) {
            log.warn("Oversized setting detected. The setting size is larger than the maximum supported size (5000 characters). Please reduce the setting size. app={}, groupName={} name={}, value={}", parentSettingGroup.getApplication(), parentSettingGroup.getName(), entity.getName(), entity.getValue());
        } else if (settingValue.length() > 2_000) {
            log.warn("Large setting detected. app={}, groupName={} name={}, value={}", parentSettingGroup.getApplication(), parentSettingGroup.getName(), entity.getName(), entity.getValue());
        }
    }
}
