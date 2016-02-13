package com.imin.communications.user;

public class TaskGetUserIdParams {

	private String deviceId;
	
	public TaskGetUserIdParams(String deviceId) {
		this.setDeviceId(deviceId);
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
}
