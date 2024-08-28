-- Gives permission on the changelog table for troubleshooting and instrumentation
GRANT ALL privileges ON TABLE databasechangelog TO psuser;
ALTER TABLE databasechangelog OWNER TO psuser;

GRANT ALL privileges ON TABLE databasechangeloglock TO psuser;
ALTER TABLE databasechangeloglock OWNER TO psuser;
