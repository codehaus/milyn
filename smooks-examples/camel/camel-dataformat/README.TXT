About This Example:
===================
 	This is a very simple example that illustrates the usage of SmooksDataFormat. A DataFormat
 	is a class that implements Camel's org.apache.camel.spi.DataFormat.

	The following features are demonstrated:
	   * using SmooksDataFormat in a Camel unmarshall call using Java DSL
	   * using SmooksDataFormat in a Camel unmarshall call using Spring DSL
	   
	See smooks-config.xml for inline comments.

    See:
        1. The "Main" class in src/main/java/example/Main.java.
        2. The input message in input-message.edi.
        3. smooks-config.xml.

How to Run?
===========
    Requirements:
        1. JDK 1.5
        2. Maven 2.x (http://maven.apache.org/download.html)

    Running:
        1. "mvn clean install"
        2. "mvn exec:java"
