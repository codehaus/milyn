package org.milyn.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.milyn.assertion.AssertArgument;


class JdbcTransactionManager extends TransactionManager {

	private Connection connection;

	public JdbcTransactionManager(Connection connection) {
		AssertArgument.isNotNull(connection, "connection");

		this.connection = connection;
	}

	/*
	 * (non-Javadoc)
	 * @see org.milyn.db.TransactionManager#commit()
	 */
	public void commit() {
		try {
			connection.commit();
		} catch (SQLException e) {
			throw new TransactionException("Exception while committing the transaction");
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.milyn.db.TransactionManager#rollback()
	 */
	public void rollback() {
		try {
			connection.rollback();
		} catch (SQLException e) {
			throw new TransactionException("Exception while rolling back the transaction");
		}
	}

	@Override
	public boolean getAutoCommit() {
		try {
			return connection.getAutoCommit();
		} catch (SQLException e) {
			throw new TransactionException("Exception while getting the autoCommit on the connection");
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.milyn.db.TransactionManager#setAutoCommit(boolean)
	 */
	public void setAutoCommit(boolean autoCommit) {
		try {
			connection.setAutoCommit(autoCommit);
		} catch (SQLException e) {
			throw new TransactionException("Exception while setting the autoCommint on the connection");
		}
	}

}
