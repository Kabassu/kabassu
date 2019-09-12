```json
{
(...)
    "mongo-config": {
      "db_name": "kabassu-dev"
    }
  }
}
```
**mongo-config** - here can be placed json entry that will configure connection to MongoDB. It is described here:  [https://vertx.io/docs/vertx-mongo-client/java/#_configuring_the_client]  

If you have setup Mongo on your local machine and haven't change port then only parameter to set and change is  
**db_name** - name of database for Kabassu

