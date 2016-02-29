[TOC]
# 1、简介  
天翼RTC插件
## 1.1、说明
 天翼RTC是基于 WebRTC 技术的 Telco-OTT 实时云通讯能力，降低 App 内多媒体通信开发和提供门槛。
  为广大 App、网站等开发者提供嵌入式实时语音和视频沟通服务（云平台+终端 SDK），实现互联网通信，降低沟通成本，并在应用内集成，保证用户体验。
  天翼RTC平台官方网站：http://www.chinartc.com/dev/ 
  天翼RTC开发者支持QQ群：172898609
> 使用之前请先查看[操作手册](http://newdocx.appcan.cn/newdocx/docx?type=1469_1278 "操作手册")

## 1.2、UI展示
![](http://plugin.appcan.cn/pluginimg/092421w2015m8g12fq.png)![](http://plugin.appcan.cn/pluginimg/092429c2015o8l12bo.png)![](http://plugin.appcan.cn/pluginimg/092437u2015g8e12co.png)
## 1.3、开源源码
插件测试用例与源码下载：[点击](http://plugin.appcan.cn/details.html?id=471_index) 插件中心至插件详情页  

# 2、API概览

## 2.1、方法
> ### setAppKeyAndAppId 设置应用程序的appKey和appId接口

`uexESurfingRtc.setAppKeyAndAppId(appKey, appId)`
**说明:**
使用插件之前，必须先调用此接口，否则会引起预料之外的错误。
**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ------------ | ------------ | ------------ | ------------ |
| appKey | String | 是 | 应用程序的appKey |
| appId | String | 是 | 应用程序的appId |

**平台支持:**
Android2.2+
iOS6.0+
**版本支持:**
3.0.0+
**示例:**
[参考](#1.3、开源源码)
> ### login 初始化RTC 客户端，并注册至RTC平台接口

`uexESurfingRtc.login(jsonViewConfig, userName)`
**说明:**
初始化RTC 客户端，并注册至RTC平台接口，回调方法[cbLogStatus](#cbLogStatus 客户端注册至RTC平台的回调函数)
**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ------------ | ------------ | ------------ | ------------ |
| jsonViewConfig | String | 是 | 通话视频窗口位置和大小数据 |
| username | String | 是 | 本地用户账号 |
jsonViewConfig为一json字符串，格式为：
````
  {"localView" : {"x":"10", "y":"800", "w":"432", "h":"528"}, "remoteView" : {"x":"440", "y":"800", "w":"432", "h":"528"}}
````
|  参数名称 |  说明 |
| ------------ | ------------ |
| localView  | 本地窗口配置信息 |
| remoteView | 远程窗口配置信息 |
| x | 窗口起始x坐标 |
| y | 窗口起始y坐标 |
| w | 窗口宽度 |
| h | 窗口高度 |

**平台支持:**
Android2.2+
iOS6.0+
**版本支持:**
3.0.0+
**示例:**
[参考](#1.3、开源源码)
> ### logout 退出RTC平台接口

`uexESurfingRtc.logout()`
**说明:**
退出RTC平台接口
**参数:**
无
**平台支持:**
Android2.2+
iOS6.0+
**版本支持:**
3.0.0+
**示例:**
[参考](#1.3、开源源码)
> ### call 创建呼叫接口

`uexESurfingRtc.call(callType, callName, callInfo)`
**说明:**
创建呼叫接口，回调方法[cbCallStatus](#cbCallStatus 呼叫状态的回调函数) 
**参数:**
  
|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ------------ | ------------ | ------------ | ------------ |
| callType | Number | 是 | 呼叫类型。1:Audio。2:Audio + Video。3:Audio + Video，RecvOnly。4:Audio + Video，SendOnly。|
| callName | String | 是 | 被叫用户名 |
| callInfo | String | 否 | 应用自定义传递的字符串，不可包含逗号。 |
**平台支持:**
Android2.2+
iOS6.0+
**版本支持:**
3.0.0+
**示例:**
[参考](#1.3、开源源码)
> ### acceptCall 接听呼叫接口

`uexESurfingRtc.acceptCall(callType)`
**说明:**
接听呼叫接口，回调方法[cbCallStatus](#cbCallStatus 呼叫状态的回调函数) 
**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ------------ | ------------ | ------------ | ------------ |
| callType | Number | 是 | 接听类型。1:Audio。2:Audio + Video。3:Audio + Video，RecvOnly。4:Audio + Video，SendOnly。|

**平台支持:**
Android2.2+
iOS6.0+
**版本支持:**
3.0.0+
**示例:**
[参考](#1.3、开源源码)
> ### hangUp 挂断呼叫接口
 
`uexESurfingRtc.hangUp()`
**说明:**
挂断呼叫接口，回调方法[cbCallStatus](#cbCallStatus 呼叫状态的回调函数) 
**参数:**
无
**平台支持:**
Android2.2+
iOS6.0+
**版本支持:**
  3.0.0+
**示例:**
[参考](#1.3、开源源码)
> ### mute 设置静音/取消静音接口
 
`uexESurfingRtc.mute(value)`
**说明:**
设置静音/取消静音接口
**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ------------ | ------------ | ------------ | ------------ |
| value | String | 是 | 静音："true"；取消静音"false"|
**平台支持:**
Android2.2+
iOS6.0+
**版本支持:**
  3.0.0+
**示例:**
[参考](#1.3、开源源码)
> ### loudSpeaker 设置扬声器/电话听筒接口
 
`uexESurfingRtc.loudSpeaker(value)`
**说明:**
设置扬声器/电话听筒接口
**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ------------ | ------------ | ------------ | ------------ |
| value | String | 是 | 扬声器："true"；电话听筒"false"|
**平台支持:**
Android2.2+
iOS6.0+
**版本支持:**
  3.0.0+
**示例:**
[参考](#1.3、开源源码)
> ### setVideoAttr 设置视频清晰度
 
`uexESurfingRtc.setVideoAttr(value)`
**说明:**
设置视频清晰度
**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ------------ | ------------ | ------------ | ------------ |
| value | Number | 否 | 可选值0-2。0：标清；1：流畅；2：高清|
**平台支持:**
Android2.2+
iOS6.0+
**版本支持:**
  3.0.0+
**示例:**
[参考](#1.3、开源源码)
> ### takeRemotePicture 截屏接口
 
`uexESurfingRtc.takeRemotePicture()`
**说明:**
截屏接口，截取远程视频的图像，回调方法[cbRemotePicPath](#cbRemotePicPath 截屏的回调函数) 
截屏图片，以“png”格式保存在本地，目录为：根目录/appName/photo/，appName为应用的名称。
	图片以时间点命名，如20150520161035.png。

**参数:**
无
**平台支持:**
Android2.2+
iOS6.0+
**版本支持:**
  3.0.0+
**示例:**
[参考](#1.3、开源源码)
> ### sendMessage 发送文本消息接口
 
`uexESurfingRtc.sendMessage(userName, msg)`
**说明:**
发送文本消息，回调方法[cbMessageStatus](#cbMessageStatus 收发文本消息的回调函数) 


**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ------------ | ------------ | ------------ | ------------ |
| userName | String | 是 | 被叫用户名|
| msg | String | 是 | 消息内容 |
**平台支持:**
Android2.2+
iOS6.0+
**版本支持:**
  Android3.1.6+
  iOS3.0.11+
**示例:**
[参考](#1.3、开源源码)
> ### switchCamera 切换摄像头接口
 
`uexESurfingRtc.switchCamera(value)`
**说明:**
切换摄像头


**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ------------ | ------------ | ------------ | ------------ |
| value | String | 是 | 前置摄像头：”front”，后置摄像头：”back”|
**平台支持:**
Android2.2+
iOS6.0+
**版本支持:**
  Android3.1.7+
  iOS3.0.12+
**示例:**
[参考](#1.3、开源源码)
> ### rotateCamera 旋转摄像头接口
 
`uexESurfingRtc.rotateCamera(value)`
**说明:**
旋转摄像头


**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ------------ | ------------ | ------------ | ------------ |
| value | Number | 是 | 画面逆时针旋转。0：旋转0度；1：旋转90度；2：旋转180度，3：旋转270度|
**平台支持:**
Android2.2+
iOS6.0+
**版本支持:**
  Android3.1.7+
  iOS3.0.12+
**示例:**
[参考](#1.3、开源源码)
> ### switchView 交换本地与远端视频画面接口
 
`uexESurfingRtc.switchView()`
**说明:**
交换本地与远端视频画面
**参数:**
无
**平台支持:**
Android2.2+
iOS6.0+
**版本支持:**
  Android3.1.7+
  iOS3.0.12+
**示例:**
[参考](#1.3、开源源码)
## 2.2、监听方法
> ### onGlobalStatus 监听客户端全局状态的回调函数

`uexESurfingRtc.onGlobalStatus(opId, dataType, data)`
**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ------------ | ------------ | ------------ | ------------ |
| opId | Number | 是 |  操作ID，在此函数中不起作用，可忽略 |
| dataType| Number | 是 | 数据类型String标识，可忽略 |
| data | String | 是 | 返回客户端实时状态，详情如下：<br>"ClientListener:onInit,result=":result为初始化结果<br>"获取token: reason:":获取token结果，reason为失败原因<br>  "DeviceListener:onNewCall,call=":有新来电，call为来电信息，其中"ci"为对方携带的呼叫信息，"t"为呼叫类型（1：音频，3：音+视频），"dir"为呼叫方向（1：去电，2：来电），"uri"为对方账号<br>       "ConnectionListener:onConnecting":通话请求连接中<br>       "ConnectionListener:onConnected":通话已接通<br>       "ConnectionListener:onDisconnect,code=":通话连接中断，code为错误码<br>"StateChanged,result=200":登录成功<br>    "StateChanged,result=-1001":没有网络<br>      "StateChanged,result=-1002":切换网络<br>       "StateChanged,result=-1003":网络较差<br>       "StateChanged,result=-1004":重连失败需要重新登录<br>       "StateChanged,result=-1500":同一账号在多个终端登录被踢下线<br>       "StateChanged,result=-1501":同一账号在多个设备类型登录<br>"call hangup":主动挂断 <br>"onReceiveIm:from:,msg:":接收到文本消息，from为发送账号，msg为消息内容|

**版本支持:**
3.0.0+
**示例：**
## 2.3、回调方法
> ###cbLogStatus 客户端注册至RTC平台的回调函数
 
`uexESurfingRtc.cbLogStatus(opId, dataType, data)`
**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ------------ | ------------ | ------------ | ------------ |
| opId | Number | 是 |  操作ID，在此函数中不起作用，可忽略 |
| dataType| Number | 是 | 参数类型详见CONSTANT中Callback方法数据类型 |
| data | String | 是 | 返回客户端注册至RTC平台的结果，详情如下：<br>"OK:LOGIN":登录成功<br>"OK:LOGOUT":退出成功 <br>"ERROR:PARM_ERROR":登录参数有误 <br>"ERROR:UNINIT":RTC平台未初始化 <br>"ERROR:error_msg":error_msg为SDK <br>返回错误信息，如获取token失败|

**版本支持:**
3.0.0+
**示例：**
[参考](#1.3、开源源码)
> ###cbCallStatus 呼叫状态的回调函数

`uexESurfingRtc.cbCallStatus(opId, dataType, data)`
**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ------------ | ------------ | ------------ | ------------ |
| opId | Number | 是 |  操作ID，在此函数中不起作用，可忽略 |
| dataType| Number | 是 | 参数类型详见CONSTANT中Callback方法数据类型|
| data | String | 是 | 返回呼叫的结果，详情如下：<br>"OK:NORMAL"：正常状态（未通话，也无来电）<br>"OK:INCOMING"：有来电，等待接听<br>"OK:CALLING"：呼叫中/接听来电<br>"ERROR:PARM_ERROR"：呼叫参数有误<br>"ERROR:UNREGISTER"：未注册至RTC平台|

**版本支持:**
3.0.0+
**示例：**
[参考](#1.3、开源源码)
> ###cbRemotePicPath 截屏的回调函数
 
`uexESurfingRtc.cbRemotePicPath(opId, dataType, data)`
**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ------------ | ------------ | ------------ | ------------ |
| opId | Number | 是 |  操作ID，在此函数中不起作用，可忽略 |
| dataType| Number | 是 | 数据类型String标识，可忽略 |
| data | String | 是 | 返回截屏图片存储的路径 |

**版本支持:**
3.0.0+
**示例：**
[参考](#1.3、开源源码)
> ###cbMessageStatus 收发文本消息的回调函数

`uexESurfingRtc.cbMessageStatus(opId, dataType, data)`
**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ------------ | ------------ | ------------ | ------------ |
| opId | Number | 是 |  操作ID，在此函数中不起作用，可忽略 |
| dataType| Number | 是 | 参数类型详见CONSTANT中Callback方法数据类型|
| data | String | 是 | 返回收发文本消息的结果，详情如下：<br>"OK:SEND":发送文本消息成功<br>"ERROR:code":发送文本消息失败，code为错误码<br>"OK:RECEIVE":接收文本消息成功|

**版本支持:**
Android3.1.6+
iOS3.1.1+
**示例：**
[参考](#1.3、开源源码)
# 3、更新历史

API 版本：uexESurfingRtc-3.0.12(iOS) uexESurfingRtc-3.1.7（Android）
最近更新时间：2016-2-29

|  历史发布版本 | iOS更新    |
| ------------ | ------------  |
| 3.0.12    | 新增切换和旋转摄像头、交换窗口接口，call接口增加callInfo参数 |
| 3.0.11  | 新增接口sendMessage，修改call和acceptCall的calltype   |
| 3.0.10  | 单例类增加后缀RTC   |
| 3.0.9  | 修改为单例模式，回调返回至root窗口    |
| 3.0.8  | 修改cbCallStatus和onGlobalStatus回调     |
| 3.0.7  | 修改注册和挂断问题，增加后台重连机制     |
| 3.0.6  | 升级RTC sdk 增加appid 和appkey 回调     |
| 3.0.5  | 改为主函数回调     |
| 3.0.4 | 测试接收appid和appkey的方式     |
| 3.0.3  | 改变接收appid和appkey的方式     |
| 3.0.2  |  测试setappid接口     |
| 3.0.1  | doNavigation参数默认传cloud2    |
| 3.0.0  | 天翼RTC插件   ||
 

最近更新时间：2016-2-29

|  历史发布版本 | 安卓更新  |
| ------------ |  ------------ |
| 3.1.7    | 新增切换和旋转摄像头、交换窗口接口，call接口增加callInfo参数 |
| 3.1.6    | 支持单例，全部回调到root页，增加文本聊天接口，完善事件通知，调整callType的值 |
| 3.1.5    | 升级sdk2.6 |
| 3.1.4    | 升级RTC sdk |
| 3.1.3    | 添加设置appKey和appId的回调 |
| 3.1.2    | 升级RTC sdk |
| 3.1.1    | 升级RTC sdk |
| 3.1.0    |  初始化插件。 ||
 
