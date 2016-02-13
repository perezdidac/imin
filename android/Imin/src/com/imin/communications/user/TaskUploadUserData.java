package com.imin.communications.user;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.imin.communications.connection.Connection;
import com.imin.events.pictures.PictureHelper;
import com.imin.user.Files;

public class TaskUploadUserData extends AsyncTask<TaskUploadUserDataParams, Integer, TaskUploadUserDataResult> {

	private TaskUploadUserDataListener taskUploadUserDataListener;

	/** Constructs the class object. **/
	public TaskUploadUserData(TaskUploadUserDataListener taskUploadUserDataListener) {
		//
		this.taskUploadUserDataListener = taskUploadUserDataListener;
	}

	public void setListener(TaskUploadUserDataListener taskUploadUserDataListener) {
		this.taskUploadUserDataListener = taskUploadUserDataListener;
	}

	protected TaskUploadUserDataResult doInBackground(TaskUploadUserDataParams... params) {
		// Create the task result object
		String privateUserId = params[0].getPrivateUserId();
		String publicUserId = params[0].getPublicUserId();
		String username = params[0].getUsername();
		Bitmap bitmap = params[0].getPicture();

		// Retrieve list of events
		String hash = "";
		String base64 = PictureHelper.encode(bitmap);
		if (base64 != null) {
			hash = Files.hashFile(base64.getBytes());
		}
		boolean success = Connection.uploadUserData(privateUserId, publicUserId, base64, hash, username);

		return new TaskUploadUserDataResult(success);
	}

	protected void onPostExecute(TaskUploadUserDataResult result) {
		// Create a message for passing the results to the calling activity
		if (result.isSuccess()) {
			taskUploadUserDataListener.onUserDataUploaded();
		} else {
			taskUploadUserDataListener.onUserDataNotUploaded();
		}
	}

	public interface TaskUploadUserDataListener {
		public void onUserDataUploaded();

		public void onUserDataNotUploaded();
	}

}
