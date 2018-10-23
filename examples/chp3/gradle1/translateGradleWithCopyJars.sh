#!/bin/sh -x
  cp build.gradle gradle.build.orig
  cp build.gradle.copyJars build.gradle
  more build.gradle
  gradle ScaCopyDependencies
  sourceanalyzer -b g1 -clean
  sourceanalyzer -b g1 -extdirs `pwd`/scadependencies src
  cp gradle.build.orig build.gradle
