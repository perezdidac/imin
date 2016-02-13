package com.imin.events;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imin.Imin;
import com.imin.R;
import com.imin.contacts.Contact;
import com.imin.events.proposals.DateTime;
import com.imin.events.proposals.DateTimeFormatter;
import com.imin.events.proposals.Location;
import com.imin.events.proposals.Proposal;
import com.nhaarman.listviewanimations.itemmanipulation.ExpandableListItemAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventListAdapter extends ExpandableListItemAdapter<Event> {

    public class PictureHolder {
        ImageView imageContact;
        TextView textContact;
    }

    private Context context;
    private EventListListener eventListListener;
    private List<Event> events;
    private LayoutInflater inflater;
    private Map<Contact, PictureHolder> contactPictureMap;

    public EventListAdapter(Context context, EventListListener eventListListener, List<Event> events) {
        super(context, R.layout.layout_event, R.id.layout_event_frame_title, R.id.layout_event_frame_content);

        this.context = context;
        this.eventListListener = eventListListener;
        this.events = events;
        inflater = LayoutInflater.from(context);
        contactPictureMap = new HashMap<Contact, PictureHolder>();
    }

    @Override
    public long getItemId(int position) {
        return (long) events.get(position).hashCode();
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getTitleView(int position, View view, ViewGroup parent) {
        TitleViewHolder viewHolder;

        // Check if the view has been already loaded
        if (view == null) {
            view = inflater.inflate(R.layout.layout_event_title, null);

            viewHolder = new TitleViewHolder();
            viewHolder.textEventType = (TextView) view.findViewById(R.id.textEventType);
            viewHolder.imageEventPicture = (ImageView) view.findViewById(R.id.imageEventPicture);
            viewHolder.textEventName = (TextView) view.findViewById(R.id.textEventName);
            viewHolder.layoutProposal = (LinearLayout) view.findViewById(R.id.layoutProposal);
            viewHolder.textLocation = (TextView) view.findViewById(R.id.textLocation);
            viewHolder.textDateTime = (TextView) view.findViewById(R.id.textDateTime);
            viewHolder.layoutEventRightBar = (LinearLayout) view.findViewById(R.id.layoutEventRightBar);

            viewHolder.textEventPeople = (TextView) view.findViewById(R.id.textEventPeople);
            viewHolder.btnEventPeople = (ImageView) view.findViewById(R.id.btnEventPeople);
            viewHolder.btnEventShare = (ImageView) view.findViewById(R.id.btnEventShare);

            viewHolder.btnEventShare.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (Integer) view.getTag();
                    eventListListener.onEventShare(position);
                }
            });

            view.setTag(viewHolder);
        } else {
            viewHolder = (TitleViewHolder) view.getTag();
        }

        Event event = events.get(position);

        // Event picture
        Bitmap bitmap = event.getPicture();
        if (bitmap != null) {
            viewHolder.imageEventPicture.setImageBitmap(bitmap);
        } else {
            viewHolder.imageEventPicture.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.img_event_default));
        }

        // Get number of total contacts
        List<Contact> allContacts = event.getContacts();
        String totalContacts = String.valueOf(allContacts.size());

        // Fill the data
        viewHolder.textEventName.setText(event.getName());
        viewHolder.textEventPeople.setText(totalContacts);
        viewHolder.btnEventPeople.setTag(position);
        viewHolder.btnEventShare.setTag(position);

        // Check if the event is closed
        if (!event.isClosed()) {
            // Event not closed
            if (event.isResponded()) {
                // Event voted
                viewHolder.layoutEventRightBar.setBackgroundColor(context.getResources().getColor(R.color.blue));
                viewHolder.textEventType.setTextColor(context.getResources().getColor(R.color.blue));
                viewHolder.textEventType.setText(R.string.responded);
            } else {
                if (event.isAdmin()) {
                    // To be responded
                    viewHolder.layoutEventRightBar.setBackgroundColor(context.getResources().getColor(R.color.red));
                    viewHolder.textEventType.setTextColor(context.getResources().getColor(R.color.red));
                    viewHolder.textEventType.setText(R.string.to_be_responded);
                } else {
                    // Invitation
                    viewHolder.layoutEventRightBar.setBackgroundColor(context.getResources().getColor(R.color.red));
                    viewHolder.textEventType.setTextColor(context.getResources().getColor(R.color.red));
                    viewHolder.textEventType.setText(R.string.invitation);
                }
            }

            viewHolder.layoutProposal.setVisibility(View.GONE);
        } else {
            viewHolder.layoutEventRightBar.setBackgroundColor(context.getResources().getColor(R.color.green));
            viewHolder.textEventType.setTextColor(context.getResources().getColor(R.color.green));
            viewHolder.textEventType.setText(R.string.closed);

            viewHolder.layoutProposal.setVisibility(View.VISIBLE);

            // Get the chosen proposal since the event is closed
            Proposal finalDateTimeProposal = event.getFinalDateTimeProposal();
            Proposal finalLocationProposal = event.getFinalLocationProposal();

            if (finalDateTimeProposal != null && finalLocationProposal != null) {
                DateTime dateTime = finalDateTimeProposal.getDateTime();
                Location location = finalLocationProposal.getLocation();

                // Put the date and time
                String day = DateTimeFormatter.formatDay(context, dateTime.getDate());
                String fullDate = DateTimeFormatter.formatLongDate(dateTime.getDate());
                viewHolder.textDateTime.setText(Html.fromHtml("<b>" + day + "</b> " + fullDate));
                viewHolder.textLocation.setText(location.getName());
            }
        }

        // Override fonts
        Imin.overrideFonts(view);

        // Other fonts
        viewHolder.textEventName.setTypeface(Imin.fontLight);

        return view;
    }

    @Override
    public View getContentView(int position, View view, ViewGroup parent) {
        ContentViewHolder viewHolder;

        // Check if the view has been already loaded
        if (view == null) {
            view = inflater.inflate(R.layout.layout_event_content, null);

            viewHolder = new ContentViewHolder();
            viewHolder.textEventDescription = (TextView) view.findViewById(R.id.textEventDescription);
            viewHolder.btnEventJoin = (Button) view.findViewById(R.id.btnEventJoin);
            viewHolder.btnEventEdit = (Button) view.findViewById(R.id.btnEventEdit);
            viewHolder.btnEventReopen = (Button) view.findViewById(R.id.btnEventReopen);
            viewHolder.btnEventPoll = (Button) view.findViewById(R.id.btnEventPoll);
            viewHolder.btnEventRemove = (Button) view.findViewById(R.id.btnEventRemove);
            viewHolder.btnEventClose = (Button) view.findViewById(R.id.btnEventClose);

            viewHolder.textInvitedContacts = (TextView) view.findViewById(R.id.textInvitedContacts);
            viewHolder.textAttendingContacts = (TextView) view.findViewById(R.id.textAttendingContacts);
            viewHolder.textNotAttendingContacts = (TextView) view.findViewById(R.id.textNotAttendingContacts);
            viewHolder.layoutContacts = (LinearLayout) view.findViewById(R.id.layoutContacts);
            viewHolder.layoutAttendingContacts = (LinearLayout) view.findViewById(R.id.layoutAttendingContacts);
            viewHolder.layoutNotAttendingContacts = (LinearLayout) view.findViewById(R.id.layoutNotAttendingContacts);
            viewHolder.layoutInvitedContacts = (LinearLayout) view.findViewById(R.id.layoutInvitedContacts);
            viewHolder.listAttendingContacts = (LinearLayout) view.findViewById(R.id.listAttendingContacts);
            viewHolder.listNotAttendingContacts = (LinearLayout) view.findViewById(R.id.listNotAttendingContacts);
            viewHolder.listInvitedContacts = (LinearLayout) view.findViewById(R.id.listInvitedContacts);
            viewHolder.layoutAdminRemember = (LinearLayout) view.findViewById(R.id.layoutAdminRemember);

            viewHolder.btnEventJoin.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (Integer) view.getTag();
                    eventListListener.onEventJoin(position);
                }
            });

            viewHolder.btnEventEdit.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (Integer) view.getTag();
                    eventListListener.onEventEdit(position);
                }
            });

            viewHolder.btnEventReopen.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (Integer) view.getTag();
                    eventListListener.onEventReopen(position);
                }
            });

            viewHolder.btnEventPoll.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (Integer) view.getTag();
                    eventListListener.onEventPoll(position);
                }
            });

            viewHolder.btnEventRemove.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (Integer) view.getTag();
                    eventListListener.onEventRemove(position);
                }
            });

            viewHolder.btnEventClose.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (Integer) view.getTag();
                    eventListListener.onEventClose(position);
                }
            });

            view.setTag(viewHolder);
        } else {
            viewHolder = (ContentViewHolder) view.getTag();
        }

        Event event = events.get(position);

        // Get number of total contacts
        List<Contact> allContacts = event.getContacts();

        // Fill the data
        viewHolder.textEventDescription.setText(event.getDescription());
        viewHolder.btnEventJoin.setTag(position);
        viewHolder.btnEventEdit.setTag(position);
        viewHolder.btnEventReopen.setTag(position);
        viewHolder.btnEventPoll.setTag(position);
        viewHolder.btnEventRemove.setTag(position);
        viewHolder.btnEventClose.setTag(position);

        // Edit button
        if (event.isAdmin() && !event.isClosed()) {
            // Event is mine
            viewHolder.layoutAdminRemember.setVisibility(View.VISIBLE);
        } else {
            // Event is not mine
            viewHolder.layoutAdminRemember.setVisibility(View.GONE);
        }

        //
        boolean showContacts = false;
        boolean showJoin = false;

        // Check if the event is closed
        if (!event.isClosed()) {
            viewHolder.btnEventReopen.setVisibility(View.GONE);

            viewHolder.btnEventPoll.setVisibility(View.VISIBLE);

            // Event not closed, we must show all the contacts
            List<Contact> invitedContacts = allContacts;

            if (invitedContacts.size() > 0) {
                viewHolder.listInvitedContacts.removeAllViews();
                for (Contact contact : invitedContacts) {
                    showContacts = true;
                    View layoutContact = inflater.inflate(R.layout.layout_proposal_contacts_list_item, null);
                    TextView textContactName = (TextView) layoutContact.findViewById(R.id.textContactName);
                    ImageView imageContactPicture = (ImageView) layoutContact.findViewById(R.id.imageContactPicture);

                    TextView textContactPicture = (TextView) layoutContact.findViewById(R.id.textContactPicture);
                    textContactName.setText(contact.getName());
                    Bitmap contactPhoto = contact.getPhoto();
                    if (contactPhoto != null) {
                        imageContactPicture.setImageBitmap(contactPhoto);
                        textContactPicture.setVisibility(View.GONE);
                        imageContactPicture.setVisibility(View.VISIBLE);
                    } else {
                        // Capital letter in the box
                        String contactName = contact.getName();
                        if (contactName.length() > 0) {
                            textContactPicture.setText(contactName.substring(0, 1));
                            textContactPicture.setBackgroundColor(contact.getColor());
                        }

                        textContactPicture.setVisibility(View.VISIBLE);
                        imageContactPicture.setVisibility(View.GONE);
                    }

                    layoutContact.setTag(contact);
                    layoutContact.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Contact contact = (Contact) view.getTag();
                            Imin.showContactProfile(context, contact);
                        }
                    });
                    viewHolder.listInvitedContacts.addView(layoutContact);

                    // Add the contact ImageView to a map in order to update it later
                    PictureHolder pictureHolder = new PictureHolder();
                    pictureHolder.imageContact = imageContactPicture;
                    pictureHolder.textContact = textContactPicture;
                    contactPictureMap.put(contact, pictureHolder);
                }
                viewHolder.layoutInvitedContacts.setVisibility(View.VISIBLE);
            } else {
                viewHolder.layoutInvitedContacts.setVisibility(View.GONE);
            }

            viewHolder.layoutAttendingContacts.setVisibility(View.GONE);
            viewHolder.layoutNotAttendingContacts.setVisibility(View.GONE);
        } else {
            // Event closed
            if (event.isAdmin()) {
                // If I am the creator
                viewHolder.btnEventReopen.setVisibility(View.VISIBLE);
            } else {
                // If not creator
                viewHolder.btnEventReopen.setVisibility(View.GONE);

                // If not responded by me
                if (!event.isResponded()) {
                    showJoin = true;
                }
            }

            viewHolder.btnEventPoll.setVisibility(View.GONE);

            // Event closed, we must split into attending and not attending
            List<Contact> attendingContacts = event.getAttendingContacts();
            List<Contact> notAttendingContacts = event.getNotAttendingContacts();

            if (attendingContacts.size() > 0) {
                viewHolder.listAttendingContacts.removeAllViews();
                for (Contact contact : attendingContacts) {
                    showContacts = true;
                    View layoutContact = inflater.inflate(R.layout.layout_proposal_contacts_list_item, null);
                    TextView textContactName = (TextView) layoutContact.findViewById(R.id.textContactName);
                    ImageView imageContactPicture = (ImageView) layoutContact.findViewById(R.id.imageContactPicture);

                    TextView textContactPicture = (TextView) layoutContact.findViewById(R.id.textContactPicture);
                    textContactName.setText(contact.getName());
                    Bitmap contactPhoto = contact.getPhoto();
                    if (contactPhoto != null) {
                        imageContactPicture.setImageBitmap(contactPhoto);
                        textContactPicture.setVisibility(View.GONE);
                        imageContactPicture.setVisibility(View.VISIBLE);
                    } else {
                        // Capital letter in the box
                        String contactName = contact.getName();
                        if (contactName.length() > 0) {
                            textContactPicture.setText(contactName.substring(0, 1));
                            textContactPicture.setBackgroundColor(contact.getColor());
                        }

                        textContactPicture.setVisibility(View.VISIBLE);
                        imageContactPicture.setVisibility(View.GONE);
                    }

                    layoutContact.setTag(contact);
                    layoutContact.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Contact contact = (Contact) view.getTag();
                            Imin.showContactProfile(context, contact);
                        }
                    });
                    viewHolder.listAttendingContacts.addView(layoutContact);

                    // Add the contact ImageView to a map in order to update it later
                    PictureHolder pictureHolder = new PictureHolder();
                    pictureHolder.imageContact = imageContactPicture;
                    pictureHolder.textContact = textContactPicture;
                    contactPictureMap.put(contact, pictureHolder);
                }
                viewHolder.layoutAttendingContacts.setVisibility(View.VISIBLE);
            } else {
                viewHolder.layoutAttendingContacts.setVisibility(View.GONE);
            }

            if (notAttendingContacts.size() > 0) {
                viewHolder.listNotAttendingContacts.removeAllViews();
                for (Contact contact : notAttendingContacts) {
                    showContacts = true;
                    View layoutContact = inflater.inflate(R.layout.layout_proposal_contacts_list_item, null);
                    TextView textContactName = (TextView) layoutContact.findViewById(R.id.textContactName);
                    ImageView imageContactPicture = (ImageView) layoutContact.findViewById(R.id.imageContactPicture);

                    TextView textContactPicture = (TextView) layoutContact.findViewById(R.id.textContactPicture);
                    textContactName.setText(contact.getName());
                    Bitmap contactPhoto = contact.getPhoto();
                    if (contactPhoto != null) {
                        imageContactPicture.setImageBitmap(contactPhoto);
                        textContactPicture.setVisibility(View.GONE);
                        imageContactPicture.setVisibility(View.VISIBLE);
                    } else {
                        // Capital letter in the box
                        String contactName = contact.getName();
                        if (contactName.length() > 0) {
                            textContactPicture.setText(contactName.substring(0, 1));
                            textContactPicture.setBackgroundColor(contact.getColor());
                        }

                        textContactPicture.setVisibility(View.VISIBLE);
                        imageContactPicture.setVisibility(View.GONE);
                    }

                    layoutContact.setTag(contact);
                    layoutContact.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Contact contact = (Contact) view.getTag();
                            Imin.showContactProfile(context, contact);
                        }
                    });
                    viewHolder.listNotAttendingContacts.addView(layoutContact);

                    // Add the contact ImageView to a map in order to update it later
                    PictureHolder pictureHolder = new PictureHolder();
                    pictureHolder.imageContact = imageContactPicture;
                    pictureHolder.textContact = textContactPicture;
                    contactPictureMap.put(contact, pictureHolder);
                }
                viewHolder.layoutNotAttendingContacts.setVisibility(View.VISIBLE);
            } else {
                viewHolder.layoutNotAttendingContacts.setVisibility(View.GONE);
            }

            viewHolder.layoutInvitedContacts.setVisibility(View.GONE);
        }

        if (showContacts) {
            viewHolder.layoutContacts.setVisibility(View.VISIBLE);
        } else {
            viewHolder.layoutContacts.setVisibility(View.GONE);
        }

        if (showJoin) {
            viewHolder.btnEventJoin.setVisibility(View.VISIBLE);
        } else {
            viewHolder.btnEventJoin.setVisibility(View.GONE);
        }

        Imin.overrideFonts(view);

        // Set fonts
        viewHolder.textInvitedContacts.setTypeface(Imin.fontBold);
        viewHolder.textAttendingContacts.setTypeface(Imin.fontBold);
        viewHolder.textNotAttendingContacts.setTypeface(Imin.fontBold);

        return view;
    }

    public void updateContactData(Contact contact) {
        // Look up the map in order to retrieve the corresponding ImageView
        // of the contact
        PictureHolder pictureHolder = contactPictureMap.get(contact);

        if (pictureHolder != null) {
            Bitmap contactPhoto = contact.getPhoto();
            if (pictureHolder.imageContact != null && contactPhoto != null) {
                pictureHolder.imageContact.setImageBitmap(contactPhoto);
                pictureHolder.imageContact.setVisibility(View.VISIBLE);
                pictureHolder.textContact.setVisibility(View.GONE);
            } else {
                pictureHolder.imageContact.setVisibility(View.GONE);
                pictureHolder.textContact.setVisibility(View.VISIBLE);
            }
        }
    }

    private static class TitleViewHolder {
        public TextView textEventType;
        public ImageView imageEventPicture;
        public TextView textEventName;
        public LinearLayout layoutProposal;
        public TextView textLocation;
        public TextView textDateTime;
        public LinearLayout layoutEventRightBar;

        public TextView textEventPeople;
        public ImageView btnEventPeople;
        public ImageView btnEventShare;
    }

    private static class ContentViewHolder {
        public TextView textEventDescription;
        public Button btnEventJoin;
        public Button btnEventEdit;
        public Button btnEventReopen;
        public Button btnEventPoll;
        public Button btnEventRemove;
        public Button btnEventClose;

        // People
        private TextView textInvitedContacts;
        private TextView textAttendingContacts;
        private TextView textNotAttendingContacts;
        private LinearLayout layoutContacts;
        private LinearLayout layoutAttendingContacts;
        private LinearLayout layoutNotAttendingContacts;
        private LinearLayout layoutInvitedContacts;
        private LinearLayout listAttendingContacts;
        private LinearLayout listNotAttendingContacts;
        private LinearLayout listInvitedContacts;
        private LinearLayout layoutAdminRemember;
    }

}
