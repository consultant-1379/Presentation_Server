/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package pipeline

import org.jenkinsci.plugins.workflow.libs.Library

@Library('mavericks') mav

node("Jenkins_fem33s11_mesos_podj") {
    timestamps {
        try {

            configurePipeline(
                dockerRoot: "docker-checkout/src/docker",
                dockerComposeFile: "docker-compose-eap7.yml",
                jbossContainerName: "netex_jboss_eap7",
                groupId: "com.ericsson.nms.pres",
                artifactId: "Presentation_Server"
            )
            checkoutPhase useConsolidatedDocker: false, checkoutMaster: true
            setupPhase isReleaseJob: true
            releasePhase(
                releaseGoals: [ '-Psite -DskipTests -Dmaven.javadoc.failOnError=false release:prepare release:perform -Dgoals="clean deploy site site:deploy -DskipTests -Dmaven.javadoc.failOnError=false" -DlocalCheckout=true']
            )

            // If the trigger is not a gerrit change (e.g manual build from master) we need to execute the tests
            if (!env.GERRIT_CHANGE_NUMBER) {
            unitTestsPhase(
                compileGoals: ["clean install -DskipTests"],
                testGoals: ["test"],
                copyFiles: [
                    "ERICps_CXP9030203/target/rpm/ERICps_CXP9030203/RPMS/noarch/*.rpm": "${env.DOCKER_ROOT}/jboss-eap7/config/init/rpms"
                ],
                archiveArtifacts: [
                    "ERICps_CXP9030203/target/rpm/ERICps_CXP9030203/RPMS/noarch/*.rpm"
                ]
            )
            startDockerPhase()
            integrationTestsPhase(
                testGoals: ["verify -Pdocker -pl presentation-server-db-schema", "install -Pdocker -pl testsuite/integration/jee"],
                generateCoverageReports: true
            )

            }else {
                collectSharedDataPhase(
                    sharedDataModule: "presentation-server-ci-assembly"
                )
            }

            qualityGatePhase checkQualityGate: false
            deliveryPhase(
                disableDelivery: false,
                disableQueue: false
            )
        } finally {
            completePipelinePhase(
                removeDockerContainers: true,
                collectDockerLogs: true,
                deleteWorkspace: true
            )
        }
    }

}

