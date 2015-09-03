package com.onefengma.wmclient2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

public class ContextToast{


    private Context context;

    private Toast activityToast;

    private boolean contextDisabled;

    public ContextToast(Context context) {
        this.context = context;
    }

    private void enable() {
        contextDisabled = false;
    }

    /**
     * @see {@link ContextToast#show(String, int)}
     */
    public void show(int msgResId, int duration) {
        if (context == null) {
            return;
        }
        show(context.getString(msgResId), duration);
    }

    /**
     * Show a piece of Toast within the source activity's lifecycle.
     * In other words, one toast will be canceled after the source activity was paused.
     */
    public void show(String msg, int duration) {
        if (!(context instanceof Activity)) {
            return;
        }

        if (contextDisabled) {
            return;
        }

        activityToast = show(activityToast, context, msg, duration);
    }

    private void disable() {
        if (activityToast != null) {
            activityToast.cancel();
        }

        contextDisabled = true;
    }

    private void destroy() {
        if (activityToast != null) {
            activityToast = null;
        }

        context = null;
    }

    private static Toast show(Toast toast, Context context, String msg, int duration) {
        if (toast != null) {
            ((TextView) toast.getView()).setText(msg);
        } else {
            toast = new Toast(context);
            TextView tv = new TextView(context);
            tv.setBackgroundResource(R.drawable.toast_bg);
            tv.setGravity(Gravity.CENTER_VERTICAL);

            tv.setTextColor(Color.WHITE);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            tv.setText(msg);

            toast.setView(tv);
            toast.setGravity(Gravity.CENTER, 0, 0);
        }
        toast.setDuration(duration);
        toast.show();

        return toast;
    }

    private static Toast applicationToast;

    /**
     * @see {@link ContextToast#show(Context, String, int)}
     */
    public static void show(Context context, int msgRes, int duration) {
        show(context, context.getString(msgRes), duration);
    }

    /**
     * Show a piece of Toast if you want to keep the toast outside of any activity's lifecycle.
     * In other words, one toast may keep showing even if the source activity was paused or even destroyed.
     */
    public static void show(Context context, String msg, int duration) {
        applicationToast = show(applicationToast, context.getApplicationContext(), msg, duration);
    }
}