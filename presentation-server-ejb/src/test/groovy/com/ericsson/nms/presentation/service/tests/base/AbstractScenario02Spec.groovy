package com.ericsson.nms.presentation.service.tests.base

import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.nms.presentation.service.util.ApplicationMetadataFinder
import org.slf4j.LoggerFactory

/**
 * <p>Base spec for the Test Scenario 02.</p>
 * <p>In this scenario we don't have any file (JSON or XML) in the file system to make sure we can read the internal metadata file.</p>
 */
class AbstractScenario02Spec extends ApplicationBasedSpec {

    /**
     * Overrides the ApplicationMetadataFinder.getMetadataDirectory method to return the test scenario
     * folder containing the resources files used on the tests.
     */
    @ImplementationInstance
    private ApplicationMetadataFinder finder =  new ApplicationMetadataFinder() {
        @Override
        String getMetadataDirectory() {
            logger = LoggerFactory.getLogger(ApplicationMetadataFinder.class)
            return Thread.currentThread().contextClassLoader.getResource("test/scenario-02").getFile()
        }
    }
}
