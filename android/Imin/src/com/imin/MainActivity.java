package com.imin;

import android.accounts.Account;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SyncStatusObserver;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.imin.analytics.Analytics;
import com.imin.communications.contacts.TaskDownloadContactsData;
import com.imin.communications.contacts.TaskDownloadContactsData.TaskDownloadContactsDataListener;
import com.imin.communications.contacts.TaskDownloadContactsDataParams;
import com.imin.communications.events.TaskCloseEvent;
import com.imin.communications.events.TaskCloseEvent.TaskCloseEventListener;
import com.imin.communications.events.TaskCloseEventParams;
import com.imin.communications.events.TaskGetEventPictures;
import com.imin.communications.events.TaskGetEventPictures.TaskGetEventPicturesListener;
import com.imin.communications.events.TaskGetEventPicturesParams;
import com.imin.communications.events.TaskGetEvents;
import com.imin.communications.events.TaskGetEvents.TaskGetEventsListener;
import com.imin.communications.events.TaskGetEventsParams;
import com.imin.communications.events.TaskLeaveEvent;
import com.imin.communications.events.TaskLeaveEvent.TaskLeaveEventListener;
import com.imin.communications.events.TaskLeaveEventParams;
import com.imin.communications.events.TaskRemoveEvent;
import com.imin.communications.events.TaskRemoveEvent.TaskRemoveEventListener;
import com.imin.communications.events.TaskRemoveEventParams;
import com.imin.communications.events.TaskRespondEvent;
import com.imin.communications.events.TaskRespondEvent.TaskRespondEventListener;
import com.imin.communications.events.TaskRespondEventParams;
import com.imin.communications.sync.AccountService;
import com.imin.communications.sync.SyncUtils;
import com.imin.contacts.Contact;
import com.imin.contacts.ProposalContactListAdapter;
import com.imin.events.Event;
import com.imin.events.EventListAdapter;
import com.imin.events.EventListListener;
import com.imin.events.proposals.Proposal;
import com.imin.events.responses.Response;
import com.imin.widgets.DialogInformation;
import com.imin.widgets.DialogProgress;
import com.imin.widgets.DialogQuestion;
import com.imin.widgets.DialogText;
import com.imin.widgets.SingleToast;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.widget.DynamicListView;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class MainActivity extends ActionBarActivity implements TaskRemoveEventListener, TaskLeaveEventListener,
        TaskCloseEventListener, TaskRespondEventListener, TaskGetEventsListener, TaskGetEventPicturesListener {

    // Class that stores the configuration instance for recreating the
    // full activity restoring the dialogs and other
    private class MainActivityConfigurationInstance {
        public TaskGetEvents taskGetEvents;
        public TaskRespondEvent taskRespondEvent;
        public TaskGetEventPictures taskGetEventPictures;
    }

    // List of objects in the layout
    private PullToRefreshLayout layoutPullToRefresh;
    private ListView listEvents;
    private RelativeLayout layoutCreateEvent;
    private RelativeLayout layoutRefreshEvents;

    // Dialogs
    private DialogProgress joinEventProgressDialog;
    private DialogProgress reopenEventProgressDialog;
    private LayoutInflater inflater;
    private EventListAdapter eventsListAdapter;

    // Tasks
    private MainActivityConfigurationInstance configurationInstance;

    // Sync
    private Object syncObserver;

    // Imin application object
    private Imin imin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Imin.overrideFonts(findViewById(android.R.id.content));

        inflater = LayoutInflater.from(this);

        // Get the Imin application object
        imin = ((Imin) getApplication());

        // Initialize objects
        initializeObjects();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        if (configurationInstance.taskGetEvents != null) {
            configurationInstance.taskGetEvents.setListener(null);
        }
        if (configurationInstance.taskRespondEvent != null) {
            configurationInstance.taskRespondEvent.setListener(null);
        }
        if (configurationInstance.taskGetEventPictures != null) {
            configurationInstance.taskGetEventPictures.setListener(null);
        }

        return configurationInstance;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // Save the values
        if (joinEventProgressDialog != null) {
            savedInstanceState.putBoolean("joinEventProgressDialog", joinEventProgressDialog.isShown());
        }
        if (reopenEventProgressDialog != null) {
            savedInstanceState.putBoolean("reopenEventProgressDialog", reopenEventProgressDialog.isShown());
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore the values
        if (savedInstanceState.getBoolean("joinEventProgressDialog")) {
            showJoinEventProgressDialog();
        }
        if (savedInstanceState.getBoolean("reopenEventProgressDialog")) {
            showReopenEventProgressDialog();
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
    public void onResume() {
        super.onResume();
        syncStatusObserver.onStatusChanged(0);

        // Watch for sync state changes
        int mask = ContentResolver.SYNC_OBSERVER_TYPE_PENDING | ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE;
        syncObserver = ContentResolver.addStatusChangeListener(mask, syncStatusObserver);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (syncObserver != null) {
            ContentResolver.removeStatusChangeListener(syncObserver);
            syncObserver = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check source activity
        if (requestCode == Imin.REQUEST_CODE_CREATE_EVENT) {
            // Check activity result
            if (resultCode == Imin.RESULT_CODE_CREATE_EVENT_OK) {
                // Event created, list all the events
                eventCreated();
            }
        } else if (requestCode == Imin.REQUEST_CODE_POLL_DATETIMES
                || requestCode == Imin.REQUEST_CODE_POLL_DATETIMES_CLOSE) {
            // Check activity result
            if (resultCode == Imin.RESULT_CODE_POLL_DATETIMES_OK) {
                // Poll responded
                eventResponded();
            }
        } else if (requestCode == Imin.REQUEST_CODE_PROFILE) {
            // Check activity result
            if (resultCode == Imin.RESULT_CODE_PROFILE_DELETE_ACCOUNT) {
                accountDeleted();
            }
        }

        // Update adapter
        sortAndUpdateEvents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create_event:
                // Create event button pressed
                createEvent();
                return true;
            case R.id.action_import_event:
                // Asks for importing an event
                importEvent();
                return true;
            case R.id.action_profile:
                // Show the comments activity
                profile();
                return true;
            case R.id.action_comments:
                // Show the comments activity
                comments();
                return true;
            case R.id.action_recommend:
                // Recommend the app
                Imin.shareApp(this);
                return true;
            case R.id.action_about:
                // Show the about
                about();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshEventList(boolean error) {
        // Get the events
        List<Event> events = imin.getUser().getEvents();

        if (error) {
            // Show big plus symbol for creating new event
            listEvents.setVisibility(View.GONE);
            layoutRefreshEvents.setVisibility(View.VISIBLE);
            layoutCreateEvent.setVisibility(View.GONE);
        } else {
            layoutRefreshEvents.setVisibility(View.GONE);
            // Check if event list is empty
            if (events.isEmpty()) {
                // Show big plus symbol for creating new event
                listEvents.setVisibility(View.GONE);
                layoutCreateEvent.setVisibility(View.VISIBLE);
            } else {
                // Sort the events
                imin.sortEvents(events);

                // setAdapter();

                listEvents.setVisibility(View.VISIBLE);
                layoutCreateEvent.setVisibility(View.GONE);
            }
        }

        // Refresh the layout with the new data
        sortAndUpdateEvents();
    }

    private void initializeObjects() {
        Button btnCreateEvent;
        Button btnRefreshEvents;

        // Save object references
        layoutPullToRefresh = (PullToRefreshLayout) findViewById(R.id.ptr_layout);
        listEvents = (ListView) findViewById(R.id.listEvents);
        layoutCreateEvent = (RelativeLayout) findViewById(R.id.layoutCreateEvent);
        layoutRefreshEvents = (RelativeLayout) findViewById(R.id.layoutRefreshEvents);
        btnCreateEvent = (Button) findViewById(R.id.btnCreateEvent);
        btnRefreshEvents = (Button) findViewById(R.id.btnRefreshEvents);

        // Now setup the PullToRefreshLayout
        ActionBarPullToRefresh.from(this).allChildrenArePullable().listener(new OnRefreshListener() {
            @Override
            public void onRefreshStarted(View view) {
                // Trigger synchronization
                listEvents();
            }
        }).setup(layoutPullToRefresh);

        /*
        listEvents.setOnItemMovedListener(new DynamicListView.OnItemMovedListener() {
            @Override
            public void onItemMoved(final int newPosition) {
                // Item moved
            }
        });
        */

        // Restore dialogs
        restoreDialogs();

        // Get the events
        List<Event> events = imin.getUser().getEvents();

        // Sort the events
        imin.sortEvents(events);

        // Set the adapter in order to show the list of events
        eventsListAdapter = new EventListAdapter(this, new EventListListener() {
            @Override
            public void onEventPeople(int position) {
                // Clicked over the People button
                eventPeople(position);
            }

            @Override
            public void onEventJoin(int position) {
                // Clicked over the Join button
                eventJoin(position);
            }

            @Override
            public void onEventEdit(int position) {
                // Clicked over the Edit button
                eventEdit(position);
            }

            @Override
            public void onEventReopen(int position) {
                // Clicked over the Reopen button
                eventReopen(position);
            }

            @Override
            public void onEventPoll(int position) {
                // Clicked over the Poll button
                eventPoll(position);
            }

            @Override
            public void onEventRemove(int position) {
                // Clicked over the Remove button
                eventRemove(position);
            }

            @Override
            public void onEventClose(int position) {
                eventClose(position);
            }

            @Override
            public void onEventShare(int position) {
                // Clicked over the Share button
                eventShare(position);
            }

        }, events);

        // Animate the list view
        setAdapter();

        // Set the listener for the create event layout
        btnCreateEvent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the create event method
                createEvent();
            }
        });

        // Set the listener for the create event layout
        btnRefreshEvents.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the create event method
                listEvents();
            }
        });

        // Add the event as an invitation
        String pollEventId = imin.getUser().getPollEventId();

        if (pollEventId != null) {
            imin.getUser().addInvitation(pollEventId);
            imin.getUser().setMustGetEvents(true);

            // Analytics event
            Analytics.send(this, Analytics.ANALYTICS_EVENT_RECEIVE_INVITATION);
        }

        if (imin.getUser().isMustGetEvents()) {
            // List events
            listEvents();

            imin.getUser().setMustGetEvents(false);
        } else {
            refreshEventList(false);
        }
    }

    private void setAdapter() {
        AnimationAdapter animAdapter = new AlphaInAnimationAdapter(eventsListAdapter);
        animAdapter.setAbsListView(listEvents);
        animAdapter.setInitialDelayMillis(250);
        listEvents.setAdapter(animAdapter);
    }

    private void restoreDialogs() {
        @SuppressWarnings("deprecation")
        Object retained = getLastNonConfigurationInstance();

        if (retained instanceof MainActivityConfigurationInstance) {
            configurationInstance = (MainActivityConfigurationInstance) retained;
            if (configurationInstance.taskGetEvents != null) {
                configurationInstance.taskGetEvents.setListener(this);
            }
            if (configurationInstance.taskRespondEvent != null) {
                configurationInstance.taskRespondEvent.setListener(this);
            }
            if (configurationInstance.taskGetEventPictures != null) {
                configurationInstance.taskGetEventPictures.setListener(this);
            }
        } else {
            configurationInstance = new MainActivityConfigurationInstance();
        }
    }

    private void eventPeople(int position) {
        // Get the events
        List<Event> events = imin.getUser().getEvents();

        // Show the extra information of the event
        Event event = events.get(position);

        // Get all the contacts
        List<Contact> contacts = event.getContacts();

        if (!contacts.isEmpty()) {
            View layoutContactsDialog = inflater.inflate(R.layout.layout_contacts_dialog, null);

            // Get the dialog and paint it
            LinearLayout layoutInvitedContacts = (LinearLayout) layoutContactsDialog
                    .findViewById(R.id.layoutInvitedContacts);
            LinearLayout layoutAttendingContacts = (LinearLayout) layoutContactsDialog
                    .findViewById(R.id.layoutAttendingContacts);
            LinearLayout layoutNotAttendingContacts = (LinearLayout) layoutContactsDialog
                    .findViewById(R.id.layoutNotAttendingContacts);
            ListView listInvitedContacts = (ListView) layoutContactsDialog.findViewById(R.id.listInvitedContacts);
            ListView listAttendingContacts = (ListView) layoutContactsDialog.findViewById(R.id.listAttendingContacts);
            ListView listNotAttendingContacts = (ListView) layoutContactsDialog
                    .findViewById(R.id.listNotAttendingContacts);

            if (event.isClosed()) {
                // Event closed, we must split into attending and not attending
                List<Contact> attendingContacts = event.getAttendingContacts();
                List<Contact> notAttendingContacts = event.getNotAttendingContacts();

                if (attendingContacts.size() > 0) {
                    ProposalContactListAdapter attendingContactsListAdapter = new ProposalContactListAdapter(this,
                            attendingContacts, imin.getUser());

                    // Animation adapter
                    AnimationAdapter animAdapter = new AlphaInAnimationAdapter(attendingContactsListAdapter);
                    animAdapter.setAbsListView(listAttendingContacts);
                    listAttendingContacts.setAdapter(animAdapter);

                    layoutAttendingContacts.setVisibility(View.VISIBLE);
                }

                if (notAttendingContacts.size() > 0) {
                    ProposalContactListAdapter notAttendingContactsListAdapter = new ProposalContactListAdapter(this,
                            notAttendingContacts, imin.getUser());

                    // Animation adapter
                    AnimationAdapter animAdapter = new AlphaInAnimationAdapter(notAttendingContactsListAdapter);
                    animAdapter.setAbsListView(listNotAttendingContacts);
                    listNotAttendingContacts.setAdapter(animAdapter);

                    layoutNotAttendingContacts.setVisibility(View.VISIBLE);
                }
            } else {
                // Event not closed, we must show all the contacts
                List<Contact> invitedContacts = event.getContacts();

                if (invitedContacts.size() > 0) {
                    ProposalContactListAdapter invitedContactsListAdapter = new ProposalContactListAdapter(this,
                            invitedContacts, imin.getUser());

                    // Animation adapter
                    AnimationAdapter animAdapter = new AlphaInAnimationAdapter(invitedContactsListAdapter);
                    animAdapter.setAbsListView(listInvitedContacts);
                    listInvitedContacts.setAdapter(animAdapter);

                    layoutInvitedContacts.setVisibility(View.VISIBLE);
                }
            }

            // An Alert Dialog must be shown with all the contacts on there
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            String title = event.getName();

            // Finally, show the dialog
            builder.setTitle(title);
            builder.setView(layoutContactsDialog);
            builder.show();
        }
    }

    private void eventJoin(int position) {
        // Get the events
        List<Event> events = imin.getUser().getEvents();

        // Show the extra information of the event
        Event event = events.get(position);

        imin.getUser().setCurrentEvent(event);

        // Join the event with the final proposal
        showJoinEventProgressDialog();

        // Get the corresponding response
        List<Proposal> proposals = event.getProposals();
        List<Response> responses = new ArrayList<Response>();
        for (Proposal proposal : proposals) {
            // Get proposal
            String proposalId = proposal.getPublicProposalId();

            //
            Response response;
            if (proposalId.equals(event.getFinalDateTimeProposalId())
                    || proposalId.equals(event.getFinalLocationProposalId())) {
                // Final proposal
                response = new Response(imin.getUser().getContact(), Response.RESPONSE_TYPE_ATTENDING, proposal);
            } else {
                response = new Response(imin.getUser().getContact(), Response.RESPONSE_TYPE_NOT_ATTENDING, proposal);
            }
            responses.add(response);
        }

        // Run an asynchronous task to respond that single event
        configurationInstance.taskRespondEvent = new TaskRespondEvent(this);
        TaskRespondEventParams params = new TaskRespondEventParams(imin.getUser().getPrivateUserId(), event, responses);
        configurationInstance.taskRespondEvent.execute(params);
    }

    private void eventEdit(int position) {
        // Get the events
        List<Event> events = imin.getUser().getEvents();

        // Show the extra information of the event
        Event event = events.get(position);

        imin.getUser().setCurrentEvent(event);
    }

    private void eventReopen(int position) {
        // Get the events
        List<Event> events = imin.getUser().getEvents();

        // Show the extra information of the event
        Event event = events.get(position);

        imin.getUser().setCurrentEvent(event);

        // Reopen event
        showReopenEventProgressDialog();

        // Run an asynchronous task to retrieve the single event
        TaskCloseEvent taskEvent = new TaskCloseEvent(this);
        TaskCloseEventParams params = new TaskCloseEventParams(imin.getUser().getPrivateUserId(), event.getId(), false,
                "", "");
        taskEvent.execute(params);
    }

    private void eventPoll(int position) {
        // Get the events
        List<Event> events = imin.getUser().getEvents();

        // Show the extra information of the event
        Event event = events.get(position);

        imin.getUser().setCurrentEvent(event.clone());

        // Go to the poll activity
        Intent intent = new Intent(this, PollDateTimesActivity.class);
        startActivityForResult(intent, Imin.REQUEST_CODE_POLL_DATETIMES);
    }

    private void eventRemove(final int position) {
        // Create a dialog for asking the user about event removal
        DialogQuestion dialogQuestion = new DialogQuestion(this, new DialogQuestion.OnClickListener() {
            @Override
            public void onClick(View view, boolean result) {
                if (result) {
                    removeEvent(position);
                }
            }
        });

        dialogQuestion.show(getString(R.string.remove_event));
    }

    private void eventClose(int position) {
        // Get the events
        List<Event> events = imin.getUser().getEvents();

        // Show the extra information of the event
        Event event = events.get(position);

        imin.getUser().setCurrentEvent(event.clone());

        // Go to the poll activity
        Intent intent = new Intent(this, PollDateTimesActivity.class);
        intent.putExtra("close", true);
        startActivityForResult(intent, Imin.REQUEST_CODE_POLL_DATETIMES_CLOSE);
    }

    private void eventShare(int position) {
        // Get the events
        List<Event> events = imin.getUser().getEvents();

        // Show the extra information of the event
        Event event = events.get(position);

        // Get the path
        Imin.shareEvent(this, event);
    }

    private void removeEvent(int position) {
        // Get the events
        List<Event> events = imin.getUser().getEvents();

        // Get the event
        Event event = events.get(position);

        // Check if we are the creators of the event
        if (event.isAdmin()) {
            // We have created the event, so maybe we must
            // ask if the event must be removed for all the
            // users or just for me
            TaskRemoveEvent taskRemoveEvent = new TaskRemoveEvent(this);
            TaskRemoveEventParams params = new TaskRemoveEventParams(imin.getUser().getPrivateUserId(), event.getId());
            taskRemoveEvent.execute(params);
        } else {
            // We are just removing the event saying that we
            // are not responding, so we must remove the
            // database entry for this event
            TaskLeaveEvent taskLeaveEvent = new TaskLeaveEvent(this);
            TaskLeaveEventParams params = new TaskLeaveEventParams(imin.getUser(), event.getId());
            taskLeaveEvent.execute(params);
        }

        // Finally, remove the event from the list
        events.remove(position);

        // Save the events
        imin.getUser().saveEvents(events);

        // Refresh the layout with the new data
        refreshEventList(false);
    }

    private void eventCreated() {
        // Since the event has been created
        listEvents.setVisibility(View.VISIBLE);
        layoutCreateEvent.setVisibility(View.GONE);

        // Get the recently created event and the list of events
        Event event = imin.getUser().getCurrentEvent();
        List<Event> events = imin.getUser().getEvents();

        // Add the event in the list of events
        events.add(event);

        // Save the events
        imin.getUser().saveEvents(events);

        listEvents();
    }

    private void eventResponded() {
        // Get the recently created event and the list of events
        Event event = imin.getUser().getCurrentEvent();
        List<Event> events = imin.getUser().getEvents();

        // Find the corresponding event and replace it
        int k = 0;
        while (k < events.size()) {
            Event testEvent = events.get(k);

            if (testEvent.getId().equals(event.getId())) {
                // Found, replace it
                events.remove(k);
                events.add(event);
                break;
            }
            ++k;
        }

        // Save the events
        imin.getUser().saveEvents(events);

        listEvents();
    }

    private void accountDeleted() {
        // Close the activity and exit the application
        finish();
        System.exit(0);
    }

    private void listEvents() {
        // Sort the events and update the list
        sortAndUpdateEvents();

        // Refreshing
        if (layoutPullToRefresh == null) {
            DialogInformation.build(this).show(getString(R.string.oops_we_are_still_experiencing_errors),
                    DialogInformation.ICON_ALERT);
            Analytics.send(this, Analytics.ERROR_LAYOUT_PULL_TO_REFRESH_NULL);
        } else {
            layoutPullToRefresh.setRefreshing(true);
        }

        // Run an asynchronous task to get the events
        configurationInstance.taskGetEvents = new TaskGetEvents(this, this);
        TaskGetEventsParams params = new TaskGetEventsParams();
        configurationInstance.taskGetEvents.execute(params);
    }

    private void getEventPictures() {
        // Run an asynchronous task to get the events
        configurationInstance.taskGetEventPictures = new TaskGetEventPictures(this, this);
        TaskGetEventPicturesParams params = new TaskGetEventPicturesParams();
        configurationInstance.taskGetEventPictures.execute(params);
    }

    private void loadEvents() {
        // Get the events
        List<Event> eventsReceived = imin.getUser().loadEvents();

        List<Event> events = imin.getUser().getEvents();

        events.clear();

        // Copy the retrieved list into the list of events to be shown
        for (Event event : eventsReceived)
            events.add(event);

        // Check if all the contact photos have been downloaded
        List<Contact> contacts = imin.getUser().getContacts();
        TaskDownloadContactsData taskDownloadContactsData = new TaskDownloadContactsData(this,
                new TaskDownloadContactsDataListener() {
                    @Override
                    public void onContactDataReceived(Contact contact) {
                        // Update layouts to show contact data
                        updateContactData(contact);
                    }

                    @Override
                    public void onContactsDataReceived() {
                        // Do nothing
                    }

                    @Override
                    public void onContactsDataNotReceived() {
                        // Do nothing
                    }

                }
        );

        TaskDownloadContactsDataParams params = new TaskDownloadContactsDataParams(contacts);
        taskDownloadContactsData.execute(params);

        // Refresh list
        refreshEventList(false);
    }

    private void createEvent() {
        // Empty the lists
        if (imin.newLocations != null) {
            imin.newLocations.clear();
        }

        // Call Event activity in order to create a new event
        Intent intent = new Intent(this, CreateEventActivity.class);
        startActivityForResult(intent, Imin.REQUEST_CODE_CREATE_EVENT);
    }

    private void importEvent() {
        // Create a dialog for asking the user about event removal
        DialogText dialogText = new DialogText(this, new DialogText.OnClickListener() {
            @Override
            public void onClick(View view, boolean result, String text) {
                if (result) {
                    if (text.length() == 8) {
                        importEvent(text);
                    } else {
                        DialogInformation.build(view.getContext()).show(
                                getString(R.string.incorrect_event_code_length), DialogInformation.ICON_ALERT);
                    }
                }
            }
        });

        dialogText.show(getString(R.string.insert_code));
    }

    private void importEvent(String text) {
        imin.getUser().addInvitation(text);

        // Analytics event
        Analytics.send(this, Analytics.ANALYTICS_EVENT_RECEIVE_INVITATION);

        // List events
        listEvents();
    }

    private void comments() {
        Intent intent = new Intent(this, CommentsActivity.class);
        startActivity(intent);
    }

    private void profile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivityForResult(intent, Imin.REQUEST_CODE_PROFILE);
    }

    private void about() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    private void showJoinEventProgressDialog() {
        joinEventProgressDialog = DialogProgress.build(this);
        joinEventProgressDialog.show(getString(R.string.joining_event));
    }

    private void showReopenEventProgressDialog() {
        reopenEventProgressDialog = DialogProgress.build(this);
        reopenEventProgressDialog.show(getString(R.string.reopening_event_));
    }

    private void updateContactData(Contact contact) {
        // Update layouts
        eventsListAdapter.updateContactData(contact);
    }

    @Override
    public void onEventsRetrieved() {
        // Load events
        loadEvents();

        // Refreshing finished
        if (layoutPullToRefresh.isRefreshing()) {
            layoutPullToRefresh.setRefreshComplete();
        }

        // Load event pictures
        getEventPictures();
    }

    @Override
    public void onEventsNotRetrieved() {
        // Load events
        loadEvents();

        // Refreshing finished
        if (layoutPullToRefresh.isRefreshing()) {
            layoutPullToRefresh.setRefreshComplete();
        }
    }

    @Override
    public void onEventPicturesRetrieved() {
        // Put the images in the corresponding ImageView
        eventsListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onEventPicturesNotRetrieved() {
        // ...
    }

    @Override
    public void onEventRemoved() {
        // Analytics event
        Analytics.send(this, Analytics.ANALYTICS_EVENT_REMOVE);
    }

    @Override
    public void onEventNotRemoved() {
        DialogInformation.build(this).show(getString(R.string.generic_error), DialogInformation.ICON_ALERT);
    }

    @Override
    public void onEventLeft() {
        // Analytics event
        Analytics.send(this, Analytics.ANALYTICS_EVENT_LEAVE);
    }

    @Override
    public void onEventNotLeft() {
        // Show a toast notifying
        DialogInformation.build(this).show(getString(R.string.generic_error), DialogInformation.ICON_ALERT);
    }

    @Override
    public void onEventClosed() {
        // Analytics event
        Analytics.send(this, Analytics.ANALYTICS_EVENT_CLOSE);
    }

    @Override
    public void onEventReopened() {
        // Error while receiving the single event
        reopenEventProgressDialog.dismiss();

        Event event = imin.getUser().getCurrentEvent();
        event.setClosed(false);

        DialogInformation.build(this, new DialogInformation.OnClickListener() {
            @Override
            public void onClick(View view) {
                listEvents();
            }
        }).show(getString(R.string.event_reopened), DialogInformation.ICON_INFO);

        // Analytics event
        Analytics.send(this, Analytics.ANALYTICS_EVENT_REOPEN);
    }

    @Override
    public void onEventNotClosed() {
        //
    }

    @Override
    public void onEventNotReopened() {
        // Event not closed
        reopenEventProgressDialog.dismiss();

        // Show a Toast notifying
        DialogInformation.build(this).show(getString(R.string.generic_error), DialogInformation.ICON_ALERT);
    }

    @Override
    public void onEventResponded() {
        // Hide the progress dialog
        joinEventProgressDialog.dismiss();

        // Show a Toast notifying
        SingleToast.show(this, getString(R.string.poll_responded), Toast.LENGTH_SHORT);

        imin.getUser().setPollResponded(true);

        // Refresh list view
        listEvents();
    }

    @Override
    public void onEventNotResponded() {
        // Hide the progress dialog
        joinEventProgressDialog.dismiss();

        // Show a Toast notifying
        SingleToast.show(this, getString(R.string.generic_error), Toast.LENGTH_SHORT);
    }

    private SyncStatusObserver syncStatusObserver = new SyncStatusObserver() {
        // Callback invoked when the sync adapter status changes
        @Override
        public void onStatusChanged(int which) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Get the account object
                    Account account = AccountService.getAccount(imin.getUser().getPublicUserId());
                    if (account == null) {
                        // Error, not refreshing
                        return;
                    }

                    // Test the ContentResolver to see if the sync adapter is active or pending.
                    // Set the state of the refresh button accordingly.
                    boolean syncActive = ContentResolver.isSyncActive(account, SyncUtils.CONTENT_AUTHORITY);
                    boolean syncPending = ContentResolver.isSyncPending(account, SyncUtils.CONTENT_AUTHORITY);

                    //if (syncActive || syncPending) {
                    //	//
                    //else { // } else if (!syncPending) {
                    //	// Load events
                    //	loadEvents();
                    //}

                    if (!syncActive && !syncPending) {
                        loadEvents();
                    }
                }
            });
        }
    };

    private void sortAndUpdateEvents() {
        List<Event> events = imin.getUser().getEvents();
        imin.sortEvents(events);
        eventsListAdapter.notifyDataSetChanged();
    }

}
