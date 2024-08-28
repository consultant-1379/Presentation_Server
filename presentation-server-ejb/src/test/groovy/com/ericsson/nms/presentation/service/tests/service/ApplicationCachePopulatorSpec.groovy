package com.ericsson.nms.presentation.service.tests.service


import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.presentation.service.api.dto.Metadata
import com.ericsson.nms.presentation.service.api.dto.WebApplication
import com.ericsson.nms.presentation.service.cache.ApplicationCachePopulator
import com.ericsson.nms.presentation.service.factory.MetadataFactory
import com.ericsson.nms.presentation.service.factory.MetadataImportWrapper
import com.ericsson.nms.presentation.service.persistence.dao.impl.ApplicationDao
import com.ericsson.nms.presentation.service.tests.base.AbstractCacheSpec
import org.slf4j.Logger
import spock.lang.Unroll

import javax.inject.Inject

/*
    Test Specification for ApplicationCachePopulator
 */

class ApplicationCachePopulatorSpec extends AbstractCacheSpec {

    @ObjectUnderTest
    ApplicationCachePopulator cachePopulator

    @MockedImplementation
    MetadataFactory metadataFactory

    @Inject
    ApplicationDao appDao

    @Inject
    Logger logger

    @Unroll
    def 'should #description'() {

        given: 'metadata read from SFS'
            def metadata = new Metadata()
            metadata.setWeb(webApps)
            def metadataWrapper = new MetadataImportWrapper(metadata)

        when: 'populating application cache'
            cachePopulator.populate()

        then: 'verify log calls'
            1 * metadataFactory.importMetadata() >> metadataWrapper
            logWarnCalls * logger.warn('No applications read from file system.')

        where:
            description                                                       | logWarnCalls | webApps
            'log "no applications" warning if metadata does not contain apps' | 1            | [] as Set
            'not log no applications" warning if metadata contains data'      | 0            | [new WebApplication('app-id', 'test-app')] as Set
    }

    @Unroll
    def 'application cache data is retained when #description'() {

        given: 'prepare metadata'
            def metadata = new Metadata()
            metadata.setWeb(webApps)
            def metadataWrapper = new MetadataImportWrapper(metadata)

        and: 'populate application cache data'
            appDao.getCache().put('app-id', new WebApplication('app-id', 'test-app'))

        when: 'populating application cache'
            cachePopulator.populate()

        then: 'application cache still has the data'
            metadataFactory.importMetadata() >> metadataWrapper
            appDao.get('app-id').name == 'test-app'

        where:
            description             | webApps
            'no apps in metadata'   | [] as Set
            'an app is in metadata' | [new WebApplication('app-id', 'test-app')] as Set
    }

}
