package com.arif.impl;

import static com.mongodb.client.model.Filters.eq;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.ObjectMapper;

import com.arif.constants.Constants;
import com.arif.exception.ScrumBoardException;
import com.arif.interfaces.ProjectNotesService;
import com.arif.model.ProjectNotes;
import com.arif.response.ScrumBoardResponse;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * contains all the logic related to project notes
 * 
 * @author arifakrammohammed
 *
 */
public class ProjectNotesServiceImpl implements ProjectNotesService {

	final static Logger LOGGER = Logger.getLogger(ProjectNotesServiceImpl.class);
	MongoDatabase database;

	public ProjectNotesServiceImpl(MongoDatabase database) {
		super();
		this.database = database;
	}

	@Override
	public List<ProjectNotes> getAllProjectNotes(String projectName) {
		List<ProjectNotes> projectNotesList = new ArrayList<>();
		// get collection
		MongoCollection<Document> projectsCollection = database.getCollection(projectName + Constants.NOTES.getValue());
		// processed retrieved data
		Block<Document> processRetreivedData = (document) -> {

			String retrivedDataAsJSON = document.toJson();
			ProjectNotes projectNotes;
			try {
				projectNotes = new ObjectMapper().readValue(retrivedDataAsJSON, ProjectNotes.class);
				projectNotesList.add(projectNotes);
			} catch (IOException e) {
				LOGGER.error("Error while processing retrieved project notes ", e);
			}

		};
		// query
		projectsCollection.find().sort(new Document(Constants.OBJECTID.getValue(), -1)).forEach(processRetreivedData);
		LOGGER.info("All project notes fetched");
		return projectNotesList;
	}

	@Override
	public void saveProjectNotes(ProjectNotes projectNotes, String projectName) {
		// get collection
		MongoCollection<Document> projectsNotesCollection = database
				.getCollection(projectName + Constants.NOTES.getValue());
		// create document
		Document document = new Document();
		// add document properties
		document.put(Constants.TITLE.getValue(), projectNotes.getTitle().trim());
		document.put(Constants.CREATEDON.getValue(), projectNotes.getCreatedOn());
		document.put(Constants.AUTHOR.getValue(), projectNotes.getAuthor());
		document.put(Constants.NOTESFIELD.getValue(), projectNotes.getNotes().trim());
		document.put(Constants.EDITORS.getValue(), new ArrayList<>(1));
		document.put(Constants.LASTUPDATEDON.getValue(), null);

		// save document
		projectsNotesCollection.insertOne(document);
	}

	@Override
	public void validateInput(ProjectNotes projectNotes) throws ScrumBoardException {
		// checking project notes title
		String title = projectNotes.getTitle();
		Pattern pattern = Pattern.compile(Constants.RESTRICT.getValue());
		Matcher matcher = pattern.matcher(title);
		while (matcher.find()) {
			throw new ScrumBoardException("Invalid input");
		}
	}

	@Override
	public ScrumBoardResponse<Void> deleteProjectNotes(ProjectNotes projectNotes, String projectName,
			String associateId) {
		ScrumBoardResponse<Void> response = new ScrumBoardResponse<>();
		// 1. check if associate belongs to the given project
		boolean isAssociateBelongsToProject = new AssociateServiceImpl(database)
				.isAssociateBelongsToProject(projectName, associateId);
		if (isAssociateBelongsToProject) {
			// 2. proceed with project notes deletion get collection
			MongoCollection<Document> projectsNotesCollection = database
					.getCollection(projectName + Constants.NOTES.getValue());
			// query
			projectsNotesCollection
					.deleteOne(eq(Constants.OBJECTID.getValue(), new ObjectId(projectNotes.get_id().toString())));
			response.setCode(200);
			response.setMessage("Notes deleted");
			return response;
		} else {
			response.setCode(404);
			response.setMessage("You are not allowed to perform this operation as you do not belong to the project - "
					+ projectName);
			return response;
		}
	}

	@Override
	public ScrumBoardResponse<Void> updateProjectNotes(ProjectNotes projectNotes, String projectName,
			String associateId) {
		ScrumBoardResponse<Void> response = new ScrumBoardResponse<>();
		// 1. check if associate belongs to the given project
		boolean isAssociateBelongsToProject = new AssociateServiceImpl(database)
				.isAssociateBelongsToProject(projectName, associateId);
		if (isAssociateBelongsToProject) {
			// ObjectId id = new ObjectId(projectNotes.get_id());
			// 2. proceed with project notes deletion get collection
			MongoCollection<Document> projectsNotesCollection = database
					.getCollection(projectName + Constants.NOTES.getValue());
			// create document to save
			Document detailsToUpdate = new Document();
			detailsToUpdate.put(Constants.NOTESFIELD.getValue(), projectNotes.getNotes());
			detailsToUpdate.put(Constants.TITLE.getValue(), projectNotes.getTitle());
			detailsToUpdate.put(Constants.LASTUPDATEDON.getValue(), projectNotes.getLastUpdatedOn());
			// updating project notes editors
			if (projectNotes.getEditors() != null && !projectNotes.getEditors().isEmpty()) {

				BasicDBObject dbObject = new BasicDBObject();
				projectNotes.getEditors().forEach(editor -> {

					dbObject.put(Constants.EDITORS.getValue(), editor);

					Document updateEditorsCmd = new Document();
					// add an item to existing array, does not add when duplicate
					updateEditorsCmd.put("$addToSet", dbObject);

					// update project notes editors query
					projectsNotesCollection.updateOne(
							eq(Constants.OBJECTID.getValue(), new ObjectId(projectNotes.get_id().toString())),
							updateEditorsCmd);
				});
			}

			Document command = new Document();
			command.put("$set", detailsToUpdate);
			// query
			projectsNotesCollection.updateOne(eq(Constants.OBJECTID.getValue(), new ObjectId(projectNotes.get_id().toString())), command);
			// construct response
			response.setCode(200);
			response.setMessage("Notes updated");
			return response;
		} else {
			response.setCode(404);
			response.setMessage("You are not allowed to perform this operation as you do not belong to the project - "
					+ projectName);
			return response;
		}
	}

}
