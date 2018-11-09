cmd /C mvn clean
cmd /C mvn com.fortify.sca.plugins.maven:sca-maven-plugin:18.20:clean
cmd /C mvn package com.fortify.sca.plugins.maven:sca-maven-plugin:18.20:translate
cmd /C mvn com.fortify.sca.plugins.maven:sca-maven-plugin:18.20:scan
