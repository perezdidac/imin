package com.imin.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.imin.Imin;
import com.imin.R;

public class DialogText {

	public interface OnClickListener {
		public void onClick(View view, boolean result, String text);
	}

	private Context context;
	private OnClickListener onClickListener;
	private EditText textText;

	public DialogText(Context context, OnClickListener onClickListener) {
		this.context = context;
		this.onClickListener = onClickListener;
	}
	
	public void show(String message) {
		final AlertDialog alertDialog = new AlertDialog.Builder(context).create();

		// Finally, show the dialog
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View view = layoutInflater.inflate(R.layout.dialog_text, null);
		TextView textMessage = (TextView) view.findViewById(R.id.textMessage);
		textText = (EditText) view.findViewById(R.id.textText);
		Button btnOk = (Button) view.findViewById(R.id.btnOk);
		Button btnCancel = (Button) view.findViewById(R.id.btnCancel);

		// Set text and font
		textMessage.setText(message);
		textMessage.setTypeface(Imin.fontLight);
		textText.setTypeface(Imin.fontLight);
		btnOk.setTypeface(Imin.fontLight);
		btnCancel.setTypeface(Imin.fontLight);

		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				alertDialog.dismiss();
				String text = textText.getText().toString();
				onClickListener.onClick(view, true, text);
			}
		});

		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				alertDialog.dismiss();
				String text = textText.getText().toString();
				onClickListener.onClick(view, false, text);
			}
		});

		alertDialog.setCancelable(false);
		alertDialog.setView(view);
		alertDialog.show();
	}
}
