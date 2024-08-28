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
package com.ericsson.nms.presentation.service.tests.service.ui_settings

import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.presentation.exceptions.UserMismatchException
import com.ericsson.nms.presentation.service.cache.scheduler.CacheMigrationScheduler
import com.ericsson.nms.presentation.service.ejb.ui_settings.UISettingsDAOResolver
import com.ericsson.nms.presentation.service.persistence.dao.impl.UISettingsCacheBasedDAO
import com.ericsson.nms.presentation.service.persistence.database.dao.UISettingGroupRepositoryBasedDAO
import com.ericsson.nms.presentation.service.security.SecurityUtil
import com.ericsson.nms.presentation.service.tests.base.AbstractPresentationServerSpec

import static org.hamcrest.CoreMatchers.equalTo
import static spock.util.matcher.HamcrestSupport.that

class UISettingsDAOResolverSpec extends AbstractPresentationServerSpec {
    def LOGGED_IN_USER = "administrator"

    @ObjectUnderTest
    UISettingsDAOResolver daoResolver

    @ImplementationInstance
    UISettingGroupRepositoryBasedDAO repositoryBasedDAO = Mock()

    @ImplementationInstance
    CacheMigrationScheduler cacheMigrationScheduler = Mock()

    @ImplementationInstance
    UISettingsCacheBasedDAO cacheBasedDAO = Mock()

    @ImplementationInstance
    SecurityUtil securityUtil = Stub{
        getCurrentUser() >> LOGGED_IN_USER
    }

    def "Resolves to cache DAO if the migration is disabled" () {
        given: "Application, user and setting type"
            def args = [app: "app", user: LOGGED_IN_USER, settingType: "someType"]

        when: "dao resolver resolves DAO"
            def dao = daoResolver.resolve(args.user, args.app, args.settingType)

        then: "cache DAO is resolved"
            that dao,  equalTo(cacheBasedDAO)

        and: "migration is disabled"
            1 * cacheMigrationScheduler.migrationEnabled >> false

        and: "no invocation of any mocks"
            0 * _
    }

    def "Resolves to cache DAO if the dmigration is enabled and data are in cache" () {
        given: "Application, user and setting type"
            def args = [app: "app", user: LOGGED_IN_USER, settingType: "someType"]

        when: "dao resolver resolves DAO"
            def dao = daoResolver.resolve(args.user, args.app, args.settingType)

        then: "cache DAO is resolved"
            that dao,  equalTo(cacheBasedDAO)

        and: "migration is enabled"
            1 * cacheMigrationScheduler.migrationEnabled >> true

        and: "the check for data to exist in cache is invoked with the positive results"
            1 * cacheBasedDAO.containsSettingGroup(args.user, args.app, args.settingType) >> true

        and: "no invocation of any other mocks"
            0 * _
    }

    def "Resolves to repository DAO if the migration is enabled and data are not in cache" () {
        given: "Application, user and setting type"
            def args = [app: "app", user: LOGGED_IN_USER, settingType: "someType"]

        when: "dao resolver resolves DAO"
            def dao = daoResolver.resolve(args.user, args.app, args.settingType)

        then: "repository DAO is resolved"
            that dao,  equalTo(repositoryBasedDAO)

        and: "migration is enabled"
            1 * cacheMigrationScheduler.migrationEnabled >> true

        and: "the check for data to exist in cache is invoked with the negative results"
            1 * cacheBasedDAO.containsSettingGroup(args.user, args.app, args.settingType) >> false

        and: "no invocation of any other mocks"
            0 * _
    }

    def "Throws an exception if the user is not logged in" () {
        given: "Application, user and setting type (user is not logged in)"
            def args = [app: "app", user: "userThatIsNotLoggedIn", settingType: "someType"]

        when: "dao resolver resolves DAO"
            def dao = daoResolver.resolve(args.user, args.app, args.settingType)

        then: "the UserMismatchException is thrown"
            UserMismatchException exception = thrown()

        and: "the exception message contains both usernames in the correct places"
            that exception.message, equalTo(
                "User that is passed to the settings service (userThatIsNotLoggedIn)" +
                    " should match the currently authenticated one (administrator)")
    }
}
