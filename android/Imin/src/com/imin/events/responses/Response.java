package com.imin.events.responses;

import org.json.JSONException;
import org.json.JSONObject;

import com.imin.Imin;
import com.imin.contacts.Contact;
import com.imin.events.EventInterface;
import com.imin.events.proposals.Proposal;

public class Response implements EventInterface {

	public static final int RESPONSE_TYPE_ATTENDING = 0;
	public static final int RESPONSE_TYPE_NOT_ATTENDING = 1;
	public static final int RESPONSE_TYPE_NOT_SURE = 2;
	public static final int RESPONSE_TYPE_INVITATION = 3;

	private Contact contact;
	private int responseType;
	private Proposal proposal;

	@Override
	public JSONObject toJson() throws JSONException {
		JSONObject jsonResponse = new JSONObject();

		jsonResponse.put("contactName", contact.getName());
		jsonResponse.put("contactPublicUserId", contact.getPublicUserId());
		jsonResponse.put("responseType", responseType);

		return jsonResponse;
	}

	@Override
	public void fromJson(JSONObject jsonObject) throws JSONException {
		// Read the JSON and fill the object values
		String contactName = jsonObject.getString("contactName");
		String contactPublicUserId = jsonObject.getString("contactPublicUserId");
		contact = new Contact(contactName, contactPublicUserId);
		contact = Imin.imin().getUser().addContact(contact);
		responseType = jsonObject.getInt("responseType");
	}

	public Response() {

	}

	public Response(Contact contact, int responseType, Proposal proposal) {
		this.setContact(contact);
		this.setResponseType(responseType);
		this.setProposal(proposal);
	}

	public Response clone(Proposal proposal) {
		Response response = new Response(contact, responseType, proposal);

		return response;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public int getResponseType() {
		return responseType;
	}

	public void setResponseType(int responseType) {
		this.responseType = responseType;
	}

	public Proposal getProposal() {
		return proposal;
	}

	public void setProposal(Proposal proposal) {
		this.proposal = proposal;
	}

	// Support methods

	public void incrementResponseType() {
		switch (responseType) {
		case RESPONSE_TYPE_ATTENDING:
			responseType = RESPONSE_TYPE_NOT_ATTENDING;
			break;
		case RESPONSE_TYPE_NOT_ATTENDING:
			responseType = RESPONSE_TYPE_ATTENDING;
			break;
		/*
		 * case RESPONSE_TYPE_NOT_SURE: responseType = RESPONSE_TYPE_ATTENDING; break;
		 */
		}
	}

}
