package com.onefengma.wmclient2;

import com.wmclient.clientsdk.Utils;
import com.wmclient.clientsdk.WMChannelInfo;

import android.util.DisplayMetrics;

public class ViewUtils {
	
    public static int dipToPix(DisplayMetrics metrics, int dip) {
        int paddingPixels = (int) ((dip * (metrics.density)) + .5);
        return paddingPixels;
    }
    
    public static String genereateChannelName(WMChannelInfo info) {
    	if (info == null) {
    		return "";
    	}
    	if (Utils.isEmpty(info.getChannelName())) {
    		return "通道" + info.getChannelId();
    	} else {
    		return info.getChannelName();
    	}
    }
    
}
