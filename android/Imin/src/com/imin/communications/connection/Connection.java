package com.imin.communications.connection;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.imin.Imin;
import com.imin.api.IminLib;
import com.imin.contacts.Contact;
import com.imin.events.Event;
import com.imin.events.proposals.DateTime;
import com.imin.events.proposals.Location;
import com.imin.events.proposals.Proposal;
import com.imin.events.responses.Response;
import com.imin.user.User;

public class Connection {

	// PUBLIC METHODS

	// Retrieve a random user id from the server
	public static List<String> getPrivateAndPublicUserId(String deviceId) {
		// Build the URL
		String url = IminLib.baseUrl + IminLib.getUserIdUrl + "?" + IminLib.deviceIdParam + "=" + deviceId;

		List<String> privateAndPublicUserId = new ArrayList<String>();

		try {
			// Perform the GET
			String response = HttpHelper.get(url);

			// Analyze response content
			JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
			int errorCode = object.getInt(IminLib.errorCodeResult);

			if (errorCode == 0) {
				// Parse the values of interest
				String privateUserId = object.getString(IminLib.privateUserIdResult);
				String publicUserId = object.getString(IminLib.publicUserIdResult);

				// Check for the values
				// ...

				// Finally, put the values
				privateAndPublicUserId.add(privateUserId);
				privateAndPublicUserId.add(publicUserId);
			} else {
				// Error
			}
		} catch (Exception e) {
			// Error
			e.printStackTrace();
		}

		return privateAndPublicUserId;
	}

	// Retrieve a single event from the server
	public static boolean getEvent(User user, Event event) {
		// Build the URL
		String url = IminLib.baseUrl + IminLib.getEventUrl + "?" + IminLib.eventIdParam + "=" + event.getId();

		// Prepare the output
		boolean result = false;

		try {
			// Perform the GET
			String getResponse = HttpHelper.get(url);

			// Parse and get the event
			result = IminLib.parseEvent(getResponse, user, event);
		} catch (Exception e) {
			// Error
			result = false;
		}

		return result;
	}

	// Retrieve the event list from the server
	public static boolean getEvents(User user, List<Event> events) {
		// Build the URL
		String url = IminLib.baseUrl + IminLib.getEventsUrl + "?" + IminLib.privateUserIdParam + "=" + user.getPrivateUserId();

		// Prepare the output
		boolean result = false;

		// Check for null values
		if (user.getPrivateUserId() == null)
			return false;

		// Perform the GET
		try {
			String getResponse = HttpHelper.get(url);

			// Parse and get the list of events
			result = IminLib.parseEvents(getResponse, user, events);
			
			if (result) {
				user.saveEvents(events);
			}
		} catch (Exception e) {
			// Error
			result = false;
		}
		
		return result;
	}

	// Removes the event from the server
	public static boolean removeEvent(String privateUserId, String eventId) {
		// Build the URL
		String url = IminLib.baseUrl + IminLib.removeEventUrl + "?" + IminLib.privateUserIdParam + "=" + privateUserId + "&" + IminLib.eventIdParam
				+ "=" + eventId;

		boolean result = false;

		try {
			// Perform the GET
			String response = HttpHelper.get(url);

			// Analyze response content
			JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
			int errorCode = object.getInt(IminLib.errorCodeResult);

			// Analyze response content
			if (errorCode == Imin.ERROR_CODE_SUCCESS) {
				// Event removed
				result = true;
			} else {
				// Retrieve the cause of the error
			}
		} catch (Exception e) {
			// Error
		}

		return result;
	}

	// Removes all the responses from a given event from the server
	public static boolean leaveEvent(String privateUserId, String eventId) {
		// Build the URL
		String url = IminLib.baseUrl + IminLib.leaveEventUrl + "?" + IminLib.privateUserIdParam + "=" + privateUserId + "&" + IminLib.eventIdParam
				+ "=" + eventId;

		boolean result = false;

		try {
			// Perform the GET
			String response = HttpHelper.get(url);

			// Analyze response content
			JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
			int errorCode = object.getInt(IminLib.errorCodeResult);

			// Analyze response content
			if (errorCode == Imin.ERROR_CODE_SUCCESS) {
				// Event removed
				result = true;
			} else {
				// Retrieve the cause of the error
			}
		} catch (Exception e) {
			// Error
		}

		return result;
	}

	// Pushes an event to the server
	public static String pushEvent(String privateUserId, Event event) {
		// Build the URL
		String url = IminLib.baseUrl + IminLib.pushEventUrl;

		String publicEventId = null;

		String eventName = event.getName();
		String eventDescription = event.getDescription();

		// Prepare parameters
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair(IminLib.privateUserIdParam, privateUserId));
		parameters.add(new BasicNameValuePair(IminLib.eventNameParam, eventName));
		parameters.add(new BasicNameValuePair(IminLib.eventDescriptionParam, eventDescription));

		// Create a JSON object with all the proposals
		List<Proposal> proposals = event.getProposals();
		JSONArray jsonProposals = new JSONArray();

		for (int k = 0; k < proposals.size(); ++k) {
			// Build the proposal string
			Proposal proposal = proposals.get(k);

			Location location = proposal.getLocation();
			DateTime dateTime = proposal.getDateTime();
			int type = proposal.getProposalType();

			String data;
			if (type == Proposal.PROPOSAL_TYPE_LOCATION && location != null) {
				data = location.getName();
			} else if (type == Proposal.PROPOSAL_TYPE_DATETIME && dateTime != null) {
				data = dateTime.getName();
			} else if (type == Proposal.PROPOSAL_TYPE_INVITATION) {
				data = "";
			} else {
				// Empty proposal, do not add
				continue;
			}

			try {
				JSONObject jsonProposal = new JSONObject();

				jsonProposal.put(IminLib.eventProposalDataParam, data);
				jsonProposal.put(IminLib.eventProposalTypeParam, type);

				// Append the proposal into the list of proposals
				jsonProposals.put(jsonProposal);
			} catch (JSONException e) {
				// Error while encoding the JSON
			}
		}

		// Finally, add the encoded JSON into the list of parameters
		parameters.add(new BasicNameValuePair(IminLib.eventProposalsParam, jsonProposals.toString()));

		try {
			// Perform the POST
			String response = HttpHelper.post(url, parameters);

			// Analyze response content
			JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
			int errorCode = object.getInt(IminLib.errorCodeResult);

			if (errorCode == 0) {
				// Parse the values of interest
				publicEventId = object.getString(IminLib.publicEventIdResult);
			} else {
				// Error
			}
		} catch (Exception e) {
			// Error
			return publicEventId;
		}

		return publicEventId;
	}

	// Uploads a picture for a given event to the server
	public static boolean uploadPicture(String privateUserId, String publicEventId, String picture, String hash) {
		// Build the URL
		String url = IminLib.baseUrl + IminLib.uploadPictureUrl;

		boolean result = false;

		// Prepare parameters
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair(IminLib.privateUserIdParam, privateUserId));
		parameters.add(new BasicNameValuePair(IminLib.eventIdParam, publicEventId));
		parameters.add(new BasicNameValuePair(IminLib.pictureParam, picture));
		parameters.add(new BasicNameValuePair(IminLib.hashParam, hash));

		try {
			// Perform the POST
			String response = HttpHelper.post(url, parameters);

			// Analyze response content
			JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
			int errorCode = object.getInt(IminLib.errorCodeResult);

			if (errorCode == 0) {
				// Check for the values
				result = true;
			} else {
				// Error
			}
		} catch (Exception e) {
			// Error
			return result;
		}

		return result;
	}

	// Gets the hash of a picture from a given event from the server
	public static String hashPicture(String publicEventId) {
		// Build the URL
		String url = IminLib.baseUrl + IminLib.hashPictureUrl + "?" + IminLib.eventIdParam + "=" + publicEventId;

		try {
			// Perform the GET
			String response = HttpHelper.get(url);

			// Analyze response content
			JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
			int errorCode = object.getInt(IminLib.errorCodeResult);

			if (errorCode == 0) {
				// Parse the values of interest
				JSONObject jsonHash = object.getJSONObject(IminLib.hashResult);
				String eventHash = jsonHash.getString(IminLib.hashResult);

				// Return
				return eventHash;
			} else {
				// Error
				return null;
			}
		} catch (Exception e) {
			// Error
			e.printStackTrace();
		}

		return null;
	}

	// Downloads a picture from a given event from the server
	public static String downloadPicture(String publicEventId) {
		// Build the URL
		String url = IminLib.baseUrl + IminLib.downloadPictureUrl + "?" + IminLib.eventIdParam + "=" + publicEventId;

		try {
			// Perform the GET
			String response = HttpHelper.get(url);

			// Analyze response content
			JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
			int errorCode = object.getInt(IminLib.errorCodeResult);

			if (errorCode == 0) {
				// Parse the values of interest
				JSONObject jsonPicture = object.getJSONObject(IminLib.pictureResult);
				String eventPicture = jsonPicture.getString(IminLib.pictureResult);

				// Return
				return eventPicture;
			} else {
				// Error
				return null;
			}
		} catch (Exception e) {
			// Error
			e.printStackTrace();
		}

		return null;
	}

	// Removes the event picture from the server
	public static boolean removePicture(String publicEventId) {
		// Build the URL
		String url = IminLib.baseUrl + IminLib.removePictureUrl + "?" + IminLib.eventIdParam + "=" + publicEventId;

		try {
			// Perform the GET
			String response = HttpHelper.get(url);

			// Analyze response content
			JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
			int errorCode = object.getInt(IminLib.errorCodeResult);

			if (errorCode == 0) {
				// Return
				return true;
			} else {
				// Error
				return false;
			}
		} catch (Exception e) {
			// Error
			e.printStackTrace();
		}

		return false;
	}

	// Uploads a picture for a given user to the server
	public static boolean uploadUserData(String privateUserId, String publicUserId, String picture, String hash,
			String username) {
		// Build the URL
		String url = IminLib.baseUrl + IminLib.uploadUserDataUrl;

		boolean result = false;

		// Prepare parameters
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair(IminLib.privateUserIdParam, privateUserId));
		parameters.add(new BasicNameValuePair(IminLib.publicUserIdParam, publicUserId));
		parameters.add(new BasicNameValuePair(IminLib.pictureParam, picture));
		parameters.add(new BasicNameValuePair(IminLib.hashParam, hash));
		parameters.add(new BasicNameValuePair(IminLib.userNameParam, username));

		try {
			// Perform the POST
			String response = HttpHelper.post(url, parameters);

			// Analyze response content
			JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
			int errorCode = object.getInt(IminLib.errorCodeResult);

			if (errorCode == 0) {
				// Check for the values
				result = true;
			} else {
				// Error
			}
		} catch (Exception e) {
			// Error
			return result;
		}

		return result;
	}

	// Gets the hash of a picture from a given user from the server
	public static String hashUserPicture(String publicUserId) {
		// Build the URL
		String url = IminLib.baseUrl + IminLib.hashUserPictureUrl + "?" + IminLib.publicUserIdParam + "=" + publicUserId;

		try {
			// Perform the GET
			String response = HttpHelper.get(url);

			// Analyze response content
			JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
			int errorCode = object.getInt(IminLib.errorCodeResult);

			if (errorCode == 0) {
				// Parse the values of interest
				JSONObject jsonHash = object.getJSONObject(IminLib.hashResult);
				String userHash = jsonHash.getString(IminLib.hashResult);

				// Return
				return userHash;
			} else {
				// Error
				return null;
			}
		} catch (Exception e) {
			// Error
			e.printStackTrace();
		}

		return null;
	}

	// Downloads a picture from a given user from the server
	public static String downloadUserData(Contact contact) {
		// Build the URL
		String url = IminLib.baseUrl + IminLib.downloadUserDataUrl + "?" + IminLib.publicUserIdParam + "=" + contact.getPublicUserId();

		try {
			// Perform the GET
			String response = HttpHelper.get(url);

			// Analyze response content
			JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
			int errorCode = object.getInt(IminLib.errorCodeResult);

			if (errorCode == 0) {
				// Parse the values of interest
				JSONObject jsonPicture = object.getJSONObject(IminLib.userDataResult);
				String userPicture = jsonPicture.getString(IminLib.pictureResult);
				String username = jsonPicture.getString(IminLib.userNameResult);

				// Set user name
				contact.setName(username);

				// Return base64-encoded image
				return userPicture;
			} else {
				// Error
				return null;
			}
		} catch (Exception e) {
			// Error
			e.printStackTrace();
		}

		return null;
	}

	// Removes the user picture from the server
	public static boolean removeUserPicture(String publicUserId) {
		// Build the URL
		String url = IminLib.baseUrl + IminLib.removeUserPictureUrl + "?" + IminLib.publicUserIdParam + "=" + publicUserId;

		try {
			// Perform the GET
			String response = HttpHelper.get(url);

			// Analyze response content
			JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
			int errorCode = object.getInt(IminLib.errorCodeResult);

			if (errorCode == 0) {
				// Return
				return true;
			} else {
				// Error
				return false;
			}
		} catch (Exception e) {
			// Error
			e.printStackTrace();
		}

		return false;
	}

	// Respond an event to the server
	public static boolean respondEvent(String privateUserId, Event event, List<Response> responses) {
		// Build the URL
		String url = IminLib.baseUrl + IminLib.respondEventUrl;

		boolean result = false;

		String publicEventId = event.getId();

		// Prepare parameters
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair(IminLib.privateUserIdParam, privateUserId));
		parameters.add(new BasicNameValuePair(IminLib.eventIdParam, publicEventId));

		// Create a JSON object with all the proposals
		JSONArray jsonResponses = new JSONArray();

		for (int k = 0; k < responses.size(); ++k) {
			// Build the response string
			Response response = responses.get(k);

			Contact contact = response.getContact();
			Proposal proposal = response.getProposal();
			int responseType = response.getResponseType();

			String name = contact.getName();
			String publicProposalId = proposal.getPublicProposalId();

			JSONObject jsonResponse = new JSONObject();
			try {
				jsonResponse.put(IminLib.eventResponseNameParam, name);
				jsonResponse.put(IminLib.eventPublicProposalId, publicProposalId);
				jsonResponse.put(IminLib.eventResponseType, responseType);
			} catch (JSONException e) {
				// Error while encoding the JSON
			}

			// Append the response into the list of responses
			jsonResponses.put(jsonResponse);
		}

		// Finally, add the encoded JSON into the list of parameters
		parameters.add(new BasicNameValuePair(IminLib.eventResponsesParam, jsonResponses.toString()));

		try {
			// Perform the POST
			String response = HttpHelper.post(url, parameters);

			// Analyze response content
			JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
			int errorCode = object.getInt(IminLib.errorCodeResult);

			if (errorCode == 0) {
				// Check for the values
				result = true;
			} else {
				// Error
			}
		} catch (Exception e) {
			// Error
			return result;
		}

		return result;
	}

	// Change the closed flag of an event
	public static boolean closeEvent(String privateUserId, String publicEventId, boolean closed,
			String finalDateTimeProposalId, String finalLocationProposalId) {
		// Build the URL
		String url = IminLib.baseUrl + IminLib.closeEventUrl;

		boolean result = false;

		// Prepare parameters
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair(IminLib.privateUserIdParam, privateUserId));
		parameters.add(new BasicNameValuePair(IminLib.eventIdParam, publicEventId));
		parameters.add(new BasicNameValuePair(IminLib.eventClosedParam, Boolean.toString(closed)));
		parameters.add(new BasicNameValuePair(IminLib.finalDateTimeProposalIdParam, finalDateTimeProposalId));
		parameters.add(new BasicNameValuePair(IminLib.finalLocationProposalIdParam, finalLocationProposalId));

		try {
			// Perform the POST
			String response = HttpHelper.post(url, parameters);

			// Analyze response content
			JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
			int errorCode = object.getInt(IminLib.errorCodeResult);

			if (errorCode == 0) {
				// Check for the values
				result = true;
			} else {
				// Error
			}
		} catch (Exception e) {
			// Error
			return result;
		}

		return result;
	}

	// Send comments/suggestions about the application
	public static boolean sendComments(String privateUserId, String name, String comments) {
		// Build the URL
		String url = IminLib.baseUrl + IminLib.sendCommentsUrl;

		boolean result = false;

		// Prepare parameters
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair(IminLib.privateUserIdParam, privateUserId));
		parameters.add(new BasicNameValuePair(IminLib.commentsNameParam, name));
		parameters.add(new BasicNameValuePair(IminLib.commentsCommentsParam, comments));

		try {
			// Perform the POST
			String response = HttpHelper.post(url, parameters);

			// Analyze response content
			JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
			int errorCode = object.getInt(IminLib.errorCodeResult);

			if (errorCode == 0) {
				// Check for the values
				result = true;
			} else {
				// Error
			}
		} catch (Exception e) {
			// Error
			return result;
		}

		return result;
	}

}
