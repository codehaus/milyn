package org.milyn.ejc;

import org.apache.commons.logging.Log;

/**
 * A simple logger for writing status inside EJC.
 */
public class EJCLogFactory {

    private static EJCLog log;

    public static Log getLog(Class clazz) {
        if (log == null) {
            log = new EJCLog(Level.INFO);
        }
        return log;
    }

    public void setLevel(Level level) {
        ((EJCLog)getLog(getClass())).setLevel(level);
    }

    public enum Level {
        DEBUG(0),
        INFO(1),
        WARN(2),
        ERROR(3),
        FATAL(4);

        private int value;

        Level(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }
}
