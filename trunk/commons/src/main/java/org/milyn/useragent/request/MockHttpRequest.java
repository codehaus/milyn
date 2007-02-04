package org.milyn.useragent.request;

import java.util.Hashtable;

/**
 * Mock object for a Http request.
 * @author Tom Fennelly
 */

public class MockHttpRequest implements HttpRequest {
    Hashtable headers = new Hashtable();
    Hashtable params = new Hashtable();

    public void setHeader(String header, String value) {
        headers.put(header, value);
    }

    public void setParameter(String parameter, String value) {
        params.put(parameter, value);
    }

    public String getHeader(String name) {
        return (String)headers.get(name);
    }

    public String getParameter(String name) {
        return (String)params.get(name);
    }

    public void reset() {
        headers.clear();
        params.clear();
    }
}
