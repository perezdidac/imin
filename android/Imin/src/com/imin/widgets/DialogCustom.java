package com.imin.widgets;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;

public class DialogCustom extends Dialog {
	public DialogCustom(Context context, View view) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
	}
}
