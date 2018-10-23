#!/bin/sh -x

MYDEMOHOME=`pwd`

echo "Chapter #1"
echo "-------------------------"
echo "Command Line Example"
echo "-------------------------"
echo " "

cd examples/chp1

./translateWithOutClassPathError.sh

echo "-------------------------"
echo " "
echo "NOTE: Java code that has dependency jars must include the classpath to all java jar files "
echo "NOTE: The next example adds the -cp classpath argument to point to the jars."

echo "-------------------------"
echo " "
./translateWithClassPath.sh
echo "-------------------------"
echo " "
echo "NOTE: Java code no error this time"
echo "-------------------------"
echo " "

cd $MYDEMOHOME

echo "Chapter #2 - MAVEN"
echo "-------------------------"
echo "Maven Example"
echo "-------------------------"
echo " "
cd examples/chp2/mavendemo

echo "-------------------------"
echo " "
./translateMavenWithOutJarsCmdLine.sh

echo "NOTE: Java code that has dependency jars must include the classpath to all java jar files "
echo "NOTE: The next example adds uses the mvn get depenency option to copy jars and then sets the classpath"
echo "-------------------------"
echo " "
./translateMavenWithJarsCmdLine.sh

cd $MYDEMOHOME


echo "Chapter #3 GRADLE"
echo "-------------------------"
echo "Gradle Example"
echo "-------------------------"

cd examples/chp3/gradle1

./translateGradleMissingJars.sh
echo "NOTE: Java code that has dependency jars must include the classpath to all java jar files "
echo "NOTE: Gradle has a bug on that does not pickup jar directories."
echo "NOTE: The next example shows a workaround for the gradle issue by adding a copy Jars tasks"
echo "-------------------------"

./translateGradleWithCopyJars.sh

