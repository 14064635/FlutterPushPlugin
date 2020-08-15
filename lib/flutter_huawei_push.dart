import 'dart:async';

import 'package:flutter/services.dart';

class FlutterPushPlugin {
  static const MethodChannel _channel =
      const MethodChannel('flutter_push_plugin');

  FlutterPushPlugin() {
    initEvent();
  }
  StreamSubscription<dynamic> _eventGetToken;

  initEvent() {
    _eventGetToken = _eventGetTokenFor()
        .receiveBroadcastStream()
        .listen(eventListener, onError: listenerError);
  }

  void eventListener(dynamic event) {
    final Map<dynamic, dynamic> map = event;
    switch (map['event']) {
      case 'tokenSuccess':
        String value = map['value'];
        print("tokenSuccess $value");
        break;
      case 'huaWeiEmpty':
        String value = map['value'];
        print("huaWeiEmpty $value");
        break;
      case 'huaWeiTokenError':
        String value = map['value'];
        print("huaWeiTokenError $value");
        break;
      case 'huaWeiTokenServicesStart':
        String value = map['value'];
        print("huaWeiTokenServicesStart........");
        break;
      case "showNotification":
        print("showNotification........");
        break;
      default:
    }
  }

  void listenerError(Object obj) {
    final PlatformException e = obj;
    throw e;
  }

  EventChannel _eventGetTokenFor() {
    return EventChannel('flutter_push_getToken');
  }

  static Future<String> initPushSDk(String XiaoMiAppId,String XiaomiAppKey,String OppoAppKey,String OppoAppSecret) async {
    final String huawei = await _channel.invokeMethod('initHuaWeiPush',<String,dynamic>{
      "XiaoMiAppId":XiaoMiAppId,
      "XiaomiAppKey":XiaomiAppKey,
      "OppoAppKey":OppoAppKey,
      "OppoAppSecret":OppoAppSecret,
    });

    return huawei;
  }

//  static Future<void> gotoScheme() async {
//    final String huawei = await _channel.invokeMethod('gotoScheme');
//    return huawei;
//  }
//
//  static Future<void> showNotification(String title,String content,String groupId,String groupName) async {
//    final String result =
//        await _channel.invokeMethod('showNotification', <String, dynamic>{
//      "title": title,
//          "content":content,
//          "groupId":groupId,
//          "groupName":groupName,
//    });
//    return result;
//  }
}
