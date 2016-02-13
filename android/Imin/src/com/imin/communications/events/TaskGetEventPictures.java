package com.imin.communications.events;

import android.content.Context;
import android.os.AsyncTask;

import com.imin.api.Events;

/** Provides asynchronous event retrieval **/
public class TaskGetEventPictures extends AsyncTask<TaskGetEventPicturesParams, Integer, TaskGetEventPicturesResult> {

	private Context context;
	private TaskGetEventPicturesListener taskGetEventPicturesListener;

	/** Constructs the class object. **/
	public TaskGetEventPictures(Context context, TaskGetEventPicturesListener taskGetEventPicturesListener) {
		//
		this.context = context;
		this.taskGetEventPicturesListener = taskGetEventPicturesListener;
	}

	public void setListener(TaskGetEventPicturesListener taskGetEventPicturesListener) {
		this.taskGetEventPicturesListener = taskGetEventPicturesListener;
	}

	protected TaskGetEventPicturesResult doInBackground(TaskGetEventPicturesParams... params) {
		// Create the task result object
		boolean success = Events.syncEventPictures(context);

		return new TaskGetEventPicturesResult(success);
	}

	protected void onPostExecute(TaskGetEventPicturesResult result) {
		// Create a message for passing the results to the calling activity
		if (result.isSuccess()) {
			taskGetEventPicturesListener.onEventPicturesRetrieved();
		} else {
			taskGetEventPicturesListener.onEventPicturesNotRetrieved();
		}
	}

	public interface TaskGetEventPicturesListener {
		public void onEventPicturesRetrieved();

		public void onEventPicturesNotRetrieved();
	}

}
