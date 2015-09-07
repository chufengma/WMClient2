package com.onefengma.wmclient2;

import java.util.List;

import com.wmclient.clientsdk.Constants;
import com.wmclient.clientsdk.Utils;
import com.wmclient.clientsdk.WMChannelInfo;
import com.wmclient.clientsdk.WMDeviceInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DeviceAdatper extends BaseExpandableListAdapter {
	
	private List<WMDeviceInfo> infos;
	private LayoutInflater inflater;
	
    public DeviceAdatper(List<WMDeviceInfo> infos, Context context) {
    	this.infos = infos;
        inflater = LayoutInflater.from(context);
    }
	
    public void setInfos(List<WMDeviceInfo> infos) {
    	this.infos = infos;
    	notifyDataSetChanged();
    }
    
	@Override
	public int getGroupCount() {
		return infos.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return infos.get(groupPosition).getChannelArr().length;
	}

	@Override
	public WMDeviceInfo getGroup(int groupPosition) {
		return infos.get(groupPosition);
	}

	@Override
	public WMChannelInfo getChild(int groupPosition, int childPosition) {
		return infos.get(groupPosition).getChannelArr()[childPosition];
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return groupPosition * 100 + childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		TextView deviceName;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.device_layout, parent, false);
			deviceName = (TextView) convertView.findViewById(R.id.device_name);
			convertView.setTag(deviceName);
		} else {
			deviceName = (TextView) convertView.getTag();
		}
		convertView.findViewById(R.id.drop_down).setSelected(isExpanded);
		deviceName.setText(infos.get(groupPosition).getDevName());
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.device_channel_item, parent, false);
		}
		WMDeviceInfo deviceInfo = getGroup(groupPosition);
		WMChannelInfo channel = getChild(groupPosition, childPosition);
		String channelName = Utils.isEmpty(channel.getChannelName()) ? "通道" + (childPosition + 1) : channel.getChannelName(); 
		((TextView)(convertView.findViewById(R.id.name))).setText(channelName);
		((ImageView)(convertView.findViewById(R.id.status))).setSelected(deviceInfo.getStatus() == Constants.DeviceStatus_Online);
		((TextView)(convertView.findViewById(R.id.name))).setSelected(deviceInfo.getStatus() == Constants.DeviceStatus_Offline);
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
