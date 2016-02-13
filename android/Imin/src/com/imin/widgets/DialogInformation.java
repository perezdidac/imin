package com.imin.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.imin.Imin;
import com.imin.R;

public class DialogInformation {

	public interface OnClickListener {
		public void onClick(View view);
	}

	public static final int ICON_INFO = 0;
	public static final int ICON_ALERT = 1;
	public static final int ICON_OK = 2;

	private Context context;
	private OnClickListener onClickListener;
	private boolean isShown;

	private DialogInformation(Context context, OnClickListener onClickListener) {
		this.context = context;
		this.onClickListener = onClickListener;
	}

	public static DialogInformation build(Context context, OnClickListener onClickListener) {
		return new DialogInformation(context, onClickListener);
	}

	public static DialogInformation build(Context context) {
		return new DialogInformation(context, null);
	}

	public void show(String text, int icon) {
		final AlertDialog alertDialog = new AlertDialog.Builder(context).create();

		// Finally, show the dialog
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View view = layoutInflater.inflate(R.layout.dialog_information, null);
		TextView textMessage = (TextView) view.findViewById(R.id.textMessage);
		ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
		Button btnOk = (Button) view.findViewById(R.id.btnOk);

		switch (icon) {
		case ICON_INFO:
			imageView.setImageResource(R.drawable.img_popup_info_icon);
			break;
		case ICON_ALERT:
			imageView.setImageResource(R.drawable.img_popup_alert_icon);
			break;
		case ICON_OK:
			imageView.setImageResource(R.drawable.img_popup_ok_icon);
			break;
		}

		// Set text and font
		textMessage.setText(text);
		textMessage.setTypeface(Imin.fontLight);
		btnOk.setTypeface(Imin.fontLight);

		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				isShown = false;
				alertDialog.dismiss();

				if (onClickListener != null) {
					onClickListener.onClick(view);
				}
			}
		});
		isShown = true;

		alertDialog.setCancelable(false);
		alertDialog.setView(view);
		alertDialog.show();
	}

	public boolean isShown() {
		return isShown;
	}
}
