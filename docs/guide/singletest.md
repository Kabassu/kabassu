# Single Test Guide

Use HTTPS protocol to connect to REST services

Single Test in Kabassu is single test project that can be run with one command. Its creation and execution contains: 
1. Login  
2. Create definition
3. Create and run execution based on definition
4. Get the status of execution
5. Get the result
6. View report
7. Rerun test

## Login

Kabassu is using JWT authentication. Each request has to contain header `Authorization: Bearer <token>`. _<token>_ can be retrieved using:  
Endpoint: `POST /kabassu/login`

Request:
```json
{
  "username": "<name>",
  "password_hash": "<password>"
}
```

If both parameters are correct then token will be in the response. **It has to be used in ever other call**

## Create Definition

Definition is the base of Kabassu system. It contains all constant information about the test. It is in some way template that can be reused.  
Endpoint: ``POST /kabassu/adddefinition`` 

Request 
```
{
  "name": "test",
  "runner": "gradle",
  "locationType": "git",
  "additionalParameters": {
  	"repository": "git@github.com:Kabassu/kabassu-simple-test.git",
  	"location": "",
  	"runnerOptions": ["clean", "test"],
  },
  "reports": [ "allure", "allure-trend" ]
}
```
**name** - just name for definition to make easier to understand it's purpose
**runner** - what runner is used for this test
**locationType** - where is test located
**additionaParameters** - array of other parameters that maybe required for **runner** or **locationType**. 
-  **runnerOptions** - array of options for runner (for example for gradle runner: list of tasks)
-  **location** - actual location od disk (used only for _filesystem_)
-  **repository** - address of git repository if you use git locationType
**reports** - list of reports to download after test

Call for this service will return id of the definition

## Create and run execution based on definition

Execution is actual request to run test based on definition. It also can contains parameters that can be changed for definition it this particular execution   
Endpoint: ``POST  /kabassu/test/run:``   

Request
```
{
  "definitionId": "5d6914b4540d7f634e494f55",
  "configurationId": "string",
  "additionalParameters": {
  	"branch": "test",
  	  "jvm":"1.8",
  },
  "description": "Test execution descriptiom"
}
```
**definitionId** - id of prepared definition
**configurationId** - id of earlier prepared **additionalParameters** !NOT DONE YET!
**additionalParameters** - list of additional parameters that can be changed for execution. 
- **branch** is parameter for git **locationType** from definition that can be used to checkout different branch the default  
- **jvm** - jvm which will be used to run test (defined in Kabassu configuration)
**description** - only the description of this execution
Call will return id of created execution

## Get the status of execution
It is simple retrieving all data for single execution   
Endpoint: ``GET /kabassu/getrequest/{id}``  
**{id}** - id of created execution

Response:  
```
{
  "_id" : "5d692388540d7f634e494f5b",
  "definitionId" : "5d691239540d7f634e494f4f",
  "description" : "uber",
  "configurationId" : "string",
  "additionalParameters" : {
    "branch" : "test",
    "jvm" : "1.8",
  },
  "status" : "finished",
  "history" : [ {
    "date" : 1567171464312,
    "event" : "Request created and started"
  }, {
    "date" : 1567171488592,
    "event" : "Test finished with: Success"
  }, {
    "date" : 1567171490735,
    "event" : "Reports downloaded"
  } ]
}
``` 
Data that were added after execution created:  
**status** - status of the execution - when _finished_ then executions ... finished and results and reports are ready   
**history** - array of events connected to the execution

## Get the result

When test execution is finished we can get the result. Because executions can be rerun there can be many results   
Endpoint: ``GET /kabassu/getallbyfield/{collection}/{field}/{value}/{page}/{pagesize}``  
For results: ``GET /kabassu/getallbyfield/kabassu-results/testRequest._id/5d5d3fa50086ca1f247da509/0/100``

**collection** - name of mongo collection in our case _kabassu-results_   
**field** - field in collection that should be in filter example: _testRequest.id_   
**value** - value of above field   
**page*** - page - for pagination
**pageSize** - how many results are present in one page

Response:
```
{
  "results" : [ {
    "_id" : "5d6923a2540d7f634e494f5c",
    "testRequest" : {
      "_id" : "5d692388540d7f634e494f5b",
      "definitionId" : "5d691239540d7f634e494f4f",
      "description" : "uber",
      "configurationId" : "string",
      "additionalParameters" : {
        "branch" : "test",
        "jvm" : "1.8"
      },
      "status" : "started",
      "history" : [ {
        "date" : 1567171464312,
        "event" : "Request created and started"
      }, {
        "date" : 1567171488592,
        "event" : "Test finished with: Success"
      } ]
    },
    "definition" : {
      "_id" : "5d691239540d7f634e494f4f",
      "name" : "GIT",
      "runner" : "gradle",
      "locationType" : "git",
      "additionalParameters" : {
        "repository" : "git@github.com:Kabassu/kabassu-simple-test.git",
        "runnerOptions" : [ "clean", "test" ],
        "location" : "D:\\code\\kabassu\\test-dir\\5d692388540d7f634e494f5b\\kabassu-simple-test"
      },
      "reports" : [ "allure", "allure-trend" ],
      "created" : 1567167033831
    },
    "configuration" : { },
    "result" : "Success",
    "downloadedReports" : [ {
      "location" : "D:\\code\\kabassu\\reports-dir\\5d692388540d7f634e494f5b-allure\\1",
      "downloadPath" : "\\5d692388540d7f634e494f5b-allure\\1/index.html",
      "reportType" : "allure",
      "type" : "multi"
    }, {
      "location" : "D:\\code\\kabassu\\reports-dir\\5d692388540d7f634e494f5b-allure-trend\\allure-report",
      "downloadPath" : "\\5d692388540d7f634e494f5b-allure-trend\\allure-report/index.html",
      "reportType" : "allure-trend",
      "type" : "single"
    } ],
    "created" : 1567171490735
  } ],
  "allItems" : 1
}
```

**allItems** - how many results we have (all not only **pageSize**)
**result** - what was result of test execution
**downloadedReports** - array of reports retrieved from test
- **location** - place on disk where reports were downloaded
- **downloadPath** - path to be added to results server to display report
- **reportType** - type of the report
- **type** - if the report is _single_ (one for each result) or _multi_ (one report for all results with the same **reportType**)  

## View report

To view report we need to use result server and **downloadPath** from results   
``GET <result-server-address>/<downloadPath>``

## Rerun test

It is possible to rerun test execution that was finished earlier.  
Endpoint ``POST /kabassu/suite/rerun``  

Request
```
{
  "requestId": "5d5d3fa50086ca1f247da509"
}
```
**requestId** - id of execution to rerun
