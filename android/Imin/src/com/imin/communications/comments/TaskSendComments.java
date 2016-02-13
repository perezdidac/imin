package com.imin.communications.comments;

import com.imin.communications.connection.Connection;

import android.os.AsyncTask;


public class TaskSendComments extends AsyncTask<TaskSendCommentsParams, Integer, TaskSendCommentsResult> {
	
	private TaskSendCommentsListener taskSendCommentsListener;
	
	/** Constructs the class object. **/
	public TaskSendComments(TaskSendCommentsListener taskSendCommentsListener) {
		//
		this.taskSendCommentsListener = taskSendCommentsListener;
	}

	public void setListener(TaskSendCommentsListener taskSendCommentsListener) {
		this.taskSendCommentsListener = taskSendCommentsListener;
	}

	protected TaskSendCommentsResult doInBackground(TaskSendCommentsParams... params) {
		// Get the parameters
		TaskSendCommentsParams parameters = params[0];
		
		// Retrieve list of events
		boolean success = Connection.sendComments(parameters.getPrivateUserId(), parameters.getName(), parameters.getComments());
		
		return new TaskSendCommentsResult(success);
	}

	protected void onPostExecute(TaskSendCommentsResult result) {
		// Create a message for passing the results to the calling activity
		if (result.isSuccess()) {
			taskSendCommentsListener.onCommentsSent();
		} else {
			taskSendCommentsListener.onCommentsNotSent();
		}
	}
	
	public interface TaskSendCommentsListener {
		public void onCommentsSent();
		public void onCommentsNotSent();
	}

}
