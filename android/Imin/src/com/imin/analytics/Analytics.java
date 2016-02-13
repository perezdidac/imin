package com.imin.analytics;

import android.content.Context;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

public class Analytics {

	public static final int ANALYTICS_APP_LAUNCH = 0;
	
	public static final int ANALYTICS_EVENT_CREATE = 100;
	public static final int ANALYTICS_EVENT_REMOVE = 101;
	public static final int ANALYTICS_EVENT_SELECT_PHOTO = 102;
	public static final int ANALYTICS_EVENT_VOTE = 103;
	public static final int ANALYTICS_EVENT_RECEIVE_INVITATION = 104;
	public static final int ANALYTICS_EVENT_LEAVE = 105;
	public static final int ANALYTICS_EVENT_CLOSE = 106;
	public static final int ANALYTICS_EVENT_REOPEN = 107;
	
	public static final int ANALYTICS_PROFILE_CREATE = 200;
	public static final int ANALYTICS_PROFILE_REMOVE = 201;
	public static final int ANALYTICS_PROFILE_CHANGE_NAME = 202;
	public static final int ANALYTICS_PROFILE_CHANGE_PICTURE = 203;
	
	public static final int ERROR_OUT_OF_MEMORY = 300;
	public static final int ERROR_LAYOUT_PULL_TO_REFRESH_NULL = 301;
	
	public static void send(Context context, int eventType) {
		EasyTracker easyTracker = EasyTracker.getInstance(context);

		String category = "";
		String action = "";
		String label = "";

		if (eventType == ANALYTICS_APP_LAUNCH) {
			category = "app";
			action = "app_launch";
			label = "";
		} else if (eventType == ANALYTICS_EVENT_CREATE) {
			category = "event";
			action = "event_create";
			label = "";
		} else if (eventType == ANALYTICS_EVENT_REMOVE) {
			category = "event";
			action = "event_remove";
			label = "";
		} else if (eventType == ANALYTICS_EVENT_SELECT_PHOTO) {
			category = "event";
			action = "event_select_photo";
			label = "";
		} else if (eventType == ANALYTICS_EVENT_VOTE) {
			category = "event";
			action = "event_vote";
			label = "";
		} else if (eventType == ANALYTICS_EVENT_RECEIVE_INVITATION) {
			category = "event";
			action = "event_receive_invitation";
			label = "";
		} else if (eventType == ANALYTICS_EVENT_LEAVE) {
			category = "event";
			action = "event_leave";
			label = "";
		} else if (eventType == ANALYTICS_EVENT_CLOSE) {
			category = "event";
			action = "event_close";
			label = "";
		} else if (eventType == ANALYTICS_EVENT_REOPEN) {
			category = "event";
			action = "event_reopen";
			label = "";
		} else if (eventType == ANALYTICS_PROFILE_CREATE) {
			category = "profile";
			action = "profile_create";
			label = "";
		} else if (eventType == ANALYTICS_PROFILE_REMOVE) {
			category = "profile";
			action = "profile_remove";
			label = "";
		} else if (eventType == ANALYTICS_PROFILE_CHANGE_NAME) {
			category = "profile";
			action = "profile_change_name";
			label = "";
		} else if (eventType == ANALYTICS_PROFILE_CHANGE_PICTURE) {
			category = "profile";
			action = "profile_change_picture";
			label = "";
		} else if (eventType == ERROR_OUT_OF_MEMORY) {
			category = "error";
			action = "error_out_of_memory";
			label = "";
		} else if (eventType == ERROR_LAYOUT_PULL_TO_REFRESH_NULL) {
			category = "error";
			action = "error_layout_pull_to_refresh_null";
			label = "";
		}

		easyTracker.send(MapBuilder.createEvent(category, action, label, null).build());
	}

}
