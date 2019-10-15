#!/bin/bash +vx
LIB_PATH=$"/java/workspace/sourcecode/lib/libthrift-0.10.0.jar:/java/workspace/sourcecode/lib/slf4j-log4j12-1.7.12.jar:/java/workspace/sourcecode/lib/slf4j-api-1.7.12.jar:/java/workspace/sourcecode/lib/log4j-1.2.17.jar"
#simple/secure ip port
java -classpath bin/client_classes:$LIB_PATH Client $1 $2
