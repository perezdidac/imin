package com.imin.communications.events;

public class TaskGetEventPicturesResult {

	private boolean success;

	public TaskGetEventPicturesResult(boolean success) {
		this.setSuccess(success);
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

}
