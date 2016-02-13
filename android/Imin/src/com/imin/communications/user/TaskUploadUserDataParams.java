package com.imin.communications.user;

import android.graphics.Bitmap;

public class TaskUploadUserDataParams {

	private String privateUserId;
	private String publicUserId;
	private String username;
	private Bitmap picture;

	/** Initializes the class variables. **/
	public TaskUploadUserDataParams(String privateUserId, String publicUserId, String username, Bitmap picture) {
		setPrivateUserId(privateUserId);
		setPublicUserId(publicUserId);
		setUsername(username);
		setPicture(picture);
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Bitmap getPicture() {
		return picture;
	}

	public void setPicture(Bitmap picture) {
		this.picture = picture;
	}

}
