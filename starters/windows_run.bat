java -javaagent:libs/kabassu-manager-0.0.1-SNAPSHOT.jar -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -Dlogback.configurationFile=logback.xml -cp "libs/*" io.kabassu.manager.LogbackLauncher -conf kabassu-config.json
