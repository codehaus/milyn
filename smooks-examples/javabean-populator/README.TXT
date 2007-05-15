About This Example:
===================
    This example illustrates how Smooks can be used to extract data from an XML
    message, and use the data to populate an Object graph.  It also shows how this
    Object graph is then accessible outside Smooks via the ExecutionContext.

    In reality, the Javabeans feature can be used as is (ala JAXB, XStream etc), or can be
    used to feed data into other fragment transforms e.g. Templating, Scripting or Java
    Transforms.

    See:
        1. The input message in input-message.xml.
        2. The code in the src/main/java folder tree (.java).
        3. smooks-config.xml.

How to Run?
===========
    Requirements:
        1. JDK 1.5
        2. Maven 2.x (http://maven.apache.org/download.html)

    Running:
        1. "mvn clean install"
        2. "mvn exec:java"
