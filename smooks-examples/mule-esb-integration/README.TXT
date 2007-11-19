About This Example:
===================
    This is a very basic example that illustrates how Smooks can be
    run within the Mule Enterprise Service Bus.

	[Work in progress]

	Preparation:
	1. Mule-1.4.3 is installed (http://mule.mulesource.org/display/MULE/Download)
	2. The following enironment variables are set:
		export MULE_HOME=<path_to_mule_1.4.3>
		export PATH=$MULE_HOME/bin:$PATH
		export MULE_LIB=$MULE_HOME/lib/user
	3. The following Smooks jar must be copied to $MULE_HOME/lib/user
		*  milyn-commons-1.0-SNAPSHOT.jar
		*  milyn-smooks-core-1.0-SNAPSHOT.jar
		*  milyn-smooks-javabean-1.0-SNAPSHOT.jar
		*  milyn-smooks-misc-1.0-SNAPSHOT.jar
		*  milyn-smooks-templating-1.0-SNAPSHOT.jar

    See:
        1. The "SmooksTransformer" class in src/main/java/org/milyn/smooks/mule/
        2. The mule-config.xml in src/main/resources

How to Run?
===========
    Requirements:
        1. JDK 1.5
        2. Maven 2.x (http://maven.apache.org/download.html)

    Running:
        1. "mvn clean install"
		2. cp target/milyn-smooks-example-mule-esb-integration-1.0.jar $MULE_HOME/lib/user
		3. "mule -config src/main/resources/mule-config.xml"
		4. Past the following a the prompt to as the input to the Mule service:
		   <a><b><c><b></b></c></b></a>
