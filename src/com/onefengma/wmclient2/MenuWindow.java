package com.onefengma.wmclient2;

import java.util.List;

import com.wmclient.clientsdk.DebugLogger;
import com.wmclient.clientsdk.Utils;
import com.wmclient.clientsdk.WMChannelInfo;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class MenuWindow extends PopupWindow {
	
	private ListView listView;
	private OnItemClickListener listener;
	private Context context;
	
	public MenuWindow(Context context, WMChannelInfo[] infos, OnItemClickListener listener) {
		this.listener = listener;
		this.context = context;
		
		listView = (ListView)LayoutInflater.from(context).inflate(R.layout.menu_list_view, null);
		listView.setAdapter(new MenuAdapter(infos));
		listView.setOnItemClickListener(listener);
		
		setWidth(ViewUtils.dipToPix(context.getResources().getDisplayMetrics(), 120));
		setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		setContentView(listView);
		setFocusable(true);
		setBackgroundDrawable(new BitmapDrawable());
		setOutsideTouchable(true);		
	}
	
	@Override
	public void showAsDropDown(View anchor) {
		super.showAsDropDown(anchor);
		DebugLogger.i(":::" + listView.getHeight());
	}
	
	private class MenuAdapter extends BaseAdapter {
		
		private WMChannelInfo[] infos;
		
		public MenuAdapter(WMChannelInfo[] infos) {
			this.infos = infos;
		}
		
		@Override
		public int getCount() {
			return infos.length;
		}

		@Override
		public WMChannelInfo getItem(int position) {
			return infos[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_list_item, parent, false);
			}
			String channelNameString = getItem(position).getChannelName();
		    ((TextView)convertView).setText(Utils.isEmpty(channelNameString) ? "通道" + (position+1) : channelNameString);
			return convertView;
		}
		
	}
	
}
