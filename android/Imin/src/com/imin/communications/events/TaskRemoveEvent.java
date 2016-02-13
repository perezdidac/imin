package com.imin.communications.events;

import com.imin.communications.connection.Connection;

import android.os.AsyncTask;


/** Provides asynchronous event retrieval **/
public class TaskRemoveEvent extends AsyncTask<TaskRemoveEventParams, Integer, TaskRemoveEventResult> {
	
	private TaskRemoveEventListener taskRemoveEventListener;
	
	/** Constructs the class object. **/
	public TaskRemoveEvent(TaskRemoveEventListener taskRemoveEventListener) {
		//
		this.taskRemoveEventListener = taskRemoveEventListener;
	}
	
	protected TaskRemoveEventResult doInBackground(TaskRemoveEventParams... params) {
		// Get the parameters
		TaskRemoveEventParams parameters = params[0];
		
		// Retrieve list of events
		boolean removed = Connection.removeEvent(parameters.getPrivateUserId(), parameters.getPublicEventId());
		
		return new TaskRemoveEventResult(removed);
	}

	protected void onPostExecute(TaskRemoveEventResult result) {
		// Create a message for passing the results to the calling activity
		if (result.isSuccess()) {
			taskRemoveEventListener.onEventRemoved();
		} else {
			taskRemoveEventListener.onEventNotRemoved();
		}
	}
	
	public interface TaskRemoveEventListener {
		public void onEventRemoved();
		public void onEventNotRemoved();
	}

}
