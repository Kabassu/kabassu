# AET Test Guide


Runner for [AET](https://github.com/Cognifide/aet) tests is _aet_
Required report type is also _aet_

AET Tests require very specific additional parameters for definition:

**server** - ip of AET server (without http)
**port** - optional if port is different than 8181
**suite** - name AET suite to run

Of course parameters for location are also required

Execution additional parameters are required if you want to override suite values:

**domain** - overrides domain specified in the suite xml file
**pattern** - id of suite that will be used as patterns source
**name** overrides the name parameter value from the sute xml file