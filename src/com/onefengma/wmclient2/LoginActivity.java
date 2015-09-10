package com.onefengma.wmclient2;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.google.gson.JsonObject;
import com.wmclient.clientsdk.Constants;
import com.wmclient.clientsdk.DebugLogger;
import com.wmclient.clientsdk.Utils;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;

public class LoginActivity extends AppCompatActivity implements OnClickListener {

	public static final int LOGIN = 0;

	private TextView userView;
	private TextView pwdView;
	private Button loginButton;
	private Handler handler;
	private UpdateManager updateManager;
	private ProgressingDialog dialog;
	
	public static void startFrom(Activity activity) {
		activity.startActivity(new Intent(activity, LoginActivity.class));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setTitle(R.string.title_activity_login);
		StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		getSupportActionBar().hide();

		userView = (TextView) findViewById(R.id.user_edit);
		pwdView = (TextView) findViewById(R.id.pwd_edit);
		loginButton = (Button) findViewById(R.id.login);
		dialog = new ProgressingDialog(this);
		
		updateManager = UpdateManager
				.getInstance(LoginActivity.this);
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what != LOGIN) {
					return;
				}

				dialog.dismiss(); 
				
				if (msg.arg1 == Constants.ErrorCode_PasswordError) {
					ContextToast.show(LoginActivity.this, "登陆失败，密码错误...",
							Toast.LENGTH_SHORT);
				} else if (msg.arg1 == Constants.ErrorCode_AccountError) {
					ContextToast.show(LoginActivity.this, "登陆失败，用户名不存在...",
							Toast.LENGTH_SHORT);
				} else if (msg.arg1 == Constants.ErrorCode_VersionTooLow) {
					// update version
					if (updateManager.checkUpdate()) {
						UpdateManager.getInstance(LoginActivity.this).downLoad(
								updateManager.getVSESION());
						ContextToast.show(LoginActivity.this,
								"客户端版本太低，正在下载新版本，请稍后...", Toast.LENGTH_SHORT);
					} else {
						ContextToast.show(LoginActivity.this, "登陆失败，版本太低...",
								Toast.LENGTH_SHORT);
					}
					showDownLoading();
					startCheck(updateManager);
				} else if (msg.arg1 == Constants.success){
					ClientApp.getInstance().setHasLogin(true);
					MainActivity.startFrom(LoginActivity.this);
					finish();
				} else if (msg.arg1 == Constants.ErrorCode_HasLogin){
					ContextToast.show(LoginActivity.this, "登陆失败, 该帐号已经登录...",
							Toast.LENGTH_SHORT);
				} else {
					ContextToast.show(LoginActivity.this, "登陆失败!",
							Toast.LENGTH_SHORT);
				}
			}
		};

		if (!Utils.isEmpty(ClientApp.getInstance().getUserName())) {
			userView.setText(ClientApp.getInstance().getUserName());
			pwdView.setText(ClientApp.getInstance().getPassword());
		}

		loginButton.setOnClickListener(this);
	}
	
	public void showDownLoading() {
		DebugLogger.i("post download" + System.currentTimeMillis());
		loginButton.post(new Runnable() {
			@Override
			public void run() {
				DebugLogger.i("show downloading:" + System.currentTimeMillis());
				loginButton.setText("后台正在下载中...");
				loginButton.setEnabled(false);
			}
		});
	}
	
	public void showLogin() {
		DebugLogger.i("post loging" + System.currentTimeMillis());
		loginButton.post(new Runnable() {
			@Override
			public void run() {
				DebugLogger.i("show loging" + System.currentTimeMillis());
				loginButton.setText("登陆");
				loginButton.setEnabled(true);
			}
		});
	}
	
	public void startCheck(final UpdateManager updateManager) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean quit = false;
				while (!quit) {
					if (updateManager.checkStatus()) {
						DebugLogger.i("downloader running");
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						ClientApp.getInstance().saveDownloadId(-1);
						DebugLogger.i("download wrong");
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								showLogin();
							}
						});
						quit = true;
					}
				}
				
				
			}
		}).start();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		long downloadId = ClientApp.getInstance().getDownloadId();
		if (downloadId != -1) {
			DebugLogger.i("download : " + downloadId);
			showDownLoading();
			updateManager.setDownId(downloadId);
			startCheck(updateManager);
		} else {
			showLogin();
		}
		DebugLogger.i("onResume:" + downloadId);
	}
	
	@Override
	public void onClick(View v) {
		dialog.setProgressText("正在登陆...");
		dialog.show();
		String user = userView.getText().toString();
		String pwd = pwdView.getText().toString();
		if (Utils.isEmpty(user) || Utils.isEmpty(pwd)) {
			ContextToast.show(this, "用户名或密码为空！", Toast.LENGTH_SHORT);
			return;
		}
		new Thread(new LoginRunnable(user, pwd, handler)).start();
	}

	public void onFindPwdClick(View view) {
		FindPwdActivity.startFrom(this);
	}

	private class LoginRunnable implements Runnable {

		private String user;
		private String pwd;
		private Handler hanlder;

		public LoginRunnable(String user, String pwd, Handler handler) {
			this.user = user;
			this.pwd = pwd;
			this.hanlder = handler;
		}

		@Override
		public void run() {
			Message message = new Message();
			message.what = LOGIN;
			if (!ClientApp.getInstance().requestAddress()) {
				message.arg1 = -1;
				return;
			}
			int ret = ClientApp.getInstance().login(user, pwd);
			message.arg1 = ret;
			DebugLogger.i("login ret:" + ret);
			if (ret == Constants.success) {
				ClientApp.getInstance().storeAccountInfo(user, pwd);
			}
			hanlder.sendMessage(message);
		}
	}

}
