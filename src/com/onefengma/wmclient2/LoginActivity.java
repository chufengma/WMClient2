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

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class LoginActivity extends AppCompatActivity implements OnClickListener {

	public static final int LOGIN = 0;
	
	private TextView userView;
	private TextView pwdView;
	private Button loginButton;
	private Handler handler;
	
	private ProgressingDialog dialog;
	
	public static void startFrom(Activity activity) {
		activity.startActivity(new Intent(activity, LoginActivity.class));
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setTitle(R.string.title_activity_login);
		
		getSupportActionBar().hide();
		
		userView = (TextView) findViewById(R.id.user_edit);
		pwdView = (TextView) findViewById(R.id.pwd_edit);
		loginButton = (Button) findViewById(R.id.login);
		dialog = new ProgressingDialog(this);
		
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what != LOGIN) {
					return;
				}
				
				dialog.dismiss();
				
				if (msg.arg1 == Constants.ErrorCode_PasswordError) {
					ContextToast.show(LoginActivity.this, "登陆失败，密码错误...", Toast.LENGTH_SHORT);
				} else if(msg.arg1 == Constants.ErrorCode_AccountError) {
					ContextToast.show(LoginActivity.this, "登陆失败，用户名不存在...", Toast.LENGTH_SHORT);
				} else if(msg.arg1 == Constants.ErrorCode_VersionTooLow) {
					ContextToast.show(LoginActivity.this, "登陆失败，版本太低...", Toast.LENGTH_SHORT);
					//update version
					UpdateManager updateManager = UpdateManager.getInstance(LoginActivity.this);
					if(updateManager.checkUpdate()) {
						updateManager.downLoad(updateManager.getVSESION());
						ContextToast.show(LoginActivity.this, "正在下载新版本中..", Toast.LENGTH_SHORT);
						finish();
					}
				} else {
					ClientApp.getInstance().setHasLogin(true);
					MainActivity.startFrom(LoginActivity.this);
					finish();
				}
			}
		};
		
		if (!Utils.isEmpty(ClientApp.getInstance().getUserName())) {
			userView.setText(ClientApp.getInstance().getUserName());
			pwdView.setText(ClientApp.getInstance().getPassword());
		}
		
		loginButton.setOnClickListener(this);
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
			if(!ClientApp.getInstance().requestAddress()) {
				message.arg1 = -1;
				return;
			}
			int ret = ClientApp.getInstance().login(user, pwd);
			message.arg1 = ret;
			if (ret == Constants.success) {
				ClientApp.getInstance().storeAccountInfo(user, pwd);
			}
			hanlder.sendMessage(message);
		}
	}
	
}
