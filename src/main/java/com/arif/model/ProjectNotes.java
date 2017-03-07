package com.arif.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectNotes {

	private Object _id;
	private String title;
	private String createdOn;
	private String lastUpdatedOn;
	/**
	 * associate Id who created the notes
	 */
	private String author;
	/**
	 * list of all associate Ids who edited the notes
	 */
	private List<String> editors;
	private String notes;

	public Object get_id() {
		return _id;
	}

	public void set_id(Object _id) {
		this._id = _id;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthorId(String author) {
		this.author = author;
	}

	public List<String> getEditors() {
		return editors;
	}

	public void setEditors(List<String> editors) {
		this.editors = editors;
	}

	public String getLastUpdatedOn() {
		return lastUpdatedOn;
	}

	public void setLastUpdatedOn(String lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}
}
