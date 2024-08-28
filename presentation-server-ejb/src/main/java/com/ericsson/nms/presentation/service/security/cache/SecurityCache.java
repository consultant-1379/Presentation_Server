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
package com.ericsson.nms.presentation.service.security.cache;

import com.ericsson.nms.presentation.service.logger.SecurityLogger;
import com.ericsson.oss.itpf.sdk.core.EServiceNotFoundException;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.EAccessControl;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.ESecurityAction;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.ESecurityResource;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.SecurityViolationException;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class provides a Thread Based cache to optimize calls to the security framework.
 * Some use cases require the same security check multiple times in the same flow so this cache helps to
 * reduce the number of calls to the LDAP server.
 */
@ApplicationScoped
public class SecurityCache {

    protected ThreadLocal<HashMap<String, Set<String>>> cache = null;

    @PostConstruct
    public void initialize() {
        cache = ThreadLocal.withInitial(HashMap::new);
    }

    @Inject
    private Logger logger;

    @Inject
    @SecurityLogger
    private Logger securityLogger;

    @Inject
    private EAccessControl accessControl;

    /**
     * Gets a map having the resource name as key and the list of authorized actions as value.
     * @param resourceNames the resources to be queried
     * @return a map representing the resources/actions permissions
     */
    public Map<String, Set<String>> getResources(final Set<String> resourceNames) {

        logSecurity("Requesting permissions on resources: {}", resourceNames);

        final Set<String> resourcesNotCached  = resourceNames.stream()
                .filter(r -> !cache.get().containsKey(r)).collect(Collectors.toSet());

        logSecurity("Resources not cached: {}", resourcesNotCached);
        logSecurity("Resources cached: {}", cache.get().keySet());

        final Map<String, Set<String>> resources = putResources(resourcesNotCached);

        Set<String> cachedResources = new HashSet<>(resourceNames);
        cachedResources.removeAll(resourcesNotCached);

        cachedResources.forEach(r -> {
            Set<String> actions = cache.get().get(r);
            if (actions != null) {
                resources.put(r, actions);
            }
        });

        return resources;
    }


    public Map<String, Set<String>> putResources(final Set<String> resourceNames) {

        final Map<String, Set<String>> permissions = getUserResourcesAndActionsWithFallback(resourceNames);

        final Map<String, Set<String>> result = new HashMap<>(permissions);

        // Add all allowed permissions to the cache
        permissions.forEach((key, value) -> cache.get().put(key, value));


        // Add all "not allowed" permissions as null to maintain then in the cache and avoid another call in the future.
        resourceNames.stream().filter(e -> !permissions.keySet().contains(e))
                .forEach(e -> cache.get().put(e, null));

        return result;
    }

    /**
     * Reset the cache clearing all data.
     */
    public void flushCache() {
        cache.remove();
    }

    /**
     * Given a list of required resources, returns the list of which resources the authenticated user has access
     * @return the list of resources the user is authorized
     */
    private Map<String, Set<String>> getUserResourcesAndActions(final Set<String> resources) {

        if (resources == null || resources.isEmpty()) {
            return Collections.emptyMap();
        }

        logSecurity("Calling accessControl.getActionsForResources with resources: {}", resources);
        final Map<ESecurityResource, Set<ESecurityAction>> actionsResourcesMap =
                accessControl.getActionsForResources(resources.stream()
                        .map(ESecurityResource::new)
                        .collect(Collectors.toSet()));

        final Map<String, Set<String>> result = actionsResourcesMap.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().getResourceId() ,
                        entry -> entry.getValue().stream().map(ESecurityAction::getActionId).collect(Collectors.toSet())
                ));

        logSecurity("Resources / Actions returned from security: {}", result);

        return result;
    }

    private Map<String, Set<String>> getUserResourcesAndActionsWithFallback(final Set<String> resources){
        try{
            return getUserResourcesAndActions(resources);

        } catch(EServiceNotFoundException | EJBException ex) {
            logger.warn("Problem with batch access control, using serial access, exception: ", ex);

            if( resources== null || resources.isEmpty()){
                return Collections.emptyMap();
            }

            return resources.stream().filter(resourceId ->{
                try {
                    final ESecurityResource eSecurityResource = new ESecurityResource(resourceId);
                    if(accessControl.isAuthorized(eSecurityResource, new ESecurityAction(""))){
                        return true;
                    }
                }catch (SecurityViolationException exception){
                    logger.error("Problem checking authorization for resource: {}, exception: {}", exception);
                }
                return false;

            }).collect(Collectors.toMap(
                    resourceId -> resourceId,
                    resourceId -> Collections.singleton("READ"))
            );
        }
    }

    private void logSecurity(final String message, Object...args) {
        if (securityLogger.isDebugEnabled()) {
            securityLogger.debug("[User: "+ accessControl.getAuthUserSubject().getSubjectId() +"] "+ message, args);
        }
    }
}
