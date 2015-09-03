package com.onefengma.wmclient2;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar.LayoutParams;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class BaseBackActivity extends AppCompatActivity {

    
	private TextView titleView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

		titleView = new TextView(this);
		titleView.setGravity(Gravity.CENTER);
		titleView.setText("我的设备");
		titleView.setTextColor(Color.WHITE);
		titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
		getSupportActionBar().setCustomView(titleView);
		
		View v = getSupportActionBar().getCustomView();
		LayoutParams lp = (LayoutParams) v.getLayoutParams();
		lp.width = getResources().getDisplayMetrics().widthPixels - ViewUtils.dipToPix(getResources().getDisplayMetrics(), 100);
		v.setLayoutParams(lp);
	}
	
	@Override
	public void setTitle(int titleId) {
		setTitle(getString(titleId));
	}
	
	@Override
	public void setTitle(CharSequence title) {
		titleView.setText(title);
	}
	
	public TextView getTitTextView() {
		return titleView;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}
}
