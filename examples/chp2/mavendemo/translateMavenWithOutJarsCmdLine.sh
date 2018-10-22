
  mvn clean
  sourceanalyzer -b mavenDemoWithOutJars -clean
  sourceanalyzer -b mavenDemoWithJars `pwd`/src
  sourceanalyzer -b mavenDemoWithJars -show-files
