package com.arif.interfaces;

import java.util.List;

import org.apache.log4j.Logger;

import com.arif.exception.ScrumBoardException;
import com.arif.model.Associate;
import com.arif.response.ScrumBoardResponse;

/**
 * Handles services related to Associate
 * 
 * @author arifakrammohammed
 *
 */
public interface AssociateService {

	final static Logger LOGGER = Logger.getLogger(AssociateService.class);

	/**
	 * Performs validations on the user input and then, Adds a new associate to
	 * the system or updates an associate if already exists
	 * 
	 * @param associate
	 *            {@link Associate} details to add or update
	 * @return a {@link ScrumBoardResponse} containing operation status
	 */
	default ScrumBoardResponse<Void> addAssociate(Associate associate) {
		ScrumBoardResponse<Void> response = new ScrumBoardResponse<>();
		// 1. validate the input
		try {
			LOGGER.debug("Validating input for add/udpate of an associate");
			validateInput(associate);
			LOGGER.debug("Input validated. Proceeding");
		} catch (ScrumBoardException e) {
			LOGGER.error("User Input Not Valid ", e);
			// construct message with error details
			response.setCode(404);
			response.setMessage("Associate details cannot contain these characters [ <> \" ! \' : { } ]");
			return response;
		}
		// 2. check if associate does not exist
		if (!isAssociateExists(associate)) {
			LOGGER.info("Adding new associate - " + associate.getAssociateId());
			// If associates does not exist, do the following
			// 3. index record for fast search
			index(associate);
			// 4. add associate to the system
			authorizeAssociate(associate);
			// send success response
			response.setCode(200);
			response.setMessage("Associate Added Successfully");
			return response;
		}
		/*
		 * as associate already exists, perform update i.e. take all not-null
		 * details provided by the user, and update the details of given
		 * associate id
		 */
		// 3. Update associate
		LOGGER.info("Associate - " + associate.getAssociateId()
				+ " - already exists, so proceeding with updating user details");
		updateAssociate(associate);
		response.setCode(200);
		response.setMessage("Associate already exists in the system. So, updated the associate with given details");
		return response;
	}

	/**
	 * check associates name and id for special characters
	 * 
	 * @param associate
	 *            {@link Associate} details to validate
	 * @throws ScrumBoardException
	 *             if user input is not per set rules
	 */
	void validateInput(Associate associate) throws ScrumBoardException;

	/**
	 * Check if an associate already exists in the database
	 * 
	 * @param associate
	 *            Associate to check
	 * @return a value <i>true</i> indicating user exists, <i>false</i>
	 *         otherwise
	 */
	boolean isAssociateExists(Associate associate);

	/**
	 * TODO: index associate details for faster retrieval
	 * 
	 * @param associate
	 *            {@link Associate} details to index
	 */
	void index(Associate associate);

	/**
	 * Add associate to the database
	 * 
	 * @param associate
	 *            {@link Associate} to add
	 */
	void authorizeAssociate(Associate associate);

	/**
	 * Update associate
	 * 
	 * @param associate
	 *            {@link Associate} to update
	 */
	void updateAssociate(Associate associate);

	/**
	 * Search for all associates who match the search criteria
	 *
	 * @param searchText
	 *            id/name of the associate to search
	 * @return
	 */
	List<Associate> searchAssociates(String searchText);

	/**
	 * check if given associate belongs to the given project
	 *
	 * @param projectName
	 *            project name
	 * @param associateId
	 *            associate Id
	 * @return true if given associate is part of the given project, false
	 *         otherwise
	 */
	boolean isAssociateBelongsToProject(String projectName, String associateId);
}
