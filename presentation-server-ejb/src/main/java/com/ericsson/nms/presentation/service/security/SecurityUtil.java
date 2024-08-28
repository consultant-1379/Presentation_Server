/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.security;

import com.ericsson.nms.presentation.service.api.dto.AbstractApplication;
import com.ericsson.nms.presentation.service.interceptors.MethodCallTimerInterceptor;
import com.ericsson.nms.presentation.service.api.dto.Resource;
import com.ericsson.nms.presentation.service.security.cache.SecurityCache;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.EAccessControl;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility class responsible to integrate the application with the Security SDK
 */
@Interceptors({MethodCallTimerInterceptor.class})
public class SecurityUtil {

    @Inject
    private EAccessControl accessControl;

    @Inject
    private SecurityCache securityCache;

    /**
     * Checks if the current user has access to a given app.
     * @param application application to be checked
     * @return true if the user is authorized or false otherwise
     */
    public boolean isAuthorisedApp(final AbstractApplication application) {
        if (StringUtils.isNotEmpty(application.getResources())) {
            final String[] resourceIds = application.getResources().split(",");
            return hasAccessToAnyResource(Stream.of(resourceIds).collect(Collectors.toSet()));
        }
        return true;
    }

    /**
     * Gets the ID fot the user currently logged in the system
     * @return user ID
     */
    public String getCurrentUser() {
        return accessControl.getAuthUserSubject() == null ? null :
                accessControl.getAuthUserSubject().getSubjectId();
    }

    /**
     * Given a list of required resources, returns the list of which resources the authenticated user has access
     * @return the list of resoruces the user is authorized
     */
    public Set<String> getUserResources(final Set<String> resources) {

        return getUserResourcesAndActions(resources).keySet();
    }

    /**
     * Given a list of required resources, returns the list of which resources the authenticated user has access
     * @return the list of resoruces the user is authorized
     */
    public Map<String, Set<String>> getUserResourcesAndActions(final Set<String> resources) {
        return securityCache.getResources(resources);
    }

    /**
     * Checks if the user has access to any resource in a given set
     * @param resources resources to be checked
     * @return
     */
    public boolean hasAccessToAnyResource(final Set<String> resources) {
        return !getUserResources(resources).isEmpty();
    }

    /**
     * Checks if the user has access to any resource in a given set
     * @param resources resources to be checked
     * @return
     */
    public boolean hasAccessToAnyResourceAndAction(final Set<Resource> resources) {

        final Map<String, Set<String>> permissions = getUserPermissions(resources);

        if (permissions.isEmpty()) {
            return false;
        }

        return permissions.entrySet().stream().anyMatch(userHasPermissionPredicate(resources));
    }

    /**
     * Checks if the user has access to all resources in a given set
     * @param resources resources to be checked
     * @return
     */
    public boolean hasAccessToAllResourcesAndActions(final Set<Resource> resources) {
        final Map<String, Set<String>> permissions = getUserPermissions(resources);

        if (permissions.isEmpty()) {
            return false;
        }

        return permissions.entrySet().stream().allMatch(userHasPermissionPredicate(resources));
    }

    Predicate<Map.Entry<String, Set<String>>> userHasPermissionPredicate(final Set<Resource> resources) {

        return entry -> {
            final Set<Resource> filteredResources = resources.stream()
                    .filter(r -> entry.getKey().equalsIgnoreCase(r.getName())).collect(Collectors.toSet());

            return filteredResources.stream().allMatch(r -> {
                if (r.getAction() == null) {
                    return true;
                } else {
                    return entry.getValue().stream().anyMatch(a -> a.equalsIgnoreCase(r.getAction()));
                }
            });
        };

    }

    private Map<String, Set<String>> getUserPermissions(final Set<Resource> resources) {
        return getUserResourcesAndActions(resources.stream()
                .map(Resource::getName).collect(Collectors.toSet()));
    }


}
