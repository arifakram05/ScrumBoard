package com.arif.interfaces;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.apache.log4j.Logger;

import com.arif.exception.ScrumBoardException;
import com.arif.model.Associate;
import com.arif.response.ScrumBoardResponse;
import com.arif.util.SecureLogin;

/**
 * Handles services related to user login
 * 
 * @author arifakrammohammed
 *
 */
public interface LoginService {

	final static Logger LOGGER = Logger.getLogger(LoginService.class);

	/**
	 * Perform user login after input validation. If user is valid responds his
	 * request with a token.
	 * 
	 * @param associateId
	 *            user to login
	 * @return {@link ScrumBoardResponse} containing {@link Associate} details
	 * @throws NoSuchAlgorithmException
	 *             if the Secret key to validate a user against does not exist;
	 *             this happens when you restart the system, and user tries
	 *             logging in with old token
	 */
	default ScrumBoardResponse<Associate> login(String associateId) throws NoSuchAlgorithmException {
		ScrumBoardResponse<Associate> response = new ScrumBoardResponse<>();
		// 1. validate the input
		try {
			LOGGER.debug("Validating input for user login");
			validateInput(associateId);
			LOGGER.debug("Input validated - OK. Proceeding with login");
		} catch (ScrumBoardException e) {
			LOGGER.error("User Input Not Valid : " + associateId, e);
			// construct message with error details
			response.setCode(404);
			response.setMessage("Login ID can only contain numbers");
			return response;
		}
		// 2. check if associate present in the system
		Associate associateDetails = getAssociateDetails(associateId);
		LOGGER.debug("Associate details retreived from database. Generating token");
		// 3. generate JWT Token
		if (associateDetails != null) {
			String authToken = SecureLogin.createJWT(associateId, -1);
			LOGGER.debug("JWT Token generated for Associate. User can login");
			// construct response with Associate details and JWT token
			response.setAuthToken(authToken);
			response.setCode(200);
			response.setMessage("Login Success");
			response.setResponse(Arrays.asList(associateDetails));
		} else {
			// if given login id is not present in the system
			LOGGER.debug("Login id not recognized by the system");
			// construct message with error details
			response.setCode(403);
			response.setMessage("Log-in Id not valid. Please check with your supervisor for access to the system");
		}
		LOGGER.info(associateId + " login success");
		return response;
	}

	/**
	 * check associates id if per rules
	 * 
	 * @param associateId
	 *            user id to validate
	 * @throws ScrumBoardException
	 *             if user input is not per set rules
	 */
	void validateInput(String associateId) throws ScrumBoardException;

	/**
	 * return the details of associate with given associate ID
	 * 
	 * @param associateId
	 *            associate whose details are to be retrieved
	 * @return {@link Associate} details
	 */
	Associate getAssociateDetails(String associateId);

}
