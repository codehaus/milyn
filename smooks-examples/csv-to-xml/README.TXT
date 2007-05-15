About This Example:
===================
    This is a very simple example that illustrates how Smooks to
    configure in a non-XML stream parser (CSV) into Smooks.

    This capability enables Smooks to be used for processing any
    hierarchical data, and not just XML.

    The EDI-to-XML and EDI-to-Java examples also illustrate this
    capability, but for an EDI message stream. 

    See:
        1. The input message in input-message.csv.
        2. smooks-config.xml.

How to Run?
===========
    Requirements:
        1. JDK 1.5
        2. Maven 2.x (http://maven.apache.org/download.html)

    Running:
        1. "mvn clean install"
        2. "mvn exec:java"