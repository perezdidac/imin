package com.imin.contacts;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.imin.Imin;
import com.imin.R;
import com.imin.contacts.TaskPhotos.TaskPhotosListener;

public class ContactListAdapter extends BaseAdapter {

	private List<Contact> originalContacts;
	private Bitmap defaultBitmap;
	private LayoutInflater inflater;

	private List<Contact> contacts;
	private TaskPhotos taskPhotos;

	public ContactListAdapter(Context context, Bitmap defaultBitmap, List<Contact> originalContacts,
			ContentResolver contentResolver) {
		inflater = LayoutInflater.from(context);
		this.defaultBitmap = defaultBitmap;
		this.originalContacts = originalContacts;

		contacts = new ArrayList<Contact>();
		filter("");

		taskPhotos = new TaskPhotos(contentResolver);
	}

	public void filter(String text) {
		// Clear the filtered list of contacts
		contacts.clear();

		if (text.length() == 0) {
			// Copy the whole list of contacts
			for (int k = 0; k < originalContacts.size(); ++k) {
				contacts.add(originalContacts.get(k));
			}
		} else {
			// Filter
			for (int k = 0; k < originalContacts.size(); ++k) {
				String name = originalContacts.get(k).getName();
				name = name.toLowerCase(Locale.getDefault());
				name = name.trim();
				if (name.contains(text)) {
					contacts.add(originalContacts.get(k));
				}
			}
		}

		// Update layout
		notifyDataSetChanged();
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
			view = inflater.inflate(R.layout.layout_contacts_list_item, null);

			viewHolder = new ViewHolder();
			viewHolder.imageContactPhoto = (ImageView) view.findViewById(R.id.imageContactPhoto);
			viewHolder.textContactName = (TextView) view.findViewById(R.id.textContactName);
			viewHolder.chkContactSelected = (CheckBox) view.findViewById(R.id.chkContactSelected);

			viewHolder.chkContactSelected.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// Mark the contact as selected or unselected
					Contact contact = (Contact) buttonView.getTag();
					contact.setSelected(isChecked);
				}

			});

			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		// Get the contact for the current position
		Contact contact = contacts.get(position);

		// First of all, load the contact photo if possible
		if (contact.getHasPicture() == Contact.CONTACT_HAS_PICTURE) {
			// TODO: CHECK OUT THIS, MAYBE TOO MUCH MEMORY LOADING ALL THE
			// PICTURES
			//ObjectAnimator animation = ObjectAnimator.ofFloat(viewHolder.imageContactPhoto, "alpha", 0f, 1f);
			//animation.setDuration(250);
			//animation.start();
			viewHolder.imageContactPhoto.setImageBitmap(contact.getPhoto());
		} else if (contact.getHasPicture() == Contact.CONTACT_HAS_PICTURE_NOT) {
			// Set default bitmap
			viewHolder.imageContactPhoto.setImageBitmap(defaultBitmap);
		} else {
			// Not queried yet
			Bitmap contactPhoto = null;

			try {
				contactPhoto = taskPhotos.loadImage(contact.getPhotoUri(), contact, viewHolder.imageContactPhoto,
						new TaskPhotosListener() {
							public void imageLoaded(Bitmap photo, Contact contact, ImageView imageContactPhoto) {
								// Got picture for this contact
								contact.setPhoto(photo);
								//ObjectAnimator animation = ObjectAnimator.ofFloat(imageContactPhoto, "alpha", 0f, 1f);
								//animation.setDuration(500);
								//animation.start();
								imageContactPhoto.setImageBitmap(contact.getPhoto());
								contact.setHasPicture(Contact.CONTACT_HAS_PICTURE);
							}

							public void imageNotLoaded(Contact contact) {
								// No picture for this contact
								contact.setHasPicture(Contact.CONTACT_HAS_PICTURE_NOT);
							}
						});

				//ObjectAnimator animation = ObjectAnimator.ofFloat(viewHolder.imageContactPhoto, "alpha", 0f, 1f);
				//animation.setDuration(500);
				//animation.start();
				if (contactPhoto != null) {
					viewHolder.imageContactPhoto.setImageBitmap(contactPhoto);
				} else {
					viewHolder.imageContactPhoto.setImageBitmap(defaultBitmap);
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		viewHolder.chkContactSelected.setTag(contact);

		viewHolder.textContactName.setText(contact.getName());
		
		//ObjectAnimator animation = ObjectAnimator.ofFloat(viewHolder.imageContactPhoto, "alpha", 0f, 1f);
		//animation.setDuration(500);
		//animation.start();
		viewHolder.imageContactPhoto.setImageBitmap(contact.getPhoto());
		viewHolder.chkContactSelected.setChecked(contact.isSelected());

		Imin.overrideFonts(view);
		return view;
	}

	private static class ViewHolder {
		public ImageView imageContactPhoto;
		public TextView textContactName;
		public CheckBox chkContactSelected;
	}

}
