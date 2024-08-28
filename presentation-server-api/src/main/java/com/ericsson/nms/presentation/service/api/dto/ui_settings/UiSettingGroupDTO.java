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
package com.ericsson.nms.presentation.service.api.dto.ui_settings;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * The settings group, that can contain multiple individual settings.
 * It is wired-up to the application, user and has a name of its own.
 */
@Data
@EqualsAndHashCode(of = {"key", "application", "user"})
@NoArgsConstructor
public class UiSettingGroupDTO {

    private String key;

    private String application;

    private String user;

    private Set<UiSettingDTO> settings;

    public UiSettingGroupDTO (String application, String user, String key, UiSettingDTO... settings) {
        this.settings = new LinkedHashSet<>(settings.length);
        this.application = application;
        this.user = user;
        this.key = key;
        this.settings.addAll(Arrays.asList(settings));
    }
}
