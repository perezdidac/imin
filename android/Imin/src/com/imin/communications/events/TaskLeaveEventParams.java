package com.imin.communications.events;

import com.imin.user.User;

public class TaskLeaveEventParams {
	private User user;
	private String publicEventId;

	public TaskLeaveEventParams(User user, String publicEventId) {
		setUser(user);
		setPublicEventId(publicEventId);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getPublicEventId() {
		return publicEventId;
	}

	public void setPublicEventId(String publicEventId) {
		this.publicEventId = publicEventId;
	}
}
