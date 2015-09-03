package com.onefengma.wmclient2;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class MaxHeightListView extends ListView {

	public MaxHeightListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MaxHeightListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MaxHeightListView(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		// set your custom height. AT_MOST means it can be as tall as needed,
		// up to the specified size.

		int height = MeasureSpec.makeMeasureSpec(800, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, height);
	}
}
