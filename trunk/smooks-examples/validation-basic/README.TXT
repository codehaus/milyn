About This Example:
===================
    This is a very basic example that illustrates how Smooks can be
    used to validate xml message fragments. 

    What this example illustrates is:
        1.  A validation of an xml representing an order format.
        2.  How the different options for validation are used:
                1. Directly specifying a regular expression in the configuration.
                2. Using a alias for the regular expression that exist in regex-default.properties.
                3. Using a alias for the regular expression that exist in user defined properties file my-regex.properties.
        4.  How errors can be reported:
                1. Immedietely by setting 'failFast' to true and exception will be thrown.
                2. After all resources have be evaluated but without an exception being thrown. This is done be setting 
                   'failFast' to false and retrieving the errors using ValidationFailures.getAll(executionContext). See Main for an example.

    See:
        1.  The "Main" class in src/main/java/example/Main.java.
        2.  The input message in input-message.xml.
        3.  The code in the src/main/java folder tree (.java).
        4.  smooks-config.xml.

How to Run?
===========
    Requirements:
        1.  JDK 1.5
        2.  Maven 2.x (http://maven.apache.org/download.html)

    Running:
        1.  "mvn clean install"
        2.  "mvn exec:java"
