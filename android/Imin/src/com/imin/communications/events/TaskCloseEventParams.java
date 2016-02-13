package com.imin.communications.events;

public class TaskCloseEventParams {
	private String privateUserId;
	private String publicEventId;
	private boolean closed;
	private String finalDateTimeProposalId;
	private String finalLocationProposalId;

	public TaskCloseEventParams(String privateUserId, String publicEventId, boolean closed,
			String finalDateTimeProposalId, String finalLocationProposalId) {
		setPrivateUserId(privateUserId);
		setPublicEventId(publicEventId);
		setClosed(closed);
		setFinalDateTimeProposalId(finalDateTimeProposalId);
		setFinalLocationProposalId(finalLocationProposalId);
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

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	public String getFinalDateTimeProposalId() {
		return finalDateTimeProposalId;
	}

	public void setFinalDateTimeProposalId(String finalDateTimeProposalId) {
		this.finalDateTimeProposalId = finalDateTimeProposalId;
	}

	public String getFinalLocationProposalId() {
		return finalLocationProposalId;
	}

	public void setFinalLocationProposalId(String finalLocationProposalId) {
		this.finalLocationProposalId = finalLocationProposalId;
	}
}
