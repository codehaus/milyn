About This Example:
===================
    This example illustrates how to perform XSLT and StringTemplate templating
    using Smooks.

    The example takes an incoming Shipping request and transforms it using an
    XSLT Templatelet.  This is a straightforward operation.

    The more tasks relate to the resonse transformation, where the tracking
    history records need to be broken up.  These records are all supplied in
    a single Text node, separated by newlines (see "response-message.xml").
    To perform this part of the transform we use the Javabean Cartridge
    (see the DataDecoder for the tracking number history).  To perform the
    templating on the response, we chose StringTemplates.

    See:
        1. The "request" and "response" messages in message in
           request-message.xml and response-message.xml.
        2. The code in the src/main/java folder tree (.java, .st).
           This contains the bea classes, as well as the StringTemplate
           templates.
        3. The "request" and "response" transformations configs in
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