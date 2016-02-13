package com.imin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.imin.analytics.Analytics;
import com.imin.communications.user.TaskUploadUserData;
import com.imin.communications.user.TaskUploadUserData.TaskUploadUserDataListener;
import com.imin.communications.user.TaskUploadUserDataParams;
import com.imin.contacts.Contact;
import com.imin.events.pictures.PictureHelper;
import com.imin.widgets.DialogInformation;
import com.imin.widgets.DialogPicture;

public class WelcomeActivity extends Activity implements TaskUploadUserDataListener {

    // Class that stores the configuration instance for recreating the
    // full activity restoring the dialogs and other
    private class WelcomeActivityConfigurationInstance {
        public TaskUploadUserData taskUploadUserData;
    }

    // Object references
    private EditText textUserName;
    private CheckBox checkLegal;

    // Objects that must be saved
    private ImageView imageUser;
    private Bitmap bitmap;

    // Tasks
    private WelcomeActivityConfigurationInstance configurationInstance;

    // Imin application object
    private Imin imin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Imin.overrideFonts(findViewById(android.R.id.content));

        // Initialize objects
        initializeObjects();
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        if (configurationInstance.taskUploadUserData != null) {
            configurationInstance.taskUploadUserData.setListener(null);
        }
        return configurationInstance;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // Save the values
        savedInstanceState.putString("bitmap", PictureHelper.encode(bitmap));
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore the values
        String bitmapData = savedInstanceState.getString("bitmap");
        bitmap = PictureHelper.decode(bitmapData);

        if (bitmap != null) {
            // Repaint views
            imageUser.setImageBitmap(bitmap);
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
        if (requestCode == Imin.REQUEST_CODE_CAMERA) {
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

    private void initializeObjects() {
        TextView textLegal;
        Button btnEnter;

        // Save object references
        textUserName = (EditText) findViewById(R.id.textUserName);
        checkLegal = (CheckBox) findViewById(R.id.checkLegal);
        textLegal = (TextView) findViewById(R.id.textLegal);
        imageUser = (ImageView) findViewById(R.id.imageUser);
        btnEnter = (Button) findViewById(R.id.btnEnter);

        // Restore dialogs
        restoreDialogs();

        checkLegal.setVisibility(View.VISIBLE);
        textLegal.setVisibility(View.VISIBLE);
        textLegal.setMovementMethod(LinkMovementMethod.getInstance());

        // Set event handlers
        imageUser.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPicture();
            }
        });

        btnEnter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                enter();
            }
        });

        // Get the Imin application object
        imin = ((Imin) getApplication());

        // Load contact data
        Contact contact = imin.getUser().getContact();
        String username = contact.getName();
        bitmap = contact.getPhoto();

        if (username != null) {
            textUserName.setText(username);
            textUserName.setSelection(username.length());
        }

        if (bitmap != null) {
            imageUser.setImageBitmap(bitmap);
        }
    }

    private void restoreDialogs() {
        @SuppressWarnings("deprecation")
        Object retained = getLastNonConfigurationInstance();

        if (retained instanceof WelcomeActivityConfigurationInstance) {
            configurationInstance = (WelcomeActivityConfigurationInstance) retained;
            if (configurationInstance.taskUploadUserData != null) {
                configurationInstance.taskUploadUserData.setListener(this);
            }
        } else {
            configurationInstance = new WelcomeActivityConfigurationInstance();
        }
    }

    private void pictureTaken(Intent intent) {
        // Get bitmap
        Bundle extras = intent.getExtras();
        Object object = extras.get("data");

        if (object != null) {
            Bitmap bitmap = (Bitmap) object;

            // Crop and resize the picture
            bitmap = PictureHelper.prepare(bitmap, 256);
            if (bitmap == null) {
                DialogInformation.build(this).show(getString(R.string.oops_we_are_still_experiencing_errors),
                        DialogInformation.ICON_ALERT);
                Analytics.send(this, Analytics.ERROR_OUT_OF_MEMORY);
            } else {
                imageUser.setImageBitmap(bitmap);
            }
        }
    }

    private void pictureSelected(Intent intent) {
        // Get bitmap
        bitmap = imin.getBitmapFromIntent(intent);

        // Check if Bitmap is correct
        if (bitmap != null) {
            // Crop and resize the picture
            bitmap = PictureHelper.prepare(bitmap, 256);
            if (bitmap == null) {
                DialogInformation.build(this).show(getString(R.string.oops_we_are_still_experiencing_errors),
                        DialogInformation.ICON_ALERT);
            } else {
                imageUser.setImageBitmap(bitmap);
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

    private void enter() {
        // Get user name
        String username = textUserName.getText().toString();
        username = username.trim();

        // Check user name
        if (username.length() == 0) {
            DialogInformation.build(this).show(getString(R.string.please_introduce_a_valid_user_name),
                    DialogInformation.ICON_ALERT);
            return;
        }

        // Check legal
        if (!checkLegal.isChecked()) {
            DialogInformation.build(this).show(getString(R.string.you_must_accept_the_legal_terms_to_proceed),
                    DialogInformation.ICON_ALERT);
            return;
        }

        // Save contact details
        Contact contact = imin.getUser().getContact();
        contact.setName(username);

        if (bitmap != null) {
            contact.setPhoto(bitmap);
        }

        // Upload image to the server
        uploadUserData(username, bitmap);

        // Save contact
        imin.getUser().saveUser();

        // Finish and come back to the last activity
        setResult(Imin.RESULT_CODE_WELCOME_OK);
        finish();
    }

    private void uploadUserData(String username, Bitmap bitmap) {
        // Create the task
        configurationInstance.taskUploadUserData = new TaskUploadUserData(this);
        TaskUploadUserDataParams params = new TaskUploadUserDataParams(imin.getUser().getPrivateUserId(), imin
                .getUser().getPublicUserId(), username, bitmap);
        configurationInstance.taskUploadUserData.execute(params);
    }

    @Override
    public void onUserDataUploaded() {
        // Do nothing
    }

    @Override
    public void onUserDataNotUploaded() {
        // Do nothing
    }

}
