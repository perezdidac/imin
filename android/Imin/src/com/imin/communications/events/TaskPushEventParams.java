package com.imin.communications.events;

import com.imin.events.Event;

public class TaskPushEventParams {

	private String privateUserId;
	private Event event;
	
	/** Initializes the class variables. **/
	public TaskPushEventParams(String privateUserId, Event event) {
		setPrivateUserId(privateUserId);
		setEvent(event);
	}

	public String getPrivateUserId() {
		return privateUserId;
	}

	public void setPrivateUserId(String privateUserId) {
		this.privateUserId = privateUserId;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}
	
}
