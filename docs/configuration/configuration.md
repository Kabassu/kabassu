# Configuration

Configuration involves changing files placed in _configuration/modules_ directory in your Kabassu installation.
Not every file has to be changed, in fact if file is not described here the the best approach is to not touch it. Uncontrolled changes can break Kabassu  

**Also please change only described parameters and make sure that configuration file is still valid json file after these changes**

Main configuration file: _kabassu-config.json_
**setupMode** - it allow to set required parameters in database for the first run of kabassu. After first run it should be set to **false**

In _modules_ directory:

1. [io.kabassu.setup.json](setup.md) - setup configuration. Used for setting start parameters in database. Usually deleted after first start.
2. [io.kabassu.server.json](server.md) - server configuration. Has to be changed only if you want to change default port  
3. [io.kabassu.runner.gradle.json](runner.gradle.md) - runner responsible for running all test that use _gradle_ here JVM are set  
**Need to be changed before start**   
4. [io.kabassu.results.retriever.main.json](retriever.main.md) - configuration for retrieving reports.
5. [io.kabassu.mongo.json](mongo.md) - configuration of Mongo connection  
**Need to be changed before start**   
6. [io.kabassu.results.server.json](results.server.md) - configuration of server that will provide access to download reports in html format   
**Has to be changed if directory in _io.kabassu.results.retriever.main.json_ was changed**
7. [io.kabassu.files.retriever.json](files.retriever.md) - configuration for getting test files from other places then local filesystem 
8. [io.kabassu.config.options.json](options.md) - configuration for options available as parameters - **SHOULD BE CHANGED ONLY IF CUSTOM MODULES ARE ADDED AND THEI OPTIONS SHOULD BE VISIBLE TO KABASSU GUI**