package org.milyn.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.milyn.assertion.AssertArgument;

public class ExternalTransactionManager implements TransactionManager {

    private Connection connection;

    private boolean isSetAutoCommitAllowed;

	private boolean autoCommit;

    /**
     * @param connection
     */
    public ExternalTransactionManager(Connection connection, boolean autoCommit, boolean isSetAutoCommitAllowed) {
    	AssertArgument.isNotNull(connection, "connection");

        this.connection = connection;
        this.autoCommit = autoCommit;
        this.isSetAutoCommitAllowed = isSetAutoCommitAllowed;
    }

    @Override
    public void begin() {
    	if(isSetAutoCommitAllowed) {
	    	try {
				if(connection.getAutoCommit() != autoCommit) {
					connection.setAutoCommit(autoCommit);
				}
			} catch (SQLException e) {
				throw new TransactionException("Exception while setting the autoCommit flag of the connection");
			}
    	}
    }

	@Override
	public void commit() {
	}

	@Override
	public void rollback() {
	}

}
