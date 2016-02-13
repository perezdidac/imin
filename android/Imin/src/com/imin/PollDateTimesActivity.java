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
import com.imin.contacts.Contact;
import com.imin.events.Event;
import com.imin.events.proposals.DateTime;
import com.imin.events.proposals.DateTimeFormatter;
import com.imin.events.proposals.Proposal;
import com.imin.events.responses.Response;
import com.imin.widgets.DialogContacts;
import com.imin.widgets.DialogInformation;
import com.imin.widgets.ResizableImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PollDateTimesActivity extends ActionBarActivity {

    // Layout objects
    private LinearLayout layoutSelectDateTimes;
    private LinearLayout layoutCloseEvent;
    private LinearLayout layoutPollDays;
    private LinearLayout layoutPollUserVotes;
    private LinearLayout layoutPollCounters;
    private LinearLayout layoutPollPoll;

    // Objects that must be saved
    private LayoutInflater inflater;
    private boolean closing;
    private boolean modified;
    private boolean proposalSelected;

    // Imin application object
    private Imin imin;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_datetimes);
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
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // Save the values
        savedInstanceState.putBoolean("closing", closing);
        savedInstanceState.putBoolean("modified", modified);
        savedInstanceState.putBoolean("proposalSelected", proposalSelected);
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
        if (requestCode == Imin.REQUEST_CODE_POLL_LOCATIONS) {
            // Check activity result
            if (resultCode == Imin.RESULT_CODE_POLL_LOCATIONS_OK) {
                // Event responded, return
                setResult(Imin.RESULT_CODE_POLL_DATETIMES_OK);
                finish();
            } else if (resultCode == Imin.RESULT_CODE_POLL_LOCATIONS_ERROR) {
                // Event responded, return
                setResult(Imin.RESULT_CODE_POLL_DATETIMES_ERROR);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.poll_datetimes, menu);
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
        Button btnNext;

        // Save object references
        layoutSelectDateTimes = (LinearLayout) findViewById(R.id.layoutSelectDateTimes);
        layoutCloseEvent = (LinearLayout) findViewById(R.id.layoutCloseEvent);
        textEventName = (TextView) findViewById(R.id.textEventName);
        buttonYourVote = (Button) findViewById(R.id.buttonYourVote);
        btnNext = (Button) findViewById(R.id.btnNext);
        layoutPollDays = (LinearLayout) findViewById(R.id.layoutPollDays);
        layoutPollUserVotes = (LinearLayout) findViewById(R.id.layoutPollUserVotes);
        layoutPollCounters = (LinearLayout) findViewById(R.id.layoutPollCounters);
        layoutPollPoll = (LinearLayout) findViewById(R.id.layoutPollPoll);

        // Set listeners for handling events
        btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });

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

    private void fillValues() {
        // Animator animator;

        // Clear the poll before recreating it
        layoutPollDays.removeAllViews();
        layoutPollPoll.removeAllViews();
        layoutPollCounters.removeAllViews();
        layoutPollUserVotes.removeAllViews();

        // Get the current selected event
        Event event = imin.getUser().getCurrentEvent();

        // Get the list of proposals
        List<Proposal> proposals = event.getProposals();

        // =================================================== //
        // FILL DATETIME NAMES ON THE TOP

        // Retrieve the possible days of all the proposals
        List<DateTime> dateTimes = event.getProposalDateTimes();

        // Sort the datetimes
        Collections.sort(dateTimes);

        for (DateTime dateTime : dateTimes) {
            // For each day, create all the layouts
            String dateTimeName = DateTimeFormatter.formatDay(context, dateTime.getName()) + "\n"
                    + DateTimeFormatter.formatDate(dateTime);

            // Find the corresponding proposal
            boolean found = false;
            Proposal proposal = null;
            for (Proposal proposalToFind : proposals) {
                if (proposalToFind.getDateTime() == dateTime) {
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

                // Inflate dateTime layout
                View layoutPollDateTime = inflater.inflate(R.layout.layout_poll_datetime, null);
                Button btnViewPollDateTime = (Button) layoutPollDateTime.findViewById(R.id.btnViewPollDateTime);

                btnViewPollDateTime.setText(dateTimeName);

                btnViewPollDateTime.setTag(proposal);
                btnViewPollDateTime.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        Proposal proposal = (Proposal) view.getTag();
                        contactsDialog(proposal);
                        return true;
                    }
                });

                // Finally, add the dateTime to the list of dateTimes
                layoutPollDays.addView(layoutPollDateTime);

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
                View layoutPollUserVote = inflater.inflate(R.layout.layout_poll_datetime_user_vote, null);
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
                            imin.getUser().setSelectedDateTimeProposal((Proposal) view.getTag());

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
                View layoutPollCounter = inflater.inflate(R.layout.layout_poll_datetime_counter, null);

                layoutPollCounters.addView(layoutPollCounter);

                // ==================================================
                // RESPONSES FROM THE REST OF CONTACTS

                for (Contact contact : attendingContacts) {
                    // Inflate the option view
                    View layoutPollVote = inflater.inflate(R.layout.layout_poll_datetime_vote, null);
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
                View layoutPollHiddenContact = inflater.inflate(R.layout.layout_poll_datetime_hidden_contact, null);

                // Add a hidden layout just for filling the list
                layoutPollVotes.addView(layoutPollHiddenContact);

                // Finally, add the list of contacts who are attending
                layoutPollPoll.addView(layoutPollVotes);
            }
        }

        paintCounters();

        // Set fonts
        Imin.overrideFonts(layoutPollDays, Imin.fontRegular);
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
        layoutSelectDateTimes.setVisibility(View.GONE);
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

    private void next() {
        if (closing && !proposalSelected) {
            DialogInformation.build(this).show(getString(R.string.tienes_que_seleccionar_un_dia), DialogInformation.ICON_ALERT);
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
            if (proposal.getProposalType() == Proposal.PROPOSAL_TYPE_DATETIME) {
                // Look up the table and get the response of the user for that
                Response response = proposal.getContactResponse(imin.getUser().getPublicUserId());

                if (response != null) {
                    // Add to the list of responses
                    responses.add(response);
                }
            }
        }

        // Save responses and go to the next activity
        imin.dateTimeResponses = responses;
        Intent intent = new Intent(this, PollLocationsActivity.class);
        intent.putExtra("modified", modified);
        intent.putExtra(Imin.EXTRA_EVENT_POLL_CLOSE, closing);
        startActivityForResult(intent, Imin.REQUEST_CODE_POLL_LOCATIONS);
    }

}
