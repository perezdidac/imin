package com.imin.communications.events;


public class TaskPushEventResult {

	private String publicEventId;
	private boolean success;
	
	public TaskPushEventResult(String publicEventId, boolean success) {
		this.setPublicEventId(publicEventId);
		this.setSuccess(success);
	}
	
	public String getPublicEventId() {
		return publicEventId;
	}
	
	public void setPublicEventId(String publicEventId) {
		this.publicEventId = publicEventId;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	
}
