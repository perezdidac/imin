package com.imin.communications.contacts;

public class TaskDownloadContactsDataResult {

	private boolean success;

	public TaskDownloadContactsDataResult(boolean success) {
		this.setSuccess(success);
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

}
