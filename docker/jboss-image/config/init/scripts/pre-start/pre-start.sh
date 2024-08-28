#! /bin/bash

# This script is executed before JBoss, so it can be used to configure the environment.
#--------------------------------------------------------------------------------------

# Load the common functions
source docker-env-functions.sh

#Rename needed to keep Mavericks acceptance tests working (see redeploy goal in jboss maven plugin)
mv /opt/ericsson/ERICps_CXP9030203/*.ear /opt/ericsson/ERICps_CXP9030203/presentation-server.ear
cp /opt/ericsson/ERICps_CXP9030203/*.ear $JBOSS_HOME/standalone/deployments

copy_jboss_config

startup.sh -NSJ

rm -rf $JBOSS_HOME/standalone/configuration/standalone_xml_history/current/* \
           /opt/ericsson/nms /opt/ericsson/service-framework

# Makes JBoss wait for DPS Integration image to be ready
wait_dps_integration
wait_postgres

# Creates 100 rules for performance testing
rm /ericsson/tor/data/apps/my-app-01/actions/rules/*-rule.json
for run in {1..100}
do
  echo " -> Creating rule $run"
  echo "{\"actionName\":\"app01-action-01\",\"condition\":{\"dataType\":\"ManagedObject\",\"properties\": \
  	[{\"name\":\"moType\",\"value\":\"MeContext\"},{\"name\":\"neType\",\"value\":\"$run\"}]}}" > \
  	/ericsson/tor/data/apps/my-app-01/actions/rules/${run}-rule.json
done

# Creates a user on JBoss to access PIB
$JBOSS_HOME/bin/add-user.sh --user pibUser --password pib12345$ -a -g PibAdmin