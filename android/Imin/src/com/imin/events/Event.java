package com.imin.events;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;

import com.imin.contacts.Contact;
import com.imin.events.pictures.PictureHelper;
import com.imin.events.proposals.DateTime;
import com.imin.events.proposals.Location;
import com.imin.events.proposals.Proposal;
import com.imin.events.responses.Response;

public class Event implements EventInterface {

	private String id;
	private String name;
	private String creator;
	private String description;
	private boolean responded;
	private boolean closed;
	private Bitmap picture;
	private Proposal finalDateTimeProposal;
	private Proposal finalLocationProposal;
	private String finalDateTimeProposalId;
	private String finalLocationProposalId;
	private boolean admin;

	// Invited contacts
	private List<Contact> invitedContacts;

	// Proposals
	private List<Proposal> proposals;

	@Override
	public JSONObject toJson() throws JSONException {
		JSONObject jsonEvent = new JSONObject();

		jsonEvent.put("id", id);
		jsonEvent.put("name", name);
		jsonEvent.put("creator", creator);
		jsonEvent.put("description", description);
		jsonEvent.put("responded", responded);
		jsonEvent.put("closed", closed);
		String picture64 = "";
		if (picture != null) {
			picture64 = PictureHelper.encode(picture);
		}
		jsonEvent.put("picture", picture64);

		jsonEvent.put("finalDateTimeProposalId", finalDateTimeProposalId);
		jsonEvent.put("finalLocationProposalId", finalLocationProposalId);
		jsonEvent.put("admin", admin);

		// Add invited contacts
		JSONArray jsonInvitedContacts = new JSONArray();
		if (invitedContacts != null) {
			for (int k = 0; k < invitedContacts.size(); ++k) {
				Contact contact = invitedContacts.get(k);
				jsonInvitedContacts.put(contact.toJson());
			}
		}

		jsonEvent.put("invitedContacts", jsonInvitedContacts);

		// Add proposals
		JSONArray jsonProposals = new JSONArray();
		if (proposals != null) {
			for (int k = 0; k < proposals.size(); ++k) {
				Proposal proposal = proposals.get(k);
				jsonProposals.put(proposal.toJson());
			}
		}

		jsonEvent.put("proposals", jsonProposals);

		return jsonEvent;
	}

    @Override
    public int hashCode() {
        return id.hashCode();
    }

	@Override
	public void fromJson(JSONObject jsonObject) throws JSONException {
		// Read the JSON and fill the object values
		id = jsonObject.getString("id");
		name = jsonObject.getString("name");
		creator = jsonObject.getString("creator");
		description = jsonObject.getString("description");
		responded = jsonObject.getBoolean("responded");
		closed = jsonObject.getBoolean("closed");
		String picture64 = jsonObject.getString("picture");
		if (picture64.length() > 0) {
			picture = PictureHelper.decode(picture64);
		}

		finalDateTimeProposalId = jsonObject.optString("finalDateTimeProposalId");
		finalLocationProposalId = jsonObject.optString("finalLocationProposalId");
		admin = jsonObject.getBoolean("admin");

		// Read invited contacts
		invitedContacts = new ArrayList<Contact>();
		JSONArray jsonInvitedContacts = jsonObject.getJSONArray("invitedContacts");
		for (int k = 0; k < jsonInvitedContacts.length(); ++k) {
			JSONObject jsonInvitedContact = jsonInvitedContacts.getJSONObject(k);
			Contact contact = new Contact();
			contact.fromJson(jsonInvitedContact);
			invitedContacts.add(contact);
		}

		// Read proposals
		proposals = new ArrayList<Proposal>();
		JSONArray jsonProposals = jsonObject.getJSONArray("proposals");
		for (int k = 0; k < jsonProposals.length(); ++k) {
			JSONObject jsonProposal = jsonProposals.getJSONObject(k);
			Proposal proposal = new Proposal();
			proposal.fromJson(jsonProposal);
			proposal.setEvent(this);

			if (proposal.getPublicProposalId().equals(finalDateTimeProposalId)) {
				finalDateTimeProposal = proposal;
			} else if (proposal.getPublicProposalId().equals(finalLocationProposalId)) {
				finalLocationProposal = proposal;
			}
			proposals.add(proposal);
		}
	}

	public Event() {

	}

	public Event clone() {
		// Create a new event
		Event event = new Event();

		// Clone everything
		event.id = id;
		event.name = name;
		event.creator = creator;
		event.description = description;
		event.responded = responded;
		event.closed = closed;
		event.picture = picture;
		event.finalDateTimeProposalId = finalDateTimeProposalId;
		event.finalLocationProposalId = finalLocationProposalId;
		event.admin = admin;

		event.invitedContacts = new ArrayList<Contact>();

		if (invitedContacts != null) {
			for (Contact invitedContact : invitedContacts) {
				event.invitedContacts.add(invitedContact);
			}
		}

		event.proposals = new ArrayList<Proposal>();
		for (int k = 0; k < proposals.size(); ++k) {
			Proposal proposal = proposals.get(k);
			Proposal proposalClone = proposal.clone(event);
			event.proposals.add(proposalClone);

			// Clone final proposal if exist
			if (proposal.getPublicProposalId().equals(finalDateTimeProposalId)) {
				finalDateTimeProposal = proposalClone;
			}

			if (proposal.getPublicProposalId().equals(finalLocationProposalId)) {
				finalLocationProposal = proposalClone;
			}
		}

		// Return
		return event;
	}

	public Event(String id, String name, String creator, boolean closed, boolean admin) {
		this.setId(id);
		this.setName(name);
		this.setCreator(creator);
		this.setClosed(closed);
		this.setAdmin(admin);
	}

	// Methods
	public List<Contact> getContacts() {
		// Retrieve the list of contacts that have responses for
		// proposals
		List<Contact> totalContacts = new ArrayList<Contact>();

		// Check if the list of proposals is null
		if (proposals == null)
			return totalContacts;

		// Fill the list
		for (int k = 0; k < proposals.size(); ++k) {
			// Get the proposal
			Proposal proposal = proposals.get(k);

			// Get the contact
			List<Contact> contacts = proposal.getContacts();

			for (int m = 0; m < contacts.size(); ++m) {
				Contact contact = contacts.get(m);

				// Check if the user has been already counted
				if (!totalContacts.contains(contact)) {
					totalContacts.add(contact);
				}
			}
		}

		return totalContacts;
	}

	// Gets and sets

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isResponded() {
		return responded;
	}

	public void setResponded(boolean responded) {
		this.responded = responded;
	}

	public List<Contact> getInvitedContacts() {
		return invitedContacts;
	}

	public void setInvitedContacts(List<Contact> invitedContacts) {
		this.invitedContacts = invitedContacts;
	}

	public List<Proposal> getProposals() {
		return proposals;
	}

	public void setProposals(List<Proposal> proposals) {
		this.proposals = proposals;
	}

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	public Bitmap getPicture() {
		return picture;
	}

	public void setPicture(Bitmap picture) {
		this.picture = picture;
	}

	// Other support methods

	public void addProposal(Proposal proposal) {
		if (proposals == null) {
			proposals = new ArrayList<Proposal>();
		}

		// Add the proposal to the event if it is not in it
		if (!proposals.contains(proposal)) {
			proposals.add(proposal);
		}
	}

	public List<DateTime> getProposalDateTimes() {
		List<DateTime> dateTimes = new ArrayList<DateTime>();

		if (proposals == null) {
			return dateTimes;
		}

		for (int k = 0; k < proposals.size(); ++k) {
			// Get the proposal
			Proposal proposal = proposals.get(k);

			if (proposal.getProposalType() != Proposal.PROPOSAL_TYPE_DATETIME) {
				continue;
			}

			// Get the date
			DateTime dateTime = proposal.getDateTime();
			String name = dateTime.getName();

			// Check duplicates
			boolean found = false;
			for (int m = 0; m < dateTimes.size(); ++m) {
				String testDateTime = dateTimes.get(m).getName();

				if (testDateTime.equals(name)) {
					// Found duplicated
					found = true;
					break;
				}
			}

			if (!found) {
				dateTimes.add(dateTime);
			}
		}

		return dateTimes;
	}

	public List<String> getProposalDays() {
		List<String> days = new ArrayList<String>();

		if (proposals == null) {
			return days;
		}

		for (int k = 0; k < proposals.size(); ++k) {
			// Get the proposal
			Proposal proposal = proposals.get(k);

			if (proposal.getProposalType() == Proposal.PROPOSAL_TYPE_INVITATION) {
				continue;
			}

			// Get the date
			DateTime dateTime = proposal.getDateTime();
			String day = dateTime.getDate();

			// Check duplicates
			boolean found = false;
			for (int m = 0; m < days.size(); ++m) {
				String testDay = days.get(m);

				if (testDay.equals(day)) {
					// Found duplicated
					found = true;
					break;
				}
			}

			if (!found) {
				days.add(day);
			}
		}

		return days;
	}

	public List<String> getProposalDateTimes(String day) {
		List<String> times = new ArrayList<String>();

		if (proposals == null) {
			return times;
		}

		List<DateTime> dateTimes = new ArrayList<DateTime>();

		for (int k = 0; k < proposals.size(); ++k) {
			// Get the proposal
			Proposal proposal = proposals.get(k);

			if (proposal.getProposalType() != Proposal.PROPOSAL_TYPE_DATETIME) {
				continue;
			}

			// Get the date
			DateTime date = proposal.getDateTime();

			// Avoid repeat dates
			boolean found = false;
			for (int m = 0; m < dateTimes.size(); ++m) {
				// Check duplicated date
				DateTime testDate = dateTimes.get(m);

				if (date.getName().equals(testDate.getName())) {
					found = true;
					break;
				}
			}

			if (!found) {
				String testDay = date.getDate();

				if (testDay.equals(day)) {
					// Day matches, let's add the time
					String time = date.getTime();
					times.add(time);
				}

				// Save the date to avoid duplicates
				dateTimes.add(date);
			}
		}

		return times;
	}

	public List<Location> getProposalLocations() {
		List<Location> locations = new ArrayList<Location>();

		if (proposals == null) {
			return locations;
		}

		for (int k = 0; k < proposals.size(); ++k) {
			// Get the proposal
			Proposal proposal = proposals.get(k);

			if (proposal.getProposalType() != Proposal.PROPOSAL_TYPE_LOCATION) {
				continue;
			}

			// Get the location
			Location location = proposal.getLocation();

			// Check duplicates
			boolean found = false;
			for (int m = 0; m < locations.size(); ++m) {
				Location testLocation = locations.get(m);

				if (testLocation.getName().equals(location.getName())) {
					// Found duplicated
					found = true;
					break;
				}
			}

			if (!found) {
				locations.add(location);
			}
		}

		return locations;
	}

	public Proposal getProposal(String day, String time, Location location) {
		Proposal proposal = null;

		if (proposals == null) {
			return proposal;
		}

		String locationName = location.getName();

		for (int k = 0; k < proposals.size(); ++k) {
			// Get the proposal
			Proposal testProposal = proposals.get(k);

			// Check if found
			Location testLocation = testProposal.getLocation();
			String testLocationName = testLocation.getName();

			// Check if location is the same
			if (testLocationName.equals(locationName)) {
				DateTime testDate = testProposal.getDateTime();
				String testDay = testDate.getDate();
				String testTime = testDate.getTime();

				// Check if date is the same
				if (testDay.equals(day) && testTime.equals(time)) {
					proposal = testProposal;
					break;
				}
			}
		}

		return proposal;
	}

	public Proposal getFinalDateTimeProposal() {
		return finalDateTimeProposal;
	}

	public void setFinalDateTimeProposal(Proposal finalDateTimeProposal) {
		this.finalDateTimeProposal = finalDateTimeProposal;
	}

	public Proposal getFinalLocationProposal() {
		return finalLocationProposal;
	}

	public void setFinalLocationProposal(Proposal finalLocationProposal) {
		this.finalLocationProposal = finalLocationProposal;
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

	public List<Contact> getAttendingContacts() {
		// Check for attending contacts
		if (this.isClosed()) {
			// Get final proposal
			Proposal finalDateTimeProposal = this.getFinalDateTimeProposal();
			Proposal finalLocationProposal = this.getFinalLocationProposal();

			// Return all the contacts attending
			List<Contact> attendingContacts = new ArrayList<Contact>();

			if (finalDateTimeProposal != null && finalLocationProposal != null) {
				// Merge two lists only if the contacts are in both lists
				List<Contact> dateTimeContacts = finalDateTimeProposal.getContacts(Response.RESPONSE_TYPE_ATTENDING);
				List<Contact> locationContacts = finalLocationProposal.getContacts(Response.RESPONSE_TYPE_ATTENDING);
	
				for (int k = 0; k < dateTimeContacts.size(); ++k) {
					for (int m = k; m < locationContacts.size(); ++m) {
						// Check for double appearances
						Contact dateTimeContact = dateTimeContacts.get(k);
						Contact locationContact = locationContacts.get(m);
	
						if (dateTimeContact.getPublicUserId().equals(locationContact.getPublicUserId())) {
							attendingContacts.add(dateTimeContact);
						}
					}
				}
			}

			return attendingContacts;
		} else {
			return this.getContacts();
		}
	}

	public List<Contact> getNotAttendingContacts() {
		// Check for not attending contacts
		if (this.isClosed()) {
			List<Contact> contacts = this.getContacts();
			List<Contact> attendingContacts = getAttendingContacts();
			List<Contact> notAttendingContacts = new ArrayList<Contact>();

			// Loop through the list of contacts
			for (int k = 0; k < contacts.size(); ++k) {
				Contact contact = contacts.get(k);

				// Check if the contact is attending
				boolean found = false;
				for (int m = 0; m < attendingContacts.size(); ++m) {
					Contact testContact = attendingContacts.get(m);

					if (testContact.getPublicUserId().equals(contact.getPublicUserId())) {
						found = true;
						break;
					}
				}

				if (!found) {
					notAttendingContacts.add(contact);
				}
			}

			return notAttendingContacts;
		} else {
			return this.getContacts();
		}
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public Proposal getInvitationProposal() {
		Proposal proposal = null;

		if (proposals == null) {
			return proposal;
		}

		for (int k = 0; k < proposals.size(); ++k) {
			// Get the proposal
			Proposal testProposal = proposals.get(k);

			if (testProposal.getProposalType() == Proposal.PROPOSAL_TYPE_INVITATION) {
				// Found
				proposal = testProposal;
				break;
			}
		}

		return proposal;
	}

}
