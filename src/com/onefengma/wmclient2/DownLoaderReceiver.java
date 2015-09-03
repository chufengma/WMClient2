package com.onefengma.wmclient2;

import java.io.File;

import com.wmclient.clientsdk.DebugLogger;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

public class DownLoaderReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(
				DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
			long id = intent.getLongExtra(
					DownloadManager.EXTRA_DOWNLOAD_ID, -1);
			DebugLogger.i("down load commplete:" + id);
			String apkFilePath = new StringBuilder(Environment
                    .getExternalStorageDirectory().getAbsolutePath())
                    .append(File.separator)
                    .append(UpdateManager.DOWNLOAD_FOLDER_NAME)
                    .append(File.separator).append(UpdateManager.getInstance(context).getFileName())
                    .toString();
            install(context, apkFilePath);
		} 
	}
	
	 public boolean install(Context context, String filePath) {
	        Intent i = new Intent(Intent.ACTION_VIEW);
	        File file = new File(filePath);
	        if (file != null && file.length() > 0 && file.exists() && file.isFile()) {
	            i.setDataAndType(Uri.parse("file://" + filePath),
	                    "application/vnd.android.package-archive");
	            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            context.startActivity(i);
	            return true;
	        }
	        return false;
	   }

}
