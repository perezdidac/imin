package com.imin.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.imin.Imin;
import com.imin.R;

public class DialogQuestion {

	public interface OnClickListener {
		public void onClick(View view, boolean result);
	}

	private Context context;
	private OnClickListener onClickListener;

	public DialogQuestion(Context context, OnClickListener onClickListener) {
		this.context = context;
		this.onClickListener = onClickListener;
	}

	public void show(String message) {
		final AlertDialog alertDialog = new AlertDialog.Builder(context).create();

		// Finally, show the dialog
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View view = layoutInflater.inflate(R.layout.dialog_question, null);
		TextView textMessage = (TextView) view.findViewById(R.id.textMessage);
		Button btnYes = (Button) view.findViewById(R.id.btnYes);
		Button btnNo = (Button) view.findViewById(R.id.btnNo);

		// Set text and font
		textMessage.setText(message);
		textMessage.setTypeface(Imin.fontLight);
		btnYes.setTypeface(Imin.fontLight);
		btnNo.setTypeface(Imin.fontLight);

		btnYes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				alertDialog.dismiss();
				onClickListener.onClick(view, true);
			}
		});

		btnNo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				alertDialog.dismiss();
				onClickListener.onClick(view, false);
			}
		});

		alertDialog.setCancelable(false);
		alertDialog.setView(view);
		alertDialog.show();
	}
}
