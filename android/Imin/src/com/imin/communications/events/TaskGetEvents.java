package com.imin.communications.events;

import android.content.Context;
import android.os.AsyncTask;

import com.imin.api.Events;

/** Provides asynchronous event retrieval **/
public class TaskGetEvents extends AsyncTask<TaskGetEventsParams, Integer, TaskGetEventsResult> {

	private Context context;
	private TaskGetEventsListener taskGetEventsListener;

	/** Constructs the class object. **/
	public TaskGetEvents(Context context, TaskGetEventsListener taskGetEventsListener) {
		//
		this.context = context;
		this.taskGetEventsListener = taskGetEventsListener;
	}

	public void setListener(TaskGetEventsListener taskGetEventsListener) {
		this.taskGetEventsListener = taskGetEventsListener;
	}

	protected TaskGetEventsResult doInBackground(TaskGetEventsParams... params) {
		// Create the task result object
		boolean success = Events.syncEvents(context, false);

		return new TaskGetEventsResult(success);
	}

	protected void onPostExecute(TaskGetEventsResult result) {
		// Create a message for passing the results to the calling activity
		if (result.isSuccess()) {
			taskGetEventsListener.onEventsRetrieved();
		} else {
			taskGetEventsListener.onEventsNotRetrieved();
		}
	}

	public interface TaskGetEventsListener {
		public void onEventsRetrieved();

		public void onEventsNotRetrieved();
	}

}
