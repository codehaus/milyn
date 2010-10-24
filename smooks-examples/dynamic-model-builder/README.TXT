About This Example:
===================
    This example illustrates how to use the ModelBuilder class
    to read and write an XML message type that contains multiple schema (xsd)
    namespaces and/or multiple evolving schema versions.

    This example defines a model for reading/writing a selection of the Smooks
    configuration namespaces (CSV and Javabean).  It supports multiple
    versions of each of these namespaces, binding their data into a single object
    model and maintaining namespace integrity when writing back out the models.

    See:
        1.  The Main class which executes the ModelBuilder.
        2.  The "smooks-config.xml" file is the input file read by the ModelBuilder.

How to Run?
===========
    Requirements:
        1. JDK 1.5+
        2. Maven 2.x (http://maven.apache.org/download.html)

    Running:
        1. "mvn clean install"
        2. "mvn exec:java"