#!/bin/bash

# define path of your JDK/JRE (must be 1.8 or older)
JAVA_HOME_PATH="/Library/Java/JavaVirtualMachines/jdk1.8*/Contents/Home"

# select level
LEVEL="world/test1_windengine.xml"
#LEVEL="world/test2_beach.xml"

$JAVA_HOME_PATH/bin/java -jar CompactCarRace.jar $LEVEL
