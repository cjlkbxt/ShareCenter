分享中心组件
1、在宿主app中的onCreate调用初始化方法，入参为微信的app_id以及给每个应用分配的app_key。
如：ShareCenterHelper.init(CommonKeys.WEIXIN_APP_ID, "LEQIAI")
分配的app_key如下：
乐其爱相馆: LEQIAI
乐其爱结婚登记照: COUPLE
智能证件照:CAMCAP

2、在app目录的build.gradle文件中加入
manifestPlaceholders = [
APPLICATION_ID: "ai.leqi.love"
]
(APPLICATION_ID的值为当前应用的包名)
用于替换分享中心组件manifest文件中的APPLICATION_ID值。

3、宿主app的跳转分享中心的入口处调用跳转，
val intent = Intent(activity, ShareCenterActivity::class.java)
activity.start(intent)
