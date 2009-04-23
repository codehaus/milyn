About This Example:
===================
    This example illustrates the use of the Edifact Java Compiler (EJC). At first the EJC
    is used to compile an edi-configuration and create a bindingfile and the corresponding
    javaclasses. Next, the example compiles the generated javaclasses. At last, the example
    tests the generated bindning-file and the compiled javaclasses in Smooks.

    If you haven't already looked at the "javabean-populator" and "edi-to-xml" example, 
    you may be interested in looking at that before looking at this example.

    See:
        1. The "Main" class in src/main/java/example/Main.java.
        2. The input message in input-message.edi.
        3. edi-config.xml.
        4. The EdiSax parser configuration edi-to-xml-order-mapping.xml
        5. http://milyn.codehaus.org/EdiSax
        6. The generated Javabean classes in src/main/java/example/beans.

How to Run?
===========
    Requirements:
        1. JDK 1.5
        2. Maven 2.x (http://maven.apache.org/download.html)

    Running:
        1. "mvn clean install"
        2. "mvn exec:java"