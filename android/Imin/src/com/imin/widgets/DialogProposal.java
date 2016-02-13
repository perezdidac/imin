package com.imin.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.imin.Imin;
import com.imin.R;
import com.imin.events.Event;
import com.imin.events.proposals.DateTimeFormatter;
import com.imin.events.proposals.Proposal;
import com.imin.user.User;

public class DialogProposal {

	public interface OnClickListener {
		public void onClick(Event event, boolean result);
	}

	private Context context;
	private OnClickListener onClickListener;
	private Event event;

	private DialogProposal(Context context, OnClickListener onClickListener) {
		this.context = context;
		this.onClickListener = onClickListener;
	}

	public static DialogProposal build(Context context, OnClickListener onClickListener) {
		return new DialogProposal(context, onClickListener);
	}

	public static DialogProposal build(Context context) {
		return new DialogProposal(context, null);
	}

	public Event getEvent() {
		return event;
	}

	public void show(User user, Event event) {
		final AlertDialog alertDialog = new AlertDialog.Builder(context).create();

		this.event = event;

		// Finally, show the dialog
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View view = layoutInflater.inflate(R.layout.dialog_proposal, null);
		TextView textProposal = (TextView) view.findViewById(R.id.textProposal);
		Button btnYes = (Button) view.findViewById(R.id.btnYes);
		Button btnNo = (Button) view.findViewById(R.id.btnNo);

		Proposal proposalDateTime = event.getFinalDateTimeProposal();
		Proposal proposalLocation = event.getFinalLocationProposal();

		String title = DateTimeFormatter.formatDateTime(proposalDateTime.getDateTime()) + " - "
				+ proposalLocation.getLocation();

		// Set text and font
		textProposal.setText(title);
		textProposal.setTypeface(Imin.fontLight);
		btnYes.setTypeface(Imin.fontLight);
		btnNo.setTypeface(Imin.fontLight);

		btnYes.setTag(event);
		btnYes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				alertDialog.dismiss();
				Event event = (Event) view.getTag();
				onClickListener.onClick(event, true);
			}
		});

		btnNo.setTag(event);
		btnNo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				alertDialog.dismiss();
				Event event = (Event) view.getTag();
				onClickListener.onClick(event, false);
			}
		});

		// Set fonts
		Imin.overrideFonts(view);
		textProposal.setTypeface(Imin.fontLight);

		alertDialog.setCancelable(false);
		alertDialog.setView(view);
		alertDialog.show();
	}
}
