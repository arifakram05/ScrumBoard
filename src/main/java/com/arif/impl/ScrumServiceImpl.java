package com.arif.impl;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.Document;
import org.codehaus.jackson.map.ObjectMapper;

import com.arif.constants.Constants;
import com.arif.exception.ScrumBoardException;
import com.arif.interfaces.ScrumService;
import com.arif.model.Associate;
import com.arif.model.Project;
import com.arif.model.Scrum;
import com.arif.model.ScrumDetails;
import com.arif.util.DateMechanic;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.DBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * contains all the logic for scrum services
 * 
 * @author arifakrammohammed
 *
 */
public class ScrumServiceImpl implements ScrumService {

	MongoDatabase database;

	public ScrumServiceImpl(MongoDatabase database) {
		super();
		this.database = database;
	}

	@Override
	public boolean isScrumExists(Scrum scrum) {
		// get collection
		MongoCollection<Document> scrumCollection = database.getCollection(scrum.getProjectName());
		// query
		return scrumCollection.count(and(eq(Constants.STARTDATE.getValue(), scrum.getStartDate()),
				eq(Constants.ENDDATE.getValue(), scrum.getEndDate()))) != 0 ? true : false;
	}

	@Override
	public List<ScrumDetails> createScrumDetails(String projectName) {
		/*
		 * LOGIC : get all the ids and names of associates on a given project
		 * from associates collection, then form ScrumDetails with details of
		 * those associates
		 */
		List<ScrumDetails> scrumDetailsList = new ArrayList<>();
		// get collection
		MongoCollection<Document> associatesCollection = database.getCollection(Constants.ASSOCIATES.getValue());
		// process each retrieved record
		Block<Document> processRetreivedData = (document) -> {

			String retrivedDataAsJSON = document.toJson();
			ScrumDetails scrumDetails;
			try {
				scrumDetails = new ObjectMapper().readValue(retrivedDataAsJSON, ScrumDetails.class);
				scrumDetailsList.add(scrumDetails);
			} catch (IOException e) {
				LOGGER.error("Error occurred while processing retrieved associate details for Add Scrum operation");
			}
		};

		/*
		 * This is the way of searching through the documents with array of
		 * objects. "projects" is the field with array of objects, and
		 * "projectName" is the one of fields in that object.
		 * 
		 * Projection class tells to retrieve only the specified fields of
		 * matched documents.
		 */
		// query
		associatesCollection.find(eq("projects.projectName", projectName)).projection(
				fields(include(Constants.ASSOCIATENAME.getValue(), Constants.ASSOCIATEID.getValue()), excludeId()))
				.forEach(processRetreivedData);

		return scrumDetailsList;
	}

	@Override
	public void authorizeScrum(Scrum scrum) throws ParseException {
		// get collection
		MongoCollection<Document> scrumCollection = database.getCollection(scrum.getProjectName());
		/*
		 * LOGIC : Get all dates between start date and end date. for each date,
		 * create a document and put it in the list save the list
		 */
		List<String> scrumDays = DateMechanic.getAllDatesBetweenTwoDates(scrum.getStartDate(), scrum.getEndDate());
		List<Document> documentList = getAllDocumentsToSave(scrumDays, scrum);
		// save
		scrumCollection.insertMany(documentList);
	}

	/**
	 * For each date in the List, creates a {@link Document}
	 * 
	 * @param scrumDays
	 * @param scrum
	 * @return
	 */
	private List<Document> getAllDocumentsToSave(List<String> scrumDays, Scrum scrum) {
		List<Document> documents = new ArrayList<Document>();
		scrumDays.forEach(date -> createDocument(documents, date, scrum));
		return documents;
	}

	private void createDocument(List<Document> documents, String date, Scrum scrum) {
		// create document
		Document document = new Document();

		document.put(Constants.ACTUALDATE.getValue(), date);
		document.put(Constants.STARTDATE.getValue(), scrum.getStartDate());
		document.put(Constants.ENDDATE.getValue(), scrum.getEndDate());
		document.put(Constants.SCRUMNAME.getValue(), scrum.getScrumName().trim());
		/*
		 * For each ScrumDetails object in the list of ScrumDetails, create a
		 * DBObject and put it in the list of DBObjects
		 * 
		 * List<ScrumDetails> -> ScrumDetails -> DBObject -> List<DBObject>
		 */
		List<DBObject> scrumDetailsDBObjectsList = new ArrayList<DBObject>();
		scrum.getScrumDetails().forEach(detail -> createDBObjectList(detail, scrumDetailsDBObjectsList));
		document.put(Constants.SCRUMDETAILS.getValue(), scrumDetailsDBObjectsList);

		documents.add(document);
	}

	/**
	 * Create a list of BSONs
	 * 
	 * @param detail
	 * @param scrumDetailsDBObjectsList
	 */
	private void createDBObjectList(ScrumDetails detail, List<DBObject> scrumDetailsDBObjectsList) {
		DBObject dbObject = createBSONFromPojo(detail);
		scrumDetailsDBObjectsList.add(dbObject);
	}

	/**
	 * Add BSON behavior for the document
	 * 
	 * @param project
	 * @return
	 */
	// TODO This needs to go into ScrumDetails class
	private DBObject createBSONFromPojo(ScrumDetails scrumDetails) {
		BasicDBObject dbObject = new BasicDBObject();

		dbObject.put(Constants.ASSOCIATEID.getValue(), scrumDetails.getAssociateId());
		dbObject.put(Constants.ASSOCIATENAME.getValue(), scrumDetails.getAssociateName());
		dbObject.put(Constants.YESTERDAY.getValue(), scrumDetails.getYesterday());
		dbObject.put(Constants.TODAY.getValue(), scrumDetails.getToday());
		dbObject.put(Constants.ROADBLOCKS.getValue(), scrumDetails.getRoadblocks());

		return dbObject;
	}

	@Override
	public List<Scrum> getScrumDetails(String scrumDate, List<Project> projectList) {
		List<Scrum> scrumDetailsList = new ArrayList<>();
		// iterate over given projectList using Lambda
		projectList.forEach(project -> processEachProjectForScrumDetails(scrumDate, project, scrumDetailsList));

		return scrumDetailsList;
	}

	@Override
	public void saveDailyScrumUpdate(ScrumDetails scrumDetails, String date, String projectName) {
		// get collection
		MongoCollection<Document> scrumCollection = database.getCollection(projectName);

		// create document to save
		Document detailsToUpdate = new Document();
		detailsToUpdate.put("scrumDetails.$.yesterday", scrumDetails.getYesterday());
		detailsToUpdate.put("scrumDetails.$.today", scrumDetails.getToday());
		detailsToUpdate.put("scrumDetails.$.roadblocks", scrumDetails.getRoadblocks());

		Document command = new Document();
		command.put("$set", detailsToUpdate);

		// update command
		scrumCollection.updateOne(and(eq(Constants.ACTUALDATE.getValue(), date),
				eq("scrumDetails.associateId", scrumDetails.getAssociateId())), command);
	}

	private void processEachProjectForScrumDetails(String scrumDate, Project project, List<Scrum> scrumDetailsList) {
		// get collection
		MongoCollection<Document> scrumDetailsCollection = database.getCollection(project.getProjectName());
		// process each retrieved record
		Block<Document> processRetreivedData = (document) -> {

			String retrivedDataAsJSON = document.toJson();
			Scrum scrumDetails;
			try {
				scrumDetails = new ObjectMapper().readValue(retrivedDataAsJSON, Scrum.class);
				scrumDetails.setProjectName(project.getProjectName());
				scrumDetailsList.add(scrumDetails);
			} catch (IOException e) {
				LOGGER.error("Error while processing retrieved scrum details");
			}
		};
		// query
		scrumDetailsCollection.find(eq(Constants.ACTUALDATE.getValue(), scrumDate))
				.projection(fields(include(Constants.SCRUMDETAILS.getValue(), Constants.ACTUALDATE.getValue(),
						Constants.STARTDATE.getValue(), Constants.ENDDATE.getValue(), Constants.SCRUMNAME.getValue()),
						excludeId()))
				.forEach(processRetreivedData);
	}

	@Override
	public void validateInput(Scrum scrum) throws ScrumBoardException, ParseException {
		if (!DateMechanic.isEndDateGreater(scrum.getStartDate(), scrum.getEndDate())) {
			throw new ScrumBoardException("End date is smaller that the Start date");
		}
		// checking scrum Name
		String scrumName = scrum.getScrumName();
		Pattern pattern = Pattern.compile(Constants.RESTRICT.getValue());
		Matcher matcher = pattern.matcher(scrumName);
		while (matcher.find()) {
			throw new ScrumBoardException("Scrum name cannot contain these characters [ < > \" ! \' : { } ]");
		}
	}

	@Override
	public List<Scrum> getFilteredScrumDetails(String scrumDate, String projectName) {
		List<Scrum> scrumDetailsList = new ArrayList<>();

		// get collection
		MongoCollection<Document> scrumDetailsCollection = database.getCollection(projectName);
		// processed retrieved data
		Block<Document> processRetreivedData = (document) -> {

			String retrivedDataAsJSON = document.toJson();
			Scrum scrum;
			try {
				scrum = new ObjectMapper().readValue(retrivedDataAsJSON, Scrum.class);
				scrum.setProjectName(projectName);
				scrumDetailsList.add(scrum);
			} catch (IOException e) {
				LOGGER.error("Error while processing retrieved filtered scrum details ", e);
			}

		};
		// query
		scrumDetailsCollection.find(eq(Constants.ACTUALDATE.getValue(), scrumDate)).forEach(processRetreivedData);
		LOGGER.info("All projects fetched");
		return scrumDetailsList;
	}

	@Override
	public List<Scrum> getRecentScrumRecord(String projectName) {
		List<Scrum> scrumDetailsList = new ArrayList<>(1);

		// get collection
		MongoCollection<Document> scrumDetailsCollection = database.getCollection(projectName);
		// processed retrieved data
		Block<Document> processRetreivedData = (document) -> {

			String retrivedDataAsJSON = document.toJson();
			Scrum scrum;
			try {
				scrum = new ObjectMapper().readValue(retrivedDataAsJSON, Scrum.class);
				scrum.setProjectName(projectName);
				scrumDetailsList.add(scrum);
			} catch (IOException e) {
				LOGGER.error("Error while processing retrieved filtered scrum details ", e);
			}

		};
		// query
		scrumDetailsCollection.find().sort(new Document(Constants.OBJECTID.getValue(), -1)).limit(1)
				.forEach(processRetreivedData);
		LOGGER.info("The most recent scrum record fetched");
		return scrumDetailsList;
	}

	@Override
	public void authorizeUpdateScrum(Scrum scrum, Associate associate) {
		// 1. check whether associate is already added to given project
		/*
		 * if associate is already part of the given project, then the
		 * associate's project list should have the given project. If not,
		 * add given project to associate first, then continue with adding the
		 * associate to the project's scrum
		 */
		// check whether associate is already part of given project
		Project existingProject = associate.getProjects().stream()
				.filter(p -> scrum.getProjectName().equals(p.getProjectName())).findAny().orElse(null);
		// if associate is not part of the given project, add to the project
		if(existingProject == null) {
			Project project = new Project();
			project.setProjectId(null);
			project.setProjectName(scrum.getProjectName());
			List<Project> projectList = new ArrayList<>(1);
			projectList.add(project);
			associate.setProjects(projectList);
			// update associate information with new project
			new AssociateServiceImpl(database).updateAssociate(associate);
		}

		// 2. add associate into given on-going scrum
		// get collection
		MongoCollection<Document> scrumCollection = database.getCollection(scrum.getProjectName());

		// create document to save
		Document scrumDocument = new Document();
		BasicDBObject dbObject = new BasicDBObject();
		dbObject.put(Constants.ASSOCIATEID.getValue(), associate.getAssociateId());
		dbObject.put(Constants.ASSOCIATENAME.getValue(), associate.getAssociateName());
		dbObject.put(Constants.YESTERDAY.getValue(), null);
		dbObject.put(Constants.TODAY.getValue(), null);
		dbObject.put(Constants.ROADBLOCKS.getValue(), null);
		scrumDocument.put(Constants.SCRUMDETAILS.getValue(), dbObject);

		Document command = new Document();
		// add an item to existing array, does not add when duplicate
		command.put("$addToSet", scrumDocument);

		if (associate.getAssociateId() != null && associate.getAssociateName() != null) {
			// update scrum query
			scrumCollection.updateMany(and(eq(Constants.STARTDATE.getValue(), scrum.getStartDate()),
					eq(Constants.ENDDATE.getValue(), scrum.getEndDate())), command);
		}
	}

	@Override
	public boolean isProjectActive(String projectName) {
		return new ProjectServiceImpl(database).isProjectActive(projectName);
	}
}
