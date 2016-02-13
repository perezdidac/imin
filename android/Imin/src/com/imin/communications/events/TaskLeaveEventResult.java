package com.imin.communications.events;

public class TaskLeaveEventResult {
	
	private boolean success;

	public TaskLeaveEventResult(boolean success) {
		setSuccess(success);
	}
	
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
}

