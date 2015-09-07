package com.onefengma.wmclient2;

import com.wmclient.clientsdk.DebugLogger;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar.LayoutParams;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MenuBaseActivity extends AppCompatActivity {

	protected MenuDrawer mDrawer;
	protected TextView deviceView;
	protected TextView replayView;
	protected TextView notificationView;
	protected TextView settingView;
	protected TextView nameView;
    
	private TextView titleView;
	
	@Override
	protected void onCreate(Bundle state) {
		super.onCreate(state);
		mDrawer = MenuDrawer.attach(this, MenuDrawer.Type.BEHIND,
				Position.LEFT, MenuDrawer.MENU_DRAG_WINDOW);
		mDrawer.setMenuView(R.layout.menu_view);
		mDrawer.setDropShadowSize(0);

		getSupportActionBar().setHomeAsUpIndicator(R.drawable.home);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		initMenuItem();
		
		titleView = new TextView(this);
		titleView.setGravity(Gravity.CENTER);
		titleView.setText("我的设备");
		titleView.setTextColor(Color.WHITE);
		titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
		getSupportActionBar().setCustomView(titleView);
		
		View v = getSupportActionBar().getCustomView();
		LayoutParams lp = (LayoutParams) v.getLayoutParams();
		lp.width = LayoutParams.MATCH_PARENT;
		lp.rightMargin = ViewUtils.dipToPix(getResources().getDisplayMetrics(), 42);
		v.setLayoutParams(lp);
		
		ClientApp.getInstance().addActivity(this);
	}
	
	@Override
	public void setTitle(int titleId) {
		setTitle(getString(titleId));
	}
	
	@Override
	public void setTitle(CharSequence title) {
		titleView.setText(title);
	}
	
	private void initMenuItem() {
		deviceView = (TextView) findViewById(R.id.devices);
		replayView = (TextView) findViewById(R.id.replay);
		notificationView = (TextView) findViewById(R.id.notification);
		settingView = (TextView) findViewById(R.id.setting);
		nameView = (TextView) findViewById(R.id.name);
		nameView.setText(ClientApp.getInstance().getUserName());
		
		settingView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (MenuBaseActivity.this instanceof SettingActivity) {
					mDrawer.toggleMenu();	
					return;
				}
				SettingActivity.startFrom(MenuBaseActivity.this);
				if (!(MenuBaseActivity.this instanceof MainActivity)) {
					finish();
				}
			}
		});
		
		notificationView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (MenuBaseActivity.this instanceof NotificationActivity) {
					mDrawer.toggleMenu();	
					return;
				}

				NotificationActivity.startFrom(MenuBaseActivity.this);	
				if (!(MenuBaseActivity.this instanceof MainActivity)) {
					finish();
				}	
			}
		});
		
		deviceView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (MenuBaseActivity.this instanceof MainActivity) {
					mDrawer.toggleMenu();	
					return;
				}
				MainActivity.startFromMenu(MenuBaseActivity.this);		
				finish();
			}
		});
		
		replayView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (MenuBaseActivity.this instanceof ReplayActivity) {
					mDrawer.toggleMenu();	
					return;
				}
				ReplayActivity.startFrom(MenuBaseActivity.this);
				if (!(MenuBaseActivity.this instanceof MainActivity)) {
					finish();
				}
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			DebugLogger.i("home clicked");
			mDrawer.toggleMenu();
		}
		return super.onOptionsItemSelected(item);
	}
}
