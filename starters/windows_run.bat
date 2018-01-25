java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -Dlogback.configurationFile=logback.xml -cp "libs/*" io.kabassu.manager.LogbackLauncher -conf kabassu-config.json
