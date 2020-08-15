package com.pushplugin.krcm;

import android.content.Context;
import android.content.Intent;

import com.vivo.push.PushClient;
import com.vivo.push.model.UPSNotificationMessage;
import com.vivo.push.sdk.OpenClientPushMessageReceiver;


public class VivoPushMessageReceiver extends OpenClientPushMessageReceiver {

    /***
     * 当通知被点击时回调此方法
     * @param context 应用上下文
     * @param msg 通知详情，详细信息见API接入文档
     */
    @Override
    public void onNotificationMessageClicked(Context context, UPSNotificationMessage msg) {
        System.out.println("msg__获取ViVo的RegId___" + msg);

        String appName = PushPlugin.getAppName(context);
        if(Tools.isAppAlive(context,appName)){
            Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage(appName);
            context.startActivity(LaunchIntent);
        }
    }


    /***
     * 当首次turnOnPush成功或regId发生改变时，回调此方法
     * 如需获取regId，请使用PushClient.getInstance(context).getRegId()
     * @param context 应用上下文
     * @param regId 注册id
     */
    @Override
    public void onReceiveRegId(Context context, String regId) {
        System.out.println("获取ViVo的RegId___");
        PushClient.getInstance(context).getRegId();
        Intent intent = new Intent();
        intent.setAction(PushPlugin.CODELABS_ACTION);
        intent.putExtra("method", "onNewToken");
        intent.putExtra("msg",regId);
        intent.putExtra("provider","VIVO");

        context.sendBroadcast(intent);
    }
}
