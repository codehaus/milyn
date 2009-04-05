package org.milyn.ejc;

/**
 * IllegalNameException is used when a {@link org.milyn.ejc.classes.JClass} or
 * {@link org.milyn.ejc.classes.JAttribute} is given a name matching a reserved
 * keyword in java.
 *
 * @author bardl
 */
public class IllegalNameException extends Exception {

    public IllegalNameException() {
        super();
    }

    public IllegalNameException(String message) {
        super(message);
    }

    public IllegalNameException(Throwable cause) {
        super(cause);
    }

    public IllegalNameException(String message, Throwable cause) {
        super(message, cause);
    }
}
