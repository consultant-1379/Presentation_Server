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
@Library('ci-pipeline-lib') ciLibrary

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
            checkoutPhase useConsolidatedDocker: true, gerritProject: env.GERRIT_PROJECT
            setupPhase()
            unitTestsPhase(
                compileGoals: ["clean install -DskipTests"],
                testGoals: ["install -Ppersistence -Punit"],
                copyFiles: [
                    "ERICps_CXP9030203/target/rpm/ERICps_CXP9030203/RPMS/noarch/*.rpm": "${env.DOCKER_ROOT}/jboss-eap7/config/init/rpms"
                ],
                archiveArtifacts: [
                    "ERICps_CXP9030203/target/rpm/ERICps_CXP9030203/RPMS/noarch/*.rpm"
                ]
            )
            startDockerPhase()
            integrationTestsPhase(
                testGoals: ["install -Pdocker -pl testsuite/integration/jee", "install -Pdocker -Psonar -pl presentation-server-db-schema"],
                generateCoverageReports: true
            )
            generateDocsPhase(
                generateJavadoc: false,
                generateMavenSite: false
            )
            qualityGatePhase()
            publishSharedDataPhase(
                sharedDataModule: "presentation-server-ci-assembly"
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

