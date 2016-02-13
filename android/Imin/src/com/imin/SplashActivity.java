package com.imin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.analytics.tracking.android.EasyTracker;
import com.imin.analytics.Analytics;
import com.imin.communications.sync.SyncUtils;
import com.imin.communications.user.TaskGetUserId;
import com.imin.communications.user.TaskGetUserId.TaskGetUserIdListener;
import com.imin.communications.user.TaskGetUserIdParams;
import com.imin.user.User;
import com.imin.widgets.DialogInformation;
import com.imin.widgets.DialogInformation.OnClickListener;
import com.imin.widgets.DialogQuestion;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity implements TaskGetUserIdListener {

    // Class that stores the configuration instance for recreating the
    // full activity restoring the dialogs and other
    private class SplashActivityConfigurationInstance {
        public TaskGetUserId taskGetUserId;
    }

    // Tasks
    private SplashActivityConfigurationInstance configurationInstance;

    // Imin application object
    private Imin imin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Imin.overrideFonts(findViewById(android.R.id.content));

        // Get the Imin application object
        imin = ((Imin) getApplicationContext());

        // Initialize
        waitSplash();
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        if (configurationInstance.taskGetUserId != null) {
            configurationInstance.taskGetUserId.setListener(null);
        }
        return configurationInstance;
    }

    @SuppressLint("HandlerLeak")
    private void waitSplash() {
        restoreDialogs();
        loadingImage();

        // Check if the app was previously loaded
        if (imin.isInitialized()) {
            // Go to the main activity
            main();
        } else {
            // Wait a second and load
            final Handler handler = new Handler() {
                public void handleMessage(Message msg) {
                    // Initialize application
                    initialize();
                }
            };

            // Set a timer which may wait for initialization
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(RESULT_OK);
                }
            }, 1000);
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
        if (requestCode == Imin.REQUEST_CODE_WELCOME) {
            // Check activity result
            if (resultCode == Imin.RESULT_CODE_WELCOME_OK) {
                // Analytics event
                Analytics.send(this, Analytics.ANALYTICS_PROFILE_CREATE);

                // User has introduced user name
                main();
            } else {
                finish();
            }
        }
    }

    private void restoreDialogs() {
        @SuppressWarnings("deprecation")
        Object retained = getLastNonConfigurationInstance();

        if (retained instanceof SplashActivityConfigurationInstance) {
            configurationInstance = (SplashActivityConfigurationInstance) retained;
            if (configurationInstance.taskGetUserId != null) {
                configurationInstance.taskGetUserId.setListener(this);
            }
        } else {
            configurationInstance = new SplashActivityConfigurationInstance();
        }
    }

    private void initialize() {
        if (imin.isUserInitialized()) {
            imin.setInitialized(true);
            main();
        } else {
            // Get user id
            getUserId();
        }

        /*
        getUserId();
        */
    }

    private void loadingImage() {
        // Show and animate the loading image
        ImageView imageProgress = (ImageView) findViewById(R.id.imageProgress);
        imageProgress.setAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_indefinitely));
        imageProgress.setVisibility(View.VISIBLE);
    }

    private void getUserId() {
        // Retrieve deviceId
        String deviceId = imin.getDeviceId();

        // Execute the asynchronous task for retrieving login credentials
        configurationInstance.taskGetUserId = new TaskGetUserId(this);
        TaskGetUserIdParams params = new TaskGetUserIdParams(deviceId);
        configurationInstance.taskGetUserId.execute(params);
    }

    private void welcome() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivityForResult(intent, Imin.REQUEST_CODE_WELCOME);
    }

    private void main() {
        // Retrieve the Intent
        Intent intentUri = getIntent();

        // Check URI
        Uri uri = intentUri.getData();

        // Reset the user object
        imin.getUser().setPollEventId(null);

        if (uri != null) {
            // Try to extract the data
            // For now, data could be a public event id, so parse it
            List<String> pathSegments = uri.getPathSegments();

            // Check validity
            if (pathSegments != null && pathSegments.size() > 1) {
                // Get the full URI string
                String uriString = uri.toString();

                // Finally, get the public event id
                String EVENT_ID = "eventId=";
                int pos = uriString.indexOf(EVENT_ID);
                String eventId = uriString.substring(pos + EVENT_ID.length());

                // Set poll as responded
                imin.getUser().setPollEventId(eventId);
            }
        }

        // Analytics event
        Analytics.send(this, Analytics.ANALYTICS_APP_LAUNCH);

        // Open the main activity normally
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onUserIdReceived(String privateUserId, String publicUserId) {
        // Set the private and public user ids
        User user = imin.getUser();
        user.setPrivateUserId(privateUserId);
        user.setPublicUserId(publicUserId);

        // Check if first login
        boolean userLoaded = user.loadUser();

        if (userLoaded) {
            // Save the user again with the private and public ids
            user.saveUser();
            imin.setInitialized(true);

            // Create account, if needed
            SyncUtils.createSyncAccount(imin.getApplicationContext(), user);

            // Go to the main activity
            main();
        } else {
            user.setPrivateUserId(privateUserId);
            user.setPublicUserId(publicUserId);

            // Go to the Welcome activity
            // Show welcome message
            DialogInformation.build(this, new OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Open the main activity normally
                    welcome();
                }
            }).show(getString(R.string.welcome_to_imin), DialogInformation.ICON_INFO);
        }
    }

    @Override
    public void onUserIdNotReceived() {
        // Error, ask for retrying
        DialogQuestion dialogQuestion = new DialogQuestion(this, new DialogQuestion.OnClickListener() {
            @Override
            public void onClick(View view, boolean result) {
                if (result) {
                    // Retry
                    getUserId();
                } else {
                    // Close the application
                    finish();
                }
            }
        });

        dialogQuestion.show(getString(R.string.network_error_would_you_like_to_retry_));
    }
}
