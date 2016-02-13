package com.imin.communications.comments;

public class TaskSendCommentsParams {
	private String privateUserId;
	private String name;
	private String comments;

	public TaskSendCommentsParams(String privateUserId, String name, String comments) {
		setPrivateUserId(privateUserId);
		this.setName(name);
		this.setComments(comments);
	}

	public String getPrivateUserId() {
		return privateUserId;
	}

	public void setPrivateUserId(String privateUserId) {
		this.privateUserId = privateUserId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

}
