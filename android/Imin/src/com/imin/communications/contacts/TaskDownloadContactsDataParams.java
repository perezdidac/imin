package com.imin.communications.contacts;

import java.util.List;

import com.imin.contacts.Contact;

public class TaskDownloadContactsDataParams {

	private List<Contact> contacts;

	public TaskDownloadContactsDataParams(List<Contact> contacts) {
		this.setContacts(contacts);
	}

	public List<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}

}
