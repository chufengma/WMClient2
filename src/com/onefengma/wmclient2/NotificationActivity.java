package com.onefengma.wmclient2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class NotificationActivity extends MenuBaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notification);
		setTitle(R.string.title_activity_notification);
	}
	
	public static void startFrom(Activity activity) {
		activity.startActivity(new Intent(activity, NotificationActivity.class));
	}
}
