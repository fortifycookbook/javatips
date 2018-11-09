#!/bin/sh
mvn install:install-file -Dfile=pom.xml -DpomFile=pom.xml
mvn install:install-file -Dfile=xcodebuild/pom.xml -DpomFile=xcodebuild/pom.xml
mvn install:install-file -Dfile=sca-maven-plugin/sca-maven-plugin-18.20.jar -DpomFile=sca-maven-plugin/pom.xml