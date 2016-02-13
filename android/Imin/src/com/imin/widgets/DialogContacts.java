package com.imin.widgets;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.AlphaInAnimationAdapter;
import com.imin.Imin;
import com.imin.R;
import com.imin.contacts.Contact;
import com.imin.contacts.ProposalContactListAdapter;
import com.imin.events.proposals.DateTimeFormatter;
import com.imin.events.proposals.Proposal;
import com.imin.events.responses.Response;
import com.imin.user.User;

public class DialogContacts {

	public interface OnClickListener {
		public void onClick(View view);
	}

	private Context context;
	private OnClickListener onClickListener;

	private DialogContacts(Context context, OnClickListener onClickListener) {
		this.context = context;
		this.onClickListener = onClickListener;
	}

	public static DialogContacts build(Context context, OnClickListener onClickListener) {
		return new DialogContacts(context, onClickListener);
	}

	public static DialogContacts build(Context context) {
		return new DialogContacts(context, null);
	}

	public void show(User user, Proposal proposal) {
		final AlertDialog alertDialog = new AlertDialog.Builder(context).create();

		// Finally, show the dialog
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View view = layoutInflater.inflate(R.layout.dialog_contacts, null);
		TextView textMessage = (TextView) view.findViewById(R.id.textMessage);
		Button btnOk = (Button) view.findViewById(R.id.btnOk);

		String title = DateTimeFormatter.formatDateTime(proposal.getDateTime()) + " - " + proposal.getLocation();

		// Set text and font
		textMessage.setText(title);
		textMessage.setTypeface(Imin.fontLight);
		btnOk.setTypeface(Imin.fontLight);

		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				alertDialog.dismiss();

				if (onClickListener != null) {
					onClickListener.onClick(view);
				}
			}
		});

		// Get all the contacts
		List<Contact> contacts = proposal.getContacts();

		if (!contacts.isEmpty()) {
			List<Contact> attendingContacts = proposal.getContacts(Response.RESPONSE_TYPE_ATTENDING);
			List<Contact> notAttendingContacts = proposal.getContacts(Response.RESPONSE_TYPE_NOT_ATTENDING);

			// Get the dialog and paint it
			LinearLayout layoutAttendingContacts = (LinearLayout) view.findViewById(R.id.layoutAttendingContacts);
			LinearLayout layoutNotAttendingContacts = (LinearLayout) view.findViewById(R.id.layoutNotAttendingContacts);
			ListView listAttendingContacts = (ListView) view.findViewById(R.id.listAttendingContacts);
			ListView listNotAttendingContacts = (ListView) view.findViewById(R.id.listNotAttendingContacts);

			// Fill the list of contacts
			if (attendingContacts.size() > 0) {
				ProposalContactListAdapter attendingContactsListAdapter = new ProposalContactListAdapter(context,
						attendingContacts, user);

				// Animation adapter
				AnimationAdapter animAdapter = new AlphaInAnimationAdapter(attendingContactsListAdapter);
				animAdapter.setAbsListView(listAttendingContacts);
				listAttendingContacts.setAdapter(animAdapter);

				layoutAttendingContacts.setVisibility(View.VISIBLE);
			}

			if (notAttendingContacts.size() > 0) {
				ProposalContactListAdapter notAttendingContactsListAdapter = new ProposalContactListAdapter(context,
						notAttendingContacts, user);

				// Animation adapter
				AnimationAdapter animAdapter = new AlphaInAnimationAdapter(notAttendingContactsListAdapter);
				animAdapter.setAbsListView(listNotAttendingContacts);
				listNotAttendingContacts.setAdapter(animAdapter);

				layoutNotAttendingContacts.setVisibility(View.VISIBLE);
			}
		}

		// Set fonts
		Imin.overrideFonts(view);
		textMessage.setTypeface(Imin.fontLight);

		alertDialog.setCancelable(true);
		alertDialog.setView(view);
		alertDialog.show();
	}
}
