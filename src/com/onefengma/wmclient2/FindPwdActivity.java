package com.onefengma.wmclient2;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class FindPwdActivity extends BaseBackActivity {

	private EditText nameEdit;
	private EditText emailEdit;

	private static final int WHAT_FIND_PWD = 1;
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int statusCode = msg.arg1;
			if (statusCode == HttpStatus.SC_OK) {
				ContextToast.show(FindPwdActivity.this, "找回密码成功，请查看邮箱",
						Toast.LENGTH_SHORT);
			} else {
				ContextToast.show(FindPwdActivity.this, "找回密码失败,请重试！",
						Toast.LENGTH_SHORT);
			}
		}
	};

	public static void startFrom(Activity activity) {
		activity.startActivity(new Intent(activity, FindPwdActivity.class));
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_pwd);
		setTitle(R.string.title_activity_find_pwd);
		
		nameEdit = (EditText) findViewById(R.id.name);
		emailEdit = (EditText) findViewById(R.id.email);
	}

	public void onClick(View view) {
		String name = nameEdit.getText().toString();
		String email = emailEdit.getText().toString();
		new Thread(new FindPwdRunnable(name, email)).start();
	}

	private class FindPwdRunnable implements Runnable {

		private String name;
		private String email;
		
		public FindPwdRunnable(String name, String email) {
			this.name = name;
			this.email = email;
		}
		
		@Override
		public void run() {
			int statusCode = 0;
			String urlAddress = "http://"
					+ ClientApp.getInstance().getServerAddress()
					+ ":8050/?msgid=372&username=" + name + "&mailbox=" + email;

			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpgets = new HttpGet(urlAddress);

			try {
				HttpResponse response = httpclient.execute(httpgets);
				statusCode = response.getStatusLine().getStatusCode();
			} catch (Exception e) {
				statusCode = -1;
			}
			Message msg = new Message();
			msg.what = WHAT_FIND_PWD;
			msg.arg1 = statusCode;
			handler.sendMessage(msg);
		}

	}

}
