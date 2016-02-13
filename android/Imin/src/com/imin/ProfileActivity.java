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
import android.widget.Button;
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
import com.imin.widgets.DialogQuestion;

public class ProfileActivity extends ActionBarActivity implements TaskUploadUserDataListener {

    // Class that stores the configuration instance for recreating the
    // full activity restoring the dialogs and other
    private class ProfileActivityConfigurationInstance {
        public TaskUploadUserData taskUploadUserData;
    }

    // Object references
    private EditText textUserName;
    private ImageView imageUser;

    // Variables and objects
    private Bitmap bitmap;
    private boolean update_picture;
    private boolean update_name;

    // Tasks
    private ProfileActivityConfigurationInstance configurationInstance;

    // Imin application object
    private Imin imin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Imin.overrideFonts(findViewById(android.R.id.content));

        // Show back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize objects
        initializeObjects();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
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
        savedInstanceState.putBoolean("update_picture", update_picture);
        savedInstanceState.putBoolean("update_name", update_name);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore the values
        String bitmapData = savedInstanceState.getString("bitmap");
        bitmap = PictureHelper.decode(bitmapData);
        update_picture = savedInstanceState.getBoolean("update_picture");
        update_name = savedInstanceState.getBoolean("update_name");

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile, menu);
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
        Button btnEnter;
        Button btnSave;
        TextView txtDeleteAccount;

        // Save object references
        textUserName = (EditText) findViewById(R.id.textUserName);
        imageUser = (ImageView) findViewById(R.id.imageUser);
        btnEnter = (Button) findViewById(R.id.btnEnter);
        btnSave = (Button) findViewById(R.id.btnSave);
        txtDeleteAccount = (TextView) findViewById(R.id.txtDeleteAccount);

        txtDeleteAccount.setVisibility(View.VISIBLE);

        // Restore dialogs
        restoreDialogs();

        btnEnter.setVisibility(View.GONE);
        btnSave.setVisibility(View.VISIBLE);

        // Set event handlers
        imageUser.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPicture();
            }
        });

        btnSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        txtDeleteAccount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccountQuestion();
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

        if (retained instanceof ProfileActivityConfigurationInstance) {
            configurationInstance = (ProfileActivityConfigurationInstance) retained;
            if (configurationInstance.taskUploadUserData != null) {
                configurationInstance.taskUploadUserData.setListener(this);
            }
        } else {
            configurationInstance = new ProfileActivityConfigurationInstance();
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

                // Must update
                update_picture = true;
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
                Analytics.send(this, Analytics.ERROR_OUT_OF_MEMORY);
            } else {
                imageUser.setImageBitmap(bitmap);
                // Must update
                update_picture = true;
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

    private void save() {
        // Get user name
        String username = textUserName.getText().toString();
        username = username.trim();

        // Check user name
        if (username.length() == 0) {
            DialogInformation.build(this).show(getString(R.string.please_introduce_a_valid_user_name),
                    DialogInformation.ICON_ALERT);
            return;
        }

        // Save contact details
        Contact contact = imin.getUser().getContact();

        if (update_picture) {
            contact.setPhoto(bitmap);
        }

        if (!contact.getName().equals(username)) {
            // Must update
            update_name = true;

            contact.setName(username);
        }

        if (update_picture || update_name) {
            // Upload image to the server
            uploadUserData(username, bitmap);

            // Save contact
            imin.getUser().saveUser();
        }

        // Finish and come back to the last activity
        setResult(Imin.RESULT_CODE_PROFILE_OK);
        finish();
    }

    private void deleteAccountQuestion() {
        // Ask the user
        DialogQuestion dialogQuestion = new DialogQuestion(this, new DialogQuestion.OnClickListener() {
            @Override
            public void onClick(View view, boolean result) {
                if (result) {
                    deleteAccount();
                }
            }
        });

        dialogQuestion.show(getString(R.string.delete_permanently_your_account));
    }

    private void deleteAccount() {
        // Delete the account
        imin.getUser().deleteUser();
        setResult(Imin.RESULT_CODE_PROFILE_DELETE_ACCOUNT);

        // Analytics event
        Analytics.send(this, Analytics.ANALYTICS_PROFILE_REMOVE);

        DialogInformation.build(this, new DialogInformation.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        }).show(getString(R.string.account_deleted), DialogInformation.ICON_INFO);
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
        if (update_picture) {
            // Analytics event
            Analytics.send(this, Analytics.ANALYTICS_PROFILE_CHANGE_PICTURE);
        }

        if (update_name) {
            // Analytics event
            Analytics.send(this, Analytics.ANALYTICS_PROFILE_CHANGE_NAME);
        }
    }

    @Override
    public void onUserDataNotUploaded() {
        // Do nothing
    }

}
