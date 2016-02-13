package com.imin.communications.events;

import java.util.List;

import android.os.AsyncTask;

import com.imin.communications.connection.Connection;
import com.imin.user.User;

public class TaskLeaveEvent extends AsyncTask<TaskLeaveEventParams, Integer, TaskLeaveEventResult> {

	private TaskLeaveEventListener taskLeaveEventListener;

	/** Constructs the class object. **/
	public TaskLeaveEvent(TaskLeaveEventListener taskLeaveEventListener) {
		//
		this.taskLeaveEventListener = taskLeaveEventListener;
	}

	protected TaskLeaveEventResult doInBackground(TaskLeaveEventParams... params) {
		// Get the parameters
		TaskLeaveEventParams parameters = params[0];

		// First, check if the event has an invitation and must be removed
		User user = parameters.getUser();
		String publicEventId = parameters.getPublicEventId();
		List<String> invitations = user.getInvitations();
		
		// Remove possible invitations and duplicates
		int index = 0;
		while (index < invitations.size()) {
			String invitation = invitations.get(index);
			
			// Check
			if (invitation.equals(publicEventId)) {
				// Found!
				invitations.remove(index);
			} else {
				++index;
			}
		}
		
		// Save invitations
		user.setInvitations(invitations);

		// Leave the event
		boolean left = Connection.leaveEvent(user.getPrivateUserId(), parameters.getPublicEventId());

		return new TaskLeaveEventResult(left);
	}

	protected void onPostExecute(TaskLeaveEventResult result) {
		// Create a message for passing the results to the calling activity
		if (result.isSuccess()) {
			taskLeaveEventListener.onEventLeft();
		} else {
			taskLeaveEventListener.onEventNotLeft();
		}
	}

	public interface TaskLeaveEventListener {
		public void onEventLeft();

		public void onEventNotLeft();
	}

}
