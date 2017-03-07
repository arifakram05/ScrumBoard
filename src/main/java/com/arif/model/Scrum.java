package com.arif.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Scrum {

	String scrumName;
	String startDate;
	String endDate;
	String actualDate;
	String projectName;
	List<ScrumDetails> scrumDetails;

	public String getScrumName() {
		return scrumName;
	}

	public void setScrumName(String scrumName) {
		this.scrumName = scrumName;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public void setActualDate(String actualDate) {
		this.actualDate = actualDate;
	}

	public String getActualDate() {
		return actualDate;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public List<ScrumDetails> getScrumDetails() {
		return scrumDetails;
	}

	public void setScrumDetails(List<ScrumDetails> scrumDetails) {
		this.scrumDetails = scrumDetails;
	}
}
