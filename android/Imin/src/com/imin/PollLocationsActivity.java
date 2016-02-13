package com.imin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.imin.analytics.Analytics;
import com.imin.communications.events.TaskCloseEvent;
import com.imin.communications.events.TaskCloseEvent.TaskCloseEventListener;
import com.imin.communications.events.TaskCloseEventParams;
import com.imin.communications.events.TaskRespondEvent;
import com.imin.communications.events.TaskRespondEvent.TaskRespondEventListener;
import com.imin.communications.events.TaskRespondEventParams;
import com.imin.contacts.Contact;
import com.imin.events.Event;
import com.imin.events.proposals.Location;
import com.imin.events.proposals.Proposal;
import com.imin.events.responses.Response;
import com.imin.widgets.DialogContacts;
import com.imin.widgets.DialogInformation;
import com.imin.widgets.DialogProgress;
import com.imin.widgets.DialogProposal;
import com.imin.widgets.ResizableImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PollLocationsActivity extends ActionBarActivity implements TaskRespondEventListener, TaskCloseEventListener {

    // Class that stores the configuration instance for recreating the
    // full activity restoring the dialogs and other
    private class PollActivityConfigurationInstance {
        public TaskCloseEvent taskCloseEvent;
        public TaskRespondEvent taskRespondEvent;
    }

    // Layout objects
    private LinearLayout layoutSelectLocations;
    private LinearLayout layoutCloseEvent;
    private LinearLayout layoutPollLocations;
    private LinearLayout layoutPollUserVotes;
    private LinearLayout layoutPollCounters;
    private LinearLayout layoutPollPoll;

    // Objects that must be saved
    private DialogInformation dialogResponded;
    private DialogInformation dialogClosed;
    private DialogProgress dialogRespondProgress;
    private DialogProgress dialogCloseProgress;

    // Objects that must be saved
    private LayoutInflater inflater;
    private boolean closing;
    private boolean modified;
    private boolean proposalSelected;

    // Tasks
    private PollActivityConfigurationInstance configurationInstance;

    // Imin application object
    private Imin imin;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_locations);
        Imin.overrideFonts(findViewById(android.R.id.content));

        inflater = LayoutInflater.from(this);

        // Get the Imin application object
        imin = ((Imin) getApplication());
        context = this;

        Intent intent = getIntent();
        closing = intent.getBooleanExtra(Imin.EXTRA_EVENT_POLL_CLOSE, false);

        // Initialize objects
        initializeObjects();

        // Check if the the activity has been opened for closing the event
        checkClose();

        // Load values and fill
        fillValues();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        if (configurationInstance.taskCloseEvent != null) {
            configurationInstance.taskCloseEvent.setListener(null);
        }
        if (configurationInstance.taskRespondEvent != null) {
            configurationInstance.taskRespondEvent.setListener(null);
        }
        return configurationInstance;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // Save the values
        savedInstanceState.putBoolean("closing", closing);
        savedInstanceState.putBoolean("modified", modified);
        savedInstanceState.putBoolean("proposalSelected", proposalSelected);

        // Save the values
        if (dialogRespondProgress != null) {
            savedInstanceState.putBoolean("dialogRespondProgress", dialogRespondProgress.isShown());
        }
        if (dialogCloseProgress != null) {
            savedInstanceState.putBoolean("dialogCloseProgress", dialogCloseProgress.isShown());
        }
        if (dialogResponded != null) {
            savedInstanceState.putBoolean("dialogResponded", dialogResponded.isShown());
        }
        if (dialogClosed != null) {
            savedInstanceState.putBoolean("dialogClosed", dialogClosed.isShown());
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore the values
        closing = savedInstanceState.getBoolean("closing");
        modified = savedInstanceState.getBoolean("modified");
        proposalSelected = savedInstanceState.getBoolean("proposalSelected");

        if (closing) {
            startCloseEvent();
        }

        // Restore the values
        if (savedInstanceState.getBoolean("dialogRespondProgress")) {
            showRespondDialog();
        }
        if (savedInstanceState.getBoolean("dialogCloseProgress")) {
            showCloseDialog();
        }
        if (savedInstanceState.getBoolean("dialogResponded")) {
            dialogResponded();
        }
        if (savedInstanceState.getBoolean("dialogClosed")) {
            dialogClosed();
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
        inflater.inflate(R.menu.poll_locations, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_share:
                shareEvent();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeObjects() {
        TextView textEventName;
        Button buttonYourVote;
        Button btnFinish;

        // Save object references
        layoutSelectLocations = (LinearLayout) findViewById(R.id.layoutSelectLocations);
        layoutCloseEvent = (LinearLayout) findViewById(R.id.layoutCloseEvent);
        textEventName = (TextView) findViewById(R.id.textEventName);
        btnFinish = (Button) findViewById(R.id.btnFinish);
        buttonYourVote = (Button) findViewById(R.id.buttonYourVote);
        layoutPollLocations = (LinearLayout) findViewById(R.id.layoutPollLocations);
        layoutPollUserVotes = (LinearLayout) findViewById(R.id.layoutPollUserVotes);
        layoutPollCounters = (LinearLayout) findViewById(R.id.layoutPollCounters);
        layoutPollPoll = (LinearLayout) findViewById(R.id.layoutPollPoll);

        // Restore dialogs
        restoreDialogs();

        // Set listeners for handling events
        btnFinish.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finishVoting();
            }
        });

        // Restore the 'modified' value
        modified = getIntent().getBooleanExtra("modified", modified);

        // Get the current selected event
        Event event = imin.getUser().getCurrentEvent();

        // Put the event name
        String eventName = event.getName();
        textEventName.setText(eventName);

        if (closing) {
            buttonYourVote.setText("");
        }

        /*
        // Check if the event has been created by the user
		if (event.isAdmin()) {
			// Check if it is closed
			if (event.isClosed()) {
				// Event closed
			} else {
				// btnCloseEvent.setVisibility(View.VISIBLE);
			}
		}
		*/
    }

    private void restoreDialogs() {
        @SuppressWarnings("deprecation")
        Object retained = getLastNonConfigurationInstance();

        if (retained instanceof PollActivityConfigurationInstance) {
            configurationInstance = (PollActivityConfigurationInstance) retained;
            if (configurationInstance.taskCloseEvent != null) {
                configurationInstance.taskCloseEvent.setListener(this);
            }
            if (configurationInstance.taskRespondEvent != null) {
                configurationInstance.taskRespondEvent.setListener(this);
            }
        } else {
            configurationInstance = new PollActivityConfigurationInstance();
        }
    }

    private void fillValues() {
        // Animator animator;

        // Clear the poll before recreating it
        layoutPollLocations.removeAllViews();
        layoutPollPoll.removeAllViews();
        layoutPollCounters.removeAllViews();
        layoutPollUserVotes.removeAllViews();

        // Get the current selected event
        Event event = imin.getUser().getCurrentEvent();

        if (!event.isResponded()) {
            modified = true;
        }

        // Get the list of proposals
        List<Proposal> proposals = event.getProposals();

        // =================================================== //
        // FILL LOCATION NAMES ON THE TOP

        // Retrieve the possible days of all the proposals
        List<Location> locations = event.getProposalLocations();

        // Sort the locations
        Collections.sort(locations);

        for (Location location : locations) {
            // For each day, create all the layouts
            String locationName = location.getName();

            // Find the corresponding proposal
            boolean found = false;
            Proposal proposal = null;
            for (Proposal proposalToFind : proposals) {
                if (proposalToFind.getLocation() == location) {
                    // Found
                    found = true;
                    proposal = proposalToFind;
                    break;
                }
            }

            if (found) {
                // Get the list of attending contacts for this proposal
                List<Contact> attendingContacts = proposal.getContacts(Response.RESPONSE_TYPE_ATTENDING);

                // ==================================================
                // HEADER ON THE TOP SHOWING DATE AND TIME

                // Inflate location layout
                View layoutPollLocation = inflater.inflate(R.layout.layout_poll_location, null);
                Button btnViewPollLocation = (Button) layoutPollLocation.findViewById(R.id.btnViewPollLocation);

                btnViewPollLocation.setText(locationName);

                btnViewPollLocation.setTag(proposal);
                btnViewPollLocation.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        Proposal proposal = (Proposal) view.getTag();
                        contactsDialog(proposal);
                        return true;
                    }
                });

                // Finally, add the location to the list of locations
                layoutPollLocations.addView(layoutPollLocation);

                // Inflate votes linear layout
                LinearLayout layoutPollVotes = (LinearLayout) inflater.inflate(R.layout.layout_poll_votes, null);

                // ==================================================
                // USER RESPONSES

                int responseType;

                // Find user response
                String publicUserId = imin.getUser().getPublicUserId();
                Response response = proposal.getContactResponse(publicUserId);

                if (response != null) {
                    // Responded, let's see which response has the user voted
                    if (response.getResponseType() == Response.RESPONSE_TYPE_ATTENDING) {
                        responseType = Response.RESPONSE_TYPE_ATTENDING;
                    } else if (response.getResponseType() == Response.RESPONSE_TYPE_NOT_ATTENDING) {
                        responseType = Response.RESPONSE_TYPE_NOT_ATTENDING;
                    } else {
                        responseType = Response.RESPONSE_TYPE_NOT_ATTENDING;
                    }
                } else {
                    // Not responded
                    responseType = Response.RESPONSE_TYPE_NOT_ATTENDING;
                }

                // Inflate the option view
                View layoutPollUserVote = inflater.inflate(R.layout.layout_poll_location_user_vote, null);
                Button btnViewPollVote = (Button) layoutPollUserVote.findViewById(R.id.btnViewPollVote);

                if (closing) {
                    btnViewPollVote.setBackgroundResource(R.drawable.button_poll_option_not_attending_background);
                } else {
                    // Set the background of the view
                    paintResponse(btnViewPollVote, responseType);
                }

                // Set the button clickable
                btnViewPollVote.setTag(proposal);
                btnViewPollVote.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!closing) {
                            // Set the modified flag
                            modified = true;

                            // Get the proposal of the view
                            Proposal proposal = (Proposal) view.getTag();

                            // Get the response of ourselves
                            Response response = proposal.getContactResponse(imin.getUser().getPublicUserId());

                            if (response != null) {
                                // Increment the response type
                                response.incrementResponseType();
                            } else {
                                // Create
                                response = new Response(imin.getUser().getContact(), Response.RESPONSE_TYPE_ATTENDING,
                                        proposal);

                                // Finally, add the user response
                                proposal.addResponse(response);
                            }

                            int responseType = response.getResponseType();

                            // Paint the response
                            Button btnVote = (Button) view;
                            paintResponse(btnVote, responseType);

                            // Paint the counters
                            paintCounters();
                        } else {
                            // Save the proposal of the view
                            imin.getUser().setSelectedLocationProposal((Proposal) view.getTag());

                            proposalSelected = true;

                            // Reset user votes and counters
                            paintUserVotes();
                            paintCounters();

                            view.setBackgroundResource(R.drawable.button_poll_option_attending_background);
                        }
                    }

                });

                // So, add the option
                layoutPollUserVotes.addView(layoutPollUserVote);

                // ==================================================
                // COUNTERS

                // Inflate the option view
                View layoutPollCounter = inflater.inflate(R.layout.layout_poll_location_counter, null);

                layoutPollCounters.addView(layoutPollCounter);

                // ==================================================
                // RESPONSES FROM THE REST OF CONTACTS

                for (Contact contact : attendingContacts) {
                    // Inflate the option view
                    View layoutPollVote = inflater.inflate(R.layout.layout_poll_location_vote, null);
                    ResizableImageView imageViewPollContact = (ResizableImageView) layoutPollVote
                            .findViewById(R.id.imageViewPollContact);
                    TextView textViewPollContact = (TextView) layoutPollVote.findViewById(R.id.textViewPollContact);

                    // Handle click
                    imageViewPollContact.setTag(contact);
                    imageViewPollContact.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Contact contact = (Contact) view.getTag();
                            Imin.showContactProfile(context, contact);
                        }
                    });

                    textViewPollContact.setTag(contact);
                    textViewPollContact.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Contact contact = (Contact) view.getTag();
                            Imin.showContactProfile(context, contact);
                        }
                    });

                    // Set the contact photo
                    Bitmap contactPhoto = contact.getPhoto();
                    if (contactPhoto != null) {
                        imageViewPollContact.setImageBitmap(contactPhoto);
                        imageViewPollContact.setVisibility(View.VISIBLE);
                        textViewPollContact.setVisibility(View.GONE);
                    } else {
                        // Capital letter in the box
                        String contactName = contact.getName();
                        if (contactName.length() > 0) {
                            textViewPollContact.setText(contactName.substring(0, 1));
                            textViewPollContact.setBackgroundColor(contact.getColor());
                        }

                        imageViewPollContact.setVisibility(View.GONE);
                        textViewPollContact.setVisibility(View.VISIBLE);
                    }

                    // So, add the option
                    layoutPollVotes.addView(layoutPollVote);
                }

                // Inflate the option view
                View layoutPollHiddenContact = inflater.inflate(R.layout.layout_poll_location_hidden_contact, null);

                // Add a hidden layout just for filling the list
                layoutPollVotes.addView(layoutPollHiddenContact);

                // Finally, add the list of contacts who are attending
                layoutPollPoll.addView(layoutPollVotes);
            }
        }

        paintCounters();

        // Set fonts
        Imin.overrideFonts(layoutPollLocations, Imin.fontRegular);
        Imin.overrideFonts(layoutPollCounters, Imin.fontRegular);
        Imin.overrideFonts(layoutPollPoll, Imin.fontRegular);
        Imin.overrideFonts(layoutPollUserVotes, Imin.fontRegular);
    }

    private void paintResponse(Button btnVote, int responseType) {
        // Paint the button depending on the response type
        if (responseType == Response.RESPONSE_TYPE_ATTENDING) {
            btnVote.setBackgroundResource(R.drawable.button_poll_option_attending_background);
        } else {
            btnVote.setBackgroundResource(R.drawable.button_poll_option_not_attending_background);
        }
    }

    private void paintUserVotes() {
        // Iterate through the counters
        for (int k = 0; k < layoutPollUserVotes.getChildCount(); ++k) {
            // Get the user response
            View layoutPollUserVote = layoutPollUserVotes.getChildAt(k);
            Button btnViewPollVote = (Button) layoutPollUserVote.findViewById(R.id.btnViewPollVote);
            btnViewPollVote.setBackgroundResource(R.drawable.button_poll_option_not_attending_background);
        }
    }

    private void paintCounters() {
        // Iterate through the counters
        for (int k = 0; k < layoutPollUserVotes.getChildCount(); ++k) {
            // Get the user response
            View layoutPollUserVote = layoutPollUserVotes.getChildAt(k);
            Button btnViewPollVote = (Button) layoutPollUserVote.findViewById(R.id.btnViewPollVote);
            Proposal proposal = (Proposal) btnViewPollVote.getTag();

            View layoutPollCounter = layoutPollCounters.getChildAt(k);
            Button btnPollCounter = (Button) layoutPollCounter.findViewById(R.id.btnPollCounter);

            // Get the response of ourselves
            Response response = proposal.getContactResponse(imin.getUser().getPublicUserId());

            // Get number of votes
            List<Contact> attendingContacts = proposal.getContacts(Response.RESPONSE_TYPE_ATTENDING);

            // Set the counter text
            int num_attending_contacts = attendingContacts.size();
            String counter = String.valueOf(num_attending_contacts);
            btnPollCounter.setText(counter);

            if (closing) {
                btnPollCounter.setEnabled(false);
                btnPollCounter.setTextColor(getResources().getColor(R.color.black));
            } else {
                if (response != null && response.getResponseType() == Response.RESPONSE_TYPE_ATTENDING) {
                    btnPollCounter.setEnabled(true);
                    btnPollCounter.setTextColor(getResources().getColor(R.color.white));
                } else {
                    btnPollCounter.setEnabled(false);
                    btnPollCounter.setTextColor(getResources().getColor(R.color.black));
                }
            }
        }
    }

    private void startCloseEvent() {
        // The event must be closed, so we must notify the user to select
        // a desired proposal for closing the event poll
        layoutSelectLocations.setVisibility(View.GONE);
        layoutCloseEvent.setVisibility(View.VISIBLE);

        // Set closing flag true indicates that the user is about to decide
        // the final proposal
        closing = true;

        // Repaint the whole poll layout
        fillValues();
    }

    private void checkClose() {
        if (closing) {
            startCloseEvent();
        }
    }

    private void shareEvent() {
        // Get the current event
        Event event = imin.getUser().getCurrentEvent();

        // Get the path
        Imin.shareEvent(this, event);
    }

    private void contactsDialog(Proposal proposal) {
        // Show the contacts in a custom dialog
        DialogContacts dialogContacts = DialogContacts.build(this);
        dialogContacts.show(imin.getUser(), proposal);
    }

    // Get the current selected event
    private void eventClose() {
        Event event = imin.getUser().getCurrentEvent();

        // Go ahead with event closing
        showCloseDialog();

        Proposal finalDateTimeProposal = event.getFinalDateTimeProposal();
        Proposal finalLocationProposal = event.getFinalLocationProposal();
        String finalDateTimeProposalId = finalDateTimeProposal.getPublicProposalId();
        String finalLocationProposalId = finalLocationProposal.getPublicProposalId();
        event.setFinalDateTimeProposalId(finalDateTimeProposalId);
        event.setFinalLocationProposalId(finalDateTimeProposalId);

        // Run an asynchronous task to retrieve the single event
        configurationInstance.taskCloseEvent = new TaskCloseEvent(this);
        TaskCloseEventParams params = new TaskCloseEventParams(imin.getUser().getPrivateUserId(), event.getId(), true,
                finalDateTimeProposalId, finalLocationProposalId);
        configurationInstance.taskCloseEvent.execute(params);
    }

    private void finishClosing() {
        if (!proposalSelected) {
            DialogInformation.build(this).show(getString(R.string.tienes_que_seleccionar_un_lugar),
                    DialogInformation.ICON_ALERT);
            return;
        }

        Event event = imin.getUser().getCurrentEvent();

        event.setFinalDateTimeProposal(imin.getUser().getSelectedDateTimeProposal());
        event.setFinalLocationProposal(imin.getUser().getSelectedLocationProposal());
        event.setFinalDateTimeProposalId(imin.getUser().getSelectedDateTimeProposal().getPublicProposalId());
        event.setFinalLocationProposalId(imin.getUser().getSelectedLocationProposal().getPublicProposalId());

        // Ask the user
        DialogProposal dialogProposal = DialogProposal.build(this, new DialogProposal.OnClickListener() {
            @Override
            public void onClick(Event event, boolean result) {
                if (result) {
                    // Yes button clicked
                    eventClose();
                }
            }
        });

        dialogProposal.show(imin.getUser(), imin.getUser().getCurrentEvent());
    }

    private void finishVoting() {
        if (closing) {
            finishClosing();

            return;
        }

        if (!modified) {
            // Come back to the previous activity since the event is not modified
            dialogResponded();

            return;
        }

        // Get the current selected event
        Event event = imin.getUser().getCurrentEvent();

        // Get the event proposals
        List<Proposal> proposals = event.getProposals();

        // Analyze the responses
        List<Response> responses = new ArrayList<Response>();

        // Analyze the poll table
        for (Proposal proposal : proposals) {
            if (proposal.getProposalType() == Proposal.PROPOSAL_TYPE_LOCATION) {
                // Look up the table and get the response of the user for that
                Response response = proposal.getContactResponse(imin.getUser().getPublicUserId());

                if (response != null) {
                    // Add to the list of responses
                    responses.add(response);
                }
            }
        }

        showRespondDialog();

        // Add the datetime responses
        responses.addAll(imin.dateTimeResponses);

        // At this point, we have the list of responses
        configurationInstance.taskRespondEvent = new TaskRespondEvent(this);
        TaskRespondEventParams params = new TaskRespondEventParams(imin.getUser().getPrivateUserId(), event, responses);
        configurationInstance.taskRespondEvent.execute(params);
    }

    private void showRespondDialog() {
        // Show the progress bar
        dialogRespondProgress = DialogProgress.build(this);
        dialogRespondProgress.show(getString(R.string.responding_event));
    }

    private void showCloseDialog() {
        // Close the event
        dialogCloseProgress = DialogProgress.build(this);
        dialogCloseProgress.show(getString(R.string.closing_event));
    }

    private void dialogClosed() {
        dialogClosed = DialogInformation.build(this, new DialogInformation.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Come back to the previous activity
                setResult(Imin.RESULT_CODE_POLL_LOCATIONS_OK);
                finish();
            }
        });
        dialogClosed.show(getString(R.string.event_closed), DialogInformation.ICON_OK);
    }

    private void dialogResponded() {
        dialogResponded = DialogInformation.build(this, new DialogInformation.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Come back to the previous activity
                setResult(Imin.RESULT_CODE_POLL_LOCATIONS_OK);
                finish();
            }
        });
        dialogResponded.show(getString(R.string.poll_responded), DialogInformation.ICON_OK);
    }

    @Override
    public void onEventResponded() {
        Event event = imin.getUser().getCurrentEvent();

        // Flag the event as responded
        event.setResponded(true);

        imin.getUser().setPollResponded(true);

        // Event closed
        dialogRespondProgress.dismiss();

        // Event responded
        dialogResponded();

        // Analytics event
        Analytics.send(this, Analytics.ANALYTICS_EVENT_VOTE);
    }

    @Override
    public void onEventNotResponded() {
        // Hide the progress dialog
        dialogRespondProgress.dismiss();

        // Show a Toast notifying
        DialogInformation.build(this).show(getString(R.string.generic_error), DialogInformation.ICON_ALERT);

        // The event has not been responded
        setResult(Imin.RESULT_CODE_POLL_LOCATIONS_ERROR);
    }

    @Override
    public void onEventReopened() {
        Event event = imin.getUser().getCurrentEvent();

        // Set closed flag
        event.setClosed(false);

        // Event reopened
        dialogCloseProgress.dismiss();

        // Show a Toast notifying
        DialogInformation.build(this).show(getString(R.string.event_reopened), DialogInformation.ICON_INFO);
    }

    @Override
    public void onEventNotClosed() {
        // Event not closed
        dialogCloseProgress.dismiss();

        // Show a Toast notifying
        DialogInformation.build(this).show(getString(R.string.generic_error), DialogInformation.ICON_ALERT);
    }

    @Override
    public void onEventClosed() {
        Event event = imin.getUser().getCurrentEvent();

        // Set closed flag
        event.setClosed(true);
        event.setFinalDateTimeProposal(imin.getUser().getSelectedDateTimeProposal());
        event.setFinalLocationProposal(imin.getUser().getSelectedLocationProposal());

        // Event closed
        dialogCloseProgress.dismiss();

        // Show information dialog
        dialogClosed();
    }

    @Override
    public void onEventNotReopened() {
        // Event not closed
        dialogCloseProgress.dismiss();

        // Show a Toast notifying
        DialogInformation.build(this).show(getString(R.string.generic_error), DialogInformation.ICON_ALERT);
    }
}
