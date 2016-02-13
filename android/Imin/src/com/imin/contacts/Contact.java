package com.imin.contacts;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.net.Uri;

import com.imin.events.EventInterface;

public class Contact implements EventInterface {

	public static final int CONTACT_HAS_PICTURE = 0;
	public static final int CONTACT_HAS_PICTURE_NOT = 1;
	public static final int CONTACT_HAS_PICTURE_UNKNOWN = 2;

	private String name;
	private Uri photoUri;
	private List<String> emails;
	private List<ContactPhone> phones;
	private int hasPicture;
	private Bitmap photo;
	private String publicUserId;

	// Non serializable values
	private boolean selected;
	private int color;

	@Override
	public JSONObject toJson() throws JSONException {
		JSONObject jsonContact = new JSONObject();

		jsonContact.put("name", name);
		jsonContact.put("hasPicture", hasPicture);
		jsonContact.put("publicUserId", publicUserId);

		return jsonContact;
	}

	@Override
	public void fromJson(JSONObject jsonContact) throws JSONException {
		// TODO Auto-generated method stub

	}

	public Contact() {

	}

	public Contact(String name, String publicUserId) {
		this.setName(name);
		this.setPublicUserId(publicUserId);
	}

	public Contact(String name, Uri photoUri, List<String> emails, List<ContactPhone> phones, int hasPicture) {
		this.setName(name);
		this.setPhotoUri(photoUri);
		this.setEmails(emails);
		this.setPhones(phones);
		this.setHasPicture(hasPicture);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Uri getPhotoUri() {
		return photoUri;
	}

	public void setPhotoUri(Uri photoUri) {
		this.photoUri = photoUri;
	}

	public List<String> getEmails() {
		return emails;
	}

	public void setEmails(List<String> emails) {
		this.emails = emails;
	}

	public List<ContactPhone> getPhones() {
		return phones;
	}

	public void setPhones(List<ContactPhone> phones) {
		this.phones = phones;
	}

	public int getHasPicture() {
		return hasPicture;
	}

	public void setHasPicture(int hasPicture) {
		this.hasPicture = hasPicture;
	}

	public Bitmap getPhoto() {
		return photo;
	}

	public void setPhoto(Bitmap photo) {
		this.photo = photo;
	}

	public String getPublicUserId() {
		return publicUserId;
	}

	public void setPublicUserId(String publicUserId) {
		this.publicUserId = publicUserId;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

}
