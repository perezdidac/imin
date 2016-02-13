/**
 * 
 */
package com.imin.api;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.imin.Imin;
import com.imin.contacts.Contact;
import com.imin.events.Event;
import com.imin.events.proposals.DateTime;
import com.imin.events.proposals.Location;
import com.imin.events.proposals.Proposal;
import com.imin.events.responses.Response;
import com.imin.user.User;

public class IminLib {

	// Web constants: URLs
	// public static final String baseUrl = "http://dealmeinapp.besaba.com/events/";
	// public static final String baseUrl = "http://192.168.1.2/";
	public static final String baseUrl = "http://www.imintheapp.com/imin/";
	// public static final String baseUrl = "https://secure3060.hostgator.com/~imin/imin/";
	public static final String getUserIdUrl = "getUserId.php";
	public static final String getEventUrl = "getEvent.php";
	public static final String getEventsUrl = "getEvents.php";
	public static final String removeEventUrl = "removeEvent.php";
	public static final String leaveEventUrl = "leaveEvent.php";
	public static final String pushEventUrl = "pushEvent.php";
	public static final String respondEventUrl = "respondEvent.php";
	public static final String closeEventUrl = "closeEvent.php";
	public static final String sendCommentsUrl = "sendComments.php";
	public static final String uploadPictureUrl = "uploadPicture.php";
	public static final String hashPictureUrl = "hashPicture.php";
	public static final String downloadPictureUrl = "downloadPicture.php";
	public static final String removePictureUrl = "removePicture.php";
	public static final String uploadUserDataUrl = "uploadUserData.php";
	public static final String downloadUserDataUrl = "downloadUserData.php";
	public static final String hashUserPictureUrl = "hashUserPicture.php";
	public static final String removeUserPictureUrl = "removeUserPicture.php";

	// Web constants: parameters
	public static final String privateUserIdParam = "privateUserId";
	public static final String publicUserIdParam = "publicUserId";
	public static final String deviceIdParam = "deviceId";
	public static final String eventIdParam = "publicEventId";
	public static final String eventNameParam = "eventName";
	public static final String eventDescriptionParam = "eventDescription";
	public static final String eventClosedParam = "closed";
	public static final String eventProposalsParam = "proposals";
	public static final String pictureParam = "picture";
	public static final String hashParam = "hash";
	public static final String userNameParam = "user_name";
	public static final String eventProposalDataParam = "data";
	public static final String eventProposalTypeParam = "type";
	public static final String eventResponsesParam = "responses";
	public static final String eventResponseNameParam = "name";
	public static final String commentsNameParam = "name";
	public static final String commentsCommentsParam = "comments";
	public static final String finalDateTimeProposalIdParam = "finalDateTimeProposalId";
	public static final String finalLocationProposalIdParam = "finalLocationProposalId";

	public static final String eventPublicProposalId = "publicProposalId";
	public static final String eventResponseType = "responseType";

	// JSON constants
	public static final String errorCodeResult = "error_code";
	public static final String privateUserIdResult = "private_user_id";
	public static final String publicUserIdResult = "public_user_id";
	public static final String eventResult = "event";
	public static final String eventsResult = "events";
	public static final String proposalsResult = "proposals";
	public static final String responsesResult = "responses";
	public static final String publicEventIdResult = "public_event_id";
	public static final String publicProposalIdResult = "public_proposal_id";
	public static final String proposalUsernameResult = "user_name";
	public static final String proposalResponseResult = "response";
	public static final String proposalDataResult = "proposal_data";
	public static final String proposalTypeResult = "proposal_type";
	public static final String eventNameResult = "name";
	public static final String eventDescriptionResult = "description";
	public static final String eventClosedResult = "closed";
	public static final String pictureResult = "picture";
	public static final String userNameResult = "user_name";
	public static final String userDataResult = "user_data";
	public static final String hashResult = "hash";
	public static final String eventFinalDateTimeProposalId = "final_datetime_proposal_id";
	public static final String eventFinalLocationProposalId = "final_location_proposal_id";

	public static boolean parseEvent(String eventString, User user, Event event) throws JSONException {
		// Prepare the output
		boolean result = false;

		// Analyze response content
		JSONObject object = (JSONObject) new JSONTokener(eventString).nextValue();
		int errorCode = object.getInt(errorCodeResult);

		if (errorCode == Imin.ERROR_CODE_SUCCESS) {
			// Parse the event
			JSONObject eventObject = object.getJSONObject(eventResult);

			String name = eventObject.getString(eventNameResult);
			String description = eventObject.getString(eventDescriptionResult);
			String creatorPublicUserId = eventObject.getString(publicUserIdResult);
			String closed = eventObject.getString(eventClosedResult);
			String finalDateTimeProposalId = eventObject.getString(eventFinalDateTimeProposalId);
			String finalLocationProposalId = eventObject.getString(eventFinalLocationProposalId);
			int intClosed = Integer.parseInt(closed);
			boolean isClosed = intClosed > 0;
			boolean isAdmin = creatorPublicUserId.equals(user.getPublicUserId());

			// Event obtained, build it
			event.setName(name);
			event.setCreator(creatorPublicUserId);
			event.setDescription(description);
			event.setClosed(isClosed);
			event.setAdmin(isAdmin);

			// Add extra fields
			if (isClosed) {
				event.setFinalDateTimeProposalId(finalDateTimeProposalId);
				event.setFinalLocationProposalId(finalLocationProposalId);
			}

			// Parse the list of proposals
			JSONArray listProposals = object.getJSONArray(proposalsResult);

			// Loop the list of retrieved proposals
			boolean found = false;
			List<Proposal> proposals = new ArrayList<Proposal>();
			for (int k = 0; k < listProposals.length(); ++k) {
				JSONObject proposalObject = listProposals.getJSONObject(k);

				String publicProposalId = proposalObject.getString(publicProposalIdResult);
				String proposalData = proposalObject.getString(proposalDataResult);
				int proposalType = proposalObject.getInt(proposalTypeResult);

				Location location = null;
				DateTime dateTime = null;
				if (proposalType == Proposal.PROPOSAL_TYPE_DATETIME) {
					dateTime = new DateTime(proposalData);
				} else if (proposalType == Proposal.PROPOSAL_TYPE_LOCATION) {
					location = new Location(proposalData);
				} else if (proposalType == Proposal.PROPOSAL_TYPE_INVITATION) {
					// ?
				}

				// Proposal obtained, build it
				Proposal proposal = new Proposal(location, dateTime, publicProposalId, proposalType, event);

				// Finally, add the proposal into the list of proposals
				// to be returned
				proposals.add(proposal);

				// Also, add the proposal to the event
				event.addProposal(proposal);

				if (isClosed) {
					// Check if this proposal is the final proposal
					if (proposal.getPublicProposalId().equals(finalDateTimeProposalId)
							|| proposal.getPublicProposalId().equals(finalLocationProposalId)) {
						found = true;
					}
				}
			}

			// Just to be sure
			event.setClosed(isClosed & found);

			// Parse the list of responses
			JSONArray listResponses = object.getJSONArray(responsesResult);

			// Loop the list of retrieved responses
			List<Response> responses = new ArrayList<Response>();
			for (int k = 0; k < listResponses.length(); ++k) {
				JSONObject responseObject = listResponses.getJSONObject(k);

				String publicProposalId = responseObject.getString(publicProposalIdResult);
				String publicUserId = responseObject.getString(publicUserIdResult);
				String proposalUsername = responseObject.getString(proposalUsernameResult);
				int proposalResponse = responseObject.getInt(proposalResponseResult);

				Proposal proposal = null;

				// Find which is the corresponding proposal for that
				// response
				for (int m = 0; m < proposals.size(); ++m) {
					Proposal testProposal = proposals.get(m);

					if (testProposal.getPublicProposalId().equals(publicProposalId)) {
						// testProposal matches
						proposal = testProposal;
						break;
					}
				}

				if (proposal != null) {
					// Build the contact
					Contact contact = user.getContact(publicUserId);
					if (contact == null) {
						contact = new Contact(proposalUsername, publicUserId);
						contact = user.addContact(contact);
					}

					// Response obtained, build it
					Response response = new Response(contact, proposalResponse, proposal);

					// Finally, add the response into the list of
					// responses to be returned
					responses.add(response);

					// Also, add the proposal to the event
					proposal.addResponse(response);

					// IMPORTANT: check if we have responded the event of
					// this proposal
					if (publicUserId.equals(user.getPublicUserId())) {
						event.setResponded(true);
					}
				}
			}

			// Check if we find the corresponding proposal id
			boolean foundDateTime = false;
			boolean foundLocation = false;
			for (int m = 0; m < proposals.size(); ++m) {
				Proposal testProposal = proposals.get(m);

				if (testProposal.getPublicProposalId().equals(event.getFinalDateTimeProposalId())) {
					// Match! we have found the final datetime proposal id
					foundDateTime = true;
					event.setFinalDateTimeProposal(testProposal);
				}

				if (testProposal.getPublicProposalId().equals(event.getFinalLocationProposalId())) {
					// Match! we have found the final location proposal id
					foundLocation = true;
					event.setFinalLocationProposal(testProposal);
				}
			}

			// If not found, close the event (should never happen!?)
			if (!foundDateTime || !foundLocation) {
				event.setClosed(false);
			}

			// Success
			result = true;
		}

		return result;
	}

	public static boolean parseEvents(String eventsString, User user, List<Event> events) throws JSONException {
		boolean result = false;

		// Analyze response content
		JSONObject object = (JSONObject) new JSONTokener(eventsString).nextValue();
		int errorCode = object.getInt(errorCodeResult);

		if (errorCode == Imin.ERROR_CODE_SUCCESS) {
			// Parse the list of events
			JSONArray listEvents = object.getJSONArray(eventsResult);

			// Loop the list of retrieved events
			for (int k = 0; k < listEvents.length(); ++k) {
				JSONObject eventObject = listEvents.getJSONObject(k);

				String publicEventId = eventObject.getString(publicEventIdResult);
				String name = eventObject.getString(eventNameResult);
				String description = eventObject.getString(eventDescriptionResult);
				String publicUserId = eventObject.getString(publicUserIdResult);
				String closed = eventObject.getString(eventClosedResult);
				String finalDateTimeProposalId = eventObject.getString(eventFinalDateTimeProposalId);
				String finalLocationProposalId = eventObject.getString(eventFinalLocationProposalId);
				int intClosed = Integer.parseInt(closed);
				boolean isClosed = intClosed > 0;
				boolean isAdmin = publicUserId.equals(user.getPublicUserId());

				// Event obtained, build it
				Event event = new Event(publicEventId, name, publicUserId, isClosed, isAdmin);

				// Add extra fields
				event.setDescription(description);
				if (isClosed) {
					event.setFinalDateTimeProposalId(finalDateTimeProposalId);
					event.setFinalLocationProposalId(finalLocationProposalId);
				}

				// Finally, add the event into the list of events to be
				// returned
				events.add(event);
			}

			// Parse the list of proposals
			JSONArray listProposals = object.getJSONArray(proposalsResult);

			// Loop the list of retrieved proposals
			List<Proposal> proposals = new ArrayList<Proposal>();
			for (int k = 0; k < listProposals.length(); ++k) {
				JSONObject proposalObject = listProposals.getJSONObject(k);

				String publicEventId = proposalObject.getString(publicEventIdResult);
				String publicProposalId = proposalObject.getString(publicProposalIdResult);
				String proposalData = proposalObject.getString(proposalDataResult);
				int proposalType = proposalObject.getInt(proposalTypeResult);

				Location location = null;
				DateTime dateTime = null;
				if (proposalType == Proposal.PROPOSAL_TYPE_DATETIME) {
					dateTime = new DateTime(proposalData);
				} else if (proposalType == Proposal.PROPOSAL_TYPE_LOCATION) {
					location = new Location(proposalData);
				} else if (proposalType == Proposal.PROPOSAL_TYPE_INVITATION) {
					// ?
				}

				Event event = null;

				// Find which is the corresponding event for that proposal
				for (int m = 0; m < events.size(); ++m) {
					Event testEvent = events.get(m);

					if (testEvent.getId().equals(publicEventId)) {
						// Event matches
						event = testEvent;
						break;
					}
				}

				if (event != null) {
					Proposal proposal = new Proposal(location, dateTime, publicProposalId, proposalType, event);

					// Finally, add the proposal into the list of proposals
					// to be returned
					proposals.add(proposal);

					// Also, add the proposal to the event
					event.addProposal(proposal);
				}
			}

			// Parse the list of responses
			JSONArray listResponses = object.getJSONArray(responsesResult);

			// Loop the list of retrieved responses
			List<Response> responses = new ArrayList<Response>();
			for (int k = 0; k < listResponses.length(); ++k) {
				JSONObject responseObject = listResponses.getJSONObject(k);

				String publicProposalId = responseObject.getString(publicProposalIdResult);
				String publicUserId = responseObject.getString(publicUserIdResult);
				String proposalUsername = responseObject.getString(proposalUsernameResult);
				int proposalResponse = responseObject.getInt(proposalResponseResult);

				Proposal proposal = null;

				// Find which is the corresponding proposal for that
				// response
				for (int m = 0; m < proposals.size(); ++m) {
					Proposal testProposal = proposals.get(m);

					if (testProposal.getPublicProposalId().equals(publicProposalId)) {
						// testProposal matches
						proposal = testProposal;
						break;
					}
				}

				if (proposal != null) {
					// Build the contact
					Contact contact = user.getContact(publicUserId);

					if (contact == null) {
						contact = new Contact(proposalUsername, publicUserId);
						contact = user.addContact(contact);
					}

					// Response obtained, build it
					Response response = new Response(contact, proposalResponse, proposal);

					// Finally, add the response into the list of
					// responses to be returned
					responses.add(response);

					// Also, add the proposal to the event
					proposal.addResponse(response);

					// IMPORTANT: check if we have responded the event of
					// this proposal
					Event proposalEvent = proposal.getEvent();
					if (publicUserId.equals(user.getPublicUserId())) {
						// Check if it is a normal proposal and not an invitation proposal
						if (proposal.getProposalType() != Proposal.PROPOSAL_TYPE_INVITATION) {
							proposalEvent.setResponded(true);
						}
					}
				}
			}

			// At this point, run trough the list of events looking for their final
			// proposal id in the case they are closed
			for (int k = 0; k < events.size(); ++k) {
				Event testEvent = events.get(k);

				if (testEvent.isClosed()) {
					List<Proposal> testProposals = testEvent.getProposals();

					// Check if we find the corresponding proposal id
					boolean foundDateTime = false;
					boolean foundLocation = false;
					for (int m = 0; m < testProposals.size(); ++m) {
						Proposal testProposal = testProposals.get(m);

						if (testProposal.getPublicProposalId().equals(testEvent.getFinalDateTimeProposalId())) {
							// Match! we have found the final datetime proposal id
							foundDateTime = true;
							testEvent.setFinalDateTimeProposal(testProposal);
						}

						if (testProposal.getPublicProposalId().equals(testEvent.getFinalLocationProposalId())) {
							// Match! we have found the final location proposal id
							foundLocation = true;
							testEvent.setFinalLocationProposal(testProposal);
						}
					}

					// If not found, close the event (should never happen!?)
					if (!foundDateTime || !foundLocation) {
						testEvent.setClosed(false);
					}
				}
			}

			// Success
			result = true;
		}

		return result;
	}

}
