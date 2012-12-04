# File: build.sh - script to build the InkML java library (inkmllibj.jar file)
# Author: Muthu, 02-July-2008

echo "================ Check for JAVA_HOME ================"
if [ ! -e $JAVA_HOME/bin/javac ]
then
	echo "================ Environment setup error ================"
	echo "Install JDK 1.5 or higher version and set JAVA_HOME environment variable"
	exit
fi

# check if the bin directory exist
if [ ! -e ../bin ]
then
	mkdir ../bin
fi

if [ ! -e ../classes ]
then
    mkdir ../classes
fi

# build the library file from source code

$JAVA_HOME/bin/javac -d ../classes -classpath $JAVA_HOME/lib/tools.jar ../src/com/hp/hpl/inkml/*.java ../src/com/hp/hpl/inkml/test/*.java


$JAVA_HOME/bin/jar cvfm ../bin/inkmllibj.jar Manifest.mf -C ../classes com

if [ -e ../classes ]
then
    rm -rf ../classes
fi

echo "Note: If successfully compiled. The library file, inkmllibj.jar saved in ../bin"
