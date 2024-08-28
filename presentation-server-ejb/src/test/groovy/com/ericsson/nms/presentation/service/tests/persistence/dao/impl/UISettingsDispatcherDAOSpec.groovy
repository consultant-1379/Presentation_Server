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
package com.ericsson.nms.presentation.service.tests.persistence.dao.impl

import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.presentation.service.ejb.ui_settings.UISettingsDAOResolver
import com.ericsson.nms.presentation.service.persistence.dao.impl.UISettingsDispatcherDAO
import com.ericsson.nms.presentation.service.persistence.database.dao.UiSettingGroupDAO
import com.ericsson.nms.presentation.service.persistence.entities.v1.ui_settings.UiSettingGroupEntity
import com.ericsson.nms.presentation.service.tests.base.AbstractPresentationServerSpec


class UISettingsDispatcherDAOSpec extends AbstractPresentationServerSpec {
    @ObjectUnderTest
    UISettingsDispatcherDAO dispatcherDAO;

    UiSettingGroupDAO settingGroupDAO = Mock()

    @ImplementationInstance
    UISettingsDAOResolver resolver = Mock()

    def "The dispatcher DAO is using the resolved DAO to save an entity"() {
        given: "an entity"
            UiSettingGroupEntity uiSettingGroupEntity = new UiSettingGroupEntity("name", "username", "app")

        when: "the dispatcher DAO is saving an entity"
            dispatcherDAO.save(uiSettingGroupEntity)

        then: "the resolved DAO is saving an entity"
            1 * settingGroupDAO.save(uiSettingGroupEntity)

        and: "the resolver is returning the resolved DAO when it is called with right arguments"
            1 * resolver.resolve("username", "app", "name") >> settingGroupDAO

        and: "no other mock interactions"
            0 * _
    }

    def "The dispatcher DAO is using the resolved DAO to remove an entity"() {
        given: "an entity"
            UiSettingGroupEntity uiSettingGroupEntity = new UiSettingGroupEntity("name", "username", "app")

        when: "the dispatcher DAO is removing an entity"
            dispatcherDAO.remove(uiSettingGroupEntity)

        then: "the resolved DAO is removing an entity"
            1 * settingGroupDAO.remove(uiSettingGroupEntity)

        and: "the resolver is returning the resolved DAO when it is called with right arguments"
            1 * resolver.resolve("username", "app", "name") >> settingGroupDAO

        and: "no other mock interactions"
            0 * _
    }

    def "The dispatcher DAO is using the resolved DAO to get an entity"() {
        given: "an set of parameters"
            def args = [user: "username", app: "app", name: "name"]

        when: "the dispatcher DAO is finding an entity"
            dispatcherDAO.findByApplicationAndNameAndUsername(args.app, args.name, args.user)

        then: "the resolved DAO is finding an entity"
            1 * settingGroupDAO.findByApplicationAndNameAndUsername(args.app, args.name, args.user)

        and: "the resolver is returning the resolved DAO when it is called with right arguments"
            1 * resolver.resolve("username", "app", "name") >> settingGroupDAO

        and: "no other mock interactions"
            0 * _
    }
}
