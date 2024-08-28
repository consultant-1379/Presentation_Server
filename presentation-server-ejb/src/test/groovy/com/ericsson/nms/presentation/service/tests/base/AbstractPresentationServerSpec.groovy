package com.ericsson.nms.presentation.service.tests.base

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.presentation.service.api.dto.Resource
import com.ericsson.oss.itpf.sdk.security.accesscontrol.EAccessControl
import com.ericsson.oss.itpf.sdk.security.accesscontrol.ESecurityAction
import com.ericsson.oss.itpf.sdk.security.accesscontrol.ESecurityResource
import com.ericsson.oss.itpf.sdk.security.accesscontrol.ESecuritySubject

import javax.inject.Inject

/**
 * Base Test Specification for Presentation Server
 */
class AbstractPresentationServerSpec extends CdiSpecification {

    @Inject
    EAccessControl accessControl

    def setup() {
        ESecuritySubject subject = mock(ESecuritySubject)
        accessControl.getAuthUserSubject() >> subject
        subject.getSubjectId() >> "anyUser"
    }

    def authorizeAllResources() {
        accessControl.getActionsForResources(_ as Set<ESecurityResource>) >> { Set<ESecurityResource> resources ->

            def permissionsMap = [:]
            resources.each {
                permissionsMap.put(it, [
                        new ESecurityAction("READ"),
                        new ESecurityAction("DELETE"),
                        new ESecurityAction("CREATE"),
                        new ESecurityAction("UPDATE")
                ] as Set)
            }

            return permissionsMap
        }
    }

    def authorizeOnResources(Set<Resource> resources) {
        accessControl.getActionsForResources(_ as Set<ESecurityResource>) >> { Set<ESecurityResource> resourcesParam ->

            def permissionsMap = [:]
            resources.each { resource ->
                if (resourcesParam.find { res -> res.resourceId == resource.name }) {
                    def securityResource = new ESecurityResource(resource.name)
                    Set<ESecurityResource> actions = permissionsMap.get(securityResource)
                    if (!actions) {
                        actions = [] as Set
                    }
                    actions.add(new ESecurityAction(resource.action))
                    permissionsMap.put(securityResource, actions)
                }
            }

            return permissionsMap
        }
    }

    /**
     * Customize the injection provider
     * @param injectionProperties
     */
    @Override
    Object addAdditionalInjectionProperties(InjectionProperties injectionProperties) {

        // Specifies which packages contains the implementations for your interfaces
        injectionProperties.autoLocateFrom('com.ericsson.nms.presentation.service')
    }

}
