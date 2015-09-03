package com.onefengma.wmclient2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ReplayActivity extends MenuBaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_replay);
		setTitle(R.string.title_activity_replay);
	}
	
	public static void startFrom(Activity activity) {
		activity.startActivity(new Intent(activity, ReplayActivity.class));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.add_device, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_add_devices) {
			ChannelListActivity.startFrom(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
}
