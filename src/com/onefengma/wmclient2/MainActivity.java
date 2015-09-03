package com.onefengma.wmclient2;


import java.util.ArrayList;
import java.util.List;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;
import com.wmclient.clientsdk.Constants;
import com.wmclient.clientsdk.WMDeviceInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

public class MainActivity extends MenuBaseActivity implements  OnRefreshListener<ExpandableListView> {
	
	private PullToRefreshExpandableListView listView;
	private DeviceAdatper adapter;
	
	public static void startFrom(Activity activity) {
		activity.startActivity(new Intent(activity, MainActivity.class));
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setTitle(R.string.title_activity_devices);
		listView = (PullToRefreshExpandableListView) findViewById(R.id.list);
		
		StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		// check if login
		if(!ClientApp.getInstance().requestAddress()) {
			LoginActivity.startFrom(this);
			finish();
			return;
		}
		
		if (ClientApp.getInstance().isAccountExisted() && ClientApp.getInstance().login()) {
			nameView.setText(ClientApp.getInstance().getUserName());
		} else {
			LoginActivity.startFrom(this);
			finish();
			return;
		}
		
		List<WMDeviceInfo> deviceList = new ArrayList<WMDeviceInfo>();
		ClientApp.getInstance().GetSdkInterface().getDeviceList(deviceList);
		
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

	@Override
	public void onRefresh(PullToRefreshBase<ExpandableListView> refreshView) {
		List<WMDeviceInfo> deviceList = new ArrayList<WMDeviceInfo>();
		ClientApp.getInstance().GetSdkInterface().getDeviceList(deviceList);
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
