package com.imin.communications.events;

public class TaskRespondEventResult {

	private boolean success;

	public TaskRespondEventResult(boolean success) {
		this.setSuccess(success);
	}
	
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
	
}
