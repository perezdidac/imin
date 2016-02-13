package com.imin.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ResizableImageView extends ImageView {
	
	public ResizableImageView(Context context) {
		super(context);
	}

	public ResizableImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ResizableImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Drawable drawable = getDrawable();
	
		if (drawable != null) {
			int width = MeasureSpec.getSize(widthMeasureSpec);
			int diw = drawable.getIntrinsicWidth();
			if (diw > 0) {
				int height = width * drawable.getIntrinsicHeight() / diw;
				setMeasuredDimension(width, height);
				return;
			}
		}
		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
	}
	
}
