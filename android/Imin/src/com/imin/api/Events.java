package com.imin.api;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;

import com.imin.Imin;
import com.imin.R;
import com.imin.communications.connection.Connection;
import com.imin.contacts.Contact;
import com.imin.events.Event;
import com.imin.events.pictures.FileCache;
import com.imin.events.pictures.PictureHelper;
import com.imin.events.proposals.Proposal;
import com.imin.events.responses.Response;
import com.imin.notifications.Notifications;
import com.imin.user.Files;
import com.imin.user.User;

public class Events {

	public static boolean syncEvents(Context context, boolean notifications) {
		// In here we must get the events
		// Retrieve list of events
		List<Event> events = new ArrayList<Event>();
		List<Event> invitationEvents = new ArrayList<Event>();

		// Get invitations
		User user = Imin.imin().getUser();
		List<Event> oldEvents = user.getEvents();
		List<String> invitations = user.getInvitations();
		for (int k = 0; k < invitations.size(); ++k) {
			String invitation = invitations.get(k);
			Event event = new Event();
			event.setId(invitation);
			if (Connection.getEvent(user, event)) {
				// Add invitation event
				invitationEvents.add(event);

				// Look for the invitation proposal
				Proposal invitationProposal = event.getInvitationProposal();

				if (invitationProposal != null) {
					// Participate in the event
					List<Response> responses = new ArrayList<Response>();
					Response invitationResponse = new Response(user.getContact(), Response.RESPONSE_TYPE_INVITATION,
							invitationProposal);
					responses.add(invitationResponse);
					Connection.respondEvent(user.getPrivateUserId(), event, responses);
				}
			}
		}

		boolean success = Connection.getEvents(user, events);

		// ======================================================
		// CHECK DUPLICATES

		boolean anyFound = false;
		int k = 0;
		while (k < invitationEvents.size()) {
			Event invitationEvent = invitationEvents.get(k);

			// Check if it exists in the retrieved events list
			boolean found = false;
			for (int m = 0; m < events.size(); ++m) {
				Event testEvent = events.get(m);

				if (invitationEvent.getId().equals(testEvent.getId())) {
					found = true;
					break;
				}
			}

			if (found) {
				anyFound = true;
				invitations.remove(invitationEvent.getId());
			} else {
				events.add(0, invitationEvent);
			}

			++k;
		}

		if (anyFound) {
			user.setInvitations(invitations);
		}
		
		// At this point, all the events have been downloaded from the server
		if (success) {
			// At this point, all the events and their pictures have been downloaded from the server, so let's save the
			// retrieved events

			// Check if events has changed, comparing 'oldEvents' to 'events'
			if (notifications) {
				createNotifications(context, events, oldEvents);
			}
			
			// Put old pictures in the new events
			for (k = 0; k < oldEvents.size(); ++k) {
				for (int m = 0; m < events.size(); ++m) {
					Event oldEvent = oldEvents.get(k);
					Event event = events.get(m);
					
					// Check if is the same
					if (oldEvent.getId().equals(event.getId())) {
						event.setPicture(oldEvent.getPicture());
					}
				}
			}

			// Finally, save the events
			user.saveEvents(events);
		}

		return success;
	}

	public static boolean syncEventPictures(Context context) {
		User user = Imin.imin().getUser();
		List<Event> events = user.getEvents();
		
		// ======================================================
		// EVENT IMAGES

		for (int k = 0; k < events.size(); ++k) {
			Event event = events.get(k);
			String base64 = null;

			// Before download the picture, check if we have it
			// in the file cache
			String hash = Connection.hashPicture(event.getId());
			FileCache fileCache = new FileCache(context);
			boolean inFileCache = true;

			if (hash != null) {
				// Check if we have same hash
				byte[] array = fileCache.get(hash);
				if (array != null) {
					// Okay, we already had the file
					base64 = new String(array);
				} else {
					// We did not had the file, get it and add into the
					// file cache as a new file
					base64 = Connection.downloadPicture(event.getId());
					inFileCache = false;
				}
			}

			if (base64 != null) {
				if (!inFileCache) {
					hash = Files.hashFile(base64.getBytes());
					fileCache.add(hash, base64.getBytes());
				}
				Bitmap bitmap = PictureHelper.decode(base64);
				event.setPicture(bitmap);
			}
		}

		// Finally, save the events
		user.saveEvents(events);

		return true;
	}

	private static void createNotifications(Context context, List<Event> events, List<Event> oldEvents) {
		// Compare both lists
		for (int k = 0; k < events.size(); ++k) {
			Event event = events.get(k);
			for (int m = 0; m < oldEvents.size(); ++m) {
				Event oldEvent = oldEvents.get(m);

				// Check if they are the same
				if (event.getId().equals(oldEvent.getId())) {
					// They are the same!

					// NOTIFICATIONS FOR CLOSING/REOPENING
					if (!event.isAdmin()) {
						// Check if event has been closed
						if (!oldEvent.isClosed() && event.isClosed()) {
							// Event recently closed, create notification
							notifyEventClosed(context, event);
						} else if (oldEvent.isClosed() && !event.isClosed()) {
							// Event recently reopened, create notification
							notifyEventReopened(context, event);
						}
					}

					// NOTIFICATIONS FOR CONTACT VOTING
					// if (!event.isClosed()) {
					// When event is not closed yet
					List<Contact> contacts = event.getContacts();
					List<Contact> oldContacts = oldEvent.getContacts();
					List<Contact> newContacts = new ArrayList<Contact>();

					for (int n = 0; n < contacts.size(); ++n) {
						boolean found = false;
						Contact contact = contacts.get(n);
						for (int l = 0; l < oldContacts.size(); ++l) {
							Contact oldContact = oldContacts.get(l);
							if (contact.getPublicUserId().equals(oldContact.getPublicUserId())) {
								found = true;
								break;
							}
						}
						if (!found) {
							// New contact not found in old contacts lists
							newContacts.add(contact);
						}
					}

					if (newContacts.size() > 1) {
						notifyNewContacts(context, event, newContacts);
					} else if (newContacts.size() > 0) {
						notifyNewContact(context, event, newContacts.get(0));
					}

					// } else {
					// Event closed

					// }
				}
			}
		}
	}

	private static void notifyNewContacts(Context context, Event event, List<Contact> newContacts) {
		// Build the notification
		String title = event.getName();

		String message = context.getString(R.string.tus_contactos) + newContacts.get(0).getName();
		for (int k = 1; k < newContacts.size() - 1; ++k) {
			message += ", " + newContacts.get(k).getName();
		}
		message += context.getString(R.string._y_) + newContacts.get(newContacts.size() - 1).getName();
		message += context.getString(R.string.han_respondido_la_encuesta);

		// Notify
		Notifications.notify(context, title, message, event.getPicture());
	}

	private static void notifyNewContact(Context context, Event event, Contact newContact) {
		// Build the notification
		String title = event.getName();
		String message = newContact.getName() + context.getString(R.string.ha_respondido_la_encuesta);

		// Notify
		Notifications.notify(context, title, message, event.getPicture());
	}

	private static void notifyEventClosed(Context context, Event event) {
		// Build the notification
		String title = event.getName();
		String message = context.getString(R.string.el_administrador_ha_cerrado_las_votaciones);

		// Notify
		Notifications.notify(context, title, message, event.getPicture());
	}

	private static void notifyEventReopened(Context context, Event event) {
		// Build the notification
		String title = event.getName();
		String message = context.getString(R.string.el_administrador_ha_abierto_las_votaciones);

		// Notify
		Notifications.notify(context, title, message, event.getPicture());
	}

}
