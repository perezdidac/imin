package com.imin.contacts;

import java.io.InputStream;
import java.lang.Thread.State;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.widget.ImageView;

public class TaskPhotos {
	
	private final class QueueItem {
		public Uri uri;
		public Contact contact;
		public ImageView imageView;
		public TaskPhotosListener listener;
	}

	private ContentResolver contentResolver;
	private final Map<String, SoftReference<Bitmap>> cache;
	private final List<QueueItem> queue;
	private final Handler handler;
	private QueueRunner runner;
	private Thread thread;

	public TaskPhotos(ContentResolver contentResolver) {
		this.contentResolver = contentResolver;
		
		cache = new HashMap<String, SoftReference<Bitmap>>();
		queue = new ArrayList<QueueItem>();
		handler = new Handler();
		runner = new QueueRunner();
		
		// Run the thread
		thread = new Thread(runner);
	}

	private class QueueRunner implements Runnable {
		public void run() {
			synchronized (this) {
				while (queue.size() > 0) {
					final QueueItem item = queue.remove(0);

					// If in the cache, return that copy and be done
					if (cache.containsKey(item.uri) && cache.get(item.uri) != null) {
						// Use a handler to get back onto the UI thread for the update
						handler.post(new Runnable() {
							public void run() {
								if (item.listener != null) {
									SoftReference<Bitmap> ref = cache.get(item.uri);
									if (ref != null) {
										item.listener.imageLoaded(ref.get(), item.contact, item.imageView);
									}
								}
							}
						});
					} else {
						// Try to retrieve the contact photo
						InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(contentResolver, item.uri);

						// Check if the contact has a photo
						if (input != null) {
							final Bitmap contactPhoto = BitmapFactory.decodeStream(input);

							cache.put(item.uri.toString(), new SoftReference<Bitmap>(contactPhoto));

							// Use a handler to get back onto the UI thread for the update
							handler.post(new Runnable() {
								public void run() {
									if (item.listener != null) {
										item.listener.imageLoaded(contactPhoto, item.contact, item.imageView);
									}
								}
							});
						} else {
							// Use a handler to get back onto the UI thread for the update
							handler.post(new Runnable() {
								public void run() {
									if (item.listener != null) {
										item.listener.imageNotLoaded(item.contact);
									}
								}
							});
						}
					}
				}
			}
		}
	}

	public Bitmap loadImage(Uri uri, Contact contact, ImageView imageView, TaskPhotosListener listener) throws MalformedURLException {
		// If it's in the cache, just get it and quit it
		if (cache.containsKey(uri)) {
			SoftReference<Bitmap> ref = cache.get(uri);
			if (ref != null) {
				return ref.get();
			}
		}

		QueueItem item = new QueueItem();
		item.uri = uri;
		item.contact = contact;
		item.imageView = imageView;
		item.listener = listener;
		
		// In here, we must remove from the queue any item that contains
		// the same ImageView that is about to be queued
		int index = 0;
		while (index < queue.size()) {
			if (queue.get(index).imageView == imageView) {
				// Same, so remove
				queue.remove(index);
			} else {
				++index;
			}
		}
		queue.add(item);

		// start the thread if needed
		if (thread.getState() == State.NEW) {
			thread.start();
		} else if (thread.getState() == State.TERMINATED) {
			thread = new Thread(runner);
			thread.start();
		}
		return null;
	}

	public interface TaskPhotosListener {
		public void imageLoaded(Bitmap imageBitmap, Contact contact, ImageView imageView);
		public void imageNotLoaded(Contact contact);
	}

}