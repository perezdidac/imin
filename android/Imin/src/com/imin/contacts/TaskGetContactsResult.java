package com.imin.contacts;

import java.util.List;


/** Represents an object that contains the result of the routes task. **/
public class TaskGetContactsResult {

	private List<Contact> contacts;
	private boolean success;

	public TaskGetContactsResult(List<Contact> contacts, boolean success) {
		this.setContacts(contacts);
		this.setSuccess(success);
	}

	public List<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

}
