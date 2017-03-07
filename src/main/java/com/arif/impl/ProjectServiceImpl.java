package com.arif.impl;

import static com.mongodb.client.model.Filters.eq;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.codehaus.jackson.map.ObjectMapper;

import com.arif.constants.Constants;
import com.arif.exception.ScrumBoardException;
import com.arif.interfaces.ProjectService;
import com.arif.model.Project;
import com.mongodb.Block;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * contains all the logic related to project
 * 
 * @author arifakrammohammed
 *
 */
public class ProjectServiceImpl implements ProjectService {

	final static Logger LOGGER = Logger.getLogger(ProjectServiceImpl.class);
	MongoDatabase database;

	public ProjectServiceImpl(MongoDatabase database) {
		super();
		this.database = database;
	}

	@Override
	public void addProject(Project project) {
		// get collection
		MongoCollection<Document> projectsCollection = database.getCollection(Constants.PROJECTS.getValue());
		// create document
		Document document = new Document();
		document.append(Constants.PROJECTNAME.getValue(), project.getProjectName().trim());
		document.append(Constants.PROJECTID.getValue(), null);
		document.append(Constants.DATECREATED.getValue(), project.getDateCreated());
		document.append(Constants.PROJECTSTATUS.getValue(), project.getProjectStatus());
		// save document
		projectsCollection.insertOne(document);
	}

	@Override
	public void validateInput(String projectName) throws ScrumBoardException {
		// anti XSS vulnerability
		Pattern pattern = Pattern.compile(Constants.RESTRICT.getValue());
		Matcher matcher = pattern.matcher(projectName);
		while (matcher.find()) {
			throw new ScrumBoardException("Invalid input");
		}
	}

	@Override
	public List<Project> getAllProjects() {
		List<Project> projectList = new ArrayList<>();
		// get collection
		MongoCollection<Document> projectsCollection = database.getCollection(Constants.PROJECTS.getValue());
		// processed retrieved data
		Block<Document> processRetreivedData = (document) -> {

			String retrivedDataAsJSON = document.toJson();
			Project project;
			try {
				project = new ObjectMapper().readValue(retrivedDataAsJSON, Project.class);
				projectList.add(project);
			} catch (IOException e) {
				LOGGER.error("Error while processing retrieved projects ", e);
			}

		};
		// query
		projectsCollection.find().forEach(processRetreivedData);
		LOGGER.info("All projects fetched");
		return projectList;
	}

	@Override
	public boolean isProjectExists(String projectName) {
		// get collection
		MongoCollection<Document> projectsCollection = database.getCollection(Constants.PROJECTS.getValue());
		// query
		return projectsCollection.count(eq(Constants.PROJECTNAME.getValue(), projectName.trim())) != 0 ? true : false;
	}

	@Override
	public boolean isProjectActive(String projectName) {
		boolean isProjectActive = false;
		List<Project> projectList = new ArrayList<>(1);
		// get collection
		MongoCollection<Document> projectsCollection = database.getCollection(Constants.PROJECTS.getValue());
		// processed retrieved data
		Block<Document> processRetreivedData = (document) -> {

			String retrivedDataAsJSON = document.toJson();
			Project project;
			try {
				project = new ObjectMapper().readValue(retrivedDataAsJSON, Project.class);
				projectList.add(project);
			} catch (IOException e) {
				LOGGER.error("Error while processing retrieved project ", e);
			}

		};
		// query
		projectsCollection.find(eq(Constants.PROJECTNAME.getValue(), projectName.trim())).forEach(processRetreivedData);
		if(!projectList.isEmpty() && projectList.get(0).getProjectStatus().equals(Constants.ACTIVE.getValue())) {
			isProjectActive = true;
		}
		return isProjectActive;
	}
}
