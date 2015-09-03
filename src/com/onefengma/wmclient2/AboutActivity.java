package com.onefengma.wmclient2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class AboutActivity extends BaseBackActivity {
	
	public static void startFrom(Activity activity) {
		activity.startActivity(new Intent(activity, AboutActivity.class));
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		setTitle(R.string.title_activity_about);
	}
}
