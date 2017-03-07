package com.arif.impl;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.Document;
import org.codehaus.jackson.map.ObjectMapper;

import com.arif.constants.Constants;
import com.arif.exception.ScrumBoardException;
import com.arif.interfaces.AssociateService;
import com.arif.model.Associate;
import com.arif.model.Project;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.DBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.TextSearchOptions;

/**
 * Contains all the logic for handling services related to Associate
 * 
 * @author arifakrammohammed
 *
 */
public class AssociateServiceImpl implements AssociateService {

	MongoDatabase database;

	public AssociateServiceImpl(MongoDatabase database) {
		super();
		this.database = database;
	}

	@Override
	public void index(Associate associate) {
		// code to index associate details
	}

	@Override
	public boolean isAssociateExists(Associate associate) {
		// get collection
		MongoCollection<Document> associatesCollection = database.getCollection(Constants.ASSOCIATES.getValue());
		// query collection
		return associatesCollection.count(eq(Constants.ASSOCIATEID.getValue(), associate.getAssociateId().trim())) != 0
				? true : false;
	}

	@Override
	public void authorizeAssociate(Associate associate) {
		// get collection
		MongoCollection<Document> projectsCollection = database.getCollection(Constants.ASSOCIATES.getValue());
		// create document
		Document document = new Document();
		// add document properties
		document.put(Constants.ASSOCIATENAME.getValue(), associate.getAssociateName().trim());
		document.put(Constants.ASSOCIATEID.getValue(), associate.getAssociateId().trim());
		if (associate.getTitle() == null || associate.getTitle().isEmpty()) {
			associate.setTitle(Constants.TEAMMEMBERTITLE.getValue());
			document.put(Constants.TITLE.getValue(), associate.getTitle());
			document.put(Constants.ROLE.getValue(), associate.getRoleFromTitle(associate.getTitle()));
		} else {
			document.put(Constants.TITLE.getValue(), associate.getTitle());
			document.put(Constants.ROLE.getValue(), associate.getRoleFromTitle(associate.getTitle()));
		}

		/*
		 * In order to insert List of Objects, you need to first add BSON
		 * behavior to each of those objects, and only then can you add the
		 * array to the document being inserted.
		 */
		// create a DBObject such that you can insert a list of Projects
		List<DBObject> projectDBObjectList = new ArrayList<DBObject>();
		if (associate.getProjects() != null && !associate.getProjects().isEmpty()) {
			associate.getProjects().forEach(project -> createDBObjectList(project, projectDBObjectList));
			document.put(Constants.PROJECTS.getValue(), projectDBObjectList);
		} else {
			document.put(Constants.PROJECTS.getValue(), projectDBObjectList);
		}

		// save document
		projectsCollection.insertOne(document);
	}

	/**
	 * Add BSON behavior to the Project<br/>
	 * Returns a DBObject that encapsulates given Project
	 * 
	 * @param project
	 * @return
	 */
	private DBObject getBsonFromPojo(Project project) {
		BasicDBObject dbObject = new BasicDBObject();

		dbObject.put("projectName", project.getProjectName());
		dbObject.put("projectId", null);// This needs to be generated

		return dbObject;
	}

	/**
	 * Create a list of BSONs <br/>
	 * 
	 * Manipulates Project in order to persist in MongoDB. List<Project> ->
	 * Project -> DBObject -> List<DBObject>
	 */
	private void createDBObjectList(Project project, List<DBObject> projectList) {
		DBObject bsonProject = getBsonFromPojo(project);
		projectList.add(bsonProject);
	}

	@Override
	public void validateInput(Associate associate) throws ScrumBoardException {
		// checking associate Id
		String associateId = associate.getAssociateId();
		if (!associateId.matches(Constants.NUMBERS_ONLY.getValue())) {
			throw new ScrumBoardException("Invalid input");
		}

		// checking associate Name
		String associateName = associate.getAssociateName();
		Pattern pattern = Pattern.compile(Constants.RESTRICT.getValue());
		Matcher matcher = pattern.matcher(associateName);
		while (matcher.find()) {
			throw new ScrumBoardException("Invalid input");
		}
	}

	@Override
	public void updateAssociate(Associate associate) {
		// get collection
		MongoCollection<Document> associatesCollection = database.getCollection(Constants.ASSOCIATES.getValue());

		// updating associate name and role will be handled separately from
		// projects

		// create document to save
		Document associateDetailsDocument = new Document();
		Document projectDetailsDocument = new Document();// in order to save
															// array of Projects

		if (associate.getAssociateName() != null && !associate.getAssociateName().trim().isEmpty()) {
			associateDetailsDocument.put(Constants.ASSOCIATENAME.getValue(), associate.getAssociateName().trim());
		}
		if (associate.getTitle() != null) {
			associateDetailsDocument.put(Constants.TITLE.getValue(), associate.getTitle());
			associateDetailsDocument.put(Constants.ROLE.getValue(), associate.getRoleFromTitle(associate.getTitle()));
		}
		// if projects is given, then add to existing list of projects
		if (associate.getProjects() != null && !associate.getProjects().isEmpty()) {

			BasicDBObject dbObject = new BasicDBObject();
			associate.getProjects().forEach(project -> {

				dbObject.put(Constants.PROJECTNAME.getValue(), project.getProjectName());
				dbObject.put(Constants.PROJECTID.getValue(), null);

				projectDetailsDocument.put(Constants.PROJECTS.getValue(), dbObject);

				Document command = new Document();
				// add an item to existing array, does not add when duplicate
				command.put("$addToSet", projectDetailsDocument);

				// update projects query
				associatesCollection.updateOne(eq(Constants.ASSOCIATEID.getValue(), associate.getAssociateId()), command);
			});
		}

		Document command = new Document();
		command.put("$set", associateDetailsDocument);

		// update associate name and role query
		associatesCollection.updateOne(eq(Constants.ASSOCIATEID.getValue(), associate.getAssociateId()), command);
	}

	@Override
	public List<Associate> searchAssociates(String searchText) {
		// get collection
		MongoCollection<Document> associatesCollection = database.getCollection(Constants.ASSOCIATES.getValue());
		List<Associate> associateList = new ArrayList<>();
		// process each retrieved record
		Block<Document> processRetreivedData = (document) -> {

			String retrivedDataAsJSON = document.toJson();
			Associate associate;
			try {
				associate = new ObjectMapper().readValue(retrivedDataAsJSON, Associate.class);
				associateList.add(associate);
			} catch (IOException e) {
				LOGGER.error("Error occurred while processing retrieved associate details for search associates operation");
			}
		};
		/*in order to do a text search, you must have already created an index on associates collection, otherwise mongoDB throws error*/
		//associatesCollection.find(new BasicDBObject("$text", new BasicDBObject("$search", searchText))).forEach(processRetreivedData);
		associatesCollection.find(text(searchText, new TextSearchOptions().caseSensitive(false))).forEach(processRetreivedData);
		return associateList;
	}

	@Override
	public boolean isAssociateBelongsToProject(String projectName, String associateId) {
		// get collection
		MongoCollection<Document> associatesCollection = database.getCollection(Constants.ASSOCIATES.getValue());
		// query
		long count = associatesCollection.count(and(eq(Constants.ASSOCIATEID.getValue(), associateId), eq("projects.projectName", projectName)));
		return (count > 0) ? true : false;
	}

}
