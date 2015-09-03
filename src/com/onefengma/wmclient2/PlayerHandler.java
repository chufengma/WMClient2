package com.onefengma.wmclient2;

import android.os.Handler;
import android.os.Message;

public class PlayerHandler extends Handler {

	public static final int START = 0;
	public static final int FAILED = 1;
	public static final int SUCCESS = 2;
	
	public void send(int status, int deviceId, int channelId, int playId, int id) {
		Message msg = new Message();
		msg.what = status;
		msg.arg1 = id;
		msg.arg2 = channelId;
		msg.obj = playId;
		sendMessage(msg);
	}

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case START:
			onStart(msg.arg1, msg.arg2);
			break;
		case FAILED:
			onFailed(msg.arg1, msg.arg2, (Integer) (msg.obj));
			break;
		case SUCCESS:
			onSuccess(msg.arg1, msg.arg2, (Integer) (msg.obj));
		default:
			break;
		}
	}

	public void onStart(int id, int channelId) {
	}

	public void onFailed(int id, int channelId, int playId) {
	}

	public void onSuccess(int id, int channelId, int playId) {
	}
}

