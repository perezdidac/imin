package com.imin.contacts;

import java.util.ArrayList;
import java.util.List;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;

/** Provides asynchronous contact retrieval. **/
public class TaskGetContacts extends AsyncTask<TaskGetContactsParams, Integer, TaskGetContactsResult> {

	private TaskGetContactsListener taskGetContactsListener;
	private ContentResolver contentResolver;

	/** Constructs the class object. **/
	public TaskGetContacts(TaskGetContactsListener taskGetContactsListener, ContentResolver contentResolver) {
		//
		this.taskGetContactsListener = taskGetContactsListener;
		this.contentResolver = contentResolver;
	}

	protected TaskGetContactsResult doInBackground(TaskGetContactsParams... search) {
		// Create the task result object
		// Prepare the list of contacts to be returned
		List<Contact> contacts = new ArrayList<Contact>();

		// Retrieve list of contacts
		Cursor cur = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		if (cur.getCount() > 0) {
			while (cur.moveToNext()) {
				// Contact retrieved
				long contactId = cur.getLong(cur.getColumnIndex(ContactsContract.Contacts._ID));
				String contactName = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				Uri contactPhotoUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
				
				/*
				List<String> emails = new ArrayList<String>();
				List<ContactPhone> phones = new ArrayList<ContactPhone>();
				
				// Get all emails
				Cursor emailCur = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
						Phone.CONTACT_ID + " = " + contactId, null, null);
				while (emailCur.moveToNext()) {
					String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
					emails.add(email);
				}
				emailCur.close();

				if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
					// Get all phone numbers
					Cursor phoneCur = contentResolver.query(Phone.CONTENT_URI, null, Phone.CONTACT_ID + " = "
							+ contactId, null, null);
					while (phoneCur.moveToNext()) {
						String number = phoneCur.getString(phoneCur.getColumnIndex(Phone.NUMBER));
						int type = phoneCur.getInt(phoneCur.getColumnIndex(Phone.TYPE));
						switch (type) {
						case Phone.TYPE_HOME:
							ContactPhone homePhone = new ContactPhone(number, type);
							phones.add(homePhone);
							break;
						case Phone.TYPE_MOBILE:
							ContactPhone mobilePhone = new ContactPhone(number, type);
							phones.add(mobilePhone);
							break;
						case Phone.TYPE_WORK:
							ContactPhone workPhone = new ContactPhone(number, type);
							phones.add(workPhone);
							break;
						}
					}
					phoneCur.close();
				}
				*/
				
				// Create the contact and add it into the list
				Contact contact = new Contact(contactName, contactPhotoUri, null, null,
						Contact.CONTACT_HAS_PICTURE_UNKNOWN);
				contacts.add(contact);
			}
		}

		cur.close();

		// It always will be true
		boolean success = true;

		return new TaskGetContactsResult(contacts, success);
	}

	protected void onPostExecute(TaskGetContactsResult result) {
		// Create a message for passing the results to the calling activity
		if (result.isSuccess()) {
			taskGetContactsListener.onContactsReceived(result.getContacts());
		} else {
			taskGetContactsListener.onContactsNotReceived();
		}
	}

	public interface TaskGetContactsListener {
		public void onContactsReceived(List<Contact> contacts);

		public void onContactsNotReceived();
	}

}
