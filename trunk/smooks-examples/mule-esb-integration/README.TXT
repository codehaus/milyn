About This Example:
===================
    This is a very basic example that illustrates how Smooks can be
    run within the Mule Enterprise Service Bus.

	Preparation:
	1. Mule-1.3 is installed (http://mule.mulesource.org/display/MULE/Download)
	2. The following enironment variables are set:
		MULE_HOME=<path_to_mule_1.3>
		PATH=$MULE_HOME/bin:$PATH
	3. The following Smooks jar must be copied to $MULE_HOME/lib/user
		*  milyn-commons-1.0-SNAPSHOT.jar
		*  milyn-smooks-core-1.0-SNAPSHOT.jar
		*  milyn-smooks-javabean-1.0-SNAPSHOT.jar
		*  milyn-smooks-misc-1.0-SNAPSHOT.jar
		*  milyn-smooks-templating-1.0-SNAPSHOT.jar
    See:
        1. The "Main" class in src/main/java/example
        2. The "SmooksMule" interface in src/main/java/example
        3. The input message in input-message.xml.
        4. The code in the src/main/java folder tree (.java, .xsl).
        5. smooks-config.xml.

How to Run?
===========
    Requirements:
        1. JDK 1.5
        2. Maven 2.x (http://maven.apache.org/download.html)

    Running:
        1. "mvn clean install"
		2. cp target/milyn-smooks-example-xslt-basic-1.0.jar $MULE_HOME/lib/user
		2. mule -config src/main/resources/mule-config.xml
