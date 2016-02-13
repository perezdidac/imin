package com.imin.contacts;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.imin.Imin;
import com.imin.R;
import com.imin.user.User;

public class ProposalContactListAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<Contact> contacts;
	private User user;

	public ProposalContactListAdapter(Context context, List<Contact> contacts, User user) {
		inflater = LayoutInflater.from(context);
		this.contacts = contacts;
		this.user = user;
	}

	@Override
	public int getCount() {
		return contacts.size();
	}

	@Override
	public Object getItem(int position) {
		return contacts.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder viewHolder;

		// Check if the view has been already loaded
		if (view == null) {
			view = inflater.inflate(R.layout.layout_proposal_contacts_list_item, null);

			viewHolder = new ViewHolder();
			viewHolder.textContactName = (TextView) view.findViewById(R.id.textContactName);
			viewHolder.imageContactPicture = (ImageView) view.findViewById(R.id.imageContactPicture);
			viewHolder.textContactPicture = (TextView) view.findViewById(R.id.textContactPicture);

			// Set fonts
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		// Get the contact for the current position
		Contact contact = contacts.get(position);
		Bitmap bitmap = contact.getPhoto();

		//ObjectAnimator animation = ObjectAnimator.ofFloat(viewHolder.imageContactPicture, "alpha", 0f, 1f);
		//animation.setDuration(250);
		//animation.start();
		if (bitmap != null) {
			viewHolder.imageContactPicture.setImageBitmap(bitmap);
			viewHolder.imageContactPicture.setVisibility(View.VISIBLE);
			viewHolder.textContactPicture.setVisibility(View.GONE);
		} else {
			String contactName = contact.getName();
			if (contactName.length() > 0) {
				viewHolder.textContactPicture.setText(contactName.substring(0, 1));
			}
			viewHolder.imageContactPicture.setVisibility(View.GONE);
			viewHolder.textContactPicture.setVisibility(View.VISIBLE);
		}

		Imin.overrideFonts(view);
		if (contact.getPublicUserId().equals(user.getPublicUserId())) {
			viewHolder.textContactName.setText(R.string.you);
			viewHolder.textContactName.setTypeface(Imin.fontRegular);
		} else {
			viewHolder.textContactName.setText(contact.getName());
			viewHolder.textContactName.setTypeface(Imin.fontLight);
		}

		return view;
	}

	private static class ViewHolder {
		public TextView textContactName;
		public ImageView imageContactPicture;
		public TextView textContactPicture;
	}

}
