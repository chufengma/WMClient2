package com.onefengma.wmclient2;

import com.wmclient.clientsdk.Constants;
import com.wmclient.clientsdk.DebugLogger;
import com.wmclient.clientsdk.Utils;
import com.wmclient.clientsdk.WMDeviceInfo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar.LayoutParams;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RealTimePreviewActivity extends BaseBackActivity implements
		View.OnTouchListener, OnPageChangeListener , OnItemClickListener {

	public static final String EXTRA_DEVICE = "device";
	public static final String EXTRA_CHANNEL_INDEX = "channel_index";

	private WMDeviceInfo deviceInfo;
	private int ptzSpeed;
	
	private View surfacePanel;
	private View surfaceMaskView;
	private View lanPanelView;
	private ViewPager viewPager;
	private SurfaceViewPager surfaceViewPager;

	private TextView channelName;
	private View actionsPanel;
	private View mainPanel;
	private DisplayMetrics displayMetrics;

	private View leftIndicater;
	private View rightIndicater;
	
	private View cloudPanel;
	private View cloudButton;
	private View cloudLeft;
	private View cloudRight;
	private View cloudTop;
	private View cloudBottom;
	private View cloudBig;
	private View cloudSmall;

	private ImageView imageView;
	private ImageView videoView;
	private ImageView recordVoiceView;
	private ImageView voiceForbidenView;
	
	private int mOritetation;
	private GestureDetector gestureDetector;
	
	private MenuWindow window;
	
	private TextView flowView;
	private Thread briefThread;
	private View brifPanel;
	private BriefRunnable briefRunnable;
	private boolean stopThread = false;
	
	public static void startFrom(Activity activity, WMDeviceInfo device,
			int channelIndex) {
		Intent intent = new Intent(activity, RealTimePreviewActivity.class);
		intent.putExtra(EXTRA_DEVICE, device);
		intent.putExtra(EXTRA_CHANNEL_INDEX, channelIndex);
		activity.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		deviceInfo = (WMDeviceInfo) getIntent().getSerializableExtra(
				EXTRA_DEVICE);
		displayMetrics = getResources().getDisplayMetrics();

		setTitle(R.string.title_activity_real_time_preview);
		window = new MenuWindow(this, deviceInfo.getChannelArr(), this);
		ptzSpeed = ClientApp.getInstance().getCloudLength();
	}

	private void initView() {
		mainPanel = findViewById(R.id.main_panel);
		surfacePanel = findViewById(R.id.surface_panel);
		surfaceMaskView = findViewById(R.id.surface_mask);
		channelName = (TextView) findViewById(R.id.channel_name);
		viewPager = (ViewPager) findViewById(R.id.view_pager);
		actionsPanel = findViewById(R.id.actions);
		lanPanelView = findViewById(R.id.lan_panel);
		
		brifPanel = findViewById(R.id.brief_panel);
		flowView = (TextView) findViewById(R.id.all_flow);
		
		leftIndicater = findViewById(R.id.left_indicater);
		rightIndicater = findViewById(R.id.right_indicater);
	
		imageView = (ImageView) findViewById(R.id.image);
	    videoView = (ImageView) findViewById(R.id.record_vedio);
	    recordVoiceView = (ImageView) findViewById(R.id.record_voice);
	    voiceForbidenView = (ImageView) findViewById(R.id.voice);
		
		gestureDetector = new GestureDetector(this,
				new GestureDetector.SimpleOnGestureListener() {

					@Override
					public boolean onDown(MotionEvent e) {
						return true;
					}

					// event when double tap occurs
					@Override
					public boolean onDoubleTap(MotionEvent e) {
						changeOrientation();
						return true;
					}
				});
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		setContentView(R.layout.activity_real_time_preview);
		initView();
		surfaceViewPager = new SurfaceViewPager(this, deviceInfo);
		viewPager.setAdapter(surfaceViewPager);
		viewPager.addOnPageChangeListener(this);
		final int index = getIntent().getIntExtra(EXTRA_CHANNEL_INDEX, 0);
		viewPager.setCurrentItem(index);
		setChannelName(index);
		
		viewPager.postDelayed(new Runnable() {
			@Override
			public void run() {
				surfaceViewPager.setCurrentPosition(index);
			}
		}, 1000);
		
		initControll();
		if (getRequestedOrientation() == Configuration.ORIENTATION_LANDSCAPE) {
			changeUiWhenLandscape();
		} else {
			changeUiWhenPortrait();
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		surfaceViewPager.clear();
	}
	
	private void initControll() {
		cloudPanel = findViewById(R.id.cloud_controll);
		cloudButton = findViewById(R.id.cloud);
		cloudLeft = findViewById(R.id.left);
		cloudRight = findViewById(R.id.right);
		cloudTop = findViewById(R.id.top);
		cloudBottom = findViewById(R.id.bottom);
		cloudBig = findViewById(R.id.big);
		cloudSmall = findViewById(R.id.small);
		
		cloudLeft.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {	
				if(event.getAction() == MotionEvent.ACTION_UP) {
					return ptzLeftControl(1);		
				}
				else if(event.getAction() == MotionEvent.ACTION_DOWN) {
					return ptzLeftControl(0);		
				}

				return true;
			}			
		});
		
		cloudRight.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {		
				if(event.getAction() == MotionEvent.ACTION_UP) {
					return ptzRightControl(1);		
				}
				else if(event.getAction() == MotionEvent.ACTION_DOWN) {
					return ptzRightControl(0);		
				}

				return true;				
			}			
		});
		
		cloudTop.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {		
				if(event.getAction() == MotionEvent.ACTION_UP) {
					return ptzTopControl(1);		
				}
				else if(event.getAction() == MotionEvent.ACTION_DOWN) {
					return ptzTopControl(0);		
				}

				return true;		
			}			
		});
		
		cloudBottom.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {	
				if(event.getAction() == MotionEvent.ACTION_UP) {
					return ptzBottomControl(1);		
				}
				else if(event.getAction() == MotionEvent.ACTION_DOWN) {
					return ptzBottomControl(0);		
				}
				return true;						
			}			
		});
		
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		mOritetation = newConfig.orientation;
		if (mOritetation == Configuration.ORIENTATION_LANDSCAPE) {
			changeUiWhenLandscape();
		} else {
			changeUiWhenPortrait();
		}
		super.onConfigurationChanged(newConfig);
	}

	private void changeUiWhenLandscape() {
		// action bar
		getSupportActionBar().hide();
		// lan panel
		LayoutParams titlelp = (LayoutParams) getTitTextView().getLayoutParams();
		titlelp.width = getResources().getDisplayMetrics().widthPixels - ViewUtils.dipToPix(getResources().getDisplayMetrics(), 95);
		getTitTextView().setLayoutParams(titlelp);
		
		lanPanelView.setVisibility(View.VISIBLE);
		actionsPanel.setVisibility(View.GONE);

		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(getResources().getColor(
						R.color.actions_panel_lan)));
		// main panel
		mainPanel.setBackgroundColor(Color.BLACK);
		mainPanel.setPadding(0, 0, 0, 0);

		// actionPanel
		actionsPanel.setBackgroundResource(R.color.actions_panel_lan);
		int padding = displayMetrics.widthPixels
				- ViewUtils.dipToPix(displayMetrics, 80) * 4;
		int defalutPadding = ViewUtils.dipToPix(displayMetrics, 15);
		actionsPanel.setPadding(padding, defalutPadding, defalutPadding,
				defalutPadding);

		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
		surfacePanel.setLayoutParams(lp);
		
		setTitle(ViewUtils.genereateChannelName(deviceInfo.getChannelArr()[viewPager.getCurrentItem()]));
		
		surfaceMaskView.setOnTouchListener(this);
		
		// actions
		imageView.setImageResource(R.drawable.image_bg_lan);
		videoView.setImageResource(R.drawable.record_vedio_bg_lan);
		recordVoiceView.setImageResource(R.drawable.record_voice_bg_lan);
		voiceForbidenView.setImageResource(R.drawable.voice_bg_lan);
		
		brifPanel.setVisibility(View.GONE);
	}
	
	private void changeUiWhenPortrait() {
		// action bar
		getSupportActionBar().show();
		setTitle(R.string.title_activity_real_time_preview);
		LayoutParams titlelp = (LayoutParams) getTitTextView().getLayoutParams();
		titlelp.width = getResources().getDisplayMetrics().widthPixels - ViewUtils.dipToPix(getResources().getDisplayMetrics(), 95);
		getTitTextView().setLayoutParams(titlelp);
		
		lanPanelView.setVisibility(View.GONE);
		actionsPanel.setVisibility(View.VISIBLE);

		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(getResources().getColor(R.color.main)));
		// main panel
		mainPanel.setBackgroundColor(Color.WHITE);
		mainPanel.setPadding(0, ViewUtils.dipToPix(displayMetrics, 45), 0, 0);

		// actionPanel
		actionsPanel.setBackgroundResource(R.color.actions_panel_por);
		int defalutPadding = ViewUtils.dipToPix(displayMetrics, 15);
		actionsPanel.setPadding(defalutPadding, defalutPadding, defalutPadding,
				defalutPadding);

		// surface
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				getResources().getDisplayMetrics().widthPixels, (getResources()
						.getDisplayMetrics().widthPixels / 4) * 3);
		lp.addRule(RelativeLayout.BELOW, R.id.channel_name);
		surfacePanel.setLayoutParams(lp);
		
		// cloud
		cloudButton.setVisibility(View.GONE);
		cloudPanel.setVisibility(View.GONE);

		surfaceMaskView.setOnTouchListener(this);
		
		// actions
		imageView.setImageResource(R.drawable.image_bg);
		videoView.setImageResource(R.drawable.record_vedio_bg);
		recordVoiceView.setImageResource(R.drawable.record_voice_bg);
		voiceForbidenView.setImageResource(R.drawable.voice_bg);

		brifPanel.setVisibility(View.VISIBLE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.real_time_preview, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == android.R.id.home) {
			onBackPressed();
			return true;
		}
		
		if (id == R.id.action_channels) {
			DebugLogger.i("click on channels");
			window.showAsDropDown(findViewById(R.id.anchor));
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
	
	public void showPopupMenu() {
		
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.voice:
			clickOnVoice(view);
			break;
		case R.id.cloud:
			clickOnCloud();
			break;
		case R.id.image:
			clickOnCatchPhone();
			break;
		case R.id.record_vedio:
			clickOnRecordVedio(view);
			break;
		case R.id.record_voice:
			clickOnRecordVoice(view);
			break;
		default:
			break;
		}
	}
	
	private void clickOnCloud() {
		if (cloudPanel.getVisibility() == View.GONE) {
			cloudPanel.setVisibility(View.VISIBLE);
		} else {
			cloudPanel.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onBackPressed() {
		if (mOritetation == Configuration.ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			channelName.postDelayed(new Runnable() {
				@Override
				public void run() {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
				}
			}, 1000);
			return;
		}
		super.onBackPressed();
	}
	
	public void clickOnLanPanel(View view) {
		if (mOritetation == Configuration.ORIENTATION_LANDSCAPE) {
			boolean gone = actionsPanel.getVisibility() == View.GONE;
			actionsPanel.setVisibility(gone ? View.VISIBLE : View.GONE);
			// cloud
			cloudButton.setVisibility(gone ? View.VISIBLE : View.GONE);
			cloudPanel.setVisibility(View.GONE);
			if (gone) {
				getSupportActionBar().show();
			} else {
				getSupportActionBar().hide();
			}
		}
	}

	private void changeOrientation() {
		if (mOritetation == Configuration.ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
	}

	public void clickOnPlay(View view) {
		// TODO
	}

	public void clickOnSurface(View view) {
		if (mOritetation == Configuration.ORIENTATION_PORTRAIT) {

		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		gestureDetector.onTouchEvent(event);
		return true;
	}

	@Override
	protected void onDestroy() {
		surfaceViewPager.clear();
		super.onDestroy();
	}
	
	private void setChannelName(int position) {
		String name = ViewUtils.genereateChannelName(deviceInfo.getChannelArr()[position]);
		channelName.setText(name);
		setIndicaterStatus(position);
		if (mOritetation == Configuration.ORIENTATION_LANDSCAPE) {
			setTitle(name);
		} else {
			setTitle(R.string.title_activity_real_time_preview);
		}
		
		// set play time & flow
//		RealStreamPlayer player = surfaceViewPager.getPlayer(position);
//		startBriefThread(player);
	}
	
	public void startBriefThread(RealStreamPlayer player) {
		if (briefRunnable != null) {
			briefRunnable.setStop(true);
			briefRunnable = null;
		}
		DebugLogger.i("player:" + player);
		briefRunnable = new BriefRunnable(player);
	    new Thread(briefRunnable).start();
	}
	
	class BriefRunnable implements Runnable {
		
		boolean stop = false;
		RealStreamPlayer realStreamPlayer;
		
		public BriefRunnable(RealStreamPlayer player) {
			this.realStreamPlayer = player;
		}
		
		public void setStop(boolean stop) {
			this.stop = stop;
		}
		
		@Override
		public void run() {
			while (!stop) {
				flowView.post(new Runnable() {
					@Override
					public void run() {
						int time = realStreamPlayer.getPlayTime();
						flowView.setText("" + realStreamPlayer.getReate()
								+ " KB/s");
					}
				});
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void setIndicaterStatus(int position) {
		leftIndicater.setSelected(position == 0);
		rightIndicater.setSelected(position == surfaceViewPager.getCount() - 1);
	}
	
	@Override
	public void onPageScrollStateChanged(int position) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int position) {
		setChannelName(position);
		surfaceViewPager.setCurrentPosition(position);
	}

	public int getCurrentChannelId() {
		return deviceInfo.getChannelArr()[viewPager.getCurrentItem()].getChannelId();
	}
	
	private void clickOnCatchPhone() {
		DebugLogger.i("click on on catch phone");
		new Thread(new Runnable() {
			@Override
			public void run() {
				surfaceViewPager.getPlayer(viewPager.getCurrentItem()).captureImage(RealTimePreviewActivity.this, deviceInfo.getDevName(), getCurrentChannelId());
			}
		}).start();
	}
	
	private void clickOnRecordVoice(View view) {
		DebugLogger.i("click on on record voice");
		if (view.isSelected()) {
			// close 
			view.setSelected(false);
		} else {
			// open
			view.setSelected(true);
		}
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				surfaceViewPager.getPlayer(viewPager.getCurrentItem()).recordVedio(RealTimePreviewActivity.this, deviceInfo.getDevName(), getCurrentChannelId());
//			}
//		}).start();
	}
	
	private void clickOnRecordVedio(View view) {
		DebugLogger.i("click on on record vedio");
		if (view.isSelected()) {
			// close 
			view.setSelected(false);
			surfaceViewPager.stopRecord((viewPager.getCurrentItem()));
		} else {
			// open
			view.setSelected(true);
			surfaceViewPager.startRecord(viewPager.getCurrentItem());
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				surfaceViewPager.getPlayer(viewPager.getCurrentItem()).recordVedio(RealTimePreviewActivity.this, deviceInfo.getDevName(), getCurrentChannelId());
			}
		}).start();
	}
	
	private void clickOnVoice(View view) {
		DebugLogger.i("click on voice");
		if (view.isSelected()) {
			// close 
			view.setSelected(false);
			surfaceViewPager.getPlayer(viewPager.getCurrentItem()).clonseSound();
		} else {
			// open
			view.setSelected(true);
			surfaceViewPager.getPlayer(viewPager.getCurrentItem()).openSound();
		}
	}
	
	private boolean ptzLeftControl(int bStop) {
		DebugLogger.i("cloud left control");
		ClientApp.getInstance().GetSdkInterface().ptzControl(deviceInfo.getDevId(), getCurrentChannelId(), Constants.WMPTZCommand_LEFT, 
				bStop, ptzSpeed);
		return true;
	}

	private boolean ptzRightControl(int bStop) {
		DebugLogger.i("cloud right control");
		ClientApp.getInstance().GetSdkInterface().ptzControl(deviceInfo.getDevId(), getCurrentChannelId(), Constants.WMPTZCommand_RIGHT,
				bStop, ptzSpeed);
		return true;
	}

	private boolean ptzTopControl(int bStop) {
		DebugLogger.i("cloud top control");
		ClientApp.getInstance().GetSdkInterface().ptzControl(deviceInfo.getDevId(), getCurrentChannelId(), Constants.WMPTZCommand_UP,
				bStop, ptzSpeed);
		return true;
	}

	private boolean ptzBottomControl(int bStop) {
		DebugLogger.i("cloud bottom control");
		ClientApp.getInstance().GetSdkInterface().ptzControl(deviceInfo.getDevId(), getCurrentChannelId(), Constants.WMPTZCommand_DOWN,
				bStop, ptzSpeed);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		viewPager.setCurrentItem(position);
		window.dismiss();
	}
	
}
