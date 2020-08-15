package com.vivo.pushupstest;

import android.content.Context;
import android.util.Log;

import com.vivo.push.model.UPSNotificationMessage;
import com.vivo.push.sdk.OpenClientPushMessageReceiver;

public class MyReceiver extends OpenClientPushMessageReceiver {
    private static final String TAG = "VIVO_PUSH";

    @Override
    public void onNotificationMessageClicked(Context context, UPSNotificationMessage upsNotificationMessage) {
        String customContentString = upsNotificationMessage.getSkipContent();
        String notifyString = "通知点击 msgId " + upsNotificationMessage.getMsgId() + " ;customContent=" + customContentString;
        Log.d(TAG, notifyString);
    }

    @Override
    public void onReceiveRegId(Context context, String s) {
        Log.e(TAG, "获取注册的regID = " + s);
    }
}
