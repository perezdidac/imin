package com.imin.communications.events;

public class TaskGetEventsResult {

	private boolean success;

	public TaskGetEventsResult(boolean success) {
		this.setSuccess(success);
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

}
