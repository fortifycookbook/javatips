This is a sample for sca-maven-plugin. You can use it to verify that the plugin functions correctly.

<< System Operating Environment >>
	sca-maven-plugin supports Maven 3.0.5 and 3.3.x.

<< System Requirement >>
	Maven is designed to download dependencies and it's triggered by a project declaring a dependency that is not present in the local repository.
	Maven needs to access the Maven central repository or other mirrors.

<<Prerequisite>>
	This sample requires that you have both Fortify SCA and sca-maven-plugin installed.
	To verify the installation, run the following commands in the current directory:
	
	$>sourceanalyzer -version
	
	$>mvn com.fortify.sca.plugins.maven:sca-maven-plugin:18.20:help

<<Usage>>
	To invoke sca-maven-plugin directly, see the sample: direct-invocation.bat (or direct-invocation.sh).
	
	To run sca-maven-plugin as a part of your build process, see the sample: association.bat (or association.sh) and the pom.xml.

<<Result>>
	After running the Maven command, the results are saved in the following directory:
	
	target/fortify
