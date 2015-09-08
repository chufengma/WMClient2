package com.onefengma.wmclient2;

import com.lib.decoder.VideoDecoder;
import com.wmclient.clientsdk.Constants;
import com.wmclient.clientsdk.DebugLogger;
import com.wmclient.clientsdk.StreamPlayer;
import com.wmclient.clientsdk.WMChannelInfo;
import com.wmclient.clientsdk.WMDeviceInfo;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

public class SurfaceViewPager extends PagerAdapter {

	private WMChannelInfo[] channelInfos;
	private WMDeviceInfo deviceInfo;
	private Context context;
	private SparseArray<RealStreamPlayer> players;
	private SparseArray<View> cacheViews;

	private static final int mask = 0x01000000;

	private int currentPosition = -1;

	public SurfaceViewPager(Context context, WMDeviceInfo deviceInfo) {
		this.deviceInfo = deviceInfo;
		this.channelInfos = deviceInfo.getChannelArr();
		this.context = context;
		players = new SparseArray<RealStreamPlayer>();
		cacheViews = new SparseArray<View>();
	}

	@Override
	public int getCount() {
		return channelInfos.length;
	}

	@Override
	public boolean isViewFromObject(View view, Object obj) {
		return view == obj;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// stop(position);
		// container.removeView((View) object);
	}

	public void stooCurrentPosition() {
		// stop pre
		if (currentPosition != -1) {
			if (players.get(currentPosition) != null) {
				stop(currentPosition);
			}
		}
	}

	public void setCurrentPosition(int position) {
		// stop pre
		if (currentPosition != -1) {
			if (players.get(currentPosition) != null) {
				stop(currentPosition);
			}
		}

		// start new
		View view = cacheViews.get(position);
		if (view == null) {
			DebugLogger.i("view do not cached!");
			this.currentPosition = position;
			return;
		}
		Object surface = view.getTag();
		play(surface, position);
		this.currentPosition = position;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View view = cacheViews.get(position);
		if (view == null) {
			view = LayoutInflater.from(context).inflate(R.layout.surface_view,
					container, false);
			cacheViews.put(position, view);
			SurfaceView surface = (SurfaceView) view.findViewById(R.id.surface);
			if (Constants.DEVICE_TYPE_XM_DEV == deviceInfo.getDevType()) {
				DebugLogger.i("XM device");
				SurfaceView glSurfaceView = new VideoDecoder(context);
				glSurfaceView.setLayoutParams(surface.getLayoutParams());
				((ViewGroup) view).removeViewAt(0);
				((ViewGroup) view).addView(glSurfaceView, 0);
				view.setTag(glSurfaceView);
			} else {
				view.setTag(surface.getHolder());
			}
			container.addView(view);
		}
		changeLoadingStatus(position, View.VISIBLE);
		return view;
	}

	private void play(final Object surface, final int position) {
		final StreamPlayer streamPlayer = ClientApp.getInstance()
				.GetSdkInterface()
				.CreatePlayer(deviceInfo.getDevType(), surface);
		streamPlayer
				.setOnStreamPlayerListener(new StreamPlayer.OnStreamPlayerListener() {
					@Override
					public void onSuccess() {
						changeLoadingStatus(position, View.GONE);
					}

					@Override
					public void onStart() {
						changeLoadingStatus(position, View.VISIBLE);
					}

					@Override
					public void onFailed() {
						changeLoadingStatus(position, View.GONE);
					}
				});

		RealStreamPlayer realStreamPlayer = players.get(position);
		if (realStreamPlayer == null) {
			realStreamPlayer = new RealStreamPlayer(new Handler());
			players.put(position, realStreamPlayer);
		}
		// int delay = position * 500;
		changeLoadingStatus(position, View.VISIBLE);
		realStreamPlayer.startPlay(deviceInfo.getDevId(), mask
				+ channelInfos[position].getChannelId(), streamPlayer);
		// new PlayerLine(delay, delay).start(deviceInfo.getDevId(), mask
		// + channelInfos[position].getChannelId(), streamPlayer,
		// realStreamPlayer);

		((RealTimePreviewActivity) context).startBriefThread(realStreamPlayer);
	}

	public void startRecord(int position) {
		cacheViews.get(position).findViewById(R.id.record)
				.setVisibility(View.VISIBLE);
	}

	public void stopRecord(int position) {
		cacheViews.get(position).findViewById(R.id.record)
				.setVisibility(View.GONE);
	}

	private void changeLoadingStatus(final int position, final int status) {
		cacheViews.get(position).postDelayed(new Runnable() {
			@Override
			public void run() {
				cacheViews.get(position).findViewById(R.id.loading)
						.setVisibility(status);
			}
		}, 2000);
	}

	public class PlayerLine extends CountDownTimer {
		int deviceId;
		int channelId;
		StreamPlayer player;
		RealStreamPlayer playerWraPlayer;

		public PlayerLine(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		public void start(int deviceId, int channelId, StreamPlayer player,
				RealStreamPlayer playerWraPlayer) {
			this.channelId = channelId;
			this.deviceId = deviceId;
			this.player = player;
			this.playerWraPlayer = playerWraPlayer;
			start();
		}

		@Override
		public void onTick(long millisUntilFinished) {
		}

		@Override
		public void onFinish() {
			playerWraPlayer.startPlay(deviceId, channelId, player);
		}

	}

	public RealStreamPlayer getPlayer(int position) {
		return players.get(position);
	}

	private void stop(int position) {
		players.get(position).stopPlay();
	}

	public void clear() {
		for (int i = 0; i < players.size(); i++) {
			players.get(players.keyAt(i)).stopPlay();
		}
	}

	// private class PlaLine {
	// LinkedList<PlayParams> list = new LinkedList<PlayParams>();
	// CountDownTimer timer = new CountDownTimer(100000, 500) {
	//
	// @Override
	// public void onTick(long millisUntilFinished) {
	// players.get(position);
	// }
	//
	// @Override
	// public void onFinish() {
	// }
	// };
	//
	// public void addPlay(int deviceId, int channelId, StreamPlayer player,
	// RealStreamPlayer playerWraPlayer) {
	// PlayParams p = new PlayParams();
	// p.playerWraPlayer = playerWraPlayer;
	// p.deviceId = deviceId;
	// p.channelId = channelId;
	// p.player = player;
	// list.add(p);
	// }
	//
	// public void start() {
	//
	// }
	// }
	//
	// public static class PlayParams {
	// int deviceId;
	// int channelId;
	// StreamPlayer player;
	// RealStreamPlayer playerWraPlayer;
	// }

}
