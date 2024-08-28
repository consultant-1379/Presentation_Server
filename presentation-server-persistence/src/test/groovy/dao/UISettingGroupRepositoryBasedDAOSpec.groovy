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
package dao

import com.ericsson.nms.presentation.service.persistence.database.dao.UISettingGroupRepositoryBasedDAO
import com.ericsson.nms.presentation.service.persistence.database.repository.UiSettingGroupRepository
import com.ericsson.nms.presentation.service.persistence.entities.v1.ui_settings.UiSettingGroupEntity
import spock.lang.Specification


class UISettingGroupRepositoryBasedDAOSpec extends Specification {

    UISettingGroupRepositoryBasedDAO uiSettingGroupRepositoryBasedDAO = new UISettingGroupRepositoryBasedDAO()

    UiSettingGroupRepository repository = Mock();

    def setup() {
        uiSettingGroupRepositoryBasedDAO.uiSettingGroupRepository = repository;
    }

    def "save call to the DAO is forwarding straight to the repository"() {
        given: "an entity"
            UiSettingGroupEntity entity = Mock()

        when: "save is invoked on a DAO"
            uiSettingGroupRepositoryBasedDAO.save(entity)

        then: "save is invoked on a repo with a same entity"
            1 * repository.save(entity)

        and: "no more interactions"
            0 * _
    }

    def "remove call to the DAO is forwarding straight to the repository"() {
        given: "an entity"
            UiSettingGroupEntity entity = Mock()

        when: "remove is invoked on a DAO"
            uiSettingGroupRepositoryBasedDAO.remove(entity)

        then: "remove is invoked on a repo with a same entity"
            1 * repository.remove(entity)

        and: "no more interactions"
            0 * _
    }

    def "get call to the DAO is forwarding straight to the repository"() {
        given: "get method parameters"
            def args = [app: "app", name: "name", username: "username"]

        when: "get is invoked on a DAO"
            uiSettingGroupRepositoryBasedDAO.findByApplicationAndNameAndUsername(args.app, args.name, args.username)

        then: "get is invoked on a repo with same parameters"
            1 * repository.findByApplicationAndNameAndUsername(args.app, args.name, args.username)

        and: "no more interactions"
            0 * _
    }
}
