package com.arif.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;

import com.arif.interfaces.ScrumBoard;
import com.arif.model.Associate;
import com.arif.model.Project;
import com.arif.model.ProjectNotes;
import com.arif.model.Scrum;
import com.arif.model.ScrumDetails;
import com.arif.response.ScrumBoardResponse;
import com.arif.util.SecureLogin;

/**
 * starting point for all the system services<br/>
 * First stop for request validation (validates user's JWT token)<br/>
 * constructs response and sends back
 * 
 * @author arifakrammohammed
 *
 */
public class ScrumBoardImpl implements ScrumBoard {

	final static Logger LOGGER = Logger.getLogger(ScrumBoardImpl.class);

	/**
	 * check whether the JWT Token provided by the user is valid
	 * 
	 * @param token
	 * @param associateIdToVerify
	 * @return
	 */
	private boolean validateToken(String token, String associateIdToVerify) {
		return SecureLogin.isTokenValid(token, associateIdToVerify);
	}

	@Override
	public ScrumBoardResponse<Associate> login(String associateId) {
		ScrumBoardResponse<Associate> response = null;
		try {
			LOGGER.info("Logging in " + associateId);
			response = getLoginServiceInstance().login(associateId);
			LOGGER.info("Logging in " + associateId + " success");
		} catch (Exception e) {
			LOGGER.error("Error while logging in the user " + associateId, e);
			response.setCode(500);
			response.setMessage("Error Occurred while logging you in");
		}
		return response;
	}

	@Override
	public ScrumBoardResponse<Void> addProject(String projectDetails, String associateId, String token) {
		ScrumBoardResponse<Void> response = null;
		try {
			LOGGER.info("Adds project request recieved from " + associateId);
			Project project;
			// validate token
			if (validateToken(token, associateId)) {
				// as token is valid, proceed with request
				LOGGER.info(
						"Token is valid for associate " + associateId + ". Proceeding ahead with processing request");
				project = new ObjectMapper().readValue(projectDetails, Project.class);
				response = getProjectServiceInstance().addProject(project, associateId);
				LOGGER.info("Project successfully added - " + project.getProjectName());
			} else {
				// as token is invalid, do not process the request
				LOGGER.info(
						"Token is not valid for associate " + associateId + ". Cannot proceed ahead with the request");
				response = new ScrumBoardResponse<>();
				response.setCode(403);
				response.setMessage(
						"System cannot proceed with your operation for security reasons. Please re-login and perform the operation again");
			}
		} catch (Exception e) {
			LOGGER.error("Error while adding project ", e);
			response = new ScrumBoardResponse<>();
			response.setCode(500);
			response.setMessage("Error Occurred. Could not save new project. Re-Login and try the operation again");
		}

		return response;
	}

	@Override
	public ScrumBoardResponse<Project> getAllProjects() {
		ScrumBoardResponse<Project> response = new ScrumBoardResponse<>();
		List<Project> projectList = null;
		try {
			LOGGER.info("Preparing to fetch all available projects");
			projectList = getProjectServiceInstance().getAllProjects();
			LOGGER.info("All projects successfully retrieved");
			response.setCode(200);
			response.setMessage("List of all projects");
			response.setResponse(projectList);
		} catch (Exception e) {
			LOGGER.error("Error while fetching all projects ", e);
			response.setCode(500);
			response.setMessage("Error occurred. Could not get project list");
		}
		return response;
	}

	@Override
	public ScrumBoardResponse<Void> addAssociate(String associateDetails, String associateId, String token) {
		ScrumBoardResponse<Void> response;
		Associate associate;
		LOGGER.info("Preparing to add or update an associate");
		try {
			// validate token
			if (validateToken(token, associateId)) {
				LOGGER.info(
						"Token is valid for associate " + associateId + ". Proceeding ahead with processing request");
				// convert JSON to POJO
				associate = new ObjectMapper().readValue(associateDetails, Associate.class);
				// as token is valid, proceed with request
				response = getAssociateServiceInstance().addAssociate(associate);
				LOGGER.info("Associated added/updated successfully");
			} else {
				// as token is invalid, do not process the request
				LOGGER.info(
						"Token is not valid for associate " + associateId + ". Cannot proceed ahead with the request");
				response = new ScrumBoardResponse<>();
				response.setCode(403);
				response.setMessage(
						"System cannot proceed with your operation for security reasons. Please re-login and perform the operation again");
			}
		} catch (Exception e) {
			LOGGER.error("Error occurred while processing request ", e);
			response = new ScrumBoardResponse<>();
			response.setCode(500);
			response.setMessage(
					"Error Ocurred. Could not add an associate to the system. Re-Login and try the operation again");
		}

		return response;
	}

	@Override
	public ScrumBoardResponse<Void> addScrum(String scrumDetails, String associateId, String token) {
		ScrumBoardResponse<Void> response;
		Scrum scrum;
		LOGGER.info("Request for add new scrum from " + associateId);
		try {
			// validate token
			if (validateToken(token, associateId)) {
				LOGGER.info(
						"Token is valid for associate " + associateId + ". Proceeding ahead with processing request");
				// convert JSON to POJO
				scrum = new ObjectMapper().readValue(scrumDetails, Scrum.class);
				// as token is valid, proceed with request
				response = getScrumServiceInstance().addScrum(scrum);
				LOGGER.info(
						"A new scrum added successfully between " + scrum.getStartDate() + " " + scrum.getEndDate());
			} else {
				// as token is invalid, do not process the request
				LOGGER.info(
						"Token is not valid for associate " + associateId + ". Cannot proceed ahead with the request");
				response = new ScrumBoardResponse<>();
				response.setCode(403);
				response.setMessage(
						"System cannot proceed with your operation for security reasons. Please re-login and perform the operation again");
			}
		} catch (Exception e) {
			LOGGER.error("Error occurred while processing request ", e);
			response = new ScrumBoardResponse<>();
			response.setCode(500);
			response.setMessage("Error Occurred. Could not add a Scrum to the system.");
		}

		return response;
	}

	@Override
	public ScrumBoardResponse<Void> updateScrum(String scrumDetails, String associateDetails, String associateId, String token) {
		ScrumBoardResponse<Void> response = new ScrumBoardResponse<>();;
		Scrum scrum;
		Associate associate;
		LOGGER.info("Request for udpate scrum from " + associateId);
		try {
			// validate token
			if (validateToken(token, associateId)) {
				LOGGER.info(
						"Token is valid for associate " + associateId + ". Proceeding ahead with processing request");
				// convert JSON to POJO
				scrum = new ObjectMapper().readValue(scrumDetails, Scrum.class);
				associate = new ObjectMapper().readValue(associateDetails, Associate.class);
				// as token is valid, proceed with request
				response = getScrumServiceInstance().updateScrum(scrum, associate);
				LOGGER.info(
						"Associate has been added successfully for scrum in the project "+  scrum.getProjectName() +" between " + scrum.getStartDate() + " " + scrum.getEndDate());
			} else {
				// as token is invalid, do not process the request
				LOGGER.info(
						"Token is not valid for associate " + associateId + ". Cannot proceed ahead with the request");
				response.setCode(403);
				response.setMessage(
						"System cannot proceed with your operation for security reasons. Please re-login and perform the operation again");
			}
		} catch (Exception e) {
			LOGGER.error("Error occurred while processing request ", e);
			response.setCode(500);
			response.setMessage("Error Occurred. Could not add associate to the Scrum");
		}

		return response;
	}

	@Override
	public ScrumBoardResponse<Scrum> getScrumDetails(String scrumDate, String projectList, String associateId,
			String token) {
		ScrumBoardResponse<Scrum> response = null;
		List<Scrum> scrumDetails;
		List<Project> projects;
		LOGGER.info("Request received to get scrum detail for the date " + scrumDate + " by " + associateId);
		try {
			// validate token
			if (validateToken(token, associateId)) {
				// as token is valid, proceed with request
				LOGGER.info(
						"Token is valid for associate " + associateId + ". Proceeding ahead with processing request");
				// convert JSON to POJO
				projects = new ObjectMapper().readValue(projectList,
						TypeFactory.collectionType(List.class, Project.class));
				scrumDetails = getScrumServiceInstance().getScrumDetails(scrumDate, projects);
				response = new ScrumBoardResponse<>();
				response.setCode(200);
				if (scrumDetails.isEmpty()) {
					response.setMessage("No Scrum records found for " + scrumDate);
				} else {
					response.setMessage("Showing Scrum details for " + scrumDate);
					response.setResponse(scrumDetails);
				}
				LOGGER.info("Retrieved scrum details successfully");
			} else {
				// as token is invalid, do not process the request
				LOGGER.info(
						"Token is not valid for associate " + associateId + ". Cannot proceed ahead with the request");
				response = new ScrumBoardResponse<>();
				response.setCode(403);
				response.setMessage(
						"System cannot proceed with your operation for security reasons. Please re-login and perform the operation again");
			}
		} catch (Exception e) {
			LOGGER.error("Error occurred while fetching scrum details for a particular date", e);
			response = new ScrumBoardResponse<>();
			response.setCode(500);
			response.setMessage(
					"Error occurred. Could not get Scrum details. Please check the scrum date or try the operation after re-login");
		}
		return response;
	}

	@Override
	public ScrumBoardResponse<Scrum> getFilteredScrumDetails(String scrumDate, String projectName, String associateId,
			String token) {
		ScrumBoardResponse<Scrum> response = null;
		List<Scrum> scrumDetails;
		LOGGER.info("Request received to get scrum detail for the date " + scrumDate + " by " + associateId
				+ " for project " + projectName);
		try {
			// validate token
			if (validateToken(token, associateId)) {
				// as token is valid, proceed with request
				LOGGER.info(
						"Token is valid for associate " + associateId + ". Proceeding ahead with processing request");
				scrumDetails = getScrumServiceInstance().getFilteredScrumDetails(scrumDate, projectName);
				response = new ScrumBoardResponse<>();
				response.setCode(200);
				if (scrumDetails.isEmpty()) {
					response.setMessage("No Scrum records found for " + projectName + " on " + scrumDate);
				} else {
					response.setMessage("Showing Scrum details for " + projectName + " on " + scrumDate);
					response.setResponse(scrumDetails);
				}
				LOGGER.info("Retrieved scrum details successfully");
			} else {
				// as token is invalid, do not process the request
				LOGGER.info(
						"Token is not valid for associate " + associateId + ". Cannot proceed ahead with the request");
				response = new ScrumBoardResponse<>();
				response.setCode(403);
				response.setMessage(
						"System cannot proceed with your operation for security reasons. Please re-login and perform the operation again");
			}
		} catch (Exception e) {
			LOGGER.error("Error occurred while fetching scrum details for a particular date", e);
			response = new ScrumBoardResponse<>();
			response.setCode(500);
			response.setMessage("Error occurred. Could not get Scrum details. Please try the operation after re-login");
		}
		return response;
	}

	@Override
	public ScrumBoardResponse<Scrum> getRecentScrumRecord(String projectName) {
		ScrumBoardResponse<Scrum> response = new ScrumBoardResponse<>();
		List<Scrum> scrumList = null;
		try {
			LOGGER.info("Preparing to fetch the most recent scrum record for the project " + projectName);
			scrumList = getScrumServiceInstance().getRecentScrumRecord(projectName);
			LOGGER.info("Most recent scrum record retrieved successfully");
			response.setCode(200);
			response.setMessage("Recent scrum record the project " + projectName + " is retrieved");
			response.setResponse(scrumList);
		} catch (Exception e) {
			LOGGER.error("Error while fetching the most recent scrum record ", e);
			response.setCode(500);
			response.setMessage("Error occurred. Could not get the most recent scrum record for " + projectName);
		}
		return response;
	}

	@Override
	public ScrumBoardResponse<Void> saveDailyScrumUpdate(String scrumDetails, String date, String projectName,
			String associateId, String token) {
		ScrumBoardResponse<Void> response = null;
		LOGGER.info("Request to save scrum update of " + associateId + " for date " + date);
		try {
			// validate token
			if (validateToken(token, associateId)) {
				// as token is valid, proceed with request
				LOGGER.info(
						"Token is valid for associate " + associateId + ". Proceeding ahead with processing request");
				ScrumDetails scrumDetailsPojo = new ObjectMapper().readValue(scrumDetails, ScrumDetails.class);
				getScrumServiceInstance().saveDailyScrumUpdate(scrumDetailsPojo, date, projectName);
				response = new ScrumBoardResponse<>();
				response.setCode(200);
				response.setMessage("Saved");
				LOGGER.info("Scrum update for the given date saved successfully");
			} else {
				// as token is invalid, do not process the request
				LOGGER.info(
						"Token is not valid for associate " + associateId + ". Cannot proceed ahead with the request");
				response = new ScrumBoardResponse<>();
				response.setCode(403);
				response.setMessage(
						"System cannot proceed with your operation for security reasons. Please re-login and perform the operation again");
			}
		} catch (Exception e) {
			LOGGER.error("Error occurred while saving scrum update for " + associateId, e);
			response = new ScrumBoardResponse<>();
			response.setCode(500);
			response.setMessage("Error Occurred. Could not save Scrum details. Please re-login and try saving");
		}
		return response;
	}

	@Override
	public ScrumBoardResponse<ProjectNotes> getAllProjectNotes(String projectName) {
		ScrumBoardResponse<ProjectNotes> response = new ScrumBoardResponse<>();
		List<ProjectNotes> projectNotesList = null;
		try {
			LOGGER.info("Preparing to fetch all available notes for project " + projectName);
			projectNotesList = getProjectNotesServiceInstance().getAllProjectNotes(projectName);
			LOGGER.info("All project notes successfully retrieved");
			response.setCode(200);
			response.setMessage("List of all available project notes for " + projectName);
			response.setResponse(projectNotesList);
		} catch (Exception e) {
			LOGGER.error("Error while fetching all projects notes ", e);
			response.setCode(500);
			response.setMessage("Error occurred. Could not get project notes list");
		}
		return response;
	}

	@Override
	public ScrumBoardResponse<Void> saveNewProjectNotes(String projectNotes, String projectName, String associateId,
			String token) {
		ScrumBoardResponse<Void> response;
		ProjectNotes projectNotesPojo;
		LOGGER.info("Preparing to add new project notes");
		try {
			// validate token
			if (validateToken(token, associateId)) {
				LOGGER.info(
						"Token is valid for associate " + associateId + ". Proceeding ahead with processing request");
				// convert JSON to POJO
				projectNotesPojo = new ObjectMapper().readValue(projectNotes, ProjectNotes.class);
				// as token is valid, proceed with request
				response = getProjectNotesServiceInstance().saveNewProjectNotes(projectNotesPojo, projectName,
						associateId);
				LOGGER.info("Add project note reqeust processed successfully");
			} else {
				// as token is invalid, do not process the request
				LOGGER.info(
						"Token is not valid for associate " + associateId + ". Cannot proceed ahead with the request");
				response = new ScrumBoardResponse<>();
				response.setCode(403);
				response.setMessage(
						"System cannot proceed with your operation for security reasons. Please re-login and perform the operation again");
			}
		} catch (Exception e) {
			LOGGER.error("Error occurred while processing request ", e);
			response = new ScrumBoardResponse<>();
			response.setCode(500);
			response.setMessage(
					"Error Ocurred. Could not add an associate to the system. Re-Login and try the operation again");
		}

		return response;
	}

	@Override
	public ScrumBoardResponse<Void> deleteProjectNotes(String projectNotes, String projectName, String associateId,
			String token) {
		ScrumBoardResponse<Void> response;
		ProjectNotes projectNotesPojo;
		LOGGER.info("Preparing to delete project notes from a project");
		try {
			// validate token
			if (validateToken(token, associateId)) {
				LOGGER.info(
						"Token is valid for associate " + associateId + ". Proceeding ahead with processing request");
				// convert JSON to POJO
				projectNotesPojo = new ObjectMapper().readValue(projectNotes, ProjectNotes.class);
				// as token is valid, proceed with request
				response = getProjectNotesServiceInstance().deleteProjectNotes(projectNotesPojo, projectName,
						associateId);
				LOGGER.info("Project notes deleted successfully");
			} else {
				// as token is invalid, do not process the request
				LOGGER.info(
						"Token is not valid for associate " + associateId + ". Cannot proceed ahead with the request");
				response = new ScrumBoardResponse<>();
				response.setCode(403);
				response.setMessage(
						"System cannot proceed with your operation for security reasons. Please re-login and perform the operation again");
			}
		} catch (Exception e) {
			LOGGER.error("Error occurred while processing request ", e);
			response = new ScrumBoardResponse<>();
			response.setCode(500);
			response.setMessage(
					"Error Ocurred. Could not add an associate to the system. Re-Login and try the operation again");
		}
		return response;
	}

	@Override
	public ScrumBoardResponse<Void> updateProjectNotes(String projectNotes, String projectName, String associateId,
			String token) {
		ScrumBoardResponse<Void> response;
		ProjectNotes projectNotesPojo;
		LOGGER.info("Preparing to edit project notes");
		try {
			// validate token
			if (validateToken(token, associateId)) {
				LOGGER.info(
						"Token is valid for associate " + associateId + ". Proceeding ahead with processing request");
				// convert JSON to POJO
				projectNotesPojo = new ObjectMapper().readValue(projectNotes, ProjectNotes.class);
				// as token is valid, proceed with request
				response = getProjectNotesServiceInstance().updateProjectNotes(projectNotesPojo, projectName,
						associateId);
				LOGGER.info("Project notes updated successfully");
			} else {
				// as token is invalid, do not process the request
				LOGGER.info(
						"Token is not valid for associate " + associateId + ". Cannot proceed ahead with the request");
				response = new ScrumBoardResponse<>();
				response.setCode(403);
				response.setMessage(
						"System cannot proceed with your operation for security reasons. Please re-login and perform the operation again");
			}
		} catch (Exception e) {
			LOGGER.error("Error occurred while processing request ", e);
			response = new ScrumBoardResponse<>();
			response.setCode(500);
			response.setMessage(
					"Error Ocurred. Could not add an associate to the system. Re-Login and try the operation again");
		}
		return response;
	}

	@Override
	public ScrumBoardResponse<Associate> searchAssociates(String searchText) {
		ScrumBoardResponse<Associate> response = new ScrumBoardResponse<>();
		List<Associate> associateList = null;
		try {
			LOGGER.info("Preparing to search for associates with pattern: "+searchText);
			associateList = getAssociateServiceInstance().searchAssociates(searchText);
			LOGGER.info("Associates searched and retrieved number is "+associateList.size());
			response.setCode(200);
			response.setMessage("List of all associates that match search criteria");
			response.setResponse(associateList);
		} catch (Exception e) {
			LOGGER.error("Error while fetching associates per search criteria", e);
			response.setCode(500);
			response.setMessage("Error occurred. Could not retrieve associates per your search criteria");
		}
		return response;
	}

}
