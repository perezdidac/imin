package com.imin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.google.analytics.tracking.android.EasyTracker;
import com.imin.analytics.Analytics;
import com.imin.events.Event;
import com.imin.events.pictures.PictureHelper;
import com.imin.widgets.DialogInformation;
import com.imin.widgets.DialogPicture;
import com.imin.widgets.DialogQuestion;
import com.imin.widgets.ResizableImageView;

public class CreateEventActivity extends ActionBarActivity {

	// List of objects in the layout
	private EditText textEventName;
	private Button btnSelectPicture;
	private Button btnNext;

	// Objects that must be saved
	private ResizableImageView imageEventPicture;
	private Bitmap bitmap;
	private boolean usePicture;

	// Imin application object
	private Imin imin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_event);
		Imin.overrideFonts(findViewById(android.R.id.content));

		// Get the Imin application object
		imin = ((Imin) getApplication());

		// Initialize objects
		initializeObjects();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		// Save the values
		savedInstanceState.putString("bitmap", PictureHelper.encode(bitmap));
		savedInstanceState.putBoolean("usePicture", usePicture);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		// Restore the values
		String bitmapData = savedInstanceState.getString("bitmap");
		usePicture = savedInstanceState.getBoolean("usePicture");
		bitmap = PictureHelper.decode(bitmapData);

		if (usePicture) {
			// Repaint views
			imageEventPicture.setImageBitmap(bitmap);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Check source activity
		if (requestCode == Imin.REQUEST_CODE_SELECT_LOCATIONS) {
			// Check activity result
			if (resultCode == Imin.RESULT_CODE_SELECT_LOCATIONS_OK) {
				// Event created, return
				setResult(Imin.RESULT_CODE_CREATE_EVENT_OK);
				finish();
			}
		} else if (requestCode == Imin.REQUEST_CODE_CAMERA) {
			// Check activity result
			if (resultCode == Imin.RESULT_CODE_CAMERA_OK) {
				pictureTaken(data);
			}
		} else if (requestCode == Imin.REQUEST_CODE_GALLERY) {
			// Check activity result
			if (resultCode == Imin.RESULT_CODE_CAMERA_OK) {
				// Picture selected
				pictureSelected(data);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_event, menu);
        return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initializeObjects() {
		// Save object references
		textEventName = (EditText) findViewById(R.id.textEventName);
		imageEventPicture = (ResizableImageView) findViewById(R.id.imageEventPicture);
		btnSelectPicture = (Button) findViewById(R.id.btnSelectPicture);
		btnNext = (Button) findViewById(R.id.btnNext);

		// Show/hide

		btnSelectPicture.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectPicture();
			}
		});

		imageEventPicture.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				removePicture();
				return true;
			}
		});

		btnNext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				createEvent();
			}
		});
	}

	private void pictureTaken(Intent intent) {
		// Get bitmap
		Bundle extras = intent.getExtras();
		bitmap = (Bitmap) extras.get("data");

		// Check if Bitmap is correct
		if (bitmap != null) {
			// Crop and resize the picture
			bitmap = PictureHelper.prepare(bitmap, 384);
			if (bitmap == null) {
				DialogInformation.build(this).show(getString(R.string.oops_we_are_still_experiencing_errors),
						DialogInformation.ICON_ALERT);
				Analytics.send(this, Analytics.ERROR_OUT_OF_MEMORY);
			} else {
				imageEventPicture.setImageBitmap(bitmap);
				usePicture = true;

				// Analytics event
				Analytics.send(this, Analytics.ANALYTICS_EVENT_SELECT_PHOTO);
			}
		}
	}

	private void pictureSelected(Intent intent) {
		// Get bitmap
		bitmap = imin.getBitmapFromIntent(intent);

		// Check if Bitmap is correct
		if (bitmap != null) {
			// Crop and resize the picture
			bitmap = PictureHelper.prepare(bitmap, 384);

			if (bitmap == null) {
				DialogInformation.build(this).show(getString(R.string.oops_we_are_still_experiencing_errors),
						DialogInformation.ICON_ALERT);
				Analytics.send(this, Analytics.ERROR_OUT_OF_MEMORY);
			} else {
				imageEventPicture.setImageBitmap(bitmap);
				usePicture = true;
			}
		}
	}

	private void selectPicture() {
		// Create a dialog for asking the user about event removal
		DialogPicture dialogPicture = new DialogPicture(this, new DialogPicture.OnClickListener() {
			@Override
			public void onClick(View view, int source) {
				if (source == DialogPicture.SELECT_CAMERA) {
					// From camera
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(intent, Imin.REQUEST_CODE_CAMERA);
				} else if (source == DialogPicture.SELECT_GALLERY) {
					// From gallery
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(Intent.createChooser(intent, getString(R.string.select_a_picture)),
							Imin.REQUEST_CODE_GALLERY);
				}
			}
		});

		dialogPicture.show(getString(R.string.add_picture));
	}

	private void removePicture() {
		// Create a dialog for asking the user about event removal
		DialogQuestion dialogQuestion = new DialogQuestion(this, new DialogQuestion.OnClickListener() {
			@Override
			public void onClick(View view, boolean result) {
				if (result) {
					// Yes button clicked
					usePicture = false;
					bitmap = null;

					// Destroy and hide the picture
					imageEventPicture.setImageBitmap(null);
				}
			}
		});

		dialogQuestion.show(getString(R.string.would_you_like_to_remove_the_picture));
	}

	private void createEvent() {
		final Event event = new Event();

		// I am the administrator!
		event.setAdmin(true);

		// Get event information
		String eventName = textEventName.getText().toString();

		// Check event information
		eventName = eventName.trim();

		if (eventName.length() == 0) {
			// Event name is empty, show a toast
			DialogInformation.build(this).show(getString(R.string.please_introduce_a_valid_name_for_the_event),
					DialogInformation.ICON_ALERT);
			return;
		}

		// Build the event
		event.setCreator(imin.getUser().getPublicUserId());
		event.setName(eventName);
		event.setDescription("");

		// Add the picture
		if (usePicture && bitmap != null) {
			event.setPicture(bitmap);
		}

		// Save it as the temporal current event
		imin.getUser().setCurrentEvent(event);
		imin.getUser().setEventCreated(false);

		// Go to the next step
		Intent intent = new Intent(this, SelectLocationsActivity.class);
		startActivityForResult(intent, Imin.REQUEST_CODE_SELECT_LOCATIONS);
	}
}
