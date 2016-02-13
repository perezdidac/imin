package com.imin.events.proposals;

import org.json.JSONException;
import org.json.JSONObject;

import com.imin.events.EventInterface;

public class DateTime implements Comparable<DateTime>, EventInterface {

	public static final DateTime DATETIME_GENERIC = new DateTime("");

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

	// Note: "yyyymmddhhmm"
	public DateTime(String name) {
		this.setName(name);
	}

	public DateTime clone() {
		DateTime dateTime = new DateTime(name);

		return dateTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// Support methods

	public String getDate() {
		String day = "";
		if (name.length() >= 8) {
			day = name.substring(0, 8);
		}

		return day;
	}

	public String getTime() {
		String time = "";
		if (name.length() >= 12) {
			time = name.substring(8, 12);
		}

		return time;
	}

	@Override
	public String toString() {
		// Encode into a String to use it into a JSON data structure
		String string = name;

		return string;
	}

	@Override
	public int compareTo(DateTime another) {
		String anotherString = another.toString();
		String thisString = this.toString();

		return thisString.compareTo(anotherString);
	}

}
