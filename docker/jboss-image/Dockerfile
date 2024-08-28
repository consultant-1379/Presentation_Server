FROM armdocker.rnd.ericsson.se/proj_oss_releases/enm/jboss-dps

COPY standalone-enm.xml $JBOSS_HOME/standalone/configuration/standalone-enm.xml

COPY policies-mock/policyManagementService.txt /tmp/policyManagementService.txt
COPY org.jacoco.agent-0.7.9-runtime.jar /opt/jacoco-agent.jar

HEALTHCHECK --interval=30s --retries=30 \
    CMD bash -c "[ -f /opt/ericsson/docker/jboss/shared/ready ]"

COPY neo4j.properties $JBOSS_HOME/standalone/data/dps/credentials/neo4j.properties

ENTRYPOINT ["entrypoint.sh"]
CMD ["-IP", "-s", "jboss"]
