About This Example:
===================
    This is a very basic example that illustrates how Smooks can be
    used to validate xml message fragments. 

    What this example illustrates is:
        1.  A validation of an xml representing an order format.
        2.  How the different options for validation are used:
                1. Using the Smooks regular expressions file.
                1. Using the a custom regulare expressions file.
        3.  How errors can be reported:
            Please refer to the options available for the onFail attribute:
            http://www.smooks.org/mediawiki/index.php?title=V1.2:Smooks_v1.2_User_Guide#onFail

    See:
        1.  The "Main" class in src/main/java/example/Main.java.
        2.  The input message in input-message.xml.
        3.  The code in the src/main/java folder tree (.java).
        4.  smooks-config.xml.
        5.  The src/main/resources/my-regexes.properites file.

How to Run?
===========
    Requirements:
        1.  JDK 1.5
        2.  Maven 2.x (http://maven.apache.org/download.html)

    Running:
        1.  "mvn clean install"
        2.  "mvn exec:java"
