/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.ejb;

import com.ericsson.nms.presentation.exceptions.NotFoundException;
import com.ericsson.nms.presentation.service.api.dto.AbstractApplication;
import com.ericsson.nms.presentation.service.api.dto.Group;
import com.ericsson.nms.presentation.service.api.dto.Localization;
import com.ericsson.nms.presentation.service.comparators.ApplicationComparator;
import com.ericsson.nms.presentation.service.interceptors.MethodCallTimerInterceptor;
import com.ericsson.nms.presentation.service.persistence.dao.impl.ApplicationDao;
import com.ericsson.nms.presentation.service.persistence.dao.impl.GroupDao;
import com.ericsson.nms.presentation.service.security.SecurityUtil;
import com.ericsson.nms.presentation.service.security.cache.interceptors.bindings.CachedSecurity;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Application server EJB
 */
@Stateless
@CachedSecurity
@Interceptors({MethodCallTimerInterceptor.class})
public class ApplicationServiceEjb implements ApplicationService {

    @Inject
    SecurityUtil securityUtil;

    @Inject
    private GroupDao grpDao;

    @Inject
    private ApplicationDao appDao;

    @Inject
    DictionaryService dictionaryService;

    @Inject
    ApplicationComparator applicationComparator;

    @Inject
    Logger logger;

    private Function<String, Set<String>> getResources = str -> {
        if (StringUtils.isNotEmpty(str)) {
            return Stream.of(str.split(",")).collect(Collectors.toSet());
        } else {
            return Collections.emptySet();
        }
    };

    @Override
    public Collection<AbstractApplication> getApps(final String... keys) {

        // Eagerly cache all the resources in one call
        securityUtil.hasAccessToAnyResource(getApplicationsResources(keys));

        return appDao.getAll(keys).stream()
                .filter(a -> securityUtil.isAuthorisedApp(a))
                .map(this::updateApplicationLocalizedTexts)
                .filter(a -> StringUtils.isNotEmpty(a.getName()))
                .sorted(Comparator.nullsLast(applicationComparator))
                .collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    public Collection<Group> getGroups(final String... keys) {
        final Collection<Group> groups = grpDao.getAll(keys);

        // Eagerly cache all the resources in one call
        Set<String> resources = new HashSet<>();
        groups.forEach(g -> resources.addAll(getApplicationsResources(g.getAppIds().toArray(new String[]{}))));
        securityUtil.hasAccessToAnyResource(resources);

        final SortedSet<Group> result = new TreeSet<>();
        for (final Group group : groups) {
            final Map<String, AbstractApplication> apps = appDao.getAll(group.getAppIds());
            final Collection<AbstractApplication> applications = apps.values();
            filterApplicationsBySecurity(applications);
            final Set<String> appIds =new HashSet<>();
            for (final AbstractApplication app : applications) {
                appIds.add(app.getId());
            }
            result.add(new Group(group.getId(), group.getName(), appIds));
        }
        return result;
    }

    @Override
    public AbstractApplication getApplication(final String appId)  {
        final AbstractApplication app = appDao.get(appId);
        if (app == null) {
            throw new NotFoundException("Application " + appId + " not found");
        } else if (!securityUtil.isAuthorisedApp(app)) {
            throw new SecurityException("Access denied to application " + appId);
        }
        updateApplicationLocalizedTexts(app);
        return app;
    }

    private AbstractApplication updateApplicationLocalizedTexts(final AbstractApplication app) {

        logger.info("Checking localization for application {}", app.getId());
        final Localization localization = dictionaryService.getLocalization(app.getId());

        // If a localization exists, tries to retrieve the localized text.
        // If there's no localized text for a given, property, just use the property name.
        if (localization != null) {

            if (StringUtils.isNotEmpty(localization.getTitle())) {
                logger.debug("    -> Using title from locale. {}", localization.getLocale());
                app.setName(localization.getTitle());
            }

            if (StringUtils.isNotEmpty(localization.getDescription())) {
                logger.debug("    -> Using description from locale {}.", localization.getLocale());
                app.setShortInfo(localization.getDescription());
            }

            if (StringUtils.isNotEmpty(localization.getAcronym())) {
                logger.debug("    -> Using acronym from locale {}.", localization.getLocale());
                app.setAcronym(localization.getAcronym());
            }
        }

        return app;
    }

    /**
     * This method removes application from list if logged-in user is not authorized to work on a particular application.
     *
     * @param applications
     */
    private void filterApplicationsBySecurity(final Collection<AbstractApplication> applications) {
        final Collection<AbstractApplication> toBeRemovedApplications = new ArrayList<>();

        logger.debug("Found {} applications in cache: {}", applications.size(), applications);
        for (final AbstractApplication application : applications) {
            if (!securityUtil.isAuthorisedApp(application)) {
                toBeRemovedApplications.add(application);
            } else {
                updateApplicationLocalizedTexts(application);
                if (StringUtils.isEmpty(application.getName())) {
                    logger.error("Application {} has no name and no localization. Skipping it.", application.getId());
                    toBeRemovedApplications.add(application);
                }
            }
        }

        applications.removeAll(toBeRemovedApplications);
        logger.debug("User has access to {} applications: {}", applications.size(), applications);
    }

    private Set<String> getApplicationsResources(final String...keys) {
        // Eagerly cache all the resources in one call
        Set<String> resources = new HashSet<>();
        appDao.getAll(keys).forEach(app -> resources.addAll(getResources.apply(app.getResources())));

        return resources;
    }

}
