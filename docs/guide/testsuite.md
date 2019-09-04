#Test Suite
Test suite is set of definitions that can be run together. After creation it can be run and test executions for all definitions are created. 
1. Create test suite
2. Create and run suite execution
3. Get the status and results of every test execution in suite
4. Rerun test execution

## Create test suite
Endpoint: ``POST /kabassu/addsuite``  

Request
```
{
  "name":	"string",
  "description":	"string"
  "definitions":	["first_definition_id","second_definition_id"]
}
```
**name** - name of the suite  
**description** - description of the suite   
**definitions** - array of ids of definitions you want to include in test suite

## Create and run suite execution

Suite execution is taking test suite, then creates test execution for every definition in suite.   
Endpoint: ``POST /kabassu/suite/run``

Request
```
{
  "suiteId": "5d664144d6c8ed646fc8d4b0",
  "definitionsData":[
  	{
  	  "definitionId": "5d6640d2d6c8ed646fc8d4ac",
		  "configurationId": "string",
		  "additionalParameters": {
		  	"branch": "test",
		  	"jvm":"1.8"	
  	},
  	{
  		"definitionId": "5d664103d6c8ed646fc8d4ae",
		  "configurationId": "string",
		  "additionalParameters": {
        "branch": "test",
       	"jvm":"1.8"	
      }	
  	}
  ]
}
```
**suiteId** - id of test suite   
*definitionData* - array of object that have configuration for every definition
- **definitionId** - id of definition
- **configurationId** - preset additional data !NOT IMPLEMENTED!
- **additionalParameters** - different additional data used for execution

## Get the status and results of every test execution in suite
To do this you need to retrieve every test execution connected to suite execution and check required information.    
This is two stage operation.    
First we need to get suite execution data   
Endpoint: ``GET /kabassu/getsuiterun/{id}``   
**id** - id of suite execution   
Response:
```
{
  "_id" : "5d6674778ba8ca129804b437",
  "suiteId" : "5d664144d6c8ed646fc8d4b0",
  "requests" : [ "5d6674778ba8ca129804b436", "5d6674778ba8ca129804b435" ],
  "history" : [ {
    "date" : 1567076134902,
    "event" : "Suite Execution rerun started"
  }, {
    "date" : 1567076221437,
    "event" : "Suite Execution rerun started"
  } ],
  "created" : 1566995575235
}
```
**_id** - id of suite execution   
**suiteID** - id of test suite   
**requests** - array of single test executions   
**history** - entries of history  
**created** - creation date (in miliseconds)

Then you can get every test execution details based on endpoint described [here](singletest.md)

## Rerun test execution
Every suite execution can be rerun    
Endpoint: ``POST /kabassu/suite/rerun``   

Request
```
{
  "suiterunId": "5d6674778ba8ca129804b437"
}
```
**suiterunId** - id of suite execution to rerun 


