package com.onefengma.wmclient2;

import com.wmclient.clientsdk.Constants;
import com.wmclient.clientsdk.Utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class UpdatePasswordActivity extends BaseBackActivity {
	
	EditText oldPwdEditText;
	EditText newPwdEditText1;
	EditText newPwdEditText2;
	
	public static void startFrom(Activity activity) {
		activity.startActivity(new Intent(activity, UpdatePasswordActivity.class));
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_password);
		setTitle(R.string.title_activity_update_password);
		
		oldPwdEditText = (EditText) findViewById(R.id.old_pwd);
		newPwdEditText1 = (EditText) findViewById(R.id.new_pwd1);
		newPwdEditText2 = (EditText) findViewById(R.id.new_pwd2);
	}
	
	public void onClick(View view) {
		String oldPwd = oldPwdEditText.getText().toString();
		String newPwd1 = newPwdEditText1.getText().toString();
		String newPwd2 = newPwdEditText2.getText().toString();
		if (Utils.isEmpty(oldPwd) || Utils.isEmpty(newPwd1)) {
			ContextToast.show(this, "密码不能为空", Toast.LENGTH_SHORT);
			return;
		}
		if (!newPwd1.equals(newPwd2)) {
			ContextToast.show(this, "新密码输入不一致", Toast.LENGTH_SHORT);
			return;
		}
		int ret = ClientApp.getInstance().GetSdkInterface().updatePassword(oldPwd, newPwd1);
		if (ret != Constants.success) {
			ContextToast.show(this, "密码修改失败！", Toast.LENGTH_SHORT);
		} else {
			ContextToast.show(this, "密码修改成功！", Toast.LENGTH_SHORT);
		}
	}
	
}
