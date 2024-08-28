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
package base

import com.ericsson.nms.presentation.service.persistence.producers.EntityManagerProducer
import com.ericsson.nms.presentation.service.validation.interceptors.ValidationInterceptor
import com.ericsson.nms.presentation.service.validation.interceptors.bindings.Validate
import org.apache.deltaspike.cdise.api.CdiContainer
import org.apache.deltaspike.cdise.api.CdiContainerLoader
import org.apache.deltaspike.cdise.api.ContextControl
import org.apache.deltaspike.core.api.provider.BeanProvider
import org.apache.deltaspike.testcontrol.api.mock.ApplicationMockManager
import org.apache.deltaspike.testcontrol.api.mock.DynamicMockManager
import spock.lang.Specification

import javax.enterprise.context.ApplicationScoped
import javax.enterprise.context.RequestScoped
import javax.enterprise.context.spi.CreationalContext
import javax.enterprise.inject.Produces
import javax.enterprise.inject.Typed
import javax.enterprise.inject.spi.AnnotatedType
import javax.enterprise.inject.spi.BeanManager
import javax.enterprise.inject.spi.InjectionTarget
import javax.enterprise.inject.spi.InterceptionType
import javax.inject.Inject
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.Persistence

/**
 * Base specification for tests that require a CDI enabled environment with access to an in-memory database
 */
class PersistenceEnabledSpecification extends Specification {
    @Inject
    protected DynamicMockManager mockManager;

    protected BeanManager beanManager

    Boolean isDBAvailable = false

    CdiContainer cdiContainer = null
    EntityManagerFactory emf
    EntityManager em

    def setup() {
        cdiContainer = CdiContainerLoader.getCdiContainer()
        cdiContainer.boot()

        def container = cdiContainer.weldContainer

        ContextControl contextControl = cdiContainer.getContextControl()
        contextControl.startContext(ApplicationScoped.class)
        contextControl.startContext(RequestScoped.class)

        beanManager = container.getBeanManager()
        CreationalContext<? extends PersistenceEnabledSpecification> creationalContext = beanManager.createCreationalContext(null)
        AnnotatedType<? extends PersistenceEnabledSpecification> annotatedType = beanManager.createAnnotatedType((Class<? extends PersistenceEnabledSpecification>) this.getClass())
        InjectionTarget<? extends PersistenceEnabledSpecification> injectionTarget = beanManager.createInjectionTarget(annotatedType)
        injectionTarget.inject(this, creationalContext);

        emf = Persistence.createEntityManagerFactory("pu-test")
        em = emf.createEntityManager()

        ApplicationMockManager applicationMockManager = BeanProvider.getContextualReference(
            ApplicationMockManager.class)
        applicationMockManager.addMock(new MockedEntityManagerProducer(emf))
    }

    protected setDBAvailabilityTo(boolean availability) {
        isDBAvailable = availability
    }

    def cleanup() {
        em.clear()
        em.close()
        emf.close()
        cdiContainer.shutdown()
    }

    static class MockedEntityManagerProducer extends EntityManagerProducer {

        MockedEntityManagerProducer() {
        }

        MockedEntityManagerProducer(EntityManagerFactory factory) {
            this.entityManagerFactory = factory
        }

        @Produces
        @Typed
        EntityManager supply() {
            return this.entityManagerFactory.createEntityManager()
        }
    }
}
