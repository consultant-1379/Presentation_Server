#!/bin/bash
#----------------------------------------------------------------------------
#############################################################################
# COPYRIGHT Ericsson 2013
# The copyright to the computer program�herein is the property of
# conditions stipulated in the agreement/contract under which the
# program have been supplied.
#############################################################################
#----------------------------------------------------------------------------
if [[ ( ! $LITP_JEE_DE_name =~ (^presentation-server) ) ]] ; then
  exit 0
fi

PROPERTY_FILE=/ericsson/tor/data/global.properties
PIB_ADDRESS=$(python -c "import sys; sys.path.insert(0, '/opt/ericsson/PlatformIntegrationBridge/etc'); import utilities; print utilities.findPIB()")
logger "***"
#Check properties file exists
if [ ! -f $PROPERTY_FILE ]; then
  logger "*******Problem, UI Server properties file $PROPERTY_FILE missing, ****EXITING***"
  exit 0
fi
. $PROPERTY_FILE

#String returned from PIB if attribute does not already exist
PS_EXISTS_CHK="Did not find configuration"

# Set ICA ADDR
# Read and format properties from /etc/hosts
CITRIX_ADDR1=$(sed -n 's/\(\s\)*\(\([0-9]\{1,3\}\.\)\{3\}[0-9]\{1,3\}\).*uas-addr1.*/\2/gp' /etc/hosts)
if [ -z "${CITRIX_ADDR1}" ]
then
  CITRIX_ADDR1="NOT_USED"
fi
CITRIX_ADDR2=$(sed -n 's/\(\s\)*\(\([0-9]\{1,3\}\.\)\{3\}[0-9]\{1,3\}\).*uas-addr2.*/\2/gp' /etc/hosts)
if [ -z "${CITRIX_ADDR2}" ]
then
  CITRIX_ADDR2="NOT_USED"
fi
CITRIX_ADDR3=$(sed -n 's/\(\s\)*\(\([0-9]\{1,3\}\.\)\{3\}[0-9]\{1,3\}\).*uas-addr3.*/\2/gp' /etc/hosts)
if [ -z "${CITRIX_ADDR3}" ]
then
  CITRIX_ADDR3="NOT_USED"
fi
CITRIX_ADDR4=$(sed -n 's/\(\s\)*\(\([0-9]\{1,3\}\.\)\{3\}[0-9]\{1,3\}\).*uas-addr4.*/\2/gp' /etc/hosts)
if [ -z "${CITRIX_ADDR4}" ]
then
  CITRIX_ADDR4="NOT_USED"
fi
CITRIX_ADDRS=$(echo icaAddr:${CITRIX_ADDR1} icaAddr1:${CITRIX_ADDR2} icaAddr2:${CITRIX_ADDR3} icaAddr3:${CITRIX_ADDR4} | tr ' ' ',')

# Check to see if property exists (i.e. see if Presentation Server is deployed yet)
icaAddrReadRes=`/opt/ericsson/PlatformIntegrationBridge/etc/config.py read --app_server_address=$PIB_ADDRESS --name=PresentationService_icaAddr --service_identifier=presentation-server`
if [ $? -ne 0 ]; then
  logger "*******Problem, Cannot read ICA ADDR Properties, ****EXITING***"
  exit 0
fi

if [[ $icaAddrReadRes =~ $PS_EXISTS_CHK ]] ; then
   logger Creating ICA ADDR $CITRIX_ADDRS
   /opt/ericsson/PlatformIntegrationBridge/etc/config.py create --app_server_address=$PIB_ADDRESS --name=PresentationService_icaAddr --value="${CITRIX_ADDRS}" --type=String[] --service_identifier=presentation-server --scope=SERVICE
else 
   logger Updating ICA ADDR $CITRIX_ADDRS
   /opt/ericsson/PlatformIntegrationBridge/etc/config.py update --app_server_address=$PIB_ADDRESS --name=PresentationService_icaAddr --value="${CITRIX_ADDRS}" --service_identifier=presentation-server
fi

if [ $? -ne 0 ]; then
  logger "*******Problem, Cannot set ICA ADDR Properties, ****EXITING***"
  exit 0
fi

# Set WEB HOST
# Read and format properties from $PROPERTY_FILE
WEB_HOST_PROPS=$(awk '/#WEB_HOSTS_END/{P=0}P;/#WEB_HOSTS_START/{P=1}' $PROPERTY_FILE)
WEB_HOSTS=$(echo $WEB_HOST_PROPS | sed -e 's/\ /,/g')
WEB_HOSTS=$(echo $WEB_HOSTS | sed -e 's/=/:/g')
# Check to see if property exists (i.e. see if Presentation Server is deployed yet)
webHostReadRes=`/opt/ericsson/PlatformIntegrationBridge/etc/config.py read --app_server_address=$PIB_ADDRESS --name=PresentationService_webHost --service_identifier=presentation-server`
logger $webHostReadRes
if [[ $webHostReadRes =~ $PS_EXISTS_CHK ]] ; then
   logger Creating WEB HOST $WEB_HOSTS
   /opt/ericsson/PlatformIntegrationBridge/etc/config.py create --app_server_address=$PIB_ADDRESS --name=PresentationService_webHost --value="${WEB_HOSTS}" --type=String[] --service_identifier=presentation-server --scope=SERVICE
else
   logger Updating WEB HOST $WEB_HOSTS
   /opt/ericsson/PlatformIntegrationBridge/etc/config.py update --app_server_address=$PIB_ADDRESS --name=PresentationService_webHost --value="${WEB_HOSTS}" --service_identifier=presentation-server
fi

if [ $? -ne 0 ]; then
  logger "*******Problem, Cannot set WEB HOST Properties, ****EXITING***"
  exit 0
fi

# Set WEB PROTOCOL
# Read and format properties from $PROPERTY_FILE
WEB_PROTOCOL_PROPS=$(awk '/#WEB_PROTOCOLS_END/{P=0}P;/#WEB_PROTOCOLS_START/{P=1}' $PROPERTY_FILE)
WEB_PROTOCOLS=$(echo $WEB_PROTOCOL_PROPS | sed -e 's/\ /,/g')
WEB_PROTOCOLS=$(echo $WEB_PROTOCOLS | sed -e 's/=/:/g')

# Check to see if property exists (i.e. see if Presentation Server is deployed yet)
webProtocolReadRes=`/opt/ericsson/PlatformIntegrationBridge/etc/config.py read --app_server_address=$PIB_ADDRESS --name=PresentationService_webProtocol --service_identifier=presentation-server`
logger $webProtocolReadRes
if [[ $webProtocolReadRes =~ $PS_EXISTS_CHK ]] ; then
   logger Creating WEB PROTOCOL $WEB_PROTOCOLS
   /opt/ericsson/PlatformIntegrationBridge/etc/config.py create --app_server_address=$PIB_ADDRESS --name=PresentationService_webProtocol --value="${WEB_PROTOCOLS}" --type=String[] --service_identifier=presentation-server --scope=SERVICE
else
   logger Updating WEB PROTOCOL $WEB_PROTOCOLS
   /opt/ericsson/PlatformIntegrationBridge/etc/config.py update --app_server_address=$PIB_ADDRESS --name=PresentationService_webProtocol --value="${WEB_PROTOCOLS}" --service_identifier=presentation-server
fi

if [ $? -ne 0 ]; then
  logger "*******Problem, Cannot set WEB PROTOCOL Properties, ****EXITING***"
  exit 0
fi

logger PIB Configuration completed

exit 0

