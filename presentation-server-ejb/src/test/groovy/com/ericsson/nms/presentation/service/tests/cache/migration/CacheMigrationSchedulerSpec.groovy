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
import com.ericsson.cds.cdi.support.rule.SpyImplementation
import com.ericsson.nms.presentation.service.cache.migration.CacheMigrator
import com.ericsson.nms.presentation.service.cache.scheduler.CacheMigrationScheduler
import com.ericsson.nms.presentation.service.persistence.dao.configuration.ConfigurationRepository
import com.ericsson.nms.presentation.service.persistence.entities.v1.configuration.ConfigurationEntity
import com.ericsson.nms.presentation.service.tests.base.AbstractPresentationServerSpec
import com.ericsson.nms.presentation.service.uis.beans.UISettingBean
import com.ericsson.nms.presentation.service.uis.beans.UISettingGroupDTO
import com.ericsson.oss.itpf.sdk.cache.annotation.NamedCache

import javax.cache.Cache
import javax.ejb.TimerService
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class CacheMigrationSchedulerSpec extends AbstractPresentationServerSpec {

    @SpyImplementation
    private CacheMigrationScheduler scheduler = new CacheMigrationScheduler()

    @SpyImplementation
    CacheMigrator cacheMigrator

    @MockedImplementation
    ConfigurationRepository configRepository

    @MockedImplementation
    TimerService timerService

    @MockedImplementation
    @NamedCache("Presentation.Server.UISettings")
    private Cache<String, UISettingGroupDTO> cache

    private void enableMigration() {
        configRepository.findByKey(CacheMigrationScheduler.MIGRATION_ENABLED_CONFIG) >>
            Optional.of(new ConfigurationEntity(value: "true"))
    }

    def "if cache.migration.enabled config is not found in the database, do not schedule a migration"() {

        given: "mock configRepository to not find the configuration in the database"
            configRepository.findByKey(CacheMigrationScheduler.MIGRATION_ENABLED_CONFIG) >> Optional.ofNullable(null)

        when: "schedule a migration"
            scheduler.scheduleMigration()

        then: "as the development environment property was not set, no migration is scheduled"
            0 * timerService.createSingleActionTimer(*_)

    }

    def "if the cache is not empty a migration should be scheduled"() {

        given: "mock the UISettings cache to have a few entries"
            cache.iterator() >> [
                [getKey: { "setting-01"}, getValue: {new UISettingGroupDTO(new UISettingBean("key-01", "value-01"))}] as Cache.Entry<String, UISettingGroupDTO>,
                [getKey: { "setting-02"}, getValue: {new UISettingGroupDTO(new UISettingBean("key-02", "value-02"))}] as Cache.Entry<String, UISettingGroupDTO>
            ].iterator()

        and: "enable migration"
            enableMigration()

        and: "mock configRepository to find the delay configuration"
            configRepository.findByKey(_) >> Optional.of(new ConfigurationEntity(CacheMigrationScheduler.SCHEDULE_DELAY_CONFIG_KEY, "24"))

        when: "schedule the migration"
            def duration = scheduler.scheduleMigration()
            def oneDay = Duration.between(LocalDateTime.now(), LocalDateTime.now().plusDays(1))
            def oneDayAnOneHour = Duration.between(LocalDateTime.now(), LocalDateTime.now().plusDays(1).plusHours(1))

        then: "the scheduler should have been scheduled to run in at least one day"
            duration.toMinutes() >= oneDay.toMinutes()

        and: "and no more than 1 day and 1 hour"
            duration.toMinutes() <= oneDayAnOneHour.toMinutes()

    }

    def "if there's still entries on the cache after a migration, then it should be rescheduled"() {

        given: "mock the UISettings cache to have a few entries"
            cache.iterator() >> [
                [getKey: { "setting-01"}, getValue: {new UISettingGroupDTO(new UISettingBean("key-01", "value-01"))}] as Cache.Entry<String, UISettingGroupDTO>,
                [getKey: { "setting-02"}, getValue: {new UISettingGroupDTO(new UISettingBean("key-02", "value-02"))}] as Cache.Entry<String, UISettingGroupDTO>
            ].iterator()

        and: "enable migration"
            enableMigration()

        and: "Mock cache migrator to not migrate anything"
            cacheMigrator.migrateAll() >> {}

        when: "trigger the migration"
            scheduler.migrate()

        then: "as the cache is not empty the migration should be rescheduled for 6 hours later"
            1 * scheduler.scheduleMigration(_ as Long) >> { Long delay ->
                assert delay == TimeUnit.HOURS.toMillis(6)
            }
    }

    def "if the cache empty no migration should be scheduled"() {

        given: "mock the UISettings cache to have a few entries"
            cache.iterator() >> [].iterator()

        and: "enable migration"
            enableMigration()

        when: "schedule the migration"
            def duration = scheduler.scheduleMigration()

        then: "duration should be null as there's no scheduling"
            duration == null
    }

}
