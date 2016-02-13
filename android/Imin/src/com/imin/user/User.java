package com.imin.user;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.imin.contacts.Contact;
import com.imin.events.Event;
import com.imin.events.pictures.PictureHelper;
import com.imin.events.proposals.Proposal;

public class User {

	private static final String FILE_INVITATIONS = "invitations.json";
	private static final String FILE_USER = "user.json";
	private static final String FILE_EVENTS = "events.json";

	// User variables and objects
	private boolean userLoaded;
	private Context context;
	private String privateUserId;
	private String publicUserId;
	private Contact contact;
	private List<Event> events;
	private Event currentEvent;
	private List<Contact> contacts;
	private boolean pollResponded;
	private String pollEventId;
	private boolean eventCreated;
	private boolean mustGetEvents;
	private List<String> invitations;
	private Proposal selectedDateTimeProposal;
	private Proposal selectedLocationProposal;

	// Random color generator
	public static final int MAX_COLORS = 4;
	public static final String COLOR_YELLOW = "#fff836";
	public static final String COLOR_PURPLE = "#764797";
	public static final String COLOR_ORANGE = "#e8a023";
	public static final String COLOR_CYAN = "#02c9cb";
	public int lastColor;

	public User(Context context) {
		this.context = context;
	}

	// Public methods

	public boolean initialize() {
		//setMustGetEvents(true);

		// Try to load private and public ids
		if (loadUser()) {
			contact = addContact(contact);
			events = loadEvents();
			
			setMustGetEvents(true);
			return true;
		} else {
			return false;
		}
	}

	public boolean isValid() {
		// Check if the user is valid, this is, it has both private and public user id
		return (privateUserId != null && publicUserId != null);
	}

	public int nextColor() {
		int color = 0;

		switch (lastColor) {
		case 0:
			color = Color.parseColor(COLOR_YELLOW);
			break;
		case 1:
			color = Color.parseColor(COLOR_PURPLE);
			break;
		case 2:
			color = Color.parseColor(COLOR_ORANGE);
			break;
		case 3:
			color = Color.parseColor(COLOR_CYAN);
			break;
		}

		lastColor = (lastColor + 1) % MAX_COLORS;

		return color;
	}

	public Contact addContact(Contact contact) {
		if (contacts == null) {
			contacts = new ArrayList<Contact>();
		}

		// Check if the contact is already in the list of contacts
		for (int k = 0; k < contacts.size(); ++k) {
			Contact testContact = contacts.get(k);
			if (testContact.getPublicUserId().equals(contact.getPublicUserId())) {
				// Do not add it
				return testContact;
			}
		}

		// New contact, assign new color
		contact.setColor(nextColor());

		contacts.add(contact);
		return contact;
	}

	public Contact getContact() {
		return contact;
	}

	public Contact getContact(String publicUserId) {
		Contact contact = null;

		if (contacts == null) {
			contacts = new ArrayList<Contact>();
		}

		// Find the contact
		for (int k = 0; k < contacts.size(); ++k) {
			Contact testContact = contacts.get(k);

			if (testContact.getPublicUserId().equals(publicUserId)) {
				contact = testContact;
				break;
			}
		}

		return contact;
	}

	// Invitations

	public void addInvitation(String eventId) {
		// Check
		if (invitations == null) {
			invitations = new ArrayList<String>();
		}

		if (!invitations.contains(eventId)) {
			// Maybe there are more invitations, so push it into the list
			invitations.add(eventId);
		}

		saveInvitations(invitations);
	}

	public void setInvitations(List<String> invitations) {
		this.invitations = invitations;

		saveInvitations(invitations);
	}

	public List<String> getInvitations() {
		// Check
		if (invitations == null) {
			invitations = loadInvitations();
		}

		return invitations;
	}

	private void saveInvitations(List<String> invitations) {
		// Save invitations into the local memory
		JSONArray jsonInvitations = new JSONArray();
		for (int k = 0; k < invitations.size(); ++k) {
			String invitation = invitations.get(k);
			JSONObject jsonInvitation = new JSONObject();
			try {
				jsonInvitation.put("invitation", invitation);

				// Append
				jsonInvitations.put(jsonInvitation);
			} catch (JSONException e) {
			}
		}

		String data = jsonInvitations.toString();

		try {
			FileOutputStream fos = context.openFileOutput(FILE_INVITATIONS, Context.MODE_PRIVATE);
			Files.writeStringFile(fos, data);
		} catch (Exception e) {
		}
	}

	private List<String> loadInvitations() {
		List<String> invitations = new ArrayList<String>();

		// Load invitations from the local memory
		try {
			FileInputStream fis = context.openFileInput(FILE_INVITATIONS);
			String data = Files.readStringFile(fis);

			JSONArray jsonInvitations = new JSONArray(data);
			for (int k = 0; k < jsonInvitations.length(); ++k) {
				JSONObject jsonInvitation = jsonInvitations.getJSONObject(k);
				String invitation = jsonInvitation.getString("invitation");

				// Put invitation
				if (!invitations.contains(invitation)) {
					// Maybe there are more invitations, so push it into the list
					invitations.add(invitation);
				}
			}
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} catch (JSONException e) {
		}

		return invitations;
	}

	public void deleteUser() {
		// Delete the user file
		context.deleteFile(FILE_USER);
		context.deleteFile(FILE_EVENTS);
	}

	public void saveUser() {
		// Save invitations into the local memory
		JSONObject jsonUser = new JSONObject();
		try {
			// Save everything
			jsonUser.put("privateUserId", privateUserId);
			jsonUser.put("publicUserId", publicUserId);
			jsonUser.put("username", contact.getName());

			// Save user picture
			Bitmap userPicture = contact.getPhoto();

			if (userPicture != null) {
				String userPictureBase64 = PictureHelper.encode(userPicture);
				jsonUser.put("userPicture", userPictureBase64);
			}
		} catch (JSONException e) {
		}

		String data = jsonUser.toString();

		try {
			FileOutputStream fos = context.openFileOutput(FILE_USER, Context.MODE_PRIVATE);
			Files.writeStringFile(fos, data);
		} catch (Exception e) {
		}
	}

	public boolean loadUser() {
		if (userLoaded) {
			return true;
		}

		if (contact == null) {
			contact = new Contact();
		}

		userLoaded = false;

		// Load invitations from the local memory
		try {
			FileInputStream fis = context.openFileInput(FILE_USER);
			String data = Files.readStringFile(fis);

			userLoaded = true;

			JSONObject jsonUser = new JSONObject(data);
			if (!jsonUser.isNull("privateUserId")) {
				String privateUserId = jsonUser.getString("privateUserId");
				this.privateUserId = privateUserId;
			} else {
				userLoaded = false;
			}

			if (!jsonUser.isNull("publicUserId")) {
				String publicUserId = jsonUser.getString("publicUserId");
				this.publicUserId = publicUserId;
				contact.setPublicUserId(publicUserId);
			} else {
				userLoaded = false;
			}

			if (!jsonUser.isNull("username")) {
				String username = jsonUser.getString("username");
				contact.setName(username);
			} else {
				userLoaded = false;
			}

			if (!jsonUser.isNull("userPicture")) {
				// Load the user picture
				String userPictureBase64 = jsonUser.getString("userPicture");
				Bitmap userPicture = PictureHelper.decode(userPictureBase64);

				if (userPicture != null) {
					contact.setPhoto(userPicture);
				}
			}
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} catch (JSONException e) {
		}

		return userLoaded;
	}

	public void saveEvents(List<Event> events) {
		// Save the events in JSON format to the disk
		JSONObject jsonObject = new JSONObject();
		try {
			JSONArray jsonEvents = new JSONArray();

			for (int k = 0; k < events.size(); ++k) {
				// Get event
				Event event = events.get(k);

				// Convert the event into JSON format
				JSONObject jsonEvent = event.toJson();

				// Add it
				jsonEvents.put(jsonEvent);
			}

			// Save everything
			jsonObject.put("events", jsonEvents);
		} catch (JSONException e) {
		}

		String data = jsonObject.toString();

		try {
			FileOutputStream fos = context.openFileOutput(FILE_EVENTS, Context.MODE_PRIVATE);
			Files.writeStringFile(fos, data);
		} catch (Exception e) {
		}
	}

	public List<Event> loadEvents() {
		List<Event> events = new ArrayList<Event>();

		// Load invitations from the local memory
		try {
			FileInputStream fis = context.openFileInput(FILE_EVENTS);

			String eventsString = Files.readStringFile(fis);

			JSONObject jsonUser = new JSONObject(eventsString);
			if (!jsonUser.isNull("events")) {
				// Read all the events
				JSONArray jsonEvents = jsonUser.getJSONArray("events");

				for (int k = 0; k < jsonEvents.length(); ++k) {
					// Read the event and add it into the list
					JSONObject jsonEvent = jsonEvents.getJSONObject(k);
					Event event = new Event();
					event.fromJson(jsonEvent);
					events.add(event);
				}
			}

			// Load the events
			// IminLib.parseEvents(eventsString, this, events);

		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} catch (JSONException e) {
		}

		return events;
	}

	// Gets and sets

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
		contact.setPublicUserId(publicUserId);
	}

	public List<Event> getEvents() {
		if (events == null) {
			events = new ArrayList<Event>();
		}
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}

	public Event getCurrentEvent() {
		return currentEvent;
	}

	public void setCurrentEvent(Event currentEvent) {
		this.currentEvent = currentEvent;
	}

	public List<Contact> getContacts() {
		if (contacts == null) {
			contacts = new ArrayList<Contact>();
		}
		return contacts;
	}

	public boolean isPollResponded() {
		return pollResponded;
	}

	public void setPollResponded(boolean pollResponded) {
		this.pollResponded = pollResponded;
	}

	public String getPollEventId() {
		return pollEventId;
	}

	public void setPollEventId(String pollEventId) {
		this.pollEventId = pollEventId;
	}

	public boolean isEventCreated() {
		return eventCreated;
	}

	public void setEventCreated(boolean eventCreated) {
		this.eventCreated = eventCreated;
	}

	public boolean isMustGetEvents() {
		return mustGetEvents;
	}

	public void setMustGetEvents(boolean mustGetEvents) {
		this.mustGetEvents = mustGetEvents;
	}

	public Proposal getSelectedDateTimeProposal() {
		return selectedDateTimeProposal;
	}

	public void setSelectedDateTimeProposal(Proposal selectedDateTimeProposal) {
		this.selectedDateTimeProposal = selectedDateTimeProposal;
	}

	public Proposal getSelectedLocationProposal() {
		return selectedLocationProposal;
	}

	public void setSelectedLocationProposal(Proposal selectedLocationProposal) {
		this.selectedLocationProposal = selectedLocationProposal;
	}

}
