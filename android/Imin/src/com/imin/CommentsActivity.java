package com.imin;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.google.analytics.tracking.android.EasyTracker;
import com.imin.communications.comments.TaskSendComments;
import com.imin.communications.comments.TaskSendComments.TaskSendCommentsListener;
import com.imin.communications.comments.TaskSendCommentsParams;
import com.imin.contacts.Contact;
import com.imin.widgets.DialogInformation;
import com.imin.widgets.DialogProgress;

public class CommentsActivity extends ActionBarActivity implements TaskSendCommentsListener {

    // Class that stores the configuration instance for recreating the
    // full activity restoring the dialogs and other
    private class CommentsActivityConfigurationInstance {
        public TaskSendComments taskSendComments;
    }

    // List of objects in the layout
    private EditText textComments;

    // Dialogs
    private DialogProgress sendCommentsProgressDialog;

    // Tasks
    private CommentsActivityConfigurationInstance configurationInstance;

    // Imin application object
    private Imin imin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        Imin.overrideFonts(findViewById(android.R.id.content));

        // Get the Imin application object
        imin = ((Imin) getApplication());

        // Initialize objects
        initializeObjects();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        if (configurationInstance.taskSendComments != null) {
            configurationInstance.taskSendComments.setListener(null);
        }
        return configurationInstance;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // Save the values
        if (sendCommentsProgressDialog != null) {
            savedInstanceState.putBoolean("sendCommentsProgressDialog", sendCommentsProgressDialog.isShown());
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore the values
        if (savedInstanceState.getBoolean("sendCommentsProgressDialog")) {
            showCommentsDialog();
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
        inflater.inflate(R.menu.comments, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void initializeObjects() {
        // Get object references
        textComments = (EditText) findViewById(R.id.textComments);
        Button btnSendComments = (Button) findViewById(R.id.btnSendComments);

        // Restore dialogs
        restoreDialogs();

        // Set listeners
        btnSendComments.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                sendComments();
            }
        });
    }

    private void restoreDialogs() {
        @SuppressWarnings("deprecation")
        Object retained = getLastNonConfigurationInstance();

        if (retained instanceof CommentsActivityConfigurationInstance) {
            configurationInstance = (CommentsActivityConfigurationInstance) retained;
            if (configurationInstance.taskSendComments != null) {
                configurationInstance.taskSendComments.setListener(this);
            }
        } else {
            configurationInstance = new CommentsActivityConfigurationInstance();
        }
    }

    private void sendComments() {
        // Load contact data
        Contact contact = imin.getUser().getContact();
        String username = contact.getName();

        String comments = textComments.getText().toString();

        comments = comments.trim();
        if (comments.length() > 0) {
            // Execute the task that may receive the event
            showCommentsDialog();

            // Run an asynchronous task to retrieve the single event
            configurationInstance.taskSendComments = new TaskSendComments(this);
            TaskSendCommentsParams params = new TaskSendCommentsParams(imin.getUser().getPrivateUserId(), username,
                    comments);
            configurationInstance.taskSendComments.execute(params);

        } else {
            DialogInformation.build(this).show(getString(R.string.dont_be_shy), DialogInformation.ICON_INFO);
        }
    }

    private void showCommentsDialog() {
        // Show comments dialog
        sendCommentsProgressDialog = DialogProgress.build(this);
        sendCommentsProgressDialog.show(getString(R.string.sending_comments_));
    }

    @Override
    public void onCommentsSent() {
        sendCommentsProgressDialog.dismiss();

        // Come back
        finish();
    }

    @Override
    public void onCommentsNotSent() {
        sendCommentsProgressDialog.dismiss();

        // Notify error
        DialogInformation.build(this).show(getString(R.string.generic_error),
                DialogInformation.ICON_ALERT);
    }

}
