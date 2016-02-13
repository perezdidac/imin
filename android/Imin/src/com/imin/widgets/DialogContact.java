package com.imin.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.imin.Imin;
import com.imin.R;
import com.imin.contacts.Contact;

public class DialogContact {

	private AlertDialog alertDialog;
	private Context context;
	private boolean isShown;

	private DialogContact(Context context) {
		this.context = context;
	}

	public static DialogContact build(Context context) {
		return new DialogContact(context);
	}

	public void show(Contact contact) {
		alertDialog = new AlertDialog.Builder(context).create();

		// Finally, show the dialog
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View view = layoutInflater.inflate(R.layout.dialog_contact, null);
		ImageView imageContactPhoto = (ImageView) view.findViewById(R.id.imageContactPhoto);
		TextView textContactName = (TextView) view.findViewById(R.id.textContactName);
		Button btnOk = (Button) view.findViewById(R.id.btnOk);

		// Set font
		textContactName.setTypeface(Imin.fontLight);
		btnOk.setTypeface(Imin.fontLight);

		// Set text
		String contactName = contact.getName();
		textContactName.setText(contactName);

		// Set picture
		Bitmap contactPhoto = contact.getPhoto();
		if (contactPhoto != null) {
			imageContactPhoto.setImageBitmap(contactPhoto);
		}

		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				isShown = false;
				alertDialog.dismiss();
			}
		});
		isShown = true;

		alertDialog.setCancelable(true);
		alertDialog.setView(view);
		alertDialog.show();
	}

	public boolean isShown() {
		return isShown;
	}
}
