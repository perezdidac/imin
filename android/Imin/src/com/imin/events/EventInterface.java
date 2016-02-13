package com.imin.events;

import org.json.JSONException;
import org.json.JSONObject;

public interface EventInterface {

	public JSONObject toJson() throws JSONException;

	public void fromJson(JSONObject jsonObject) throws JSONException;
}
