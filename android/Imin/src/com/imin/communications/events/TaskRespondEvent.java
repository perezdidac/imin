package com.imin.communications.events;

import java.util.List;

import android.os.AsyncTask;

import com.imin.communications.connection.Connection;
import com.imin.events.Event;
import com.imin.events.responses.Response;

/** Provides asynchronous event retrieval **/
public class TaskRespondEvent extends AsyncTask<TaskRespondEventParams, Integer, TaskRespondEventResult> {

	private TaskRespondEventListener taskRespondEventListener;

	/** Constructs the class object. **/
	public TaskRespondEvent(TaskRespondEventListener taskRespondEventListener) {
		//
		this.taskRespondEventListener = taskRespondEventListener;
	}

	public void setListener(TaskRespondEventListener taskRespondEventListener) {
		this.taskRespondEventListener = taskRespondEventListener;
	}

	protected TaskRespondEventResult doInBackground(TaskRespondEventParams... params) {
		// Create the task result object
		String privateUserId = params[0].getPrivateUserId();
		Event event = params[0].getEvent();
		List<Response> responses = params[0].getResponses();

		// Retrieve list of events
		boolean responded = Connection.respondEvent(privateUserId, event, responses);

		return new TaskRespondEventResult(responded);
	}

	protected void onPostExecute(TaskRespondEventResult result) {
		// Create a message for passing the results to the calling activity
		if (result.isSuccess()) {
			taskRespondEventListener.onEventResponded();
		} else {
			taskRespondEventListener.onEventNotResponded();
		}
	}

	public interface TaskRespondEventListener {
		public void onEventResponded();

		public void onEventNotResponded();
	}

}
