package com.arif.listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.arif.database.DBConnecton;
import com.arif.util.ResourceReader;
import com.mongodb.MongoClient;

/**
 * A listerner class that establishes connection when WAR is deployed, and
 * closes connection before server is stopped
 * 
 * @author arifakrammohammed
 *
 */
public class ScrumBoardContextListener implements ServletContextListener {

	MongoClient mongoClient = null;

	@Override
	public void contextInitialized(ServletContextEvent event) {
		// Read project properties file
		ResourceReader resouceReader = new ResourceReader();
		resouceReader.readProperties();

		// open MongoDB connection
		mongoClient = DBConnecton.establishConnection();
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		// close MongoDB connection
		mongoClient.close();
	}

}
