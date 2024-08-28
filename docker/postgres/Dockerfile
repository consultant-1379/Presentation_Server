FROM armdocker.rnd.ericsson.se/proj_oss_releases/enm/postgres-enm_v2:latest

COPY ./10svc_4scp_8str_rack_3ebs_6asr_enm_physical_production_dd.xml /ericsson/deploymentDescriptions/10svc_4scp_8str_rack_3ebs_6asr_enm_physical_production_dd.xml

ENV POSTGRES_USER postgres

COPY ./config/init/scripts/db-config/init-db.sh /opt/ericsson/docker/config/init/scripts/db-config/
RUN sed -i 's/\r$//' /opt/ericsson/docker/config/init/scripts/db-config/init-db.sh

HEALTHCHECK --interval=30s --retries=30 \
    CMD bash -c "[ -f /opt/ericsson/docker/postgres/shared/ready ]"