package com.arif.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.arif.constants.Constants;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Associate {

	String associateId;
	String associateName;
	String role;
	String title;
	List<Project> projects;

	public String getAssociateId() {
		return associateId;
	}

	public void setAssociateId(String associateId) {
		this.associateId = associateId;
	}

	public String getAssociateName() {
		return associateName;
	}

	public void setAssociateName(String associateName) {
		this.associateName = associateName;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public List<Project> getProjects() {
		return projects;
	}

	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getRoleFromTitle(String title) {
		String role = null;
		switch (title) {
		case "Team Member":
			role = new String(Constants.TEAMMEMBER.getValue());
			break;
		case "Scrum Master":
			role = new String(Constants.SCRUMMASTER.getValue());
			break;
		case "Team Lead":
			role = new String(Constants.TEAMLEAD.getValue());
			break;
		default:
			throw new IllegalArgumentException("Invalid title " + title);
		}
		return role;
	}
}
