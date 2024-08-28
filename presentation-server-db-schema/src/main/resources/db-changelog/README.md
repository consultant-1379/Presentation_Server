# Database Upgrade (Liquibase)

The files provided here are used by Liquibase at the application deployment time to upgrade the database schema to the 
appropriate version.

## Database Info

* Database name: psdb
* Database user: psuser
* Database password: ps123

## DTAG requirements

* The database must be owned by a single user (psuser) and this user must not have permissions to create objects.
* The superuser (postgres) must be used to update the schema, and we must make sure that all required grants and 
ownership are applied to the application user (psuse)
* The superuser should never be used by the application for any other purpose than update schema.     