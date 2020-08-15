# FlutterPushplugin

the plugin is android ofline plush, it support platform of HUAWEI,XIAOMI,OPPO,VIVO and JPUSH.
[1].优先4大手机厂商,保证杀死进程的情况下能收到消息推送。
[2].在不是4大手机生产厂商的时候会用JPUSH取代。JPUSH无法在进程杀死后推送，只能在APP进程存在时才能收到推送。

## 快速集成
tencent_im_plugin: ^[最新版本号]


## 缺陷
[1]由于是集成各大厂商的SDK所以推送依赖于各个厂商的推送消息限制。目前小米和华为支持得最好基本上是秒到，而VIVO每天消息推送限制5条，OPPO延迟非常高，开发者可以在各个手机厂商的后台自行进行测试。
[2]没有集成IOS的相关推送，IOS需要IOS开发人员自行接入。