package com.imin.contacts;

public class ContactPhone {

	public static final int CONTACT_PHONE_TYPE_HOME = 0;
	public static final int CONTACT_PHONE_TYPE_MOBILE = 1;
	public static final int CONTACT_PHONE_TYPE_WORK = 2;

	private String number;
	private int type;

	public ContactPhone(String number, int type) {
		this.setNumber(number);
		this.setType(type);
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
