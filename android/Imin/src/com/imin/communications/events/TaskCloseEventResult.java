package com.imin.communications.events;

public class TaskCloseEventResult {
	
	private boolean success;

	public TaskCloseEventResult(boolean success) {
		setSuccess(success);
	}
	
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
}

