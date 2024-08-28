#!/bin/bash

##
## Copyright (c) 2013 Ericsson AB, 2013 - 2014.
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
##  This script will check that the logstash Elastic Search is available on this node
##  This script will check the logstash SU Presence State and will check the Elastic Search based on that
##  It will return 0 if SU is INSTANTIATED and Elastic Search check returns 200
##  It will return 0 if SU is UNINSTANTIATED with message saying that check is not needed
##  1 if SU is INSTANTIATED and Elastic Search check fails
##  1 if problem checking the availabililty model

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

echo "Running All Healthchecks for UI Services..."
${SCRIPT_DIR}/ui_pres_server_deployed_hc.sh
${SCRIPT_DIR}/ui_pres_server_returns_apps_hc.sh
${SCRIPT_DIR}/ui_sg_admin_hc.sh
${SCRIPT_DIR}/ui_si_admin_hc.sh
${SCRIPT_DIR}/ui_si_assign_hc.sh
${SCRIPT_DIR}/ui_su_admin_hc.sh
${SCRIPT_DIR}/ui_su_oper_hc.sh
${SCRIPT_DIR}/ui_su_pres_hc.sh
${SCRIPT_DIR}/ui_su_ready_hc.sh
