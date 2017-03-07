package com.arif.interfaces;

import java.util.List;

import org.apache.log4j.Logger;

import com.arif.exception.ScrumBoardException;
import com.arif.model.Project;
import com.arif.response.ScrumBoardResponse;

/**
 * Handles services related to Project
 * 
 * @author arifakrammohammed
 *
 */
public interface ProjectService {

	final static Logger LOGGER = Logger.getLogger(ProjectService.class);

	/**
	 * Performs validations on the user input and then adds a new project to the
	 * system
	 * 
	 * @param projectName
	 *            new project name
	 * @param associateId
	 *            user who is adding a new project
	 * @return a {@link ScrumBoardResponse} containing operation status
	 */
	default ScrumBoardResponse<Void> addProject(Project project, String associateId) {
		ScrumBoardResponse<Void> response = null;
		// 1. validate the input
		try {
			LOGGER.debug("validating input " + project.getProjectName());
			validateInput(project.getProjectName());
			LOGGER.debug("Input validated - OK");
		} catch (ScrumBoardException e) {
			LOGGER.error("User Input Not Valid ", e);
			// construct message with error details
			response = new ScrumBoardResponse<>();
			response.setCode(404);
			response.setMessage("Project Name cannot contain < > \" ! \' : { } ] characters");
			return response;
		}
		// 2. check if project exists already
		if (isProjectExists(project.getProjectName())) {
			LOGGER.info("Project - " + project.getProjectName() + " - already exists");
			response = new ScrumBoardResponse<>();
			response.setCode(404);
			response.setMessage("Project already exists");
			return response;
		}
		// 3. add project
		LOGGER.debug("Proceeding with saving the given project");
		addProject(project);
		response = new ScrumBoardResponse<>();
		response.setCode(200);
		response.setMessage("Project Saved");
		LOGGER.info("A new project " + project.getProjectName() + " saved");
		return response;
	}

	/**
	 * check if project name has any special characters
	 * 
	 * @param projectName
	 *            a new project name
	 * @throws ScrumBoardException
	 *             if user input is not per set rules
	 */
	void validateInput(String projectName) throws ScrumBoardException;

	/**
	 * check if the given project already exists
	 * 
	 * @param projectName
	 *            project name to verify if exists
	 * @return <i>true</i> if project already exists
	 */
	boolean isProjectExists(String projectName);

	/**
	 * Add a given project to the system
	 * 
	 * @param project
	 *            new project to add
	 */
	void addProject(Project project);

	/**
	 * get all existing projects in the system
	 * 
	 * @return {@link List} of all {@link Project}
	 */
	List<Project> getAllProjects();

	/**
	 * check if the given project is active or inactive
	 *
	 * @param projectName
	 *            name of the project
	 * @return true is project is active, false otherwise
	 */
	boolean isProjectActive(String projectName);
}
