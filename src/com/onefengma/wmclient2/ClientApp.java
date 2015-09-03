package com.onefengma.wmclient2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.wmclient.clientsdk.Constants;
import com.wmclient.clientsdk.DebugLogger;
import com.wmclient.clientsdk.Utils;
import com.wmclient.clientsdk.WMClientSdk;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Process;

public class ClientApp extends Application {
	
	public static final String HOST = "http://www.vomont.com/veye/";
	
	private static final String spFileName = "wmclientsp";	
	private static final String SP_USER_NAME = "username";
	private static final String SP_USER_PWD = "password";
	private static final String SP_USER_NEED_LOGIN = "need_login";
	private static final String SP_CLOUD_LENGTH = "cloud_length";
	private static final String SP_PATH = "path";
	private static final String SP_VIDEO_PATH = "path";
	
	private static ClientApp instance;	
	private List<Activity> activities = new ArrayList<Activity>();
	public String serverAddress = "221.7.12.157";
    public int serverPort = 9001;	
    private String userName;
    private String password;
    
    private SharedPreferences sp;
    
    private boolean bLogin = false;
	
    static public ClientApp getInstance(){
        return instance;
    }	
    
    public WMClientSdk GetSdkInterface() {
    	return WMClientSdk.getInstance();
    }
    
    public boolean requestAddress() {
    	String urlAddress = HOST + "addr.htm";
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpgets = new HttpGet(urlAddress);
		try {
			HttpResponse response = httpclient.execute(httpgets);
		    String jsonStr = EntityUtils.toString(response.getEntity());
		    JSONObject json = new JSONObject(jsonStr);
		    String ip = json.getString("ip");
		    int port = Integer.parseInt(json.getString("port"));
			DebugLogger.i("ip:" + ip + "," + "port:" + port);
		    serverPort = port;
		    serverAddress = ip;
		    return true;
		} catch (Exception e) {
			e.printStackTrace();
			DebugLogger.i("get http wrong!");
			return false;
		}
    }
    
    public void addActivity(Activity activity){
        activities.add(activity);
    } 
    
    public void finishActivity(Activity activity){
        if (activity != null) {
            this.activities.remove(activity);
            
            activity.finish();
            activity = null;
        }
    }  
    
    public boolean hasLogin() {
    	return bLogin;
    }
    
    public void setHasLogin(boolean hasLogin) {
    	bLogin = hasLogin;
    	sp.edit().putBoolean(SP_USER_NEED_LOGIN, false).commit();
    }
    
//    public void storeServerInfo(String serverIp, int port) {
//        SharedPreferences.Editor editor = sp.edit();
//        
//        serverAddress = serverIp;
//        editor.putString("ip", serverAddress);
//        serverPort = port;
//        editor.putInt("port", serverPort);
//        
//        editor.commit();    	
//    }
//      
//    public void initServerInfo() {
//        try {
//        	serverAddress = sp.getString("ip", serverAddress);
//        	serverPort = sp.getInt("port", 9001);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }   	
//    }
    
    public void storeAccountInfo(String userName, String password) {
        SharedPreferences.Editor editor = sp.edit();
        this.userName = userName;
        editor.putString(SP_USER_NAME, userName);
        this.password = password;
        editor.putString(SP_USER_PWD, password);
        editor.commit();    	
    }
    
    public void setNeedLogin() {
    	sp.edit().putBoolean(SP_USER_NEED_LOGIN, true).commit();
    }
    
    public boolean needLogin() {
    	return sp.getBoolean(SP_USER_NEED_LOGIN, true) || !hasLogin();
    }
    
    public boolean isAccountExisted() {
    	if (Utils.isEmpty(userName) || Utils.isEmpty(password)) {
    		return false;
    	} else {
    		return true;
    	}
    }
    
    public boolean login() {
    	GetSdkInterface().logout();
    	int ret = GetSdkInterface().login(userName, password, serverAddress, serverPort);
    	if (ret == Constants.success) {
    		setHasLogin(true);
    		return true;
    	}
    	return false;
    }
    
    public int login(String userName, String password) {
    	GetSdkInterface().logout();
    	int ret = GetSdkInterface().login(userName, password, serverAddress, serverPort);
    	return ret;
    }
    
    public void initAccountInfo() {
        try {
        	this.userName = sp.getString(SP_USER_NAME, "");
        	this.password = sp.getString(SP_USER_PWD, "");
        } catch (Exception e) {
            e.printStackTrace();
        }      	
    }
    
    public String getServerAddress() {
    	return serverAddress;
    }
    
    public int getServerPort() {
    	return serverPort;
    }
    
    public String getUserName() {
    	return userName;
    }
    
    public String getPassword() {
    	return password;
    }
    
    public int getCloudLength() {
    	return sp.getInt(SP_CLOUD_LENGTH, 2);
    }
    
    public String getCaptureImagePath() {
    	return sp.getString(SP_PATH, Environment.getExternalStorageDirectory()
				+ File.separator + "wmclient-photo");
    }
    
    public String getVideoPath() {
    	return sp.getString(SP_VIDEO_PATH, Environment.getExternalStorageDirectory()
				+ File.separator + "wmclient-file");
    }
        
    public void saveCloudLength(int length) {
    	sp.edit().putInt(SP_CLOUD_LENGTH, length).commit();
    }
    
    public void saveCaptureImagePath(String path) {
    	sp.edit().putString(SP_PATH, path).commit();
    }
    
    public void saveVideoImagePath(String path) {
    	sp.edit().putString(SP_VIDEO_PATH, path).commit();
    }
    
    @Override
    public void onCreate() {
        instance = this;
    	sp = instance.getSharedPreferences(spFileName, Context.MODE_PRIVATE);
        instance.initAccountInfo();
    	
    	if(instance.GetSdkInterface().init(63) != 0) {
    		return;
    	}
    } 
    
    public void exit() {
    	for(Activity activity : activities) {
    		activity.finish();
    	}
    }
}
