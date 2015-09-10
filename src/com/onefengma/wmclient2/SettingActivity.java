package com.onefengma.wmclient2;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

public class SettingActivity extends MenuBaseActivity {
	
	private TextView versionText;
	
	public static void startFrom(Activity activity) {
		activity.startActivity(new Intent(activity, SettingActivity.class));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		setTitle(R.string.title_activity_setting);
		
		// version
		versionText = (TextView) findViewById(R.id.version);
		PackageManager manager = this.getPackageManager();
		PackageInfo info;
		try {
			info = manager.getPackageInfo(this.getPackageName(), 0);
			versionText.setText("版本号：" + info.versionName);
			checkUpdate();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void checkUpdate() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				UpdateManager updateManager = UpdateManager.getInstance(SettingActivity.this);
				if(updateManager.checkUpdate()) {
					showVersionDialog(updateManager);
				}
			}
		}).start();
	}
	
	private void showVersionDialog(final UpdateManager updateManager) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final String newVersion = updateManager.getVSESION();
				final String details = updateManager.getDETAILS();
				versionText.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.new_version), null);
				versionText.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
						builder.setTitle("新版本:" + newVersion);
						builder.setMessage(details);
						builder.setNegativeButton("取消", null);
						builder.setPositiveButton("点击下载", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								UpdateManager.getInstance(SettingActivity.this).downLoad(newVersion);
							}
						});
						builder.create().show();
					}
				});
			}
			}
		);
	}
	
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.logout:
			ClientApp.getInstance().setNeedLogin(true);
			ClientApp.getInstance().GetSdkInterface().logout();
			ClientApp.getInstance().exit();
			LoginActivity.startFrom(this);
			finish();
			break;
		case R.id.change_pwd:
			UpdatePasswordActivity.startFrom(this);
			break;
		case R.id.cloud_length:
			clickOnCloudLength();
			break;
		case R.id.path:
			clickOnPath();
			break;
		case R.id.video_path:
			clickOnVideoPath();
			break;
		}
	}
	
	public void clickOnCloudLength() {
		final SettingValueDialog settingDialog = new SettingValueDialog(this);
		settingDialog.alertDialog.setTitle("修改云台步长");
		settingDialog.alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ClientApp.getInstance().saveCloudLength(Integer.valueOf(settingDialog.getInputText()));
			}
		});
		settingDialog.setEditType(EditorInfo.TYPE_CLASS_NUMBER);
		settingDialog.setHint(ClientApp.getInstance().getCloudLength() + "");
		settingDialog.alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				settingDialog.alertDialog.dismiss();
			}
		});
		settingDialog.alertDialog.show();
	}

	public void clickOnPath() {
		final SettingValueDialog settingDialog = new SettingValueDialog(this);
		settingDialog.alertDialog.setTitle("修改截图保存路径");
		settingDialog.alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ClientApp.getInstance().saveCaptureImagePath(settingDialog.getInputText());
			}
		});
		settingDialog.setHint(ClientApp.getInstance().getCaptureImagePath() + "");
		settingDialog.alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				settingDialog.alertDialog.dismiss();
			}
		});
		settingDialog.alertDialog.show();
	}
	
	public void clickOnVideoPath() {
		final SettingValueDialog settingDialog = new SettingValueDialog(this);
		settingDialog.alertDialog.setTitle("修改录像保存路径");
		settingDialog.alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ClientApp.getInstance().saveCaptureImagePath(settingDialog.getInputText());
			}
		});
		settingDialog.setHint(ClientApp.getInstance().getVideoPath() + "");
		settingDialog.alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				settingDialog.alertDialog.dismiss();
			}
		});
		settingDialog.alertDialog.show();
	}

}
