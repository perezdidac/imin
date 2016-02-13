package com.imin.events.proposals;

import org.json.JSONException;
import org.json.JSONObject;

import com.imin.events.EventInterface;

public class Location implements Comparable<Location>, EventInterface {

	public static final Location LOCATION_GENERIC = new Location("");

	private String name;

	@Override
	public JSONObject toJson() throws JSONException {
		JSONObject jsonEvent = new JSONObject();

		jsonEvent.put("name", name);

		return jsonEvent;
	}

	@Override
	public void fromJson(JSONObject jsonObject) throws JSONException {
		// Read the JSON and fill the object values
		name = jsonObject.getString("name");
	}

	public Location(String name) {
		this.setName(name);
	}

	public Location clone() {
		Location location = new Location(name);

		return location;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		// Encode into a String to use it into a JSON data structure
		String string = name;

		return string;
	}

	@Override
	public int compareTo(Location another) {
		String anotherString = another.toString();
		String thisString = this.toString();

		return thisString.compareTo(anotherString);
	}

}
