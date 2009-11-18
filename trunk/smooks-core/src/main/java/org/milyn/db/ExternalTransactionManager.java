package org.milyn.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.milyn.assertion.AssertArgument;

public class ExternalTransactionManager extends TransactionManager {

    private Connection connection;

    private boolean isSetAutoCommitAllowed;

    /**
     * @param connection
     */
    public ExternalTransactionManager(Connection connection, boolean isSetAutoCommitAllowed) {
    	AssertArgument.isNotNull(connection, "connection");

        this.connection = connection;
        this.isSetAutoCommitAllowed = isSetAutoCommitAllowed;
    }

	@Override
	public boolean getAutoCommit() {
		if(isSetAutoCommitAllowed) {
			try {
				return connection.getAutoCommit();
			} catch (SQLException e) {
				throw new TransactionException("Exception while getting the autoCommit on the connection");
			}
		}
		return false;
	}

    @Override
    public void setAutoCommit(boolean autoCommit) {
    	if(isSetAutoCommitAllowed) {
    		try {
	    		connection.setAutoCommit(autoCommit);
	    	} catch (SQLException e) {
				throw new TransactionException("Exception while setting the 'autoCommit' flag on the connection.", e);
			}
    	}
    }

}
