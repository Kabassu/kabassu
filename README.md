# kabassu
 
[![Build Status](https://travis-ci.org/Kabassu/kabassu.svg?branch=master)](https://travis-ci.org/Kabassu/kabassu)

[![Quality Gate](https://sonarcloud.io/api/badges/gate?key=io.kabassu)](https://sonarcloud.io/dashboard?id=io.kabassu)

# Description
The goal of Kabassu is to create test central, place where users can gather different tests for different application, run them against different enviroments and configurations

# Requirements

1. Java 12
2. MongoDB

# How to run it

1. Create database in Mongo.
2. Clone this repository
3. Build with gradle
4. Go to distribution folder, unzip zip file in desired location
5. Go to configuration/modules/io.kabassu.mongo.json and change entry:
 ```
  "mongo-config": {
       "db_name": "kabassu-dev"
 ```
Here you can find possible options [https://vertx.io/docs/vertx-mongo-client/java/#_configuring_the_client]

6. Run windows_run.bat
7. Application will use ports 8080 and 8090. They can be changed in server configuration files