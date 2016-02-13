package com.imin.widgets;

import android.content.Context;
import android.widget.Toast;

public class SingleToast {
	private static Toast toast;

	public static void show(Context context, String text, int duration) {
		if (toast != null)
			toast.cancel();
		toast = Toast.makeText(context, text, duration);
		toast.show();
	}
}
