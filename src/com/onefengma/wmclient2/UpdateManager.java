package com.onefengma.wmclient2;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import com.wmclient.clientsdk.DebugLogger;

public class UpdateManager {

	public static final String DOWNLOAD_FOLDER_NAME = "veye";
	private Context context;
	private static long DOWN_ID;
	private static UpdateManager instance;
	
	private static String VSESION;
	private static String DETAILS;
	private static String FILE_NAME = "veye";;
	
	public static UpdateManager getInstance(Context context) {
		if (instance == null) {
			instance = new UpdateManager(context.getApplicationContext());
		}
		return instance;
	} 
	
	private UpdateManager(Context context) {
		this.context = context;
	}
	
	public long getDownId() {
		return DOWN_ID;
	}
	
	public boolean checkUpdate() {
		String urlAddress = ClientApp.HOST + "version.htm";
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpgets = new HttpGet(urlAddress);
		try {
			HttpResponse response = httpclient.execute(httpgets);
			String jsonStr = new String(EntityUtils.toByteArray(response.getEntity()), "utf-8");
			DebugLogger.i("json:" + jsonStr);
			JSONObject json = new JSONObject(jsonStr);
			VSESION = json.getString("version");
			DETAILS = json.getString("details");
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
			DebugLogger.i("code number:" + info.versionName);
			if (!info.versionName.equals(VSESION)) {
				DebugLogger.i("find new version:" + VSESION);
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			DebugLogger.i("get http wrong!");
			return false;
		}
	}

	public void downLoad(String version) {
		FILE_NAME = FILE_NAME + version + ".apk";
		String url = ClientApp.HOST + FILE_NAME;
		DownloadManager manager = (DownloadManager) context
				.getSystemService(Context.DOWNLOAD_SERVICE);
		DownloadManager.Request request = new Request(Uri.parse(url));
		request.setDescription("正在下载中...");
		request.setDestinationInExternalPublicDir(DOWNLOAD_FOLDER_NAME, FILE_NAME);
		DOWN_ID = manager.enqueue(request);
	}

	public String getVSESION() {
		return VSESION;
	}

	public String getDETAILS() {
		return DETAILS;
	}
	
	public String getFileName() {
		return FILE_NAME;
	}
}
