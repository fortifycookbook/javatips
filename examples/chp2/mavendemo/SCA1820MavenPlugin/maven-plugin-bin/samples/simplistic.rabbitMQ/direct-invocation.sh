#!/bin/sh
mvn clean
mvn com.fortify.sca.plugins.maven:sca-maven-plugin:18.20:clean
mvn package com.fortify.sca.plugins.maven:sca-maven-plugin:18.20:translate
mvn com.fortify.sca.plugins.maven:sca-maven-plugin:18.20:scan
