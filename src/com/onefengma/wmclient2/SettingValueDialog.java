package com.onefengma.wmclient2;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.EditText;

public class SettingValueDialog {
	
	AlertDialog alertDialog;
	EditText editText;
	Context context;
	
	protected SettingValueDialog(Context context) {
		this.context = context;
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
	    editText = (EditText) LayoutInflater.from(context).inflate(R.layout.setting_value_dialog, null);
	    builder.setView(editText);
		alertDialog = builder.create();
	}
	
	public void setHint(String hint) {
		editText.setText(hint);
		editText.setSelection(hint.length());
	}
	
	public void setEditType(int type) {
		editText.setInputType(type);
	}
	
	public String getInputText() {
		return editText.getText().toString();
	}
	
}
