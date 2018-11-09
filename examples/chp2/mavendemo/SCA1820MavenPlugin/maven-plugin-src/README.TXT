================================================================
          Software Name: sca-maven-plugin - ver. 18.20
================================================================

[ Software Name ]         sca-maven-plugin
[ Version ]               18.20
[ Organization ]          Fortify
[ Organization URL ]      https://software.microfocus.com/software/enterprise-security
[ Build Environment ]     JDK 1.8.0, Maven 3.0.5
[ Operation Environment ] Same as Fortify SCA 18.20
[ Last Modified ]         2018-01-08

----------------------------------------------------------------

<< System Operating Environment >>
   sca-maven-plugin supports Maven 3.0.5 and 3.5.x.

<< System Requirement >>
   Maven is designed to download plugins and it's triggered by a project declaring a dependency that is not present in the local repository.
   Therefore, Maven needs to access Maven central repository or other mirrors.

<< Installation >>
   There are two versions of the SCA plugin: a binary version and a source version.

   To install the binary version from a command prompt or terminal, navigate to the root directory, then run:

   $mvn install:install-file -Dfile=pom.xml -DpomFile=pom.xml
   $mvn install:install-file -Dfile=xcodebuild/pom.xml -DpomFile=xcodebuild/pom.xml
   $mvn install:install-file -Dfile=sca-maven-plugin/sca-maven-plugin-18.20.jar -DpomFile=sca-maven-plugin/pom.xml

   Install script: install.bat and install.sh are available.

   To install the source version from a command prompt or terminal, navigate to the root directory, then run:

   $mvn clean install

<< Uninstallation >>
   To unintall the package from the local repository, manually delete sca-maven-plugin from the local repository.

<< Usage >>
   Fortify provides two ways to run the sca-maven-plugin.

   * Use as a Maven plugin:
   $mvn com.fortify.sca.plugins.maven:sca-maven-plugin:<version>:translate

   For more detail, see the included documentation.

   * Use the Maven Integration feature which is similar to Ant Integration:
   $sourceanalyzer -b <build-id> [sca build options] mvn com.fortify.sca.plugins.maven:sca-maven-plugin:<version>:translate

   For more details, see the Fortify Static Code Analyzer User Guide.

<< Documentation >>
   When you install the binary version, the documentation is in the docs directory.

   When you install the source version, the documentation is located in the sca-maven-plugin/target/site directory.

<< Xcode Maven Project >>
   sca-maven-plugin supports the following xcode-maven-plugins.

   - ca.mestevens.ios:xcode-maven-plugin:0.9.3
   - com.sap.prd.mobile.ios.mios:xcode-maven-plugin:1.14.1
   - de.felixschulze.maven.plugins.xcode:xcode-maven-plugin:2.0

   An Xcode Maven project must be successfully compiled with one of the plugins above. SCA only supports Objective-C iOS projects.