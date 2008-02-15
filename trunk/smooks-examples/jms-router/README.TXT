About This Example:
===================
    This example illustrates how Smooks can be used to transform and then
	route the output to a JMS destination.

	NOTE: This currenlty only runs from within eclipse. I'm trying to
	sort this out :( /Daniel 2008-02-15

    See:
        1. The "Main" class in src/main/java/example/Main.java.

How to Run?
===========
    Requirements:
        1. JDK 1.5
        2. Maven 2.x (http://maven.apache.org/download.html)
		3. To run from eclipse jboss-messaging-client.jar and jboss-remoting.jar
		   must be placed first on the classpath. Also on must make sure that
		   the application server is runnin the same version of jboss-messaging.
		4. Deploy the queue definition to the applications server.
			* JBoss : copy src/main/resources/jbm-queue-service.xml to the servers
			  deploy directory

    Running:
        1. "mvn clean install"
        2. "mvn exec:java"
