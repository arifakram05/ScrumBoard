package com.arif.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import com.arif.constants.Constants;
import com.arif.model.Jira;
import com.arif.util.ResourceReader;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class JiraServiceImpl {

	private final static Client CLIENT = Client.create();
	private final static Logger LOGGER = Logger.getLogger(ScrumBoardImpl.class);
	private final static String URL = ResourceReader.projectProperties.get("jira");
	//private final static String URL = "https://jira2.cerner.com/rest/api/2/search?jql=assignee=am046752&maxResults=2&fields=summary,description,creator,created,issueType,priority,project,status,reporter";

	public List<Jira> getJiraIssues(String associateId, int maxResults, String status) {
		List<Jira> jiraList = new ArrayList<>();
		
		String processedURL = formatURL(URL, associateId, maxResults);
		WebResource webResource = CLIENT.resource(processedURL);
		ClientResponse response = webResource.accept(Constants.MEDIATYPE.getValue()).get(ClientResponse.class);

		if (response.getStatus() != 200) {
			throw new RuntimeException("Error : " + response.getStatus());
		}

		String jiraResponse = response.getEntity(String.class);
		LOGGER.debug("Response JSON from JIRA :" + jiraResponse);
		
		try {
			processJson(jiraResponse, jiraList);
		} catch (Exception e) {
			LOGGER.error("Error occurred while processing results from JIRA ",e);
		}
		return jiraList;
	}

	private String formatURL(String URL, String associateId, int maxResults) {
		return MessageFormat.format(URL, new Object[] { associateId, maxResults});
	}
	private void processJson(String json, List<Jira> jiraList) throws Exception {
		JSONObject jsonObject = new JSONObject(json);
		if (jsonObject != null) {
			JSONArray issuesArray = jsonObject.getJSONArray("issues");
			for (int i = 0; i < issuesArray.length(); i++) {
				Jira jira = new Jira();

				JSONObject issuesObject = issuesArray.optJSONObject(i);

				jira.setKey(issuesObject.opt("key").toString());
				jira.setSummary(issuesObject.optJSONObject("fields").opt("summary").toString());
				jira.setDescription(issuesObject.optJSONObject("fields").opt("description").toString());
				jira.setDateCreated(issuesObject.optJSONObject("fields").opt("created").toString());
				jira.setProject(issuesObject.optJSONObject("fields").optJSONObject("project").opt("name").toString());
				jira.setCreator(issuesObject.optJSONObject("fields").optJSONObject("creator").opt("displayName").toString());
				jira.setReporter(issuesObject.optJSONObject("fields").optJSONObject("reporter").opt("displayName").toString());
				jira.setPriority(issuesObject.optJSONObject("fields").optJSONObject("priority").opt("name").toString());
				jira.setStatus(issuesObject.optJSONObject("fields").optJSONObject("status").opt("name").toString());

				jiraList.add(jira);
			}
		}
	}

}
