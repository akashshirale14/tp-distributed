LIB_PATH=/Users/akashshirale/Desktop/Assignment_2/cs457-557-f19-pa2-akashshirale14/libthrift-0.12.0.jar:/Users/akashshirale/Desktop/Assignment_2/cs457-557-f19-pa2-akashshirale14/slf4j-log4j12-1.7.12.jar:/Users/akashshirale/Desktop/Assignment_2/cs457-557-f19-pa2-akashshirale14/slf4j-api-1.7.12.jar
all: clean
	mkdir bin
	mkdir bin/client_classes
	 mkdir bin/server_classes
	#javac -classpath $(LIB_PATH) -d bin/client_classes/ Client.java FileStoreHandler.java gen-java/*
	javac -classpath $(LIB_PATH) -d bin/server_classes/ JavaServer.java OperationHandler.java  genjava/*

clean:
	rm -rf bin/

