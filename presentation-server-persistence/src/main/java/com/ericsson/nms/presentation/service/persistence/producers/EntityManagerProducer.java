/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.presentation.service.persistence.producers;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

/**
 * CDI producer for the JPA EntityManager
 */
@ApplicationScoped
public class EntityManagerProducer {

    @PersistenceUnit
    protected EntityManagerFactory entityManagerFactory;

    /**
     * Produces and injectable instance of EntityManager which is used by DeltaSpike to manage persistence.
     * The EntityManager will be scoped to a single request.
     * @return created entity manager
     */
    @Produces
    @Default
    @RequestScoped
    protected EntityManager createEntityManager() {
        return this.entityManagerFactory.createEntityManager();
    }

    /**
     * Make sure the entity manager is properly released after the scope has finished.
     * @param entityManager entity manager to dispose of
     */
    public void dispose(@Disposes @Default EntityManager entityManager) {
        if (entityManager.isOpen()) {
            entityManager.close();
        }
    }

}
