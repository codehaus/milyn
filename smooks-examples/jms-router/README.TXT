About This Example:
===================
    This example illustrates how Smooks can be used to transform and then
	route the output to a JMS destination.

    See:
        1. The "Main" class in src/main/java/example/Main.java.

How to Run?
===========
    Requirements:
        1. JDK 1.5
        2. Maven 2.x (http://maven.apache.org/download.html)

     Running:
        1.  In the "jms-router" directory, run "mvn clean install".
        2.  Open a new console window ("Window 1")
        3.  In "Window 1", change directory into the "jms-provider" directory.
        4.  In "Window 1", run "mvn activemq:run"
        5.  Open a new console window ("Window 2")
        6.  In "Window 2", change directory into the "jms-consumer" directory.
        7.  In "Window 2", run "mvn exec:java".
        8.  Open a new console window ("Window 3")
        9.  In "Window 3", change directory into the "splitter-router" directory.
        10. Arrange "Window 1", "Window 2" and "Window 3" such that the contents of all
            of them are visible at the same time.
        11. In "Window 3", run "mvn exec:java". 

