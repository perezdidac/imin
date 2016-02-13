package com.imin.communications.events;

import java.util.List;

import com.imin.events.Event;
import com.imin.events.responses.Response;

public class TaskRespondEventParams {

	private String privateUserId;
	private Event event;
	private List<Response> responses;
	
	/** Initializes the class variables. **/
	public TaskRespondEventParams(String privateUserId, Event event, List<Response> responses) {
		setPrivateUserId(privateUserId);
		setEvent(event);
		setResponses(responses);
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

	public List<Response> getResponses() {
		return responses;
	}

	public void setResponses(List<Response> responses) {
		this.responses = responses;
	}
	
}
