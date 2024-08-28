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
package com.ericsson.nms.presentation.service.tests.cache.migration


import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.nms.presentation.exceptions.cache.CacheMigrationFailedException
import com.ericsson.nms.presentation.service.cache.scheduler.CacheMigrationScheduler
import com.ericsson.nms.presentation.service.persistence.dao.configuration.ConfigurationRepository
import com.ericsson.nms.presentation.service.persistence.database.repository.UiSettingGroupRepository
import com.ericsson.nms.presentation.service.persistence.entities.v1.configuration.ConfigurationEntity
import com.ericsson.nms.presentation.service.persistence.entities.v1.ui_settings.UiSettingGroupEntity
import com.ericsson.nms.presentation.service.tests.base.AbstractPresentationServerSpec
import com.ericsson.nms.presentation.service.uis.beans.UISettingBean
import com.ericsson.nms.presentation.service.uis.beans.UISettingGroupDTO
import com.ericsson.oss.itpf.sdk.cache.annotation.NamedCache

import javax.cache.Cache
import javax.inject.Inject

class CacheMigrationSpec extends AbstractPresentationServerSpec {

    @Inject
    CacheMigrationScheduler scheduler

    @MockedImplementation
    ConfigurationRepository configRepository

    @MockedImplementation
    UiSettingGroupRepository settingsRepository

    @MockedImplementation
    @NamedCache("Presentation.Server.UISettings")
    private Cache<String, UISettingGroupDTO> cache

    def setup() {
        configRepository.findByKey("cache.migration.enabled") >> Optional.of(
            new ConfigurationEntity("cache.migration.enabled", "true")
        )
    }

    def "all setting on the cache should be migrated to the database"() {

        given: "mock the UISettings cache to have a few entries"
            def multipleSettings = new UISettingGroupDTO()
            (1..2).each { multipleSettings.put(new UISettingBean("key-${it}", "value-${it}")) }

            cache.iterator() >> [
                [getKey: { "user01_app01_setting-01"}, getValue: {new UISettingGroupDTO(new UISettingBean("key-01", "value-01"))}] as Cache.Entry<String, UISettingGroupDTO>,
                [getKey: { "user01_app01_setting-02"}, getValue: {multipleSettings}] as Cache.Entry<String, UISettingGroupDTO>
            ].iterator()

        and: "mock the repository so none of the settings are found in the database (not migrated yet)"
            settingsRepository.findByApplicationAndNameAndUsername(*_) >> Optional.ofNullable(null)

        when: "trigger the migration"
            scheduler.migrate()

        then: "All entries should be migrated"
            2 * settingsRepository.save(_ as UiSettingGroupEntity) >> { UiSettingGroupEntity entity ->

                assert entity.application == "app01"
                assert entity.username == "user01"

                if (entity.name == "setting-01") {
                    assert entity.settings.size() == 1
                    assert entity.settings[0].name == "key-01"
                    assert entity.settings[0].value == "value-01"
                    assert entity.migrationDate != null
                }

                if (entity.name == "setting-02") {
                    assert entity.settings.size() == 2
                    assert entity.settings.find {it.name == "key-1"}.value == "value-1"
                    assert entity.settings.find {it.name == "key-2"}.value == "value-2"
                    assert entity.migrationDate != null
                }
            }


        and: "All entries should be deleted from the cache"
            1 * cache.removeAll(_ as Set<String>)  >> { Set<String> keys ->
                assert keys.containsAll(["user01_app01_setting-01","user01_app01_setting-02"])
            }

    }

    def "when an invalid key is found, rollback the operation (do not delete any entry)"() {

        given: "mock the UISettings cache to have an invalid key entry"
            cache.iterator() >> [
                [getKey: { "setting-01"}, getValue: {new UISettingGroupDTO(new UISettingBean("key-01", "value-01"))}] as Cache.Entry<String, UISettingGroupDTO>,
            ].iterator()

        and: "mock the repository so none of the settings are found in the database (not migrated yet)"
            settingsRepository.findByApplicationAndNameAndUsername(*_) >> Optional.ofNullable(null)

        when: "trigger the migration"
            scheduler.migrate()

        then: "an exception should be raised"
            thrown(CacheMigrationFailedException)

        and: "No cache entry should be removed"
            0 * cache.removeAll(_ as Set<String>)
    }


    def "do not import settings already migrated to the database"() {

        given: "mock the UISettings cache to have a few entries"
            def multipleSettings = new UISettingGroupDTO()
            (1..2).each { multipleSettings.put(new UISettingBean("key-${it}", "value-${it}")) }

            cache.iterator() >> [
                [getKey: { "user01_app01_setting-01"}, getValue: {new UISettingGroupDTO(new UISettingBean("key-01", "value-01"))}] as Cache.Entry<String, UISettingGroupDTO>,
                [getKey: { "user01_app01_setting-02"}, getValue: {multipleSettings}] as Cache.Entry<String, UISettingGroupDTO>
            ].iterator()

        and: "mock the repository so none of the settings are found in the database for setting-01"
            settingsRepository.findByApplicationAndNameAndUsername("app01", "setting-01","user01") >>
                Optional.ofNullable(null)

        and: "but found something for setting-02"
            settingsRepository.findByApplicationAndNameAndUsername("app01", "setting-02","user01") >>
                Optional.of(new UiSettingGroupEntity(id: 1L))

        when: "trigger the migration"
            scheduler.migrate()

        then: "only setting-01 should be saved in the database"
            1 * settingsRepository.save(_ as UiSettingGroupEntity) >> { UiSettingGroupEntity entity ->
                assert entity.name == "setting-01"
            }

        and: "All entries should be deleted from the cache"
            1 * cache.removeAll(_ as Set<String>)  >> { Set<String> keys ->
                assert keys.containsAll(["user01_app01_setting-01","user01_app01_setting-02"])
            }

    }

}
