# Presentation Server

This repository contains the source code and documentation for Presentation Server (PS).

### SonarQube

You can check the SonarQube status for this project 
[here](https://sonarqube.lmera.ericsson.se/dashboard?id=com.ericsson.nms.pres%3APresentation_Server).

## Docker Test Environment

You can have a full test environment with all direct dependencies for PS starting our docker compose file provided in 
this repository.

The docker-compose.yml file can be found at the root of the repository.

Assuming you have docker installed, all you need to do is run the following commend at the root of the project folder:

    docker-compose up
    
If you don't have docker yet, follow these instructions:
[Install docker on Windows 10](https://confluence-nam.lmera.ericsson.se/display/Mavericks/Windows+10+Setup)    
    
You can update your EAR package on the server either running our integration tests or uploading the package using JBoss administration 
console at [http://localhost:9990](http://localhost:9990)

### API Docs

With our docker environment we provide a testable documentation for our REST endpoints.
You can access it at [http://localhost/apidocs](http://localhost/apidocs)

### PostgreSQL

Presentation Server uses a PostgreSQL database that is accessible in the docker container in the port 5432.
You can use any client of your preference, or use the Web Admin interface provided in our docker compose file:

http://localhost:8181

Use the following parameters:

* **System**: PostgreSQL
* **Server**: postgres
* **User**: psuser
* **Password**: ps123
* **Database**: psdb
 

## How to build the source code

### Compile, package and test 

    mvn clean install
    
The packages will be generated at the target folder on each module, and will be installed in your local maven 
repository (M2_HOME/repository).

The unit test reports can be found at: 

    presentation-server-ejb/build/spock-reports/index.html

The code coverage reports can be found at:

    presentation-server-ejb/target/jacoco-coverage/index.html
    
### Run the integration tests

To run the integration tests you need the docker test environment running. Please follow the instructions on the 
previous topic if you don't know how to do it.

When you have docker running you can trigger the integration tests running the following command:

    mvn clean install -P docker
    
This command will rebuild your packages, deploy the ear in docker JBoss server and run unit and integration tests.

If you want to skip unit tests just disable the unit profile:

    mvn clean install -P !unit,docker
    
In linux you may be required to escape the ! char:

    mvn clean install -P \!unit,docker
    
The test reports can be found at:

     testsuite/integration/jee/build/spock-reports/index.html 
