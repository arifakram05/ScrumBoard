package com.arif.constants;

public enum Constants {

	//patterns
	NUMBERS_ONLY("[0-9]+"),

	//object id
	OBJECTID("_id"),

	//collections
	ASSOCIATES("associates"),
	PROJECTS("projects"),

	//fields in associates
	ASSOCIATEID("associateId"),
	ASSOCIATENAME("associateName"),
	ROLE("role"),

	//fields in projects
	PROJECTNAME("projectName"),
	PROJECTID("projectId"),
	DATECREATED("dateCreated"),
	PROJECTSTATUS("projectStatus"),

	ACTIVE("active"),
	INACTIVE("inactive"),

	//fields
	ACTUALDATE("actualDate"),
	SCRUMDETAILS("scrumDetails"),
	STARTDATE("startDate"),
	ENDDATE("endDate"),
	SCRUMNAME("scrumName"),
	YESTERDAY("yesterday"),
	TODAY("today"),
	ROADBLOCKS("roadblocks"),

	//validatons
	RESTRICT("[<>\"!\':{}]"),

	//notes
	NOTES("Notes"),
	TITLE("title"),
	CREATEDON("createdOn"),
	LASTUPDATEDON("lastUpdatedOn"),
	AUTHOR("author"),
	EDITORS("editors"),
	NOTESFIELD("notes"),

	//title
	TEAMMEMBERTITLE("Team Member"),

	//associate role
	TEAMMEMBER("member"),
	SCRUMMASTER("admin"),
	TEAMLEAD("lead");

	private String value;

	private Constants(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
