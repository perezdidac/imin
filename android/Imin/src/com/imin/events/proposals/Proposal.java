package com.imin.events.proposals;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.imin.contacts.Contact;
import com.imin.events.Event;
import com.imin.events.EventInterface;
import com.imin.events.responses.Response;

public class Proposal implements EventInterface {

	public static final int PROPOSAL_TYPE_INVITATION = 1;
	public static final int PROPOSAL_TYPE_DATETIME = 10;
	public static final int PROPOSAL_TYPE_LOCATION = 20;

	private String publicProposalId;
	private int proposalType;
	private Location location;
	private DateTime dateTime;

	private List<Response> responses;

	private Event event;

	@Override
	public JSONObject toJson() throws JSONException {
		JSONObject jsonProposal = new JSONObject();

		jsonProposal.put("publicProposalId", publicProposalId);
		jsonProposal.put("proposalType", proposalType);

		if (proposalType == PROPOSAL_TYPE_DATETIME) {
			jsonProposal.put("dateTime", dateTime.toJson());
		} else if (proposalType == PROPOSAL_TYPE_LOCATION) {
			jsonProposal.put("location", location.toJson());
		}

		// Add responses
		JSONArray jsonResponses = new JSONArray();
		if (responses != null) {
			for (int k = 0; k < responses.size(); ++k) {
				Response response = responses.get(k);
				jsonResponses.put(response.toJson());
			}
		}

		jsonProposal.put("responses", jsonResponses);

		return jsonProposal;
	}

	@Override
	public void fromJson(JSONObject jsonObject) throws JSONException {
		// Read the JSON and fill the object values
		publicProposalId = jsonObject.getString("publicProposalId");
		proposalType = jsonObject.getInt("proposalType");

		// Read dateTime and location
		if (proposalType == PROPOSAL_TYPE_DATETIME) {
			dateTime = new DateTime("");
			dateTime.fromJson(jsonObject.getJSONObject("dateTime"));
		} else if (proposalType == PROPOSAL_TYPE_LOCATION) {
			location = new Location("");
			location.fromJson(jsonObject.getJSONObject("location"));
		}

		// Read responses
		responses = new ArrayList<Response>();
		JSONArray jsonResponses = jsonObject.getJSONArray("responses");
		for (int k = 0; k < jsonResponses.length(); ++k) {
			JSONObject jsonResponse = jsonResponses.getJSONObject(k);
			Response response = new Response();
			response.fromJson(jsonResponse);
			response.setProposal(this);
			responses.add(response);
		}
	}

	public Proposal clone(Event event) {
		Proposal proposal = new Proposal();

		if (proposalType == PROPOSAL_TYPE_DATETIME) {
			proposal.dateTime = dateTime.clone();
		} else if (proposalType == PROPOSAL_TYPE_LOCATION) {
			proposal.location = location.clone();
		}

		proposal.publicProposalId = publicProposalId;
		proposal.proposalType = proposalType;

		proposal.event = event;

		proposal.responses = new ArrayList<Response>();
		if (responses != null) {
			for (int k = 0; k < responses.size(); ++k) {
				Response response = responses.get(k);
				Response responseClone = response.clone(proposal);
				proposal.responses.add(responseClone);
			}
		}

		return proposal;
	}

	// Methods
	public List<Contact> getContacts() {
		// Retrieve the list of contacts that have responses for
		// that proposal
		List<Contact> contacts = new ArrayList<Contact>();

		// Check if the list of proposal is null
		if (responses == null)
			return contacts;

		// Fill the list
		for (int k = 0; k < responses.size(); ++k) {
			// Get the contact
			Response response = responses.get(k);

			// Get the contact
			Contact contact = response.getContact();

			// Check if the user has been already counted
			if (!contacts.contains(contact)) {
				contacts.add(contact);
			}
		}

		return contacts;
	}

	// Constructors

	public Proposal() {

	}

	public Proposal(Location location, DateTime dateTime, String publicProposalId, int proposalType, Event event) {
		this.setLocation(location);
		this.setDateTime(dateTime);
		this.setPublicProposalId(publicProposalId);
		this.setProposalType(proposalType);
		this.setEvent(event);
	}

	// Gets and sets

	public String getPublicProposalId() {
		return publicProposalId;
	}

	public void setPublicProposalId(String publicProposalId) {
		this.publicProposalId = publicProposalId;
	}

	public int getProposalType() {
		return proposalType;
	}

	public void setProposalType(int proposalType) {
		this.proposalType = proposalType;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public DateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(DateTime dateTime) {
		this.dateTime = dateTime;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public List<Response> getResponses() {
		if (responses == null) {
			responses = new ArrayList<Response>();
		}

		return responses;
	}

	public void setResponses(List<Response> responses) {
		this.responses = responses;
	}

	public void addResponse(Response response) {
		if (responses == null) {
			responses = new ArrayList<Response>();
		}

		// Add the response to the proposal if it is not in it
		if (!responses.contains(response)) {
			responses.add(response);
		}
	}

	public Response getContactResponse(String publicUserId) {
		Response response = null;

		// Check null responses
		if (responses != null) {

			// Find user response
			for (int k = 0; k < responses.size(); ++k) {
				Response testResponse = responses.get(k);

				// Get contact of the response
				Contact contact = testResponse.getContact();

				if (contact != null) {
					// Check ourselves
					if (contact.getPublicUserId().equals(publicUserId)) {
						response = testResponse;
						break;
					}
				}
			}
		}

		return response;
	}

	public List<Contact> getContacts(int responseType) {
		List<Contact> contacts = new ArrayList<Contact>();

		// Check null responses
		List<Response> responses = getResponses();

		// Return attending responses
		for (int k = 0; k < responses.size(); ++k) {
			Response response = responses.get(k);

			if (response.getResponseType() == responseType) {
				contacts.add(response.getContact());
			}
		}

		return contacts;
	}

}
