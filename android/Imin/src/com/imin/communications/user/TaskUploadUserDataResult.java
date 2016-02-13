package com.imin.communications.user;


public class TaskUploadUserDataResult {

	private boolean success;
	
	public TaskUploadUserDataResult(boolean success) {
		this.setSuccess(success);
	}
	
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
	
}
