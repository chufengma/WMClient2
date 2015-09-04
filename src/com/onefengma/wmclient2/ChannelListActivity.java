
package com.onefengma.wmclient2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;
import com.wmclient.clientsdk.WMDeviceInfo;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.test.ActivityUnitTestCase;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class ChannelListActivity extends BaseBackActivity implements OnDateSetListener, OnTimeSetListener{
	
	private PullToRefreshExpandableListView listView;
	private SelectDeviceAdatper adapter;
	
	private TextView startTimeTextView;
	private TextView endTimeTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_channel_list);
		setTitle(R.string.title_activity_channel_list);
		
		startTimeTextView = (TextView) findViewById(R.id.time1);
		endTimeTextView = (TextView) findViewById(R.id.time2);
		
		listView = (PullToRefreshExpandableListView) findViewById(R.id.list);
		List<WMDeviceInfo> deviceList = new ArrayList<WMDeviceInfo>();
		ClientApp.getInstance().GetSdkInterface().getDeviceList(deviceList);
		
		adapter = new SelectDeviceAdatper(deviceList, this);
		listView.getRefreshableView().setAdapter(adapter);
	}
	
    public static void startFrom(Activity activity) {
    	activity.startActivity(new Intent(activity, ChannelListActivity.class));
    }
    
    public void onTimeClick(View view) {
    	TextView timeTextView;
    	final StringBuilder time = new StringBuilder();
    	if (view.getId() == R.id.time1) {
    		timeTextView = startTimeTextView;
    		time.append("开始时间：");
    	} else {
    		timeTextView = endTimeTextView;
    		time.append("结束时间：");
    	}
    	final TimeHolder holder = new TimeHolder();
    	final TextView timeView = timeTextView;
    	DatePickerDialog dialog = new DatePickerDialog(this, new OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				holder.year = year;
				holder.month = monthOfYear + 1;
				holder.day = dayOfMonth;
				TimePickerDialog timeDialog = new TimePickerDialog(ChannelListActivity.this, new OnTimeSetListener() {
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
						holder.hour = hourOfDay;
						holder.minitue = minute;
						timeView.setText(time.toString() + holder.toString());
					}
				} , Calendar.getInstance().get(Calendar.HOUR_OF_DAY),  Calendar.getInstance().get(Calendar.MINUTE), true);
				timeDialog.show();
			}
		}, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
    	dialog.show();
    }
    
    public void onPlayClick(View view) {
    	ContextToast.show(this, "还没有视频可供播放", Toast.LENGTH_SHORT);
    }

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		
	}
    
	class TimeHolder {
		int year;
		int month;
		int day;
		int hour;
		int minitue;
		
		@Override
		public String toString() {
			return year + "-" + month + "-" + day + "  " + hour + ":" + (minitue/10 > 1 ? (minitue + "") : ("0" + minitue));
		}
	}
	
}
