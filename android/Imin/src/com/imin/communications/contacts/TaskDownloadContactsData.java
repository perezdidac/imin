package com.imin.communications.contacts;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.imin.communications.connection.Connection;
import com.imin.contacts.Contact;
import com.imin.events.pictures.FileCache;
import com.imin.events.pictures.PictureHelper;
import com.imin.user.Files;

public class TaskDownloadContactsData extends
		AsyncTask<TaskDownloadContactsDataParams, Contact, TaskDownloadContactsDataResult> {

	private Context context;
	private TaskDownloadContactsDataListener taskDownloadContactsDataListener;

	/** Constructs the class object. **/
	public TaskDownloadContactsData(Context context, TaskDownloadContactsDataListener taskDownloadContactsDataListener) {
		//
		this.context = context;
		this.taskDownloadContactsDataListener = taskDownloadContactsDataListener;
	}

	protected TaskDownloadContactsDataResult doInBackground(TaskDownloadContactsDataParams... params) {
		// Retrieve an event
		List<Contact> contacts = params[0].getContacts();

		// For each contact, check if bitmap is null and
		// try to download the picture
		for (Contact contact : contacts) {
			if (contact.getPhoto() == null) {
				// Must try to download, check if we have cached the image
				// Get the picture
				String base64 = null;

				// Before download the picture, check if we have it
				// in the file cache
				String hash = Connection.hashUserPicture(contact.getPublicUserId());
				FileCache fileCache = new FileCache(context);
				boolean inFileCache = true;

				if (hash != null) {
					// Check if we have same hash
					byte[] array = fileCache.get(hash);
					if (array != null) {
						// Okay, we already had the file
						base64 = new String(array);
					} else {
						// We did not had the file, get it and add into the
						// file cache as a new file
						base64 = Connection.downloadUserData(contact);
						inFileCache = false;
					}
				} else {
					base64 = Connection.downloadUserData(contact);
					inFileCache = false;
				}

				if (base64 != null) {
					if (!inFileCache) {
						hash = Files.hashFile(base64.getBytes());
						fileCache.add(hash, base64.getBytes());
					}
					Bitmap bitmap = PictureHelper.decode(base64);

                    if (bitmap != null) {
                        contact.setPhoto(bitmap);
                    }
					
					// Update layouts
					publishProgress(contact);
				}
			}
		}

        boolean success = true;

		return new TaskDownloadContactsDataResult(success);
	}
	
	protected void onProgressUpdate(Contact... contact) {
		// Update layouts
		taskDownloadContactsDataListener.onContactDataReceived(contact[0]);
    }
	
	protected void onPostExecute(TaskDownloadContactsDataResult result) {
		// Create a message for passing the results to the calling activity
		if (result.isSuccess()) {
			taskDownloadContactsDataListener.onContactsDataReceived();
		} else {
			taskDownloadContactsDataListener.onContactsDataNotReceived();
		}
	}

	public interface TaskDownloadContactsDataListener {

		public void onContactDataReceived(Contact contact);

		public void onContactsDataReceived();

		public void onContactsDataNotReceived();
	}

}
