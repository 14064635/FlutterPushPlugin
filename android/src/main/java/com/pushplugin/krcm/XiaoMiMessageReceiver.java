package com.pushplugin.krcm;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.huaweipush.naoxuejia.flutter_huawei_push.R;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class XiaoMiMessageReceiver extends PushMessageReceiver {

    private String mRegId;
    private String mTopic;
    private String mAlias;
    private String mAccount;
    private String mStartTime;
    private String mEndTime;
    private String TAG = "com.naoxuejia.clinent.android";
    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
        Log.v(TAG,
                "onReceivePassThroughMessage is called. " + message.toString());
        String log = "Receive a passthrough message. Content is";

        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        }

        Message msg = Message.obtain();
        msg.obj = log;
        PushPlugin.getHandler().sendMessage(msg);
    }

    /**
     * 用户点击的推送消息后触发
     * 只对自定义消息有效
     * {"key":"naoxuejia://microVideoLessonDetails?microLessonId=359&courseTrainingId=1"}
     * @param context
     * @param message
     */
    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        Log.v(TAG,
                "onNotificationMessageClicked is called. " + message.toString());
        System.out.println("onNotificationMessageClicked is called.");
        String log = context.getString(R.string.click_notification_message, message.getContent());

        String appName = PushPlugin.getAppName(context);
        if(Tools.isAppAlive(context,appName)){
            Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage(appName);
            context.startActivity(LaunchIntent);
        }
    }


    /**
     * 客户端接收到新的推送后触发
     * @param context
     * @param message
     */
    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        Log.v(TAG,
                "onNotificationMessageArrived is called. " + message.toString());
        String log = context.getString(R.string.arrive_notification_message, message.getContent());
        //MainActivity.logList.add(0, getSimpleDate() + " " + log);

        System.out.println("小米接受到了消息——————————");

        String schema = message.getExtra().get("intent_uri");
        PushPlugin.Scheme.put(message.getMessageId(),schema);
        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        }

        System.out.println("接受到了消息——————————");

        Message msg = Message.obtain();
        msg.obj = log;
        PushPlugin.getHandler().sendMessage(msg);
    }

    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
//        Log.v(DemoApplication.TAG,
//                "onCommandResult is called. " + message.toString());
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        String log;
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
                log = context.getString(R.string.register_success);
            } else {
                log = context.getString(R.string.register_fail);
            }
        } else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
                log = context.getString(R.string.set_alias_success, mAlias);
            } else {
                log = context.getString(R.string.set_alias_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
                log = context.getString(R.string.unset_alias_success, mAlias);
            } else {
                log = context.getString(R.string.unset_alias_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_SET_ACCOUNT.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAccount = cmdArg1;
                log = context.getString(R.string.set_account_success, mAccount);
            } else {
                log = context.getString(R.string.set_account_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_UNSET_ACCOUNT.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAccount = cmdArg1;
                log = context.getString(R.string.unset_account_success, mAccount);
            } else {
                log = context.getString(R.string.unset_account_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
                log = context.getString(R.string.subscribe_topic_success, mTopic);
            } else {
                log = context.getString(R.string.subscribe_topic_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
                log = context.getString(R.string.unsubscribe_topic_success, mTopic);
            } else {
                log = context.getString(R.string.unsubscribe_topic_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mStartTime = cmdArg1;
                mEndTime = cmdArg2;
                log = context.getString(R.string.set_accept_time_success, mStartTime, mEndTime);
            } else {
                log = context.getString(R.string.set_accept_time_fail, message.getReason());
            }
        } else {
            log = message.getReason();
        }
        //MainActivity.logList.add(0, getSimpleDate() + "    " + log);

        Message msg = Message.obtain();
        msg.obj = log;

        String token =MiPushClient.getRegId(context);
        Intent intent = new Intent();
        intent.setAction(PushPlugin.CODELABS_ACTION);
        intent.putExtra("method", "onNewToken");
        intent.putExtra("msg",MiPushClient.getRegId(context));
        intent.putExtra("provider","XIAOMI");

        context.sendBroadcast(intent);
//        PushPlugin.getHandler().sendMessage(msg);
    }

    /**
     * 当用户注册成功后要把mRegId返回给服务器
     * @param context
     * @param message
     */
    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        Log.v(TAG,
                "onReceiveRegisterResult is called. " + message.toString());
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String log;
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
                log = "Register push success.";
            } else {
                log = "Register push fail.";
            }
        } else {
            log = message.getReason();
        }

        Message msg = Message.obtain();
        msg.obj = log;
        PushPlugin.getHandler().sendMessage(msg);
    }

    @Override
    public void onRequirePermissions(Context context, String[] permissions) {
        super.onRequirePermissions(context, permissions);
        Log.e(TAG,
                "onRequirePermissions is called. need permission" + arrayToString(permissions));

        if (Build.VERSION.SDK_INT >= 23 && context.getApplicationInfo().targetSdkVersion >= 23) {
        }
    }

    @SuppressLint("SimpleDateFormat")
    private static String getSimpleDate() {
        return new SimpleDateFormat("MM-dd hh:mm:ss").format(new Date());
    }

    public String arrayToString(String[] strings) {
        String result = " ";
        for (String str : strings) {
            result = result + str + " ";
        }
        return result;
    }
}
