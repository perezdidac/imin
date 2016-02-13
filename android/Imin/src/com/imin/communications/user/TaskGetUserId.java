package com.imin.communications.user;

import java.util.List;

import android.os.AsyncTask;

import com.imin.communications.connection.Connection;

/** Provides asynchronous event retrieval **/
public class TaskGetUserId extends AsyncTask<TaskGetUserIdParams, Integer, TaskGetUserIdResult> {

	private TaskGetUserIdListener taskGetUserIdListener;

	/** Constructs the class object. **/
	public TaskGetUserId(TaskGetUserIdListener taskGetUserIdListener) {
		//
		this.taskGetUserIdListener = taskGetUserIdListener;
	}

	public void setListener(TaskGetUserIdListener taskGetUserIdListener) {
		this.taskGetUserIdListener = taskGetUserIdListener;
	}

	protected TaskGetUserIdResult doInBackground(TaskGetUserIdParams... search) {
		// Retrieve the user id
		String deviceId = search[0].getDeviceId();

		List<String> privateAndPublicUserId = Connection.getPrivateAndPublicUserId(deviceId);

		if (privateAndPublicUserId.size() == 2) {
			return new TaskGetUserIdResult(true, privateAndPublicUserId.get(0), privateAndPublicUserId.get(1));
		} else {
			return new TaskGetUserIdResult(false, null, null);
		}
	}

	protected void onPostExecute(TaskGetUserIdResult result) {
		// Create a message for passing the results to the calling activity
		if (result.isSuccess()) {
			taskGetUserIdListener.onUserIdReceived(result.getPrivateUserId(), result.getPublicUserId());
		} else {
			taskGetUserIdListener.onUserIdNotReceived();
		}
	}

	public interface TaskGetUserIdListener {
		public void onUserIdReceived(String privateUserId, String publicUserId);

		public void onUserIdNotReceived();
	}

}
