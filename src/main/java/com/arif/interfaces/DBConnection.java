package com.arif.interfaces;

import com.arif.database.DBConnecton;
import com.mongodb.client.MongoDatabase;

/**
 * Return MongoDB connection
 * 
 * @author arifakrammohammed
 *
 */
public interface DBConnection {

	/**
	 * Get a connection with database; MongoDB internally handles Connection
	 * Pooling
	 * 
	 * @return a <i>MongoDatabase</i> to interact with
	 */
	default MongoDatabase getDBConnection() {
		return DBConnecton.getConnection();
	}

}
