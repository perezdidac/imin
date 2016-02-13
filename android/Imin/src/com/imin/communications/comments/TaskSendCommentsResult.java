package com.imin.communications.comments;

public class TaskSendCommentsResult {
	
	private boolean success;

	public TaskSendCommentsResult(boolean success) {
		setSuccess(success);
	}
	
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
}

