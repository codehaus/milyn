package org.milyn.delivery.lifecyclecleanup;

import org.milyn.SmooksException;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.ExecutionLifecycleInitializable;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXVisitBefore;

import java.io.IOException;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class InitializeOnlyVisitor implements ExecutionLifecycleInitializable {

    public static boolean initialized;    

    public void executeExecutionLifecycleInitialize(ExecutionContext executionContext) {
        initialized = true;
    }

    public void visitBefore(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
