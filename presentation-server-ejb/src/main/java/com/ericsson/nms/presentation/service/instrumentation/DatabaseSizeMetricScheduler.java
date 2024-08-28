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
package com.ericsson.nms.presentation.service.instrumentation;

import com.ericsson.nms.presentation.service.persistence.dao.relations.RelationDAO;
import com.ericsson.nms.presentation.service.database.availability.DBAvailabilityChecker;
import com.ericsson.nms.presentation.service.persistence.entities.v1.instrumentation.RelationEntity;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import org.slf4j.Logger;

import javax.ejb.DependsOn;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Startup
@Singleton
@DependsOn({"DatabaseUpgraderService"})
public class DatabaseSizeMetricScheduler {

    private static final String PS_DB_SIZE = "PRESENTATION_SERVER.DATABASE.SIZE";

    @Inject
    private RelationDAO relationDAO;

    @Inject
    private SystemRecorder systemRecorder;

    @Inject
    private DBAvailabilityChecker dbAvailabilityChecker;

    @Inject
    private Logger logger;

    /**
     * Record DB size event data every 5 minutes.
     */
    @Schedule(hour = "*", minute = "*/5", persistent = false)
    public void scheduleDatabaseSizeTimer() {
        if (dbAvailabilityChecker.isAvailable()) {
            final List<RelationEntity> relationEntities = relationDAO.findAll();
            final Map<String, Object> dbSizeEventData = getEventData(relationEntities);
            systemRecorder.recordEventData(PS_DB_SIZE, dbSizeEventData);
        } else {
            logger.warn("DatabaseSizeMetricScheduler: Unable to retrieve database size metrics because the DB is unavailable");
        }
    }

    private Map<String, Object> getEventData(final List<RelationEntity> relationEntities) {
        return relationEntities.stream().collect(Collectors.toMap(RelationEntity::getName, RelationEntity::getSize));
    }

}
