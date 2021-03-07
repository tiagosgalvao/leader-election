# Leader Election

Me, Tiago Galvao hereby declare: that this code is authentic created by my own authorship. 
It might or not have also been resulted from for some proof of concept (Poc), personal purposes or even selection process, although 
the company(ies) involved won't be disclosed. There is a chance that during the development process some references, tutorials, 
videos from the web or books supported me in order to support best practices resulting in a good application.

## Introduction
This guide walks you through the process of creating an application that generates elects a running instance as leader, similar process that Zookeeper does in order to support Kafka.

## About the application
This application uses redis(cache) as helper in order to define which of many app instances should be the leader instance.

## Leader election - application objectives
1. Create a SpringBoot project using Java or Kotlin, and Maven or Gradle * Kotlin, Gradle is the recommendation
2. Using the docker-compose file provided to run Redis and Kafka
3. Connect your SpringBoot application to the running Kafka and Redis instances
4. Set the SpringBoot port to random
    * port: 0 is random is SpringFramework
5. Run multiple instances of your project and implement a leader election strategy using Redis
    * Check the basic flowchart provided as a hint, or you can follow any other approaches
6. Publish the server time as an object to Kafka every "publishInterval" seconds using the leader instance
    * publishInterval should be a configurable parameter

## Running the application locally
* Important: Apache Kafka and Redis instances must be running.

There are several ways to run a Spring Boot application on your local machine. One way is to execute the `main` method in the 
`com.galvao.leader.election.LeaderElectionApplication` class from your IDE.

Alternatively you can use the gradlew like so:

```shell
./gradlew bootRun
```

* compiles Java classes to the /target directory
* copies all resources to the /target directory
* starts an embedded Apache Tomcat server

## Folder structure + important files

```bash
.
├── HELP.md                                   # Reference guides for the uses technologies 
├── README.md                                 # Important! Read before changing configuration
├── build.gradle
├── settings.gradle
└── src
    ├── main
    │  ├── java                               # service
    │  └── resources
    │     ├── application.yml                 # Common application configuration runnning using docker configs
    └── test
        ├── java                              # Sample Testcases
        └── resources
            └── application-TEST.yml
```

## Setup to run a local instance of Apache Kafka
* Once Kafka still uses zookeeper as broker when using docker will be necessary to start them with docker-compose.

* Starting leader-elector_zookeeper_1 ... done
* Starting leader-elector_kafka_1     ... done

## Testing the application
There is a documentation swagger page with some helper methods in order to check the cache and clean it.
Application is picking a random port on start up (make sure to check it) - e.g.: 8080

http://localhost:8080/api/docs/

## Test coverage
The application has a very high test coverage - 100%, that guarantees that all the scenarios are covered.