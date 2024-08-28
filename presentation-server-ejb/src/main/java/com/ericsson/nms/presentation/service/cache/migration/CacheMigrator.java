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
package com.ericsson.nms.presentation.service.cache.migration;

import com.ericsson.nms.presentation.exceptions.cache.CacheMigrationFailedException;
import com.ericsson.nms.presentation.service.persistence.database.repository.UiSettingGroupRepository;
import com.ericsson.nms.presentation.service.persistence.entities.v1.ui_settings.UiSettingEntity;
import com.ericsson.nms.presentation.service.persistence.entities.v1.ui_settings.UiSettingGroupEntity;
import com.ericsson.nms.presentation.service.uis.beans.UISettingGroupDTO;
import com.ericsson.oss.itpf.sdk.cache.annotation.NamedCache;
import org.slf4j.Logger;

import javax.cache.Cache;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * <p>This service is reponsible to deal with the migration from the replicated Infinispan Cache to the postgres database for UISettings.</p>
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class CacheMigrator {

    @Inject
    @NamedCache("Presentation.Server.UISettings")
    private Cache<String, UISettingGroupDTO> cache;

    @Inject
    private UiSettingGroupRepository settingsRepository;

    @Inject
    private Logger logger;

    /**
     * <p>Migrates every setting on infinispan cache to the database.</p>
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void migrateAll() {

        Iterator<Cache.Entry<String, UISettingGroupDTO>> iterator = cache.iterator();
        Set<String> keys = new TreeSet<>();

        try {
            while (iterator.hasNext()) {
                final Cache.Entry<String, UISettingGroupDTO> entry = iterator.next();

                migrateEntry(entry.getKey(), entry.getValue());
                keys.add(entry.getKey());
            }

            cache.removeAll(keys);

        // Catching just for troubleshooting log
        } catch (RuntimeException exception) {
            logger.error("Migration failed due to exception! Transaction will be rolled back.", exception);
            throw exception;
        }

    }

    private void migrateEntry(final String key, final UISettingGroupDTO settingGroup) {

        final String[] decomposedKey = key.split("_", 3);
        if (decomposedKey.length != 3) {
            throw new CacheMigrationFailedException(String.format("Failed to migrate setting key %s! Key does not have user + app + id.", key));
        }

        final String user = decomposedKey[0];
        final String app = decomposedKey[1];
        final String actualKey = decomposedKey[2];

        final UiSettingGroupEntity entity = settingsRepository.findByApplicationAndNameAndUsername(app, actualKey, user)
            .orElse(new UiSettingGroupEntity(actualKey, user, app));

        // We only need to migrate entries that were not migrated yet.
        // If the entity has an ID it means it was found in the database, so it was previously migrated.
        if (entity.getId() == null) {
            entity.setSettings(new ArrayList<>());
            entity.setMigrationDate(new Date());
            settingGroup.getSettings().forEach( (k, v) ->
                entity.getSettings().add(new UiSettingEntity(v.getId(), v.getValue())));

            logger.warn("Migrating entry {} for user {} and application {} to the database.",
                entity.getName(), entity.getUsername(), entity.getApplication());

            settingsRepository.save(entity);
        }
    }

}
