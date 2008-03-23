About This Example:
===================
    This example illustrates how Smooks can be used to transform and then
	route(append) the output to a file.

    See:
        1. The "Main" class in src/main/java/example/Main.java.

How to Run?
===========
    Requirements:
        1. JDK 1.5
        2. Maven 2.x (http://maven.apache.org/download.html)

    Running:
        1. "mvn clean install"
        2. "mvn exec:java"

	Monitoring (optional):
		1. export MAVEN_OPTS=-Dcom.sun.management.jmxremote
		2. "jconsole" 

	Generate larger input file (optional)
		1. mvn exec:java -Dexec.mainClass="InputOrderGenerator" -Dexec.args="<filename> <nrOfOrderItems>"
		   Example:
			mvn exec:java -Dexec.mainClass="InputOrderGenerator" -Dexec.args="input-message.xml 10000"
		Actually the above does not work. To create a larger input-message.xml uncomment the pom.xml 
		and set the number of order-items in the arguments section.


