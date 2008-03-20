/*
	Milyn - Copyright (C) 2006

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU Lesser General Public
	License (version 2.1) as published by the Free Software
	Foundation.

	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

	See the GNU Lesser General Public License for more details:
	http://www.gnu.org/licenses/lgpl.txt
*/
package example;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import javax.xml.transform.stream.StreamResult;

import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.java.JavaSource;
import org.milyn.event.report.HtmlReportGenerator;
import org.milyn.javabean.BeanAccessor;
import org.milyn.routing.file.FileListAccessor;
import org.xml.sax.SAXException;

import example.model.Order;

/**
 * This is a simple example that demonstrates how Smooks can be 
 * configured to "route" the output of a transform to file(s).
 * 
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>
 *
 */
public class Main
{
	private static final String LINE_SEP = System.getProperty( "line.separator" );
	
    protected void runSmooksTransform( final Object order) throws IOException, SAXException, ClassNotFoundException
    {
        final Smooks smooks = new Smooks("smooks-config.xml");
        final ExecutionContext executionContext = smooks.createExecutionContext();
        
        // 	manually inserting the Order bean into the execution context
        BeanAccessor.addBean( executionContext, "order", order );

        //	generate a smooks-report.html
    	Writer reportWriter = new FileWriter("smooks-report.html");
    	executionContext.setEventListener(new HtmlReportGenerator(reportWriter));
    	
    	//	perform the transform
        final StringWriter writer = new StringWriter();
        smooks.filter(new JavaSource(order), new StreamResult(writer), executionContext);
        
        //	display the output from the transform
        System.out.println( LINE_SEP );
        System.out.println( "List file : [" + FileListAccessor.getFileName( executionContext ) + "]" );
        List<String> fileNames = (List<String>) FileListAccessor.getFileList( executionContext );
        for (String fileName : fileNames)
		{
            System.out.println( LINE_SEP );
            System.out.println( "fileName : [" + fileName + "] content:" );
            System.out.println( new ObjectInputStream( new FileInputStream( fileName ) ).readObject() );
            System.out.println();
			
		}
    }

    public static void main(String[] args) throws IOException, SAXException, SmooksException, InterruptedException, ClassNotFoundException
    {
        final Main smooksMain = new Main();
        final Order order = new Order();

        pause("Press 'enter' to display the input Order Object...");
        System.out.println( LINE_SEP );
        System.out.println( order );
        System.out.println( LINE_SEP );
        System.out.println("This needs to be transformed and appended to a file");
        pause("Press 'enter' to display the transformed message...");
        smooksMain.runSmooksTransform(order);
        System.out.println( LINE_SEP );
        pause("That's it ");
    }

    private static void pause(String message) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("> " + message);
            in.readLine();
        } 
        catch (IOException e) 
        {
        	e.printStackTrace();
        }
        System.out.println( LINE_SEP );
    }

}