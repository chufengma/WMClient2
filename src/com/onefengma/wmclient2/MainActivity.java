package com.onefengma.wmclient2;


import java.util.ArrayList;
import java.util.List;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;
import com.wmclient.clientsdk.Constants;
import com.wmclient.clientsdk.DebugLogger;
import com.wmclient.clientsdk.WMDeviceInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

public class MainActivity extends MenuBaseActivity implements  OnRefreshListener<ExpandableListView> {
	
	private static final String EXTRA_FIRST = "first";
	private PullToRefreshExpandableListView listView;
	private DeviceAdatper adapter;
	private List<WMDeviceInfo> deviceList;
	
	public static void startFrom(Activity activity) {
		activity.startActivity(new Intent(activity, MainActivity.class));
	}
	
	public static void startFromMenu(Activity activity) {
		Intent intent = new Intent(activity, MainActivity.class);
		intent.putExtra(EXTRA_FIRST, false);
		activity.startActivity(intent);
	} 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setTitle(R.string.title_activity_devices);
		listView = (PullToRefreshExpandableListView) findViewById(R.id.list);
		
		if (ClientApp.getInstance().needLogin()) {
			startLogin();
			finish();
			return;
		} 
		
		checkLogin();
		
		deviceList = new ArrayList<WMDeviceInfo>();
		adapter = new DeviceAdatper(deviceList, this);
		listView.getRefreshableView().setAdapter(adapter);
		listView.getRefreshableView().setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				WMDeviceInfo device = adapter.getGroup(groupPosition);
				if (device.getStatus() == Constants.DeviceStatus_Offline) {
					ContextToast.show(MainActivity.this, "设备不在线，无法实时预览！", Toast.LENGTH_SHORT);
					return true;
				}
				RealTimePreviewActivity.startFrom(MainActivity.this, device, childPosition);
				return true;
			}
		});
		
		listView.setOnRefreshListener(this);
	}
	
	public void checkLogin() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// check if login
				if(!ClientApp.getInstance().requestAddress()) {
					startLogin();
				}
				if (ClientApp.getInstance().login()) {
					deviceGoon();
				} else {
					startLogin();
				}
			}
		}).start();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		DebugLogger.i("new intent");
		mDrawer.closeMenu(false);
	}
	
	public void deviceGoon() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				nameView.setText(ClientApp.getInstance().getUserName());
				loadDeviceList();
			}
		});
	}
	
	public void startLogin() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				LoginActivity.startFrom(MainActivity.this);
				finish();
				return;
			}
		});
	}
	
	public void loadDeviceList() {
		final boolean first = getIntent().getBooleanExtra(EXTRA_FIRST, true);
		new Thread(new Runnable() {
			@Override
			public void run() {
				ClientApp.getInstance().GetSdkInterface().getDeviceList(deviceList, first);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						adapter.setInfos(deviceList);
					}
				});
			}
		}).start();
	}
	
	@Override
	public void onRefresh(PullToRefreshBase<ExpandableListView> refreshView) {
		deviceList = new ArrayList<WMDeviceInfo>();
		ClientApp.getInstance().GetSdkInterface().getDeviceList(deviceList, true);
		adapter.setInfos(deviceList);
		listView.onRefreshComplete();
	}
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.add_device, menu);
//		return true;
//	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		
		if (id == R.id.action_add_devices) {
			AddDeviceActivity.startFrom(this);
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
}
