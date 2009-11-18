package org.milyn.db;

/**
 * The transaction manager manages the transaction
 * of a data source
 * <p />
 *
 * This transaction manager does nothing and has a default level
 * because it can change in future versions of Smooks.
 *
 * @author <a href="mailto:maurice.zeijen@smies.com">maurice.zeijen@smies.com</a>
 *
 */
class TransactionManager {

	/**
	 * Begin the transaction
	 *
	 * @throws TransactionException If an exception got thrown while beginning the exception
	 */
    public void begin() {
    }

    /**
	 * Commit the transaction
	 *
	 * @throws TransactionException If an exception got thrown while committing the exception
	 */
    public void commit() {
    }

    /**
	 * Rollback the transaction
	 *
	 * @throws TransactionException If an exception got thrown while rollingback the exception
	 */
    public void rollback() {
    }

    /**
	 * returns the autoCommit flag of the connection
	 *
	 * @throws TransactionException If an exception got thrown while getting the autoCommit
	 */
    public boolean getAutoCommit() {
    	return false;
    }

    /**
	 * Set the autocommit of the connection
	 *
	 * @throws TransactionException If an exception got thrown while setting the autoCommit
	 */
    public void setAutoCommit(boolean autoCommit) {
    }

}
