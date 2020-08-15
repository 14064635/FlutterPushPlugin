package com.pushplugin.krcm;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.heytap.msp.push.HeytapPushManager;
import com.heytap.msp.push.callback.ICallBackResultService;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;
import com.vivo.push.IPushActionListener;
import com.vivo.push.PushClient;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;


/**
 * @author  naoxuejia.Cc.
 * PushPlugin */
public class PushPlugin implements MethodCallHandler {

  private final MethodChannel channel;

  public static final String TAG = "com.pushplugin.android.client";

  ///事件派发对象
  private EventChannel.EventSink eventSink = null;

  private  static String XIAOMI_APP_ID;
  private  static String XIAOMI_APP_KEY;

    private static DemoHandler sHandler = null;

    private  static String OPPO_APP_KEY;

  private  static String OPPO_APP_Secret;

  ///跳转到的Scheme
  public static Map<String,String> Scheme = new HashMap<>();

  ///Notification的推送Code
  public static int requestCode = 1;

  ///消息通知栏管理器
  private NotificationManager notificationManager;

  ///
  private  NotificationCompat.Builder builder;

  ///事件派发流
  private EventChannel.StreamHandler streamHandler = new EventChannel.StreamHandler(){

    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {
      eventSink = events;
    }

    @Override
    public void onCancel(Object arguments) {
      eventSink = null;
    }
  };

  private final Context context;

  public final static String CODELABS_ACTION = "com.pushplugin.android.client.action";

  ///点击详细事件
  public final static String ACTION_CLICKK_MESSAGE = "com.pushplugin.android.client.clickMessage";

  ///点击消息的通知栏
  public final static String ACTION_CLICK_NOTIFICATION = "com.pushplugin.android.client.clickNotificaton";

  ///当前被点击Scheme
//  public static String currentClickMessageId = "";

  private MyReceiver receiver;

  private PushPlugin(final MethodChannel channel, Registrar registrar) {
    this.channel = channel;
    this.channel.setMethodCallHandler(this);
    this.context = registrar.context();


    ///初始化事件
    EventChannel eventChannel = new EventChannel(registrar.messenger(),"flutter_push_getToken");
    eventChannel.setStreamHandler(streamHandler);

    receiver = new MyReceiver();
    IntentFilter filter = new IntentFilter();
    filter.addAction(CODELABS_ACTION);
    filter.addAction(ACTION_CLICK_NOTIFICATION);
    filter.addAction(ACTION_CLICKK_MESSAGE);
    context.registerReceiver(receiver, filter);
  }

  // This static function is optional and equivalent to onAttachedToEngine. It supports the old
  // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
  // plugin registration via this function while apps migrate to use the new Android APIs
  // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
  //
  // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
  // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
  // depending on the user's project. onAttachedToEngine or registerWith must both be defined
  // in the same class.
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_push_plugin");
    channel.setMethodCallHandler(new PushPlugin(channel,registrar));
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    try {
      handleMethodCall(call, result);
    } catch (Exception e) {
      result.error("Unexpected error!", e.getMessage(), e);
    }
  }

  private void handleMethodCall(final MethodCall call, final MethodChannel.Result response) {
    switch (call.method){
      case "initHuaWeiPush":
        initPushSdk(call,response);
        break;
    }
  }

  ///判断App是否在前台运行
  public static boolean isBackground(Context context) {
    ActivityManager activityManager = (ActivityManager) context
            .getSystemService(Context.ACTIVITY_SERVICE);
    List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
            .getRunningAppProcesses();
    for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
      if (appProcess.processName.equals(context.getPackageName())) {
        Log.i(context.getPackageName(), "此appimportace ="
                + appProcess.importance
                + ",context.getClass().getName()="
                + context.getClass().getName());
        if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
          Log.i(context.getPackageName(), "处于后台"
                  + appProcess.processName);
          return true;
        } else {
          Log.i(context.getPackageName(), "处于前台"
                  + appProcess.processName);
          return false;
        }
      }
    }
    return false;
  }


  private void initPushSdk(final MethodCall methodCall,final MethodChannel.Result response){
    OPPO_APP_KEY = methodCall.argument("OppoAppKey");
    OPPO_APP_Secret = methodCall.argument("OppoAppSecret");

    XIAOMI_APP_ID = methodCall.argument("XiaoMiAppId");
    XIAOMI_APP_KEY = methodCall.argument("XiaomiAppKey");

    if(RomUtil.isMiui()){//如果是小米设备，则初始化小米推送
      initXiaoMiPush(response);
    }else if(RomUtil.isEmui()){
      //如果是华为设备，则初始化华为推送
      getHuaWeiToken(response);
    }else if(RomUtil.isOppo()){
        initOppo();
    }else if(RomUtil.isVivo()){
      initVivoPush();
    }else{
      initJpush();
    }
  }

  private void initJpush(){
    JPushInterface.setDebugMode(true);
    JPushInterface.init(context);
    sendToFlutter("JPush",JPushInterface.getRegistrationID(context),"JPUSH");
  }

  private void initOppo(){
    //初始化push，调用注册接口
    try {
      HeytapPushManager.init(context, true);
      HeytapPushManager.register(context, OPPO_APP_KEY, OPPO_APP_Secret, mPushCallback);//setPushCallback接口也可设置callback
      HeytapPushManager.requestNotificationPermission();
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("opperrpo " + e.toString());
    }
  }

  ///初始化Oppe
  private ICallBackResultService mPushCallback = new ICallBackResultService() {
    @Override
    public void onRegister(int code, String regid) {
      if (code == 0) {
        System.out.println("注册成功  registerId:" + regid);
        Handler successHandler = new SuccessHandler(Looper.getMainLooper());
        Message msg = successHandler.obtainMessage();

        Bundle b = new Bundle();
        b.putString("message", regid);
        b.putString("provider","OPPO");
        msg.setData(b);
        msg.sendToTarget();
      } else {
        System.out.println("注册失败 code=" + code + ",msg=");
      }
    }

    @Override
    public void onUnRegister(int code) {
      if (code == 0) {
        System.out.println("注销成功   code=" + code);
      } else {
        System.out.println("注销失败  code=" + code);
      }
    }

    @Override
    public void onGetPushStatus(final int code, int status) {
      if (code == 0 && status == 0) {
        System.out.println("Push状态正常 code=" + code + ",status=" + status);
      } else {
        System.out.println("Push状态错误 code=" + code + ",status=" + status);
      }
    }

    @Override
    public void onGetNotificationStatus(final int code, final int status) {
      if (code == 0 && status == 0) {
        System.out.println("通知状态正常 code=" + code + ",status=" + status);
      } else {
        System.out.println("通知状态错误 code=" + code + ",status=" + status);
      }
    }

    @Override
    public void onSetPushTime(final int code, final String s) {
      System.out.println("SetPushTime  code=" + code + ",result:" + s);
    }


  };

  ///初始化ViVoPush
  private void initVivoPush(){
    // 在当前工程入口函数，建议在Application的onCreate函数中，添加以下代码
//    PushClient.getInstance(context).initialize();
//// 打开push开关, 关闭为turnOffPush，详见api接入文档
    PushClient.getInstance(context).turnOnPush(new IPushActionListener() {
      @Override
      public void onStateChanged(int state) {
        // TODO: 开关状态处理， 0代表成功
        System.out.println("state...."+state);
        if(state==0){
          String regid = PushClient.getInstance(context).getRegId();
          System.out.println("krcm110"+regid);

          Handler successHandler = new SuccessHandler(Looper.getMainLooper());
          Message msg = successHandler.obtainMessage();

          Bundle b = new Bundle();
          b.putString("message", regid);
          b.putString("provider","VIVO");
          msg.setData(b);
          msg.sendToTarget();
        }
      }
    });
  }

  ///初始化小米
  private void initXiaoMiPush(final MethodChannel.Result response){
    if(shouldInit()) {
      MiPushClient.registerPush(context, XIAOMI_APP_ID, XIAOMI_APP_KEY);
    }
    //打开Log
    LoggerInterface newLogger = new LoggerInterface() {

      @Override
      public void setTag(String tag) {
        // ignore
      }

      @Override
      public void log(String content, Throwable t) {
        Log.d(TAG, content, t);
      }

      @Override
      public void log(String content) {
        Log.d(TAG, content);
      }
    };
    Logger.setLogger(context, newLogger);

      if (sHandler == null) {
          sHandler = new DemoHandler(context);
      }
  }

  ///
  private boolean shouldInit() {
    ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
    List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
    String mainProcessName = context.getPackageName();
    int myPid = Process.myPid();
    for (ActivityManager.RunningAppProcessInfo info : processInfos) {
      if (info.pid == myPid && mainProcessName.equals(info.processName)) {
        return true;
      }
    }
    return false;
  }

  private void getHuaWeiToken(final MethodChannel.Result response) {
    Thread t = new Thread(new Runnable() {
      public void run() {
        try {
          String appId = AGConnectServicesConfig.fromContext(context).getString("client/app_id");
          String token = HmsInstanceId.getInstance(context).getToken(appId, "HCM");
          if(!TextUtils.isEmpty(token)){

            Handler successHandler = new SuccessHandler(Looper.getMainLooper());
            Message msg = successHandler.obtainMessage();

            Bundle b = new Bundle();
            b.putString("message", token);
            b.putString("provider","HUAWEI");
            msg.setData(b);
            msg.sendToTarget();

          }else{
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
              @Override
              public void run() {
                sendToFlutter("huaWeiEmpty","the message is empty","");
                System.out.println("tokentokenEmpty");
              }
            });
          }
        }catch (ApiException e){
          Handler mainHandler = new ErrorHandler(Looper.getMainLooper());
          Message msg = mainHandler.obtainMessage();

          Bundle b = new Bundle();
          b.putString("message", e.getMessage());
          msg.setData(b);
          msg.sendToTarget();
        }
      }
    });
    t.start();
    System.out.println("This is Android Message");
//    response.success("tst");
  }


  ///CallBack Flutter
  private void sendToFlutter(String event,String value,String pushProvider){
    if(eventSink!=null){
      ConstraintsMap params = new ConstraintsMap();
      params.putString("event",event);
      params.putString("value",value);
      params.putString("pushProvider",pushProvider);
      eventSink.success(params.toMap());
    }
  }

  /**
   * MyReceiver
   */
  public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
     //sendToFlutter("huaWeiTokenServicesStart","servicesSuccess","");
      Bundle bundle = intent.getExtras();
      if(intent.getAction().equals(CODELABS_ACTION)){
        if (bundle != null && bundle.getString("msg") != null) {
          String content = bundle.getString("msg");
          String provider = bundle.getString("provider");
          System.out.println("content——————————————————"+content);
          sendToFlutter("tokenSuccess",content,provider);
        }
      }else if(intent.getAction().equals(ACTION_CLICKK_MESSAGE)){

        System.out.println("ACTION_CLICKK_MESSAGE");
        String scheme = bundle.getString("scheme");
        String messageId = bundle.getString("messageId");

        if(Scheme.containsKey(messageId)){
          Scheme.remove(messageId);
//          currentClickMessageId = "";
        }

        ConstraintsMap params = new ConstraintsMap();
        params.putString("event","clickPlushMessage");
        params.putString("scheme",scheme);
        params.putString("messageId",messageId);
        eventSink.success(params.toMap());
      }
      else if(intent.getAction().equals(ACTION_CLICK_NOTIFICATION)){
        if(eventSink!=null){
          ConstraintsMap params = new ConstraintsMap();

          params.putString("event","clickNotification");
          params.putString("groupId",intent.getStringExtra("groupId"));
          params.putString("groupName",intent.getStringExtra("groupName"));
          eventSink.success(params.toMap());
        }
      }
    }
  }


  /**
   * 加入Handler机制
   * 非主线程通知主线程
   */
  class ErrorHandler extends Handler {

    public ErrorHandler() {
      super();
    }

    public ErrorHandler(Looper looper) {
      super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      //int args = msg.arg1;
      //String s = (String)msg.obj;

      //获取bundle对象的值
      Bundle b = msg.getData();
      String value = b.getString("message");
      System.out.println("token error is "+value);
      sendToFlutter("huaWeiTokenError",value,"");
    }
  }

  /**
   * 获取Token成功
   */
  class SuccessHandler extends Handler{

    public SuccessHandler(){
      super();
    }

    public SuccessHandler(Looper looper) {
      super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      //int args = msg.arg1;
      //String s = (String)msg.obj;

      //获取bundle对象的值
      Bundle b = msg.getData();
      String value = b.getString("message");
      String provider = b.getString("provider");
      System.out.println("token is "+value);
      sendToFlutter("tokenSuccess",value,provider);
    }
  }

    public static DemoHandler getHandler() {
        return sHandler;
    }


    public static class DemoHandler extends Handler {

        private Context context;

        public DemoHandler(Context context) {
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg) {
        }
    }


  public static synchronized String getAppName(Context context) {
    try {
      PackageManager packageManager = context.getPackageManager();
      PackageInfo packageInfo = packageManager.getPackageInfo(
              context.getPackageName(), 0);
      return packageInfo.packageName;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}

