import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_huawei_push/flutter_huawei_push.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
    FlutterPushPlugin flutterHuaweiPush = FlutterPushPlugin();
//    FlutterHuaweiPush.showNotification("nnnnnnnnnnnnssssssssaaaaa", "ooksdldsdksldkl;;asd", "@@Dsdsd", "Ttilte");
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await FlutterPushPlugin.initPushSDk("2882303761518469876","5791846952876","8c39e246163249a0832f60df5cf8513e","2a8bb4ca98a746b1892a029c9f049f41");
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Column(
          children: <Widget>[
            GestureDetector(onTap: (){
//              FlutterHuaweiPush.showNotification("这是表头", "这是内容", "123", "2222");
            },child: Text("点我啊"),),
          ],
        ),
      ),
    );
  }
}
