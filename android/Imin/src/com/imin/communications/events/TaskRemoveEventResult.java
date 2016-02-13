package com.imin.communications.events;

public class TaskRemoveEventResult {
	
	private boolean success;

	public TaskRemoveEventResult(boolean success) {
		setSuccess(success);
	}
	
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
}

