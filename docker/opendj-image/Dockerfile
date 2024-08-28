FROM armdocker.rnd.ericsson.se/proj_oss_releases/enm/access-control

RUN mkdir -p /ericsson/tor/data/apps/

COPY my-app-01/         /ericsson/tor/data/apps/my-app-01/
COPY my-app-external/   /ericsson/tor/data/apps/my-app-external/
COPY my-app-01-copy-1/  /ericsson/tor/data/apps/my-app-01-copy-1/
COPY my-app-01-copy-2/  /ericsson/tor/data/apps/my-app-01-copy-2/
COPY my-app-02/         /ericsson/tor/data/apps/my-app-02/
COPY my-app-03/         /ericsson/tor/data/apps/my-app-03/

COPY alex/                          /ericsson/tor/data/apps/alex/
COPY autoidmanagement/              /ericsson/tor/data/apps/autoidmanagement/
COPY command_line_interface/        /ericsson/tor/data/apps/command_line_interface/
COPY networkdiscovery/              /ericsson/tor/data/apps/networkdiscovery/
COPY networkexplorer/               /ericsson/tor/data/apps/networkexplorer/
COPY networkexplorercollections/    /ericsson/tor/data/apps/networkexplorercollections/
COPY networkhealthmonitor/          /ericsson/tor/data/apps/networkhealthmonitor/
COPY networkviewer/                 /ericsson/tor/data/apps/networkviewer/
COPY nodemonitor/                   /ericsson/tor/data/apps/nodemonitor/
COPY nodesecurityconfiguration/     /ericsson/tor/data/apps/nodesecurityconfiguration/
COPY nodeversionsupport/            /ericsson/tor/data/apps/nodeversionsupport/
COPY fmx/                           /ericsson/tor/data/apps/fmx/
COPY kpimanagement/                 /ericsson/tor/data/apps/kpimanagement/
COPY Log_Viewer/                    /ericsson/tor/data/apps/Log_Viewer/
COPY pkientitymanagement/           /ericsson/tor/data/apps/pkientitymanagement/
COPY pkiprofilemanagement/          /ericsson/tor/data/apps/pkiprofilemanagement/
COPY pmic/                          /ericsson/tor/data/apps/pmic/
COPY role_management/               /ericsson/tor/data/apps/role_management/
COPY shm/                           /ericsson/tor/data/apps/shm/
COPY syssecconfig/                  /ericsson/tor/data/apps/syssecconfig/
COPY targetmanagement/              /ericsson/tor/data/apps/targetmanagement/
COPY topologybrowser/               /ericsson/tor/data/apps/topologybrowser/
COPY user_management/               /ericsson/tor/data/apps/user_management/

# access-control image provides the /ericsson/tor/data volume
# so we need to extend it to include the presentation_server folder
# and expose it to jboss image

VOLUME /ericsson/tor/data/ericsson/tor/data/presentation_server/config
VOLUME /ericsson/tor/data/apps/my-app-01/
VOLUME /ericsson/tor/data/apps/my-app-external/
VOLUME /ericsson/tor/data/apps/my-app-01/actions/rules
VOLUME /ericsson/tor/data/apps/my-app-01-copy-1/
VOLUME /ericsson/tor/data/apps/my-app-01-copy-2/
VOLUME /ericsson/tor/data/apps/my-app-02/
VOLUME /ericsson/tor/data/apps/my-app-02/actions/rules
VOLUME /ericsson/tor/data/apps/my-app-03/
VOLUME /ericsson/tor/data/apps/my-app-03/actions/rules

VOLUME /ericsson/tor/data/apps/alex/
VOLUME /ericsson/tor/data/apps/autoidmanagement/
VOLUME /ericsson/tor/data/apps/command_line_interface/
VOLUME /ericsson/tor/data/apps/networkdiscovery/
VOLUME /ericsson/tor/data/apps/networkexplorer/
VOLUME /ericsson/tor/data/apps/networkexplorercollections/
VOLUME /ericsson/tor/data/apps/networkhealthmonitor/
VOLUME /ericsson/tor/data/apps/networkviewer/
VOLUME /ericsson/tor/data/apps/nodemonitor/
VOLUME /ericsson/tor/data/apps/nodesecurityconfiguration/
VOLUME /ericsson/tor/data/apps/nodeversionsupport/
VOLUME /ericsson/tor/data/apps/fmx/
VOLUME /ericsson/tor/data/apps/kpimanagement/
VOLUME /ericsson/tor/data/apps/Log_Viewer/
VOLUME /ericsson/tor/data/apps/pkientitymanagement/
VOLUME /ericsson/tor/data/apps/pkiprofilemanagement/
VOLUME /ericsson/tor/data/apps/pmic/
VOLUME /ericsson/tor/data/apps/role_management/
VOLUME /ericsson/tor/data/apps/shm/
VOLUME /ericsson/tor/data/apps/syssecconfig/
VOLUME /ericsson/tor/data/apps/targetmanagement/
VOLUME /ericsson/tor/data/apps/topologybrowser/
VOLUME /ericsson/tor/data/apps/user_management/
