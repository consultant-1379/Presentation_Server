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
package com.ericsson.nms.presentation.service.tests.service.security

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.presentation.service.logger.SecurityLogger
import com.ericsson.nms.presentation.service.security.cache.SecurityCache
import com.ericsson.nms.presentation.service.tests.base.AbstractPresentationServerSpec
import com.ericsson.oss.itpf.sdk.core.EServiceNotFoundException
import com.ericsson.oss.itpf.sdk.security.accesscontrol.ESecurityAction
import com.ericsson.oss.itpf.sdk.security.accesscontrol.ESecurityResource
import org.slf4j.Logger

import javax.ejb.EJBException

class SecurityCacheSpec extends AbstractPresentationServerSpec {

    @ObjectUnderTest
    SecurityCache securityCache

    def "Security resources are requested only once and are cached after, batch access control throws exception"() {

        when: "ask permissions on resource-01"
            def permissions = securityCache.getResources(["resource-01"] as Set)

        then: "accessControl should be called once for getActionsForResources() and once for isAuthorized()"
            1 * accessControl.getActionsForResources(_ as Set) >> {throw new EServiceNotFoundException("testing exception")}
            1 * accessControl.isAuthorized(_ as ESecurityResource, _ as ESecurityAction  ) >> true

        and: "resource-01 should be found with action READ"
            permissions['resource-01']?.size() == 1
            permissions['resource-01'][0] == "READ"

        when: "ask for the same permission"
            securityCache.getResources(["resource-01"] as Set)

        then: "now access control should not be called again as the resource is cached"
            0 * accessControl.getActionsForResources(_ as Set)
    }

    def "Security resources are requested only once and are cached after"() {

        when: "ask permissions on resource-01"
            def permissions = securityCache.getResources(["resource-01"] as Set)

        then: "accessControl should be called once"
            1 * accessControl.getActionsForResources(_ as Set) >>
                [(new ESecurityResource ("resource-01")) : [new ESecurityAction("READ")] as Set ]

        and: "resource-01 should be found with action READ"
            permissions['resource-01']?.size() == 1
            permissions['resource-01'][0] == "READ"

        when: "ask for the same permission"
            securityCache.getResources(["resource-01"] as Set)

        then: "now access control should not be called again as the resource is cached"
            0 * accessControl.getActionsForResources(_ as Set)

    }

    def "Unauthorized resources should be cached as well"() {

        when: "ask permissions on resource-01 and resource-02"
            def permissions = securityCache.getResources(["resource-01", "resource-02"] as Set)

        then: "accessControl should be called once but user should have access only to resource-01"
            1 * accessControl.getActionsForResources(_ as Set) >>
                    [(new ESecurityResource ("resource-01")) : [new ESecurityAction("READ")] as Set ]

        and: "resource-01 should be found with action READ"
            permissions['resource-01']?.size() == 1
            permissions['resource-01'][0] == "READ"

        when: "ask for the same permissions"
            securityCache.getResources(["resource-01", "resource-02"] as Set)

        then: "now access control should not be called again as authorized and unauthorized resources are cached"
            0 * accessControl.getActionsForResources(_ as Set)
    }

    def "After a cache flush security framework is used again to retrieve resources"() {

        when: "ask permissions on resource-01"
            def permissions = securityCache.getResources(["resource-01"] as Set)

        then: "accessControl should be called once"
            1 * accessControl.getActionsForResources(_ as Set) >>
                    [(new ESecurityResource ("resource-01")) : [new ESecurityAction("READ")] as Set ]

        and: "resource-01 should be found with action READ"
            permissions['resource-01']?.size() == 1
            permissions['resource-01'][0] == "READ"

        when: "flush the cache"
            securityCache.flushCache()

        and: "ask for the same permissions"
            securityCache.getResources(["resource-01"] as Set)

        then: "accessControl should be called again as the cache was cleared"
            1 * accessControl.getActionsForResources(_ as Set) >>
                    [(new ESecurityResource ("resource-01")) : [new ESecurityAction("READ")] as Set ]
    }

    def "When debug is enabled then debug logs should be generated"() {
        def logger = Mock(Logger)

        when: "debug is enabled"
            logger.isDebugEnabled() >> true
            securityCache.securityLogger = logger

        and:  "check permissions on (empty) resource list"
            securityCache.getResources([] as Set)

        then: "security logger should be called at least once at debug level"
            (1.._) * logger.debug(_ as String, _ as Collection)
    }

    def "test that fallback works when access-control method is not available (incorrect version)"() {

        when: "ask permissions on resource-01"
        def permissions = securityCache.getResources(["resource-01"] as Set)

        then: "accessControl should be called once for getActionsForResources() and once for isAuthorized()"
        1 * accessControl.getActionsForResources(_ as Set) >> {throw new EJBException("testing EJB exception")}
        1 * accessControl.isAuthorized(_ as ESecurityResource, _ as ESecurityAction  ) >> true

        and: "resource-01 should be found with action READ"
        permissions['resource-01']?.size() == 1
        permissions['resource-01'][0] == "READ"

        when: "ask for the same permission"
        securityCache.getResources(["resource-01"] as Set)

        then: "now access control should not be called again as the resource is cached"
        0 * accessControl.getActionsForResources(_ as Set)
    }
}
