package com.ericsson.nms.presentation.service.tests.service

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.presentation.service.api.dto.ActionRuleCondition
import com.ericsson.nms.presentation.service.api.dto.Localization
import com.ericsson.nms.presentation.service.cache.LocaleCachePopulator
import com.ericsson.nms.presentation.service.ejb.ActionServiceEjb
import com.ericsson.nms.presentation.service.ejb.ApplicationServiceEjb
import com.ericsson.nms.presentation.service.ejb.DictionaryServiceEjb
import com.ericsson.nms.presentation.service.locale.LocaleServiceEjb
import com.ericsson.nms.presentation.service.tests.base.AbstractScenario04Spec
import spock.lang.Unroll

import javax.inject.Inject

/**
 * Test specification for DictionaryService
 */
class DictionaryServiceSpec extends AbstractScenario04Spec {

    @ObjectUnderTest
    DictionaryServiceEjb dictionaryService

    @Inject
    LocaleCachePopulator localeCachePopulator

    @Inject
    LocaleServiceEjb localeService

    @Inject
    ApplicationServiceEjb applicationService

    @Inject
    ActionServiceEjb actionService

    def setup() {
        // All resources are always authorized for these tests
        authorizeAllResources()
    }

    @Unroll
    def "get application localized texts when first preferred locale is available: #description"() {

        given: "set preferred locales"
            localeService.userLocale = locales as LinkedHashSet

        when: "get the application from the cache"
            def app = applicationService.getApps("app01")[0]

        then:
            app.name == localizedTexts.name
            app.shortInfo == localizedTexts.shortInfo
            app.acronym == localizedTexts.acronym

        where:
            description                 | locales            | localizedTexts
            "en-us is the first choice" | ["en-us", "pt-br"] | [name: "My Application 01", shortInfo: "This is a brief description of My Application", acronym: "APP01"]
            "pt-br is the first choice" | ["pt-br", "en-us"] | [name: "Minha Aplicação 01", shortInfo: "Esta é uma breve descrição da minha aplicação", acronym: "AP01"]
    }

    def "get application localized texts when first preferred locale is not available"() {

        given: "set preferred locales where first one does not exist in the dictionary"
            localeService.userLocale = ["fr", "en-us", "pt-br"] as LinkedHashSet

        when: "get the application from the cache"
            def app = applicationService.getApps("app01")[0]

        then: "application should be returned in en-us"
            app.name == "My Application 01"
            app.shortInfo == "This is a brief description of My Application"
            app.acronym == "APP01"
    }

    def "get application localized texts when there's no locale defined"() {

        when: "get the application from the cache"
            def app = applicationService.getApps("app01")[0]

        then: "application should use the default locale (en-us)"
            app.name == "My Application 01"
            app.shortInfo == "This is a brief description of My Application"
            app.acronym == "APP01"
    }

    def "get application localized texts when dictionary is not available"() {

        when: "get the application from the cache"
            def app = applicationService.getApps("app02")[0]

        then: "application should use the texts defined in the json metadata file"
            app.name == "My Application Name"
            app.shortInfo == "My Description"
            app.acronym == "APP02"
    }

    def "get application when dictionary is not available and no name is defined"() {

        when: "get the application from the cache"
            def apps = applicationService.getApps("app03")

        then: "No application should be found as name is required to exist in the metadata or localization"
            apps.size() == 0
    }

    @Unroll
    def "get localized action name when first preferred locale is available: #description"() {

        given: "set preferred locales"
            localeService.userLocale = locales as LinkedHashSet

        when: "request actions for ManagedObjects"
            def actions = actionService.getActionsBySelection("app01", false,
                    [new ActionRuleCondition(dataType: "ManagedObject")])

        then:
            actions.size() == 1
            actions[0].defaultLabel == actionName

        where:
            description                 | locales            | actionName
            "en-us is the first choice" | ["en-us", "pt-br"] | "First Action"
            "pt-br is the first choice" | ["pt-br", "en-us"] | "Primeira Ação"


    }

    def "get localized action name when first preferred locale is not available"() {

        given: "set preferred locales"
            localeService.userLocale = ["fr", "en-us", "pt-br"] as LinkedHashSet

        when: "request actions for ManagedObjects"
            def actions = actionService.getActionsBySelection("app01", false,
                [new ActionRuleCondition(dataType: "ManagedObject")])

        then:
            actions.size() == 1
            actions[0].defaultLabel == "First Action"
    }

    def "get localized action name when none of the preferred is available"() {

        given: "set preferred locales"
        localeService.userLocale = ["fr", "it"] as LinkedHashSet

        when: "request actions for ManagedObjects"
        def actions = actionService.getActionsBySelection("app01", false,
                [new ActionRuleCondition(dataType: "ManagedObject")])

        then:
        actions.size() == 1
        actions[0].defaultLabel == "First Action"
    }

    def "get localized action name when no locale is defined"() {

        when: "request actions for ManagedObjects"
            def actions = actionService.getActionsBySelection("app01", false,
                [new ActionRuleCondition(dataType: "ManagedObject")])

        then: "action should return in the default locale (en-us)"
            actions.size() == 1
            actions[0].defaultLabel == "First Action"
    }

    def "get localized action name when no dictionary is available"() {

        given: "set preferred locales"
            localeService.userLocale = ["en-us", "pt-br"] as LinkedHashSet

        when: "request actions for Collection"
            def actions = actionService.getActionsBySelection("app01", false,
                [new ActionRuleCondition(dataType: "Collection")])

        then:
            actions.size() == 1
            actions[0].defaultLabel == "Unmodified Action Name"
    }


    def "after read the SFS removed dictionaries should not be present in the cache"() {

        given: "a sample locale zh-cn"
            def newLocale = "zh-cn"
            def application = "app01"

        when: "Add the new locale to app01"
            dictionaryService.addLocalization(application, new Localization(newLocale))

        then: "the new locale should be available as supported locale for app01"
            dictionaryService.getSupportedLocales(application).contains(newLocale)

        when: "trigger the cache populator to read the file system"
            localeCachePopulator.populate()

        then: "as zh-cn is not in the file system it should be removed from the cache"
            !dictionaryService.getSupportedLocales(application).contains(newLocale)

    }



}
