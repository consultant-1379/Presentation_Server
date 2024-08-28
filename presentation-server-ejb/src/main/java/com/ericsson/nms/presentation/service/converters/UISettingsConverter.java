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
package com.ericsson.nms.presentation.service.converters;

import com.ericsson.nms.presentation.service.api.dto.ui_settings.UiSettingDTO;
import com.ericsson.nms.presentation.service.api.dto.ui_settings.UiSettingGroupDTO;
import com.ericsson.nms.presentation.service.persistence.entities.v1.ui_settings.UiSettingEntity;
import com.ericsson.nms.presentation.service.persistence.entities.v1.ui_settings.UiSettingGroupEntity;
import com.ericsson.nms.presentation.service.uis.beans.UISettingBean;
import com.ericsson.nms.presentation.service.uis.beans.UISettingGroupDTO;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Converter for UISetting and UISettingGroup entity.
 * Conversion is done between Old/new DTOs and hibernate entities.
 */
@ApplicationScoped
public class UISettingsConverter {
    /**
     * 'Created' and 'updated' fields are not converted this way because the data layer handles it completely.
     * Hibernate attaches the DTO to the session and the fields are updated by preInsert and preUpdate.
     * @param dto the dto to be converted
     *
     * @return the converted entity
     */
    public UiSettingEntity settingToEntity(UiSettingDTO dto) {
        return new UiSettingEntity(dto.getId(), dto.getValue());
    }

    public UiSettingDTO settingToDTO(UiSettingEntity entity){
        UiSettingDTO uiSettingDTO = new UiSettingDTO(entity.getName(), entity.getValue());
        uiSettingDTO.setCreated(entity.getCreated());
        uiSettingDTO.setLastUpdated(entity.getLastUpdated());
        return uiSettingDTO;
    }

    public UiSettingGroupEntity settingsGroupToEntity(UiSettingGroupDTO dto) {
        if (dto == null) {
            return null;
        }

        final UiSettingGroupEntity entity = new UiSettingGroupEntity();
        entity.setApplication(dto.getApplication());
        entity.setName(dto.getKey());
        entity.setUsername(dto.getUser());
        entity.setSettings(dto.getSettings().stream()
                .map(this::settingToEntity)
                .collect(Collectors.toList()));

        return entity;
    }

    public UiSettingGroupDTO settingsGroupToDTO(UiSettingGroupEntity entity) {
        if (entity == null) {
            return null;
        }
        final UiSettingGroupDTO dto = new UiSettingGroupDTO();
        dto.setSettings(entity.getSettings().stream()
                .map(this::settingToDTO)
                .collect(Collectors.toSet()));

        dto.setApplication(entity.getApplication());
        dto.setKey(entity.getName());
        dto.setUser(entity.getUsername());

        return dto;
    }

    public UiSettingEntity settingFromUIBeanToEntity(UISettingBean uiSettingBean) {
        UiSettingEntity uiSettingEntity = new UiSettingEntity();
        uiSettingEntity.setName(uiSettingBean.getId());
        uiSettingEntity.setValue(uiSettingBean.getValue());
        return uiSettingEntity;
    }

    public UISettingBean settingFromEntityToUIBean(UiSettingEntity uiSettingEntity) {
        UISettingBean uiSettingBean = new UISettingBean();
        uiSettingBean.setId(uiSettingEntity.getName());
        uiSettingBean.setValue(uiSettingEntity.getValue());
        return uiSettingBean;
    }

    /**
     * This converter requires additional information as it's not present in the old DTO.
     * @param oldUiSettingGroupDTO old dto to be converted from
     * @param application the application for which the setting is saved. Leave null to ignore
     * @param username the use for which the setting is saved. Leave null to ignore
     * @param settingGroupName setting group name
     * @return converted entity
     */
    public UiSettingGroupEntity settingGroupFromOldDTOToEntity (UISettingGroupDTO oldUiSettingGroupDTO,
                                                             String application,
                                                             String username,
                                                             String settingGroupName) {
        UiSettingGroupEntity uiSettingGroupEntity = new UiSettingGroupEntity();
        uiSettingGroupEntity.setApplication(application);
        uiSettingGroupEntity.setUsername(username);
        uiSettingGroupEntity.setName(settingGroupName);
        uiSettingGroupEntity.setSettings(new ArrayList<>(oldUiSettingGroupDTO.getSettings().size()));

        for (UISettingBean uiSettingBean: oldUiSettingGroupDTO.getSettings().values()) {
            uiSettingGroupEntity.getSettings().add(settingFromUIBeanToEntity(uiSettingBean));
        }

        return uiSettingGroupEntity;
    }

    /**
     * Warning: this conversion loses the data about application, username and settings group name.
     * @param uiSettingGroupEntity entity to be converted from
     * @return the converted dto
     */
    public UISettingGroupDTO settingGroupFromEntityToOldDTO (UiSettingGroupEntity uiSettingGroupEntity) {
        UISettingGroupDTO oldUiSettingGroupDTO = new UISettingGroupDTO();
        oldUiSettingGroupDTO.setSettings(new HashMap<>(uiSettingGroupEntity.getSettings().size()));
        for (UiSettingEntity uiSettingEntity: uiSettingGroupEntity.getSettings()) {
            oldUiSettingGroupDTO.getSettings().put(uiSettingEntity.getName(), settingFromEntityToUIBean(uiSettingEntity));
        }
        return oldUiSettingGroupDTO;
    }
}
