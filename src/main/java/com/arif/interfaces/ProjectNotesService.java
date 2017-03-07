package com.arif.interfaces;

import java.util.List;

import org.apache.log4j.Logger;

import com.arif.exception.ScrumBoardException;
import com.arif.model.ProjectNotes;
import com.arif.response.ScrumBoardResponse;

/**
 * Handles services related to Project Notes
 * 
 * @author arifakrammohammed
 *
 */
public interface ProjectNotesService {

	final static Logger LOGGER = Logger.getLogger(ProjectNotesService.class);

	/**
	 * Validate given notes and persists it
	 * 
	 * @param projectNotes
	 *            a note in a specified project
	 * @param projectName
	 *            a project to which the notes belongs
	 * @param associateId
	 *            author of the notes
	 * @return a {@link ScrumBoardResponse} containing operation status
	 */
	default ScrumBoardResponse<Void> saveNewProjectNotes(ProjectNotes projectNotes, String projectName,
			String associateId) {
		ScrumBoardResponse<Void> response = new ScrumBoardResponse<>();
		// 1. validate the input
		try {
			LOGGER.debug("Validating input for add new project notes operation");
			validateInput(projectNotes);
			LOGGER.debug("Input validated - OK");
		} catch (ScrumBoardException e) {
			LOGGER.error("Title has special characters", e);
			response.setCode(404);
			response.setMessage("Notes title cannot have these special characters [ < > \" ! \' : { } ]");
			// TODO: handle the below case
			// construct message with error details
			response.setCode(403);
			response.setMessage(
					"You do not have access to the project your are trying to save. Please contact your project/team lead");
			return response;
		}
		// 2. save project notes
		saveProjectNotes(projectNotes, projectName);
		LOGGER.debug("New project note successfully added for the project " + projectName);
		// send success response
		response.setCode(200);
		response.setMessage("Notes added to the project - projectName");
		return response;
	}

	/**
	 * Fetch all notes associated with the given project
	 * 
	 * @param projectName
	 *            project whose notes is to retrieved
	 * @return {@link List} of all {@link ProjectNotes}
	 */
	List<ProjectNotes> getAllProjectNotes(String projectName);

	/**
	 * Saves given project notes under a given project
	 * 
	 * @param projectNotes
	 *            details of new notes
	 * @param projectName
	 *            name of the project
	 */
	void saveProjectNotes(ProjectNotes projectNotes, String projectName);

	/**
	 * check project title has any special characters
	 *
	 * @param projectNotes
	 *            new notes details
	 * @throws ScrumBoardException
	 *             if user input is not per set rules
	 */
	void validateInput(ProjectNotes projectNotes) throws ScrumBoardException;

	/**
	 * delete given project notes<br/>
	 * check whether given associate has access to the project being delete;
	 * proceeds with the operation only if associate belongs to project under
	 * processing
	 *
	 * @param projectNotes
	 *            notes to delete
	 * @param projectName
	 *            name of the project
	 * @param associateId
	 *            associate performing this operation
	 * @return a {@link ScrumBoardResponse} containing operation status
	 */
	ScrumBoardResponse<Void> deleteProjectNotes(ProjectNotes projectNotes, String projectName, String associateId);

	/**
	 * update given project notes<br/>
	 * check whether given associate has access to the project being delete;
	 * proceeds with the operation only if associate belongs to project under
	 * processing
	 * 
	 * @param projectNotes
	 *            notes to update
	 * @param projectName
	 *            name of the project
	 * @param associateId
	 *            associate performing this operation
	 * @return a {@link ScrumBoardResponse} containing operation status
	 */
	ScrumBoardResponse<Void> updateProjectNotes(ProjectNotes projectNotes, String projectName, String associateId);
}
