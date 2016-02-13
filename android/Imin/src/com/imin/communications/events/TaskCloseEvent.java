package com.imin.communications.events;

import android.os.AsyncTask;

import com.imin.communications.connection.Connection;

public class TaskCloseEvent extends AsyncTask<TaskCloseEventParams, Integer, TaskCloseEventResult> {

	private TaskCloseEventListener taskCloseEventListener;
	private boolean close;

	/** Constructs the class object. **/
	public TaskCloseEvent(TaskCloseEventListener taskCloseEventListener) {
		//
		this.taskCloseEventListener = taskCloseEventListener;
	}

	public void setListener(TaskCloseEventListener taskCloseEventListener) {
		this.taskCloseEventListener = taskCloseEventListener;
	}

	protected TaskCloseEventResult doInBackground(TaskCloseEventParams... params) {
		// Get the parameters
		TaskCloseEventParams parameters = params[0];

		close = parameters.isClosed();

		// Retrieve list of events
		boolean success = Connection.closeEvent(parameters.getPrivateUserId(), parameters.getPublicEventId(),
				parameters.isClosed(), parameters.getFinalDateTimeProposalId(), parameters.getFinalLocationProposalId());

		return new TaskCloseEventResult(success);
	}

	protected void onPostExecute(TaskCloseEventResult result) {
		// Create a message for passing the results to the calling activity
		if (result.isSuccess()) {
			if (close) {
				taskCloseEventListener.onEventClosed();
			} else {
				taskCloseEventListener.onEventReopened();
			}
		} else {
			if (close) {
				taskCloseEventListener.onEventNotClosed();
			} else {
				taskCloseEventListener.onEventNotReopened();
			}
		}
	}

	public interface TaskCloseEventListener {
		public void onEventClosed();

		public void onEventNotClosed();

		public void onEventReopened();

		public void onEventNotReopened();

	}

}
