package com.imin;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.imin.analytics.Analytics;
import com.imin.communications.events.TaskPushEvent;
import com.imin.communications.events.TaskPushEvent.TaskPushEventListener;
import com.imin.communications.events.TaskPushEventParams;
import com.imin.events.Event;
import com.imin.events.proposals.DateTime;
import com.imin.events.proposals.Location;
import com.imin.events.proposals.Proposal;
import com.imin.widgets.CalendarView;
import com.imin.widgets.CalendarView.Date;

import java.util.ArrayList;
import java.util.List;

public class EventCreatedActivity extends ActionBarActivity implements TaskPushEventListener {

    // Class that stores the configuration instance for recreating the
    // full activity restoring the dialogs and other
    private class EventCreatedActivityConfigurationInstance {
        public TaskPushEvent taskPushEvent;
    }

    // List of objects in the layout
    private TextView textEventCreating;
    private TextView textEventCreated;
    private TextView textEventNotCreated;
    private TextView textEventId;
    private ImageView imageProgress;
    private Button btnShare;
    private Button btnRetry;
    private Button btnHome;

    // Tasks
    private EventCreatedActivityConfigurationInstance configurationInstance;

    // Imin application object
    private Imin imin;

    private boolean create;
    private boolean completed;
    private boolean successful;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_created);
        Imin.overrideFonts(findViewById(android.R.id.content));

        // Get the Imin application object
        imin = ((Imin) getApplication());

        // Initialize objects
        initializeObjects();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        if (configurationInstance.taskPushEvent != null) {
            configurationInstance.taskPushEvent.setListener(null);
        }
        return configurationInstance;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // Save the values
        savedInstanceState.putBoolean("create", create);
        savedInstanceState.putBoolean("completed", completed);
        savedInstanceState.putBoolean("successful", successful);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore the values
        create = savedInstanceState.getBoolean("create");
        completed = savedInstanceState.getBoolean("completed");
        successful = savedInstanceState.getBoolean("successful");

        if (completed && successful) {
            successful();
        } else if (completed) {
            failed();
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
        inflater.inflate(R.menu.event_created, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                back();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void initializeObjects() {
        Button btnBack;

        // Save object references
        textEventCreating = (TextView) findViewById(R.id.textEventCreating);
        textEventCreated = (TextView) findViewById(R.id.textEventCreated);
        textEventNotCreated = (TextView) findViewById(R.id.textEventNotCreated);
        textEventId = (TextView) findViewById(R.id.textEventId);
        imageProgress = (ImageView) findViewById(R.id.imageProgress);
        btnShare = (Button) findViewById(R.id.btnShare);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnRetry = (Button) findViewById(R.id.btnRetry);
        btnHome = (Button) findViewById(R.id.btnHome);

        // Animate image
        imageProgress.setAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_indefinitely));

        // Restore dialogs
        restoreDialogs();

        // Set listeners for handling events
        btnShare.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.isEnabled()) {
                    shareEvent();
                }
            }
        });

        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        btnRetry.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                retry();
            }
        });

        btnHome.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                home();
            }
        });

        // Check for data from previous activity
        if (!imin.getUser().isEventCreated()) {
            createEvent();

            // Flag to avoid recreate
            imin.getUser().setEventCreated(true);
        }
    }

    private void restoreDialogs() {
        @SuppressWarnings("deprecation")
        Object retained = getLastNonConfigurationInstance();

        if (retained instanceof EventCreatedActivityConfigurationInstance) {
            configurationInstance = (EventCreatedActivityConfigurationInstance) retained;
            if (configurationInstance.taskPushEvent != null) {
                configurationInstance.taskPushEvent.setListener(this);
            }
        } else {
            configurationInstance = new EventCreatedActivityConfigurationInstance();
        }
    }

    private void createEvent() {
        imageProgress.setVisibility(View.VISIBLE);

        // Get the event to be created
        Event event = imin.getUser().getCurrentEvent();

        // Build the proposals
        List<Proposal> proposals = getProposals();
        event.setProposals(proposals);

        // Push the event to the server
        configurationInstance.taskPushEvent = new TaskPushEvent(this, this);
        TaskPushEventParams params = new TaskPushEventParams(imin.getUser().getPrivateUserId(), event);
        configurationInstance.taskPushEvent.execute(params);
    }

    private List<Proposal> getProposals() {
        // Get the event to be created
        Event event = imin.getUser().getCurrentEvent();

        List<Proposal> proposals = new ArrayList<Proposal>();

        // Build the proposals
        List<String> locations = imin.newLocations;
        List<CalendarView.Date> dates = imin.newDates;

        // For each location
        for (String location : locations) {
            // Build the proposal
            Proposal proposal = new Proposal(new Location(location), null, null, Proposal.PROPOSAL_TYPE_LOCATION, event);
            proposals.add(proposal);
        }

        // For each date
        for (Date date : dates) {
            // Build DateTime: "yyyymmddhhmm"
            String dateTimeName = "";
            dateTimeName += String.format("%04d", date.year);
            dateTimeName += String.format("%02d", date.month);
            dateTimeName += String.format("%02d", date.day + 1);
            dateTimeName += String.format("%02d", 0);
            dateTimeName += String.format("%02d", 0);
            DateTime dateTime = new DateTime(dateTimeName);

            // Build the proposal
            Proposal proposal = new Proposal(null, dateTime, null, Proposal.PROPOSAL_TYPE_DATETIME, event);
            proposals.add(proposal);
        }

        return proposals;
    }

    private void shareEvent() {
        // Get the current event
        Event event = imin.getUser().getCurrentEvent();

        // Get the path
        Imin.shareEvent(this, event);
    }

    private void back() {
        if (completed) {
            finish();
        }
    }

    private void home() {
        setResult(Imin.RESULT_CODE_EVENT_CREATED_OK);
        finish();
    }

    private void retry() {
        completed = false;
        textEventNotCreated.setVisibility(View.GONE);
        textEventCreating.setVisibility(View.VISIBLE);
        btnRetry.setVisibility(View.GONE);

        createEvent();
    }

    private void successful() {
        // Notify that the event has been sent to the server and
        // continue sending the notifications to the invited contacts
        textEventCreated.setVisibility(View.VISIBLE);
        textEventCreating.setVisibility(View.GONE);
        textEventId.setVisibility(View.VISIBLE);
        imageProgress.setAnimation(null);
        imageProgress.setVisibility(View.GONE);
        btnShare.setVisibility(View.VISIBLE);
        btnHome.setVisibility(View.VISIBLE);
    }

    private void failed() {
        // Notify that an error occurred while creating the event
        textEventCreating.setVisibility(View.GONE);
        textEventNotCreated.setVisibility(View.VISIBLE);
        btnRetry.setVisibility(View.VISIBLE);
        imageProgress.setAnimation(null);
        imageProgress.setVisibility(View.GONE);
    }

    @Override
    public void onEventPushed(String publicEventId) {
        // Set the received public event id
        Event event = imin.getUser().getCurrentEvent();
        event.setId(publicEventId);

        textEventId.setText(publicEventId);

        successful();
        completed = true;
        successful = true;

        // Finally, set the result of the activity
        setResult(Imin.RESULT_CODE_EVENT_CREATED_OK);

        // Analytics event
        Analytics.send(this, Analytics.ANALYTICS_EVENT_CREATE);
    }

    @Override
    public void onEventNotPushed() {
        failed();

        // Finally, set the result of the activity
        setResult(Imin.RESULT_CODE_EVENT_CREATED_NOK);
        completed = true;
    }
}
