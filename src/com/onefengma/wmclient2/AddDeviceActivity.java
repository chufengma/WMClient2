package com.onefengma.wmclient2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class AddDeviceActivity extends BaseBackActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_device);
		setTitle(R.string.title_activity_add_device);
	}
	
	
	public static void startFrom(Activity activity) {
		activity.startActivity(new Intent(activity, AddDeviceActivity.class));
	}
	
}
