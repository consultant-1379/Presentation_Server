##
## Copyright (c) 2011 Ericsson AB, 2010 - 2011.
##
## All Rights Reserved. Reproduction in whole or in part is prohibited
## without the written consent of the copyright owner.
##
## ERICSSON MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
## SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING
## BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
## FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ERICSSON
## SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A
## RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
## DERIVATIVES.
##
## Script to check whether the UI Service Unit is UNLOCKED
##
## returns: 0 if the SU is UNLOCKED
##          1 if the SU is LOCKED, or if there is a problem checking
##            the availabililty model
##          

##
## COMMON FUNCTIONS AND VARS
##
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
. ${SCRIPT_DIR}/healthcheck_common.bsh

##
## COMMANDS
##

##
## PATHS
##

##
## ENVIRONMENT
##
AMF_COMPONENT_NAME=UIServ
AMF_COMPONENT_TYPE="Service Unit"
AMF_STATE_NAME="Admin State"
RETURN_VAL=""
SCRIPT_NAME=$( ${BASENAME} ${0} )

##
## EXECUTION
##

check_su_ready_state ${AMF_COMPONENT_NAME}
RETURN_VALUE=${?}

if [ ${RETURN_VALUE} -eq 0 ]; then
	info ${SCRIPT_NAME} "${AMF_COMPONENT_NAME} ${AMF_COMPONENT_TYPE} ${AMF_STATE_NAME} is UNLOCKED"
	exit 0
elif [ ${RETURN_VALUE} -eq 1 ]; then
	info ${SCRIPT_NAME} "${AMF_COMPONENT_NAME} ${AMF_COMPONENT_TYPE} ${AMF_STATE_NAME} is LOCKED"
	exit 1
else
	error ${SCRIPT_NAME} "Problem checking the availabililty model which needs MS connectivity"
	exit 1
fi
