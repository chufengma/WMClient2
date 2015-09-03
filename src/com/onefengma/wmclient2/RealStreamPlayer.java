package com.onefengma.wmclient2;

import java.io.File;
import java.text.SimpleDateFormat;

import com.wmclient.clientsdk.Constants;
import com.wmclient.clientsdk.DebugLogger;
import com.wmclient.clientsdk.StreamPlayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class RealStreamPlayer {
	private StreamPlayer mPlayer;
	private int playerId = Constants.WMPLAYERID_INVALID;
	private boolean isRecording;
	private boolean isStoped = true;
	private PlayRunnable pendingPlayRunnable;
	
	public RealStreamPlayer(Handler handler) {
	}

	public void startPlay(final int deviceId, final int channelId,
			final StreamPlayer player) {
		mPlayer = player;
		if (!isStoped) {
			DebugLogger.i("is not stoped, add pending runnable");
			pendingPlayRunnable = new PlayRunnable(deviceId, channelId, player);
			return;
		}
		new Thread(new PlayRunnable(deviceId, channelId, player)).start();
	}
	
	class PlayRunnable implements Runnable {
		
		int deviceId;
		int channelId;
	    StreamPlayer player;
		
	    public PlayRunnable(int deviceId, int channelId, StreamPlayer player) {
	    	this.deviceId = deviceId;
	    	this.channelId = channelId;
	    	this.player = player;
		}
	    
		@Override
		public void run() {
			try {
				playerId = ClientApp.getInstance().GetSdkInterface()
						.startRealPlay(deviceId, channelId, player);
				DebugLogger.i("start play:" + deviceId + ":" + channelId
						+ "  playId:" + playerId + ":" + player);
				if (playerId == Constants.WMPLAYERID_INVALID) {
					return;
				}
				isStoped = false;
				// wait for stop
				synchronized (RealStreamPlayer.this) {
					RealStreamPlayer.this.wait();
				}
				DebugLogger.i("stop in sub thread");
				ClientApp.getInstance().GetSdkInterface()
				 .DestroyPlayer(mPlayer);
				if (playerId != Constants.WMPLAYERID_INVALID)
					ClientApp.getInstance().GetSdkInterface()
							.stopRealPlay(playerId);
				isStoped = true;
				
				if (pendingPlayRunnable != null) {
					DebugLogger.i("pending runnable running");
					new Thread(pendingPlayRunnable).start();
					pendingPlayRunnable = null;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void stopPlay() {
		if (playerId == Constants.WMPLAYERID_INVALID) {
			DebugLogger.i("playId is wrong, just return!" + playerId);
			return;
		}
		synchronized (this) {
			notify();
		}
	}
	
	public boolean hasStoped() {
		return isStoped;
	}

	// public void startPlay(final int deviceId, final int channelId,
	// final StreamPlayer player) {
	// mPlayer = player;
	// playerId = ClientApp.getInstance().GetSdkInterface()
	// .startRealPlay(deviceId, channelId, player);
	// }
	//
	// public void stopPlay() {
	// DebugLogger.i("stop in main thread");
	// ClientApp.getInstance().GetSdkInterface()
	// .DestroyPlayer(mPlayer);
	// if (playerId != Constants.WMPLAYERID_INVALID) {
	// ClientApp.getInstance().GetSdkInterface()
	// .stopRealPlay(playerId);
	// }
	// }

	public void openSound() {
		ClientApp.getInstance().GetSdkInterface().openSound(playerId);
	}

	public void clonseSound() {
		ClientApp.getInstance().GetSdkInterface().closeSound(playerId);
	}

	public void captureImage(Activity context, String deviceName, int channelId) {
		if (playerId == Constants.WMPLAYERID_INVALID) {
			return;
		}

		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			toast(context, "无效的输入!");
			return;
		}

		String directoryName = ClientApp.getInstance().getCaptureImagePath();
		File directory = new File(directoryName);
		if (!directory.exists() && !directory.mkdir()) {
			toast(context, "创建存储目录失败!");
			return;
		}

		SimpleDateFormat sDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd-hh-mm-ss");
		String fileName = directoryName + File.separator + deviceName + "-"
				+ channelId + "-" + sDateFormat.format(new java.util.Date());

		if (Constants.success == ClientApp.getInstance().GetSdkInterface()
				.saveSnapshot(playerId, fileName)) {
			toast(context, "已截图，图片保存路径：" + fileName + ".jpg");
		}
	}

	public void toast(final Activity activity, final String str) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ContextToast.show(activity, str, Toast.LENGTH_SHORT);
			}
		});
	}

	public void recordVedio(Activity context, String deviceName, int channelId) {
		if (playerId == Constants.WMPLAYERID_INVALID) {
			toast(context, "无效的输入!");
			return;
		}

		if (!isRecording) {
			DebugLogger.i("start recording");
			if (!Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				toast(context, "无效的输入!");
				return;
			}

			String directoryName = ClientApp.getInstance().getVideoPath();
			File directory = new File(directoryName);
			if (!directory.exists() && !directory.mkdir()) {
				toast(context, "创建存储目录失败!");
				return;
			}

			SimpleDateFormat sDateFormat = new SimpleDateFormat(
					"MM-dd-hh-mm-ss");
			String fileName = directoryName + File.separator + deviceName + "-"
					+ channelId + "-"
					+ sDateFormat.format(new java.util.Date());

			int nRet = ClientApp.getInstance().GetSdkInterface()
					.startRecord(playerId, fileName);
			if (nRet != Constants.success) {
				toast(context, "开始录像失败!");
				return;
			} else {
				toast(context, "正在开始录像...");
			}
			isRecording = true;
		} else {
			DebugLogger.i("stop recording");
			int nRet = ClientApp.getInstance().GetSdkInterface()
					.stopRecord(playerId);
			if (nRet != Constants.success) {
				toast(context, "停止录像失败!");
				return;
			} else {
				toast(context, "已停止录像");
			}
			isRecording = false;
		}
	}

	public int getPlayTime() {
		return mPlayer.GetPlayTime();
	}

	public long getAllRecvLen() {
		return mPlayer.GetAllRecvLen();
	}

	public int getReate() {
		return mPlayer.GetCodeRate();
	}

}
