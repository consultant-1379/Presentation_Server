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
package dao

import base.PersistenceEnabledSpecification
import com.ericsson.nms.presentation.service.persistence.dao.configuration.ConfigurationRepository
import spock.lang.Stepwise

import javax.inject.Inject

@Stepwise
class DBAvailabilityMonitoringSpec extends PersistenceEnabledSpecification {

    @Inject
    ConfigurationRepository configurationRepository

    def CONNECTION_POLLING_TIME_MS = 100;

    def DELAY_PROPERTY_NAME = "ps.dbAvailabilityConnectionPollingTimerDelay"
    def delayPropertyToRestore = Optional.empty()

    def CONNECTION_POLLING_PERIOD_PROPERTY_NAME = "ps.dbAvailabilityConnectionPollingPeriod"
    def pollingPeriodPropertyToRestore = Optional.empty()

    def setup() {
        setDBAvailabilityTo(true)

        def delay = System.getProperty(DELAY_PROPERTY_NAME)
        if (delay) {
            delayPropertyToRestore = Optional.ofNullable(delay)
        }

        def connectionPollingPeriod = System.getProperty(CONNECTION_POLLING_PERIOD_PROPERTY_NAME)
        if (connectionPollingPeriod) {
            pollingPeriodPropertyToRestore = Optional.ofNullable(connectionPollingPeriod)
        }

        System.setProperty(DELAY_PROPERTY_NAME, CONNECTION_POLLING_TIME_MS.toString())
        System.setProperty(CONNECTION_POLLING_PERIOD_PROPERTY_NAME, CONNECTION_POLLING_TIME_MS.toString())

    }

    def cleanup () {
        System.clearProperty(DELAY_PROPERTY_NAME)
        delayPropertyToRestore.ifPresent(
            {
                System.setProperty(DELAY_PROPERTY_NAME, it as String)
            })
        delayPropertyToRestore = Optional.empty()

        System.clearProperty(CONNECTION_POLLING_PERIOD_PROPERTY_NAME)
        delayPropertyToRestore.ifPresent(
            {
                System.setProperty(CONNECTION_POLLING_PERIOD_PROPERTY_NAME, it as String)
            })
        delayPropertyToRestore = Optional.empty()
    }
}
