About This Example:
===================
    This example illustrates how to perform XSLT and FreeMarker templating
    using Smooks.

    The example takes an incoming Shipping request and transforms it using an
    XSLT Templatelet.  This is a straightforward operation.

    The more interesting tasks relate to the resonse transformation, where the tracking
    history records need to be broken up.  These records are all supplied in
    a single Text node.  The records are separated by newlines (see "response-message.xml").
    To execute this part of the transform, we perform a model driven transform in which we use
    the Javabean Cartridge to create the model from the input message and then use a FreeMarker
    template to convert that model into the required output message.

    See:
        1. The "Main" class in src/main/java/example/Main.java.
        2. The "request" and "response" messages in request-message.xml and response-message.xml.
        3. The model code in the "example.model" package in the src/main/java folder.
        4. The FreeMarker template in the "example.templates" package in the src/main/java folder.
        5. The "request" and "response" Smooks transformation configs in
           smooks-request-config.xml and smooks-response-config.xml.

How to Run?
===========
    Requirements:
        1. JDK 1.5
        2. Maven 2.x (http://maven.apache.org/download.html)

    Running:
        1. "mvn clean install"
        2. "mvn exec:java"
        3. Modify pom.xml to switch XSLT Processors.
        4. Re-run "mvn exec:java".