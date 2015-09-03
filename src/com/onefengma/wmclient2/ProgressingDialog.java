package com.onefengma.wmclient2;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ProgressingDialog extends Dialog {

    private String progressingText;
    private TextView progressingTextView;

    public ProgressingDialog(Context context) {
        super(context, R.style.ProgressingDialog);
    }

    public ProgressingDialog(Context context, int progressingTextRes) {
        this(context);
        this.progressingText = context.getString(progressingTextRes);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progressing_dialog);
        progressingTextView = (TextView) findViewById(R.id.progressing_text);
        if (progressingText != null) {
            progressingTextView.setVisibility(View.VISIBLE);
            progressingTextView.setText(progressingText);
        }
    }

    public void setProgressText(String progressingText) {
        this.progressingText = progressingText;
    }
}
