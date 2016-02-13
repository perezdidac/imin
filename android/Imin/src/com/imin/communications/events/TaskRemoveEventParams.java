package com.imin.communications.events;

public class TaskRemoveEventParams {
	private String privateUserId;
	private String publicEventId;
	
	public TaskRemoveEventParams(String privateUserId, String publicEventId) {
		setPrivateUserId(privateUserId);
		setPublicEventId(publicEventId);
	}
	
	public String getPrivateUserId() {
		return privateUserId;
	}
	
	public void setPrivateUserId(String privateUserId) {
		this.privateUserId = privateUserId;
	}
	
	public String getPublicEventId() {
		return publicEventId;
	}
	
	public void setPublicEventId(String publicEventId) {
		this.publicEventId = publicEventId;
	}
}
