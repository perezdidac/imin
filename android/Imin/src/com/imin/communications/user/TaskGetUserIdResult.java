package com.imin.communications.user;

public class TaskGetUserIdResult {

	private boolean success;
	private String privateUserId;
	private String publicUserId;

	public TaskGetUserIdResult(boolean success, String privateUserId, String publicUserId) {
		this.setSuccess(success);
		this.setPrivateUserId(privateUserId);
		this.setPublicUserId(publicUserId);
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getPrivateUserId() {
		return privateUserId;
	}

	public void setPrivateUserId(String privateUserId) {
		this.privateUserId = privateUserId;
	}

	public String getPublicUserId() {
		return publicUserId;
	}

	public void setPublicUserId(String publicUserId) {
		this.publicUserId = publicUserId;
	}

}
