package com.vivo.push.sample;

import android.content.Context;
import android.util.Log;

import com.vivo.push.model.UPSNotificationMessage;
import com.vivo.push.sdk.OpenClientPushMessageReceiver;

import static com.vivo.push.sample.Utils.updateContent;

public class PushMessageReceiverImpl extends OpenClientPushMessageReceiver {
    /**
     * TAG to Log
     */
    public static final String TAG = PushMessageReceiverImpl.class.getSimpleName();

    @Override
    public void onNotificationMessageClicked(Context context, UPSNotificationMessage msg) {
        String customContentString = msg.getSkipContent();
        String notifyString = "通知点击 msgId " + msg.getMsgId() + " ;customContent=" + customContentString;
        Log.d(TAG, notifyString);

        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
        updateContent(notifyString);
    }

    @Override
    public void onReceiveRegId(Context context, String regId) {
        String responseString = "onReceiveRegId regId = " + regId;
        Log.d(TAG, responseString);
        updateContent(responseString);
    }
}
