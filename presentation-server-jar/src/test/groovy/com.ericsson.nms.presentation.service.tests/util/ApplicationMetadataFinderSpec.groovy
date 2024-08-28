package com.ericsson.nms.presentation.service.tests.util

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.presentation.service.util.ApplicationMetadataFinder
import spock.lang.Ignore

@Ignore
class ApplicationMetadataFinderSpec extends CdiSpecification {


    ApplicationMetadataFinder finder = Spy()

    File[] directories;

    def setup() {
        directories = new File[2]
        File netexDirectory = new File(ApplicationMetadataFinderSpec.class.getClassLoader().getResource("testDirectories/NetworkExplorer").getPath())
        File topBDirectory = new File(ApplicationMetadataFinderSpec.class.getClassLoader().getResource("testDirectories/TopologyBrowser").getPath())
        directories[0] = netexDirectory
        directories[1] = topBDirectory
    }

    def "getApplicationJsonResources should return 2 results"() {

        given: "directories for Netex and TopologyBrowser returned"
            finder.getAppsDirectories() >> directories

        when: "get all the application json files"
            def jsonFiles = finder.getApplicationJsonResources();

        then: "2 json files are found"
            jsonFiles.size() == 2
    }

    def "getApplictionJsonResources should carry on when a directory returns null"() {

        given: "directories for Netex and TopologyBrowser returned"
            finder.getAppsDirectories() >> directories

        and: "Netex directory does not exist"
            directories[0] = new File("nonExistentDirectory")

        when: "get all the application json files"
            def jsonFiles = finder.getApplicationJsonResources();

        then: "only 1 json files is found"
            jsonFiles.size() == 1

    }

}