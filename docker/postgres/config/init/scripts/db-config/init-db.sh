#!/bin/bash
psql -v ON_ERROR_STOP=1 -U${POSTGRES_USER} <<-EOSQL
    CREATE USER psuser LOGIN PASSWORD 'ps123' REPLICATION VALID UNTIL 'infinity';
    ALTER database psdb OWNER TO psuser;
EOSQL