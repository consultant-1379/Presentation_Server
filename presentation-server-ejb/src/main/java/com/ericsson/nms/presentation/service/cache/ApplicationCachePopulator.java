/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.cache;

import static com.ericsson.nms.presentation.service.instrumentation.InstrumentableAction.APPLICATIONS_DEPLOYED;
import static com.ericsson.nms.presentation.service.instrumentation.InstrumentableAction.APPLICATION_METADATA_READ_FAILURES;

import com.ericsson.nms.presentation.service.api.dto.AbstractApplication;
import com.ericsson.nms.presentation.service.api.dto.Action;
import com.ericsson.nms.presentation.service.api.dto.Group;
import com.ericsson.nms.presentation.service.api.dto.Metadata;
import com.ericsson.nms.presentation.service.api.dto.WebApplication;
import com.ericsson.nms.presentation.service.factory.MetadataFactory;
import com.ericsson.nms.presentation.service.factory.MetadataImportWrapper;
import com.ericsson.nms.presentation.service.instrumentation.MetricUtil;
import com.ericsson.nms.presentation.service.persistence.dao.impl.ActionDao;
import com.ericsson.nms.presentation.service.persistence.dao.impl.ApplicationDao;
import com.ericsson.nms.presentation.service.persistence.dao.impl.GroupDao;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Populates Presentation Server cache with the information found on the metadata.
 */
@Stateless
public class ApplicationCachePopulator {

    @Inject
    private ApplicationDao appDao;

    @Inject
    private GroupDao groupDao;

    @Inject
    private ActionDao actionDao;

    @Inject
    private MetadataFactory metadataFactory;

    @Inject
    private Logger logger;

    @Inject
    private MetricUtil metricUtil;

    /**
     * Import all metadata info to the cache
     */
    public void populate(){
        final MetadataImportWrapper metadataImportWrapper = metadataFactory.importMetadata();
        if (metadataImportWrapper.getReadFailures() > 0) {
            metricUtil.count(APPLICATION_METADATA_READ_FAILURES, metadataImportWrapper.getReadFailures());
        }
        final Metadata metadata = metadataImportWrapper.getMetadata();
        final Collection<WebApplication> webApps = metadata.getWeb();

        if(webApps.isEmpty()) {
            logger.warn("No applications read from file system.");
        } else {
            removeDeletedApplications(metadata);
            removeDeletedGroups(metadata);
        }

        updateApplicationsCache(metadata.getIca());
        updateApplicationsCache(webApps);
        updateGroups(metadata.getGroups());
        updateActions(metadata.getActions());
    }

    private void updateGroups(final Collection<Group> groups){
        for (final Group group : groups) {
            logger.info("Inserting group {} on the cache: {}", group.getId(), group);
            groupDao.put(group.getId(), group);
        }
    }

    private void updateActions(final Collection<Action> actions) {
        actionDao.retainAll(getCurrentActionNames(actions));

        final Map<String, Map<Integer, Set<String>>> actionsGroupedOnCategoryAndOrder = groupActionsOnCategoryAndOrderAndName(actions);
        final Set<String> invalidActionNames = getActionsWithInvalidOrdering(actionsGroupedOnCategoryAndOrder);
        for(final Action action : actions){
            if (action.getOrder() == null){
                logger.warn("Action {} has order of null", action.getName());
            }
            if(invalidActionNames.contains(action.getName())){
                actionDao.remove(action.getName());
                continue;
            }
            logger.info("Updating action: '{}' in cache.", action);
            actionDao.put(action.getName(), action);
        }
    }

    private Collection<String> getCurrentActionNames(final Collection<Action> actions){
        final Set<String> actionNames = new HashSet<>();
        for(Action action : actions){
            actionNames.add(action.getName());
        }
        return actionNames;
    }

    private Set<String> getActionsWithInvalidOrdering(final Map<String, Map<Integer, Set<String>>> actionsGroupedOnCategoryAndOrder){
        final Set<String> invalidActionNames = new HashSet<>();
        for (Map<Integer, Set<String>> groupedActionNames : actionsGroupedOnCategoryAndOrder.values()){
            for(Map.Entry<Integer, Set<String>> actionsWithSameOrderEntry : groupedActionNames.entrySet()){
                if(actionsWithSameOrderEntry.getValue().size() > 1 && actionsWithSameOrderEntry.getKey() != null){
                    logger.warn("Actions: '{}' are being rejected as they share same order!", actionsWithSameOrderEntry.getValue());
                    invalidActionNames.addAll(actionsWithSameOrderEntry.getValue());
                }
            }
        }
        return invalidActionNames;
    }

    private Map<String, Map<Integer, Set<String>>> groupActionsOnCategoryAndOrderAndName(final Collection<Action> actions){
        final Map<String, Map<Integer, Set<String>>> groupedCategoriesOnOrder = new HashMap<>();
        for(Action action:actions)   {
            final Map<Integer, Set<String>> mappedCategory =
                    groupedCategoriesOnOrder.computeIfAbsent(action.getCategory(), k -> new HashMap<>());
            final Set<String> orderedActionNames =
                    mappedCategory.computeIfAbsent(action.getOrder(), k -> new HashSet<>());
            orderedActionNames.add(action.getName());
        }
        return groupedCategoriesOnOrder;
    }

    private void updateApplicationsCache(final Collection<? extends AbstractApplication> apps) {
        for(AbstractApplication app : apps) {
            final AbstractApplication cacheApp = appDao.get(app.getId());
            if (cacheApp == null) {
                logger.info("Inserting application {} on the cache: {}", app.getId(), app);
                appDao.put(app.getId(), app);
            } else if (!StringUtils.equals(cacheApp.getHash(), app.getHash())) {
                logger.info("Updating application {} on the cache as the hash checks are different",
                        app.getId());
                appDao.put(app.getId(), app);
            } else {
                logger.info("Application {} has the same hash on the cache. There's no need to update.",
                        app.getId());
            }
        }
        metricUtil.measure(APPLICATIONS_DEPLOYED, appDao.getApplicationCount());
    }

    private void removeDeletedGroups(final Metadata metadata) {
        groupDao.retainAll(metadata.getGroups()
                .stream()
                .map(Group::getId)
                .collect(Collectors.toSet()));
    }

    private void removeDeletedApplications(final Metadata metadata) {
        final Collection<AbstractApplication> allApps = new ArrayList<>();
        allApps.addAll(metadata.getIca());
        allApps.addAll(metadata.getWeb());

        appDao.retainAll(allApps.stream()
                .map(AbstractApplication::getId)
                .collect(Collectors.toSet()));

        metricUtil.measure(APPLICATIONS_DEPLOYED, appDao.getApplicationCount());
    }
}