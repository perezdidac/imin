package com.imin.communications.events;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.imin.communications.connection.Connection;
import com.imin.events.Event;
import com.imin.events.pictures.FileCache;
import com.imin.events.pictures.PictureHelper;
import com.imin.user.Files;

/** Provides asynchronous event retrieval **/
public class TaskPushEvent extends AsyncTask<TaskPushEventParams, Integer, TaskPushEventResult> {

	private Context context;
	private TaskPushEventListener taskPushEventListener;

	/** Constructs the class object. **/
	public TaskPushEvent(Context context, TaskPushEventListener taskPushEventListener) {
		//
		this.context = context;
		this.taskPushEventListener = taskPushEventListener;
	}

	public void setListener(TaskPushEventListener taskPushEventListener) {
		this.taskPushEventListener = taskPushEventListener;
	}

	protected TaskPushEventResult doInBackground(TaskPushEventParams... params) {
		// Create the task result object
		String privateUserId = params[0].getPrivateUserId();
		Event event = params[0].getEvent();

		// Retrieve list of events
		String publicEventId = Connection.pushEvent(privateUserId, event);
		boolean success = (publicEventId.length() > 0);

		if (success) {
			event.setId(publicEventId);
			Bitmap bitmap = event.getPicture();
			if (bitmap != null) {
				String base64 = PictureHelper.encode(bitmap);

				FileCache fileCache = new FileCache(context);
				String hash = Files.hashFile(base64.getBytes());
				fileCache.add(hash, base64.getBytes());
				Connection.uploadPicture(privateUserId, publicEventId, base64, hash);
			}
		}

		return new TaskPushEventResult(publicEventId, success);
	}

	protected void onPostExecute(TaskPushEventResult result) {
		// Create a message for passing the results to the calling activity
		if (result.isSuccess()) {
			taskPushEventListener.onEventPushed(result.getPublicEventId());
		} else {
			taskPushEventListener.onEventNotPushed();
		}
	}

	public interface TaskPushEventListener {
		public void onEventPushed(String publicEventId);

		public void onEventNotPushed();
	}

}
