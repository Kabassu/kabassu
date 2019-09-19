```json
{
    "name": "standard:io.kabassu.config.options",
    "config": {
        "verticle": "io.kabassu.config.options.KabassuConfigOptionsVerticle",
        "modulesDir": "./modules",
        "configurations" : [
          {
            "fileName": "io.kabbasu.testcontext.json",
            "options" : ["runners"]
          },
          {
            "fileName": "io.kabbasu.runner.gradle.json",
            "options" : ["jvm"]
          }
        ]
    }
}
```
**configurations** array of options
- **fileName** - in which file we can find parameters
- **options** - array of options from file (node _config_ and _._ is separator to create path in _json_)

