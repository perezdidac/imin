package com.imin.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.imin.Imin;
import com.imin.R;

public class DialogPicture {

	public interface OnClickListener {
		public void onClick(View view, int source);
	}

	public static final int SELECT_CAMERA = 0;
	public static final int SELECT_GALLERY = 1;

	private Context context;
	private OnClickListener onClickListener;

	public DialogPicture(Context context, OnClickListener onClickListener) {
		this.context = context;
		this.onClickListener = onClickListener;
	}

	public void show(String text) {
		final AlertDialog alertDialog = new AlertDialog.Builder(context).create();

		// Finally, show the dialog
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View view = layoutInflater.inflate(R.layout.dialog_picture, null);
		TextView textMessage = (TextView) view.findViewById(R.id.textMessage);
		Button btnCamera = (Button) view.findViewById(R.id.btnCamera);
		Button btnGallery = (Button) view.findViewById(R.id.btnGallery);

		// Set text and font
		textMessage.setText(text);
		textMessage.setTypeface(Imin.fontLight);
		btnCamera.setTypeface(Imin.fontLight);
		btnGallery.setTypeface(Imin.fontLight);
		
		btnCamera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				alertDialog.dismiss();
				onClickListener.onClick(view, SELECT_CAMERA);
			}
		});

		btnGallery.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				alertDialog.dismiss();
				onClickListener.onClick(view, SELECT_GALLERY);
			}
		});

		alertDialog.setView(view);
		alertDialog.show();
	}
}
