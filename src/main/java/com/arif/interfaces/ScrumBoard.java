package com.arif.interfaces;

import com.arif.impl.AssociateServiceImpl;
import com.arif.impl.LoginServiceImpl;
import com.arif.impl.ProjectNotesServiceImpl;
import com.arif.impl.ProjectServiceImpl;
import com.arif.impl.ScrumBoardImpl;
import com.arif.impl.ScrumServiceImpl;
import com.arif.model.Associate;
import com.arif.model.Project;
import com.arif.model.ProjectNotes;
import com.arif.model.Scrum;
import com.arif.response.ScrumBoardResponse;

/**
 * Contains all services this system provides<br/>
 * gets database connection and passes it on to the implementation classes<br/>
 * gets the object of implementation classes<br/>
 * 
 * @author arifakrammohammed
 *
 */
public interface ScrumBoard extends DBConnection {

	/**
	 * login a user to use system services
	 * 
	 * @param associateId
	 *            user to login
	 * @return a {@link ScrumBoardResponse} containing {@link Associate} details
	 *         or failure details
	 */
	ScrumBoardResponse<Associate> login(String associateId);

	/**
	 * Add a new project
	 * 
	 * @param project
	 *            project to add
	 * @param associateId
	 *            user who is performing this operation
	 * @param token
	 *            user's JWT Token
	 * @return a {@link ScrumBoardResponse} containing operation status
	 */
	ScrumBoardResponse<Void> addProject(String project, String associateId, String token);

	/**
	 * get all projects
	 * 
	 * @return a {@link ScrumBoardResponse} containing all {@link Project}s or
	 *         failure details
	 */
	ScrumBoardResponse<Project> getAllProjects();

	/**
	 * Add/Update an associate
	 * 
	 * @param associateDetails
	 *            {@link Associate} details to add or update
	 * @param associateId
	 *            user who is performing this operation
	 * @param token
	 *            user's JWT token
	 * @return a {@link ScrumBoardResponse} containing operation status
	 */
	ScrumBoardResponse<Void> addAssociate(String associateDetails, String associateId, String token);

	/**
	 * Add Scrum for a project
	 * 
	 * @param scrumDetails
	 *            {@link Scrum} to add
	 * @param associateId
	 *            user who is performing this operation
	 * @param token
	 *            user's JWT token
	 * @return a {@link ScrumBoardResponse} containing operation status
	 */
	ScrumBoardResponse<Void> addScrum(String scrumDetails, String associateId, String token);

	/**
	 * Add an associate to an existing scrum in a project
	 *
	 * @param scrumDetails
	 *            existing scrum details
	 * @param associateDetails
	 *            associate to add
	 * @param associateId
	 *            user doing this operation
	 * @param token
	 *            JWT token
	 * @return a {@link ScrumBoardResponse} containing operation status
	 */
	ScrumBoardResponse<Void> updateScrum(String scrumDetails, String associateDetails, String associateId,
			String token);

	/**
	 * Get Scrum details for a given date and projects
	 * 
	 * @param scrumDate
	 *            date for which scrum details are to be retrieved
	 * @param projectList
	 *            JSON string containing list of {@link Project}s
	 * @param associateId
	 *            user who is performning this operation
	 * @param token
	 *            user's JWT token
	 * @return a {@link ScrumBoardResponse} containing operation status
	 */
	ScrumBoardResponse<Scrum> getScrumDetails(String scrumDate, String projectList, String associateId, String token);

	/**
	 * Get filtered Scrum details for a given date and a given project
	 * 
	 * @param scrumDate
	 *            date for which scrum details are to be retrieved
	 * @param projectName
	 *            name of the project
	 * @param associateId
	 *            user who is performning this operation
	 * @param token
	 *            user's JWT token
	 * @return a {@link ScrumBoardResponse} containing operation status
	 */
	ScrumBoardResponse<Scrum> getFilteredScrumDetails(String scrumDate, String projectName, String associateId,
			String token);

	/**
	 * Return the most recent scrum record for the given project.
	 * 
	 * @param projectName
	 *            name of the project
	 * @return a {@link ScrumBoardResponse} containing operation status
	 */
	ScrumBoardResponse<Scrum> getRecentScrumRecord(String projectName);

	/**
	 * Save daily Scrum updates of an associate
	 * 
	 * @param scrumDetails
	 *            scrum details to save
	 * @param date
	 *            date of the scrum
	 * @param projectName
	 *            name of the project
	 * @param associateId
	 *            user who is performing this operation
	 * @param token
	 *            user's JWT token
	 * @return a {@link ScrumBoardResponse} containing operation status
	 */
	ScrumBoardResponse<Void> saveDailyScrumUpdate(String scrumDetails, String date, String projectName,
			String associateId, String token);

	/**
	 * get all notes of a project
	 * 
	 * @param projectName
	 *            name of the project whose notes is to be retrieved
	 * @return a {@link ScrumBoardResponse} containing operation status
	 */
	ScrumBoardResponse<ProjectNotes> getAllProjectNotes(String projectName);

	/**
	 * Search and return all associates that match the search string
	 *
	 * @param searchText
	 *            name/id of the associate to search for
	 * @return a {@link ScrumBoardResponse} containing operation status
	 */
	ScrumBoardResponse<Associate> searchAssociates(String searchText);

	/**
	 * Save a new project note
	 * 
	 * @param projectNotes
	 *            JSON string containing {@link ProjectNotes}
	 * @param projectName
	 *            name of the project this notes is associated with
	 * @param associateId
	 *            user who is performing this operation
	 * @param token
	 *            user's JWT token
	 * @return a {@link ScrumBoardResponse} containing operation status
	 */
	ScrumBoardResponse<Void> saveNewProjectNotes(String projectNotes, String projectName, String associateId,
			String token);

	/**
	 * delete a notes from a project
	 * 
	 * @param projectNotes
	 *            JSON string containing {@link ProjectNotes} to delete
	 * @param projectName
	 *            name of the project this notes is associated with
	 * @param associateId
	 *            user who is performing this operation
	 * @param token
	 *            user's JWT token
	 * @return a {@link ScrumBoardResponse} containing operation status
	 */
	ScrumBoardResponse<Void> deleteProjectNotes(String projectNotes, String projectName, String associateId,
			String token);

	/**
	 * update a notes from a project
	 * 
	 * @param projectNotes
	 *            JSON string containing {@link ProjectNotes} to update
	 * @param projectName
	 *            name of the project this notes is associated with
	 * @param associateId
	 *            user who is performing this operation
	 * @param token
	 *            user's JWT token
	 * @return a {@link ScrumBoardResponse} containing operation status
	 */
	ScrumBoardResponse<Void> updateProjectNotes(String projectNotes, String projectName, String associateId,
			String token);

	/**
	 * Java 8 feature.<br/>
	 * Get an object of the implementations class
	 * 
	 * @return {@link ScrumBoardImpl} Object of the class that implements this
	 *         interface
	 */
	static ScrumBoardImpl getInstance() {
		return new ScrumBoardImpl();
	}

	/*
	 * below methods provide instances of implementation classes
	 */

	default LoginService getLoginServiceInstance() {
		return new LoginServiceImpl(getDBConnection());
	}

	default ProjectService getProjectServiceInstance() {
		return new ProjectServiceImpl(getDBConnection());
	}

	default AssociateService getAssociateServiceInstance() {
		return new AssociateServiceImpl(getDBConnection());
	}

	default ScrumService getScrumServiceInstance() {
		return new ScrumServiceImpl(getDBConnection());
	}

	default ProjectNotesService getProjectNotesServiceInstance() {
		return new ProjectNotesServiceImpl(getDBConnection());
	}

}
