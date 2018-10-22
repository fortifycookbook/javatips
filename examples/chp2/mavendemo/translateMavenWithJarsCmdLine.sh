
  mvn clean
  mvn dependency:copy-dependencies -DoutputDirectory=`pwd`/tmpjars
  sourceanalyzer -b mavenDemoWithJars -clean
  sourceanalyzer -b mavenDemoWithJars -extdirs `pwd`/tmpjars  `pwd`/src
  sourceanalyzer -b mavenDemoWithJars -show-files
