#!/bin/bash
#----------------------------------------------------------------------------
#############################################################################
# COPYRIGHT Ericsson 2013
# The copyright to the computer program�herein is the property of
# conditions stipulated in the agreement/contract under which the
# program have been supplied.
#############################################################################
#----------------------------------------------------------------------------

LOG_PREFIX="[PS Update Cache]"
logger "${LOG_PREFIX} Value of JB_INTERNAL -> ${JB_INTERNAL}"

if [ ! -f $GLOBAL_CONFIG ]; then
  logger "${LOG_PREFIX} The variable GLOBAL_CONFIG was found. Defining PROPERTY_FILE to ${GLOBAL_CONFIG}"
  PROPERTY_FILE=$GLOBAL_CONFIG
else
  logger "${LOG_PREFIX} The variable GLOBAL_CONFIG was not found. Defining PROPERTY_FILE to /ericsson/tor/data/global.properties"
  PROPERTY_FILE=/ericsson/tor/data/global.properties
fi

if [ -z "$PIB_HOME" ]; then
  logger "${LOG_PREFIX} The variable PIB_HOME was not found. Defining as /opt/ericsson/PlatformIntegrationBridge/etc"
  PIB_HOME="/opt/ericsson/PlatformIntegrationBridge/etc"
else
  logger "${LOG_PREFIX} The variable PIB_HOME is already defined as ${PIB_HOME}"
fi


if [ -z "$PIB_ADDRESS" ]; then
  logger "${LOG_PREFIX} The variable PIB_ADDRESS was not found. Defining as ${JB_INTERNAL}:8080"
  PIB_ADDRESS="$JB_INTERNAL:8080"
else
  logger "${LOG_PREFIX} The variable PIB_ADDRESS is already defined as ${PIB_ADDRESS}"
fi

logger "***"
#Check properties file exists
if [ ! -f $PROPERTY_FILE ]; then
  logger "*******Problem, UI Server properties file $PROPERTY_FILE missing, ****EXITING***"
  exit 1
fi
. $PROPERTY_FILE

#String returned from PIB if attribute does not already exist
PS_EXISTS_CHK="Did not find configuration"

# Set ICA ADDR
# Read and format properties from /etc/hosts
CITRIX_ADDR1=$(sed -n 's/\(\s\)*\(\([0-9]\{1,3\}\.\)\{3\}[0-9]\{1,3\}\).*uas-addr1.*/\2/gp' /etc/hosts)
if [ -z "${CITRIX_ADDR1}" ] || [ "${CITRIX_ADDR1}" = "127.0.0.1" ]
then
  CITRIX_ADDR1="NOT_USED"
fi
CITRIX_ADDR2=$(sed -n 's/\(\s\)*\(\([0-9]\{1,3\}\.\)\{3\}[0-9]\{1,3\}\).*uas-addr2.*/\2/gp' /etc/hosts)
if [ -z "${CITRIX_ADDR2}" ] || [ "${CITRIX_ADDR2}" = "127.0.0.1" ]
then
  CITRIX_ADDR2="NOT_USED"
fi
CITRIX_ADDR3=$(sed -n 's/\(\s\)*\(\([0-9]\{1,3\}\.\)\{3\}[0-9]\{1,3\}\).*uas-addr3.*/\2/gp' /etc/hosts)
if [ -z "${CITRIX_ADDR3}" ] || [ "${CITRIX_ADDR3}" = "127.0.0.1" ]
then
  CITRIX_ADDR3="NOT_USED"
fi
CITRIX_ADDR4=$(sed -n 's/\(\s\)*\(\([0-9]\{1,3\}\.\)\{3\}[0-9]\{1,3\}\).*uas-addr4.*/\2/gp' /etc/hosts)
if [ -z "${CITRIX_ADDR4}" ] || [ "${CITRIX_ADDR4}" = "127.0.0.1" ]
then
  CITRIX_ADDR4="NOT_USED"
fi
CITRIX_ADDRS=$(echo icaAddr:${CITRIX_ADDR1} icaAddr1:${CITRIX_ADDR2} icaAddr2:${CITRIX_ADDR3} icaAddr3:${CITRIX_ADDR4} | tr ' ' ',')

# Check to see if property exists (i.e. see if Presentation Server is deployed yet)
icaAddrReadRes=`$PIB_HOME/config.py read --app_server_address=$PIB_ADDRESS --name=PresentationService_icaAddr --service_identifier=presentation-server`
logger "${LOG_PREFIX} Reading ICA ADDR on PIB -> ${icaAddrReadRes}"
if [ $? -ne 0 ]; then
  logger "*******Problem, Cannot read ICA ADDR Properties, ****EXITING***"
  exit 1
fi

if [[ $icaAddrReadRes =~ $PS_EXISTS_CHK ]] ; then
   logger "Creating ICA ADDR ${CITRIX_ADDRS} -> ${PIB_HOME}/config.py create --app_server_address=${PIB_ADDRESS} --name=PresentationService_icaAddr --value=\"${CITRIX_ADDRS}\" --type=String[] --service_identifier=presentation-server --scope=SERVICE"
   $PIB_HOME/config.py create --app_server_address=$PIB_ADDRESS --name=PresentationService_icaAddr --value="${CITRIX_ADDRS}" --type=String[] --service_identifier=presentation-server --scope=SERVICE
else
   logger "Updating ICA ADDR ${CITRIX_ADDRS} -> ${PIB_HOME}/config.py update --app_server_address=${PIB_ADDRESS} --name=PresentationService_icaAddr --value=\"${CITRIX_ADDRS}\" --service_identifier=presentation-server"
   $PIB_HOME/config.py update --app_server_address=$PIB_ADDRESS --name=PresentationService_icaAddr --value="${CITRIX_ADDRS}" --service_identifier=presentation-server
fi

if [ $? -ne 0 ]; then
  logger "*******Problem, Cannot set ICA ADDR Properties, ****EXITING***"
  exit 1
fi

# Set WEB HOST
CUSTOM_FILE=/ericsson/tor/data/ci_custom_properties
if [ -f $CUSTOM_FILE ]; then
  # Read and format properties from $LITP_FILE
  WEB_HOST_PROPS=$(grep '^UI_PRES' $CUSTOM_FILE)
else
  # Read and format properties from $PROPERTY_FILE
  WEB_HOST_PROPS=$(grep '^web_host' $PROPERTY_FILE)
fi
WEB_HOSTS=$(echo $WEB_HOST_PROPS | sed -e 's/[ \t]*=[\t]*/:/g')
WEB_HOSTS=$(echo $WEB_HOSTS | sed -e 's/\ /,/g')
if [ -f $CUSTOM_FILE ]; then
 WEB_HOSTS=$(echo $WEB_HOSTS | sed -e 's/UI_PRES_SERVER://g')
 WEB_HOSTS=$(echo default:${WEB_HOSTS})
else
  WEB_HOSTS=$(echo $WEB_HOSTS | sed -e 's/web_host_//g')
fi


logger "${LOG_PREFIX} Variable WEB_HOSTS content -> ${WEB_HOSTS}"

# Check to see if property exists (i.e. see if Presentation Server is deployed yet)
webHostReadRes=`$PIB_HOME/config.py read --app_server_address=$PIB_ADDRESS --name=PresentationService_webHost --service_identifier=presentation-server`
logger "${LOG_PREFIX} Reading Web Hosts on PIB -> ${webHostReadRes}"

if [[ $webHostReadRes =~ $PS_EXISTS_CHK ]] ; then

   logger "${LOG_PREFIX} Creating WEB HOST Config -> ${PIB_HOME}/config.py create --app_server_address=${PIB_ADDRESS} --name=PresentationService_webHost --value=\"${WEB_HOSTS}\" --type=String[] --service_identifier=presentation-server --scope=SERVICE"
   $PIB_HOME/config.py create --app_server_address=$PIB_ADDRESS --name=PresentationService_webHost --value="${WEB_HOSTS}" --type=String[] --service_identifier=presentation-server --scope=SERVICE
else
   logger "${LOG_PREFIX} Updating WEB HOST Config -> ${PIB_HOME}/config.py update --app_server_address=${PIB_ADDRESS} --name=PresentationService_webHost --value=\"${WEB_HOSTS}\" --service_identifier=presentation-server"
   $PIB_HOME/config.py update --app_server_address=$PIB_ADDRESS --name=PresentationService_webHost --value="${WEB_HOSTS}" --service_identifier=presentation-server
fi

if [ $? -ne 0 ]; then
  logger "*******Problem, Cannot set WEB HOST Properties, ****EXITING***"
  exit 0
fi

# Set WEB PROTOCOL
# Read and format properties from $PROPERTY_FILE
WEB_PROTOCOL_PROPS=$(grep '^web_protocols_' $PROPERTY_FILE)
WEB_PROTOCOLS=$(echo $WEB_PROTOCOL_PROPS | sed -e 's/\ /,/g')
WEB_PROTOCOLS=$(echo $WEB_PROTOCOLS | sed -e 's/=/:/g')
WEB_PROTOCOLS=$(echo $WEB_PROTOCOLS | sed -e 's/web_protocols_//g')

logger "${LOG_PREFIX} Variable WEB_PROTOCOLS content -> ${WEB_PROTOCOLS}"

# Check to see if property exists (i.e. see if Presentation Server is deployed yet)
webProtocolReadRes=`$PIB_HOME/config.py read --app_server_address=$PIB_ADDRESS --name=PresentationService_webProtocol --service_identifier=presentation-server`
logger "${LOG_PREFIX} Reading Web Protocols on PIB -> ${webProtocolReadRes}"

if [[ $webProtocolReadRes =~ $PS_EXISTS_CHK ]] ; then
   logger "${LOG_PREFIX} Creating WEB PROTOCOL -> ${PIB_HOME}/config.py create --app_server_address=${PIB_ADDRESS} --name=PresentationService_webProtocol --value=\"${WEB_PROTOCOLS}\" --type=String[] --service_identifier=presentation-server --scope=SERVICE"
   $PIB_HOME/config.py create --app_server_address=$PIB_ADDRESS --name=PresentationService_webProtocol --value="${WEB_PROTOCOLS}" --type=String[] --service_identifier=presentation-server --scope=SERVICE
else
   logger "${LOG_PREFIX} Updating WEB PROTOCOL - > ${PIB_HOME}/config.py update --app_server_address=${PIB_ADDRESS} --name=PresentationService_webProtocol --value=\"${WEB_PROTOCOLS}\" --service_identifier=presentation-server"
   $PIB_HOME/config.py update --app_server_address=$PIB_ADDRESS --name=PresentationService_webProtocol --value="${WEB_PROTOCOLS}" --service_identifier=presentation-server
fi

if [ $? -ne 0 ]; then
  logger "*******Problem, Cannot set WEB PROTOCOL Properties, ****EXITING***"
  exit 0
fi

# Set ENM HOST
# ------------------------------------------------

# Get the value from global.properties
enmHostNamePropertyValue=$(grep '^host_system_identifier' $PROPERTY_FILE | sed -e 's/host_system_identifier=//g')
if [ "x$enmHostNamePropertyValue" = "x" ]; then

    logger "${LOG_PREFIX} The property host_system_identifier is not defined in global.properties. enmHostName will not be available!"
else
    logger "${LOG_PREFIX} Found a value for host_system_identifier in global.properties: ${enmHostNamePropertyValue}"

    # Check to see if property exists
    enmHostName=`$PIB_HOME/config.py read --app_server_address $PIB_ADDRESS --name=enmHostName --service_identifier=presentation-server`
    logger "${LOG_PREFIX} Reading enmHostName on PIB -> ${enmHostName}"
    if [ $? -ne 0 ]; then
      logger "${LOG_PREFIX} *******Problem, Cannot read enmHostName Properties, ****EXITING***"
      exit 1
    fi

    if [ "x$enmHostName" = "x" ] ; then
       logger "${LOG_PREFIX} Creating enmHostName ${enmHostNamePropertyValue} -> ${PIB_HOME}/config.py create --app_server_address=${PIB_ADDRESS} --name=enmHostName --value=${enmHostNamePropertyValue} --type=String --service_identifier=presentation-server --scope=SERVICE"
       $PIB_HOME/config.py create --app_server_address=$PIB_ADDRESS --name=enmHostName --value=$enmHostNamePropertyValue --type=String --service_identifier=presentation-server --scope=SERVICE

    elif [ "$enmHostName" = "[]" ] ; then
       logger "${LOG_PREFIX} Updating empty enmHostName ${enmHostNamePropertyValue} -> ${PIB_HOME}/config.py update --app_server_address=${PIB_ADDRESS} --name=enmHostName --value=${enmHostNamePropertyValue} --service_identifier=presentation-server --scope=SERVICE"
       $PIB_HOME/config.py update --app_server_address=$PIB_ADDRESS --name=enmHostName --value=$enmHostNamePropertyValue --service_identifier=presentation-server --scope=SERVICE
    else
       logger "${LOG_PREFIX} Property enmHostName already exists -> ${enmHostName}"
    fi

    if [ $? -ne 0 ]; then
      logger "${LOG_PREFIX} *******Problem, Cannot set enmHostName Properties, ****EXITING***"
      exit 1
    fi
fi


logger "${LOG_PREFIX} PIB Configuration completed"
exit 0

