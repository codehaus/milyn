About This Example:
===================
    This is a very basic example that illustrates how Smooks can be
    used to apply an XSLT based transform on a message fragments that
	use namespaces. 

    What this example illustrates is a transformation of an xml representing an
	order format an external system to a different xml format:
	Order.xml -> CanonicalOrderFormat.xml

	The Order.xml uses a namespace and the smooks-config shows how to select 
	values with namespaces.
	I also shows how to exclude the orders namespace form the target namespace.

    See:
        1. The "Main" class in src/main/java/example/Main.java.
        2. The input message in input-message.xml.
        3. The code in the src/main/java folder tree (.java, .xsl).
        4. smooks-config.xml.

How to Run?
===========
    Requirements:
        1. JDK 1.5
        2. Maven 2.x (http://maven.apache.org/download.html)

    Running:
        1. "mvn clean install"
        2. "mvn exec:java"
