package com.imin.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.imin.Imin;
import com.imin.R;

public class DialogProgress {

	private Context context;
	private AlertDialog alertDialog;
	private boolean isShown;

	private DialogProgress(Context context) {
		this.context = context;
	}

	public static DialogProgress build(Context context) {
		return new DialogProgress(context);
	}

	public void show(String text) {
		alertDialog = new AlertDialog.Builder(context).create();

		// Finally, show the dialog
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View view = layoutInflater.inflate(R.layout.dialog_progress, null);
		TextView textMessage = (TextView) view.findViewById(R.id.textMessage);
		ImageView imageProgress = (ImageView) view.findViewById(R.id.imageProgress);

		// Set text and font
		textMessage.setTypeface(Imin.fontLight);
		textMessage.setText(text);
		
		imageProgress.setAnimation(AnimationUtils.loadAnimation(context, R.anim.rotate_indefinitely));

		// Save state for restoring after view repaint
		alertDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				isShown = false;
			}
		});
		isShown = true;
		
		alertDialog.setCancelable(false);
		alertDialog.setView(view);
		alertDialog.show();
	}

	public void dismiss() {
		if (alertDialog != null && alertDialog.isShowing()) {
			alertDialog.dismiss();
		}
	}

	public boolean isShown() {
		return isShown;
	}
}
