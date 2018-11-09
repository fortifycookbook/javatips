cmd /C mvn install:install-file -Dfile=pom.xml -DpomFile=pom.xml
cmd /C mvn install:install-file -Dfile=xcodebuild/pom.xml -DpomFile=xcodebuild/pom.xml
cmd /C mvn install:install-file -Dfile=sca-maven-plugin/sca-maven-plugin-18.20.jar -DpomFile=sca-maven-plugin/pom.xml
