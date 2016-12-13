/*

Sort: 7
Toc: 1
Tips: 天翼RTC
keywords: appcan开发文档,插件API,uexESurfingRtc 
description: uexESurfingRtc 是基于 WebRTC 技术的 Telco-OTT 实时云通讯能力,降低 App 内多媒体通信开发和提供门槛.为广大 App、网站等开发者提供嵌入式实时语音和视频沟通服务(云平台+终端 SDK),实现互联网通信,降低沟通成本,并在应用内集成,保证用户体验.
天翼RTC平台官方网站：http://www.chinartc.com/dev/ 
天翼RTC开发者支持QQ群：172898609
天翼RTC公有云咨费标准：http://www.chinartc.com/dev/rtcweb/price.html
更多appcan开发文档，请见http://newdocx.appcan.cnShow: /newdocx/docx?type=1468_975

*/



#### **1、简介 ** <ignore>
天翼RTC插件
###### **1.1、说明**<ignore>
 天翼RTC是基于 WebRTC 技术的 Telco-OTT 实时云通讯能力,降低 App 内多媒体通信开发和提供门槛.
  为广大 App、网站等开发者提供嵌入式实时语音和视频沟通服务(云平台+终端 SDK),实现互联网通信,降低沟通成本,并在应用内集成,保证用户体验.
天翼RTC平台官方网站：http://www.chinartc.com/dev/ 
天翼RTC开发者支持QQ群：172898609
天翼RTC公有云咨费标准：http://www.chinartc.com/dev/rtcweb/price.html
 使用之前请先查看[操作手册](/open-service/Third-party-api/Third-party-cloud-chat-RTC "操作手册")

###### ** 1.2、UI展示**<ignore>
*![](http://plugin.appcan.cn/pluginimg/092421w2015m8g12fq.png)![](http://plugin.appcan.cn/pluginimg/092429c2015o8l12bo.png)![](http://plugin.appcan.cn/pluginimg/092437u2015g8e12co.png)*
###### ** 1.3、开源源码**<ignore>
插件测试用例与源码下载:<a href="http://plugin.appcan.cn/details.html?id=471_index" target="_blank">点击</a>插件中心至插件详情页
请使用iOS8+定制引擎打包，否则会导致打包失败。  

#### **2、API概览**<ignore>

###### **2.1、方法**<ignore>
>###### **setAppKeyAndAppId //设置应用程序的appKey和appId接口**

`uexESurfingRtc.setAppKeyAndAppId(appKey, appId)`

**说明:**

使用插件之前,必须先调用此接口,否则会引起预料之外的错误.

**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ----- | ----- | ----- | ----- |
| appKey | String | 是 | 应用程序的appKey |
| appId | String | 是 | 应用程序的appId |

**平台支持:**

Android2.2+
iOS6.0+

**版本支持:**

3.0.0+

**示例:**

```
uexESurfingRtc.setAppKeyAndAppId(appKey, appId); 
```
>###### **login //初始化RTC客户端，并注册至RTC平台接口**

`uexESurfingRtc.login(jsonViewConfig, userName)`

**说明:**

初始化RTC 客户端,并注册至RTC平台接口,回调方法[cbLogStatus](#-cblogstatus-)

**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ----- | ----- | ----- | ----- |
| jsonViewConfig | String | 是 | 通话视频窗口位置和大小数据 |
| username | String | 是 | 本地用户账号，账号不可包含“~”、“-”、空格、中文字符 |
| nickName | String | 否 | 本地用户昵称，只支持iOS |
jsonViewConfig为一json字符串,格式为:
````
  {"localView" : {"x":"10", "y":"800", "w":"432", "h":"528"}, "remoteView" : {"x":"440", "y":"800", "w":"432", "h":"528"}}
````
|  参数名称 |  说明 |
| ----- | ----- |
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

```
var json = {
            localView:{
                x:x1,
                y:y1,
                w:w1,
                h:h1
            },
            remoteView:{
                x:x2,
                y:y2,
                w:w2,
                h:h2
            }
        };
uexESurfingRtc.login(JSON.stringify(json), userName);
 
```
>###### **logout //退出RTC平台接口**

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

```
uexESurfingRtc.logout(); 
```
>###### **call //创建呼叫接口**

`uexESurfingRtc.call(callType, callName, callInfo)`

**说明:**

创建呼叫接口,回调方法[cbCallStatus](#-cbcallstatus-) 

**参数:**

  
|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ----- | ----- | ----- | ----- |
| callType | Number | 是 | 呼叫类型：1-4.1:Audio.2:Audio + Video.3:Audio + Video，RecvOnly.4:Audio + Video，SendOnly.|
| callName | String | 是 | 被叫用户名 |
| callInfo | String | 否 | 应用自定义传递的字符串，不可包含逗号 |

**平台支持:**

Android2.2+
iOS6.0+

**版本支持:**

3.0.0+

**示例:**

```
uexESurfingRtc.call(callType, callName, callInfo); 
```
>###### **acceptCall //接听呼叫接口**

`uexESurfingRtc.acceptCall(callType)`

**说明:**

接听呼叫接口,回调方法[cbCallStatus](#-cbcallstatus-) 

**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ----- | ----- | ----- | ----- |
| callType | Number | 是 | 接听类型:1-4.1:Audio.2:Audio + Video.3:Audio + Video，RecvOnly.4:Audio + Video，SendOnly.|

**平台支持:**

Android2.2+
iOS6.0+

**版本支持:**

3.0.0+

**示例:**

```
uexESurfingRtc.acceptCall(callType); 
```
>###### **hangUp //挂断呼叫接口**
 
`uexESurfingRtc.hangUp()`

**说明:**

挂断呼叫接口,回调方法[cbCallStatus](#-cbcallstatus-) 

**参数:**

无

**平台支持:**

Android2.2+
iOS6.0+

**版本支持:**

  3.0.0+

**示例:**

```
uexESurfingRtc.hangUp(); 
```
>###### **mute //设置静音/取消静音接口**
 
`uexESurfingRtc.mute(value)`

**说明:**

设置静音/取消静音接口

**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ----- | ----- | ----- | ----- |
| value | String | 是 | 静音:"true";取消静音"false"|

**平台支持:**

Android2.2+
iOS6.0+

**版本支持:**

  3.0.0+

**示例:**

```
uexESurfingRtc.mute("true"); 
```
>###### **loudSpeaker //设置扬声器/电话听筒接口**
 
`uexESurfingRtc.loudSpeaker (value)`

**说明:**

设置扬声器/电话听筒接口

**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ----- | ----- | ----- | ----- |
| value | String | 是 | 扬声器:"true";电话听筒"false"|

**平台支持:**

Android2.2+
iOS6.0+

**版本支持:**

  3.0.0+

**示例:**

```
uexESurfingRtc.loudSpeaker("true"); 
```
>###### **setVideoAttr //设置视频清晰度**
 
`uexESurfingRtc.setVideoAttr (value)`

**说明:**

设置视频清晰度

**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ----- | ----- | ----- | ----- |
| value | Number | 是 | 可选值0-7。0：标清；1：流畅；2：高清；3：720P；4：1080P；5：高清横屏（仅支持Android）；6：720P横屏（仅支持Android）；7：1080P横屏（仅支持Android）|

**平台支持:**

Android2.2+
iOS6.0+

**版本支持:**

  3.0.0+

**示例:**

```
uexESurfingRtc.setVideoAttr(videoAttr); 
```
>###### **takeRemotePicture //截屏接口**
 
`uexESurfingRtc.takeRemotePicture ()`

**说明:**

截屏接口,截取远程视频的图像,回调方法[cbRemotePicPath](#-cbremotepicpath-) 
截屏图片,在Android系统中以“png”格式保存在本地,目录为:根目录/appName/photo/,appName为应用的名称.
	图片以时间点命名,如20150520161035.png.
在iOS系统中保存在相册中。

**参数:**

无

**平台支持:**

Android2.2+
iOS6.0+

**版本支持:**

  3.0.0+

**示例:**

```
uexESurfingRtc.takeRemotePicture(); 
```
>###### **sendMessage //发送文本消息接口**
 
`uexESurfingRtc.sendMessage (userName, msg)`

**说明:**

发送文本消息,回调方法[cbMessageStatus](#-cbmessagestatus-) 

**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ----- | ----- | ----- | ----- |
| userName | String | 是 | 被叫用户名|
| msg | String | 是 | 消息内容 |

**平台支持:**

Android2.2+
iOS6.0+

**版本支持:**

  Android3.1.6+
  iOS3.0.11+

**示例:**

```
uexESurfingRtc.sendMessage(userName, msg); 
```
>###### **switchCamera //切换摄像头接口**
 
`uexESurfingRtc.switchCamera (value)`

**说明:**

切换摄像头

**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ----- | ----- | ----- | ----- |
| value | String | 是 | 前置摄像头：”front”，后置摄像头：”back”|

**平台支持:**

Android2.2+
iOS6.0+

**版本支持:**

  Android3.1.7+
  iOS3.0.12+

**示例:**

```
uexESurfingRtc.switchCamera("back");
```
>###### **rotateCamera //旋转摄像头接口**
 
`uexESurfingRtc.rotateCamera (value)`

**说明:**

旋转摄像头

**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ----- | ----- | ----- | ----- |
| value | Number | 是 | 画面逆时针旋转。0：旋转0度；1：旋转90度；2：旋转180度，3：旋转270度|

**平台支持:**

Android2.2+
iOS6.0+

**版本支持:**

  Android3.1.7+
  iOS3.0.12+

**示例:**

```
uexESurfingRtc.rotateCamera(value);
```
>###### **switchView //交换本地与远端视频画面接口**
 
`uexESurfingRtc.switchView ()`

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

```
uexESurfingRtc.switchView(); 
```
>###### **hideLocalView //隐藏本地画面**
 
`uexESurfingRtc.hideLocalView (value)`

**说明:**

隐藏本地画面，对端将看不到本端的画面，显示黑屏

**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ----- | ----- | ----- | ----- |
| value | String | 是 | 显示：”show”，隐藏：”hide”|

**平台支持:**

Android2.2+
iOS6.0+

**版本支持:**

  Android3.1.8+
  iOS3.0.14+

**示例:**

```
uexESurfingRtc.hideLocalView("hide");
```
>###### **groupCreate //创建多人会议**

`uexESurfingRtc.groupCreate(param)`

**说明:**

创建多人会议,回调方法[cbGroupStatus](#-cbGroupStatus-) 

**参数:**

param为json字符串。

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ------------ | ------------ | ------------ | ------------ |
| members | String | 是 | 参会成员账号，多个账号之间以英文逗号隔开，发起人账号要放在第一个，不能为空。账号不可包含“~”、"-"、空格、中文字符 |
| groupType | Number | 是 | 会议类型。取值如下：0：多人语音群聊，每个成员均为可发言状态；1：多人语音对讲，创建后所有成员默认为禁言状态，均需抢麦才可发言；2：多人两方语音，此类型下发起方默认是非禁言状态，其他成员默认为禁言状态，需要主持人给麦才可发言；9：多人语音微直播，此类型下发起方默认是作为多人语音直播方的，并且其他成员默认均为禁言状态；20：多人视频群聊(语音+视频)；21：多人视频对讲(语音+视频)；22：多人两方视频(语音+视频)；29：多人视频微直播，此类型下发起方默认是作为多人视频直播方的，并且其他成员默认为只接收状态； |
| groupName | String | 是 | 群组名称 |
| passWord | String | 是 | 自定义的会议密码 |
| max | Number | 否 | 会议成员数上限，默认为16 |
| screenSplit | Number | 否 | 分屏数量，默认为0。0：由多人服务器自行设置分屏数量，1：1x1，2：1x2，3：2x2，4：2x3，5：3x3 |
| lv | Number | 否 | 是否启用语音激励，默认为0。0：不启用，1：启用。 |

**平台支持:**

Android2.2+
iOS6.0+

**版本支持:**

iOS3.0.18+

**示例:**

```
var param = {
    members:memberName,
    groupType:gType,
    groupName:"myGroup",
    passWord:"123456"
    };
uexESurfingRtc.groupCreate(JSON.stringify(param));
```
>###### **groupMember //获取会议成员列表**

`uexESurfingRtc.groupMember()`

**说明:**

获取会议成员列表,回调方法[cbGroupStatus](#-cbGroupStatus-) 

**参数:**

无

**平台支持:**

Android2.2+
iOS6.0+

**版本支持:**

iOS3.0.18+

**示例:**

```
uexESurfingRtc.groupMember();
```
>###### **groupInvite //邀请成员加入会议**

`uexESurfingRtc.groupInvite(members)`

**说明:**

邀请成员加入会议,回调方法[cbGroupStatus](#-cbGroupStatus-) 

**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ------------ | ------------ | ------------ | ------------ |
| members | String | 是 | 参会成员账号，多个账号之间以英文逗号隔开。账号不可包含“~”、"-"、空格、中文字符 |

**平台支持:**

Android2.2+
iOS6.0+

**版本支持:**

iOS3.0.18+

**示例:**

```
uexESurfingRtc.groupInvite(members);
```
>###### **groupList //查询当前appid的应用所有正在进行的会议**

`uexESurfingRtc.groupList()`

**说明:**

查询当前appid的应用所有正在进行的会议,回调方法[cbGroupStatus](#-cbGroupStatus-) 

**参数:**

无

**平台支持:**

Android2.2+
iOS6.0+

**版本支持:**

iOS3.0.18+

**示例:**

```
uexESurfingRtc.groupList();
```
>###### **groupJoin //主动加入会议**

`uexESurfingRtc.groupJoin(callId, passWord)`

**说明:**

主动加入会议,回调方法[cbGroupStatus](#-cbGroupStatus-) 

**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ------------ | ------------ | ------------ | ------------ |
| callId | String | 是 | 要加入会议的会议id|
| passWord | String | 是 | 要加入会议的的会议密码 |

**平台支持:**

Android2.2+
iOS6.0+

**版本支持:**

iOS3.0.18+

**示例:**

```
uexESurfingRtc.groupJoin(callId,"123456");
```
>###### **groupKick //踢出会议成员**

`uexESurfingRtc.groupKick(members)`

**说明:**

踢出会议成员,回调方法[cbGroupStatus](#-cbGroupStatus-) 

**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ------------ | ------------ | ------------ | ------------ |
| members | String | 是 | 要踢出的成员账号，多个账号之间以英文逗号隔开。账号不可包含“~”、"-"、空格、中文字符。|

**平台支持:**

Android2.2+
iOS6.0+

**版本支持:**

iOS3.0.18+

**示例:**

```
uexESurfingRtc.groupKick(members);
```
>###### **groupClose //结束会议**

`uexESurfingRtc.groupClose()`

**说明:**

结束会议,回调方法[cbGroupStatus](#-cbGroupStatus-) 

**参数:**

无

**平台支持:**

Android2.2+
iOS6.0+

**版本支持:**

iOS3.0.18+

**示例:**

```
uexESurfingRtc.groupClose();
```
>###### **groupMic //进行成员给麦收麦操作**

`uexESurfingRtc.groupMic(members, upMode, downMode)`

**说明:**

进行成员给麦收麦操作,回调方法[cbGroupStatus](#-cbGroupStatus-) 

**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ------------ | ------------ | ------------ | ------------ |
| members | String | 是 | 要操作的成员账号，多个账号之间以英文逗号隔开。账号不可包含“~”、"-"、空格、中文字符。|
| upMode | Number | 是 | 上行媒体流控制，以音频方式发起的会议，控制方式不能带有视频。0：禁止发送语音，收回麦克。1：允许发送语音，给麦。2：禁止发送视频。3：允许发送视频。4：禁止发送语音+视频。5：允许发送语音+视频|
| downMode | Number | 是 | 下行媒体流控制，以音频方式发起的会议，控制方式不能带有视频。0：禁止发送语音，收回麦克。1：允许发送语音，给麦。2：禁止发送视频。3：允许发送视频。4：禁止发送语音+视频。5：允许发送语音+视频|

**平台支持:**

Android2.2+
iOS6.0+

**版本支持:**

iOS3.0.18+

**示例:**

```
uexESurfingRtc.groupMic(members, upMode, downMode);
```
>###### **groupVideo //多人视频画面管理**

`uexESurfingRtc.groupVideo(param)`

**说明:**

多人视频画面管理,回调方法[cbGroupStatus](#-cbGroupStatus-) 

**参数:**

param为json字符串。

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ------------ | ------------ | ------------ | ------------ |
| screenSplit | Number | 否 | 分屏数量，默认为0。0：由多人服务器自行设置分屏数量。1：1x1，2：1x2，3：2x2，4：2x3，5：3x3|
| lv | Number | 否 | 是否启用语音激励，默认为0。0：不启用，1：启用|
| mode | Number | 否 | 成员位置设置，默认为0。0：不设置，1：把memberToSet用户移出显示画面，并由memberToShow用户代替，2：把memberToSet用户设置为最大|
| memberToSet | String | 否 | 当mode值为1或2时，才需传入此参数|
| memberToShow | String | 否 | 当mode值为1时，才需传入此参数|

**平台支持:**

Android2.2+
iOS6.0+

**版本支持:**

iOS3.0.18+

**示例:**

```
var param = {
    screenSplit:split,
    lv:lv,
    mode:mode,
    memberToSet:toset,
    memberToShow:toshow
    };
uexESurfingRtc.groupVideo(JSON.stringify(param));
```
###### **2.2、监听方法**<ignore>
>###### **onGlobalStatus //监听客户端全局状态的回调函数**

`uexESurfingRtc.onGlobalStatus (opId, dataType, data)`

**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ----- | ----- | ----- | ----- |
| opId | Number | 是 |  操作ID,在此函数中不起作用,可忽略 |
| dataType| Number | 是 | 数据类型String标识,可忽略 |
| data | String | 是 | 返回客户端实时状态，详情如下：<br>"ClientListener:onInit,result=":result为初始化结果<br>"获取token: reason:":获取token结果，reason为失败原因<br>  "DeviceListener:onNewCall,call=":有新来电，call为来电信息，其中"ci"为对方携带的呼叫信息，"t"为呼叫类型（1：音频，3：音+视频），"dir"为呼叫方向（1：去电，2：来电），"uri"为对方账号<br>       "ConnectionListener:onConnecting":通话请求连接中<br>       "ConnectionListener:onConnected":通话已接通<br>"ConnectionListener:onVideo":通话接通后，媒体建立成功<br>"ConnectionListener:onDisconnect,code=":通话连接中断，code为错误码<br>"StateChanged,result=200":登录成功<br>    "StateChanged,result=-1001":没有网络<br>      "StateChanged,result=-1002":切换网络<br>       "StateChanged,result=-1003":网络较差<br>       "StateChanged,result=-1004":重连失败需要重新登录<br>       "StateChanged,result=-1500":同一账号在多个终端登录被踢下线<br>       "StateChanged,result=-1501":同一账号在多个设备类型登录<br>"call hangup":主动挂断 <br>"onReceiveIm:from:,msg:":接收到文本消息，from为发送账号，msg为消息内容<br>"APNs:xxx":仅适用于iOS，xxx为推送内容。以下三种情况会触发此回调。a)当应用未启动的情况下收到通知后，点击或滑动通知会触发应用启动，并触发回调；b)当应用在前台运行的情况下收到来电，不会弹出通知，但会触发回调；c)当应用在后台运行的情况下收到通知后，点击或滑动通知会进入应用，并触发回调。 |

**版本支持:**

3.0.0+

**示例:**

```
uexESurfingRtc.onGlobalStatus = updateGlobalStatus;

function updateGlobalStatus(opCode, dataType, data){
        document.getElementById('globalStatus').innerHTML = data;
    } 
```
###### **2.3、回调方法**<ignore>
>###### **cbLogStatus //客户端注册至RTC平台的回调函数**
 
`uexESurfingRtc.cbLogStatus(opId, dataType, data)`

**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ----- | ----- | ----- | ----- |
| opId | Number | 是 |  操作ID,在此函数中不起作用,可忽略 |
| dataType| Number | 是 | 数据类型String标识，可忽略 |
| data | String | 是 | 返回客户端注册至RTC平台的结果，详情如下：<br>"OK:LOGIN":登录成功<br>"OK:LOGOUT":退出成功 <br>"ERROR:PARM_ERROR":登录参数有误 <br>"ERROR:UNINIT":RTC平台未初始化 <br>"ERROR:error_msg":error_msg为SDK <br>返回错误信息，如获取token失败
|

**版本支持:**

3.0.0+

**示例:**

```
uexESurfingRtc.cbLogStatus = updateLogStatus;

function updateLogStatus(opCode, dataType, data){
         document.getElementById('globalStatus').innerHTML = data;
    } 
```
>###### **cbCallStatus //呼叫状态的回调函数**

`uexESurfingRtc.cbCallStatus(opId, dataType, data)`

**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ----- | ----- | ----- | ----- |
| opId | Number | 是 |  操作ID,在此函数中不起作用,可忽略 |
| dataType| Number | 是 | 数据类型String标识，可忽略 |
| data | String | 是 | 返回呼叫的结果，详情如下：<br>"OK:NORMAL"：正常状态（未通话，也无来电）<br>"OK:INCOMING"：有来电，等待接听<br>"OK:CALLING"：呼叫中/接听来电<br>"ERROR:PARM_ERROR"：呼叫参数有误<br>"ERROR:UNREGISTER"：未注册至RTC平台
|

**版本支持:**

3.0.0+

**示例:**

```
uexESurfingRtc.cbCallStatus = updateCallStatus;

function updateCallStatus(opCode, dataType, data){
         document.getElementById('globalStatus').innerHTML = data;
    } 
```
>###### **cbRemotePicPath //截屏的回调函数**

`uexESurfingRtc.cbRemotePicPath(opId, dataType, data);`

**说明:**

截屏的回调函数

**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ----- | ----- | ----- | ----- |
| opId | Number | 是 |  操作ID,在此函数中不起作用,可忽略 |
| dataType| Number | 是 | 数据类型String标识,可忽略 |
| data | String | 是 | 返回截屏图片存储的路径 |

**版本支持:**

3.0.0+

**示例:**

```
uexESurfingRtc.cbRemotePicPath = showRemotePicPath;

function showRemotePicPath(opCode, dataType, data){
        document.getElementById('remotePicPathTx').innerHTML = data;
    } 
```
>###### **cbMessageStatus //收发文本消息的回调函数**

`uexESurfingRtc.cbMessageStatus(opId, dataType, data);`

**说明:**

收发文本消息的回调函数

**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ----- | ----- | ----- | ----- |
| opId | Number | 是 |  操作ID,在此函数中不起作用,可忽略 |
| dataType| Number | 是 | 数据类型String标识,可忽略 |
| data | String | 是 | 返回收发文本消息的结果，详情如下：<br>"OK:SEND":发送文本消息成功<br>"ERROR:code":发送文本消息失败，code为错误码<br>"OK:RECEIVE":接收文本消息成功 |

**版本支持:**

3.0.0+

**示例:**

```
uexESurfingRtc.cbMessageStatus = updateMessageStatus;

function updateMessageStatus(opCode, dataType, data){
        alert(data);
    } 
```
>###### **cbGroupStatus //多人状态的回调函数**

`uexESurfingRtc.cbGroupStatus(opId, dataType, data);`

**说明:**

多人状态的回调函数

**参数:**

|  参数名称 | 参数类型  | 是否必选  |  说明 |
| ------------ | ------------ | ------------ | ------------ |
| opId | Number | 是 |  操作ID，在此函数中不起作用，可忽略 |
| dataType| Number | 是 | 数据类型String标识，可忽略 |
| data | String | 是 | 返回客户端注册至RTC平台的结果，详情如下：<br>“onNewGroupCall,call={"callid":"xx","name":"xx","type"=xx}”：有会议来电，json参数分别表示callid、群组名、会议类型<br>“statusChangedInfo={"uri":"xx","status":xx}”：某个成员在会状态发生变化，uri表示号码，status表示状态<br>“micChangedInfo={"uri":"xx","da":xx,"dv":xx,"ua":xx,"uv":xx}”：某个成员麦克状态发生变化，uri表示号码，da表示下行音频状态，dv表示下行视频状态，ua表示上行音频状态，uv表示上行视频状态<br>“OK:groupCreate,callid="xx"”：创建会议成功，返回callid<br>“OK:groupMember,list="xx","xx"...”：查询成员成功，返回成员号码<br>“OK:groupInvite”：邀请成员成功，被邀请成员会收到来电请求<br>“OK:groupKick”：踢出成员成功<br>“OK:groupList,list={"callid":"xx","name":"xx"},{"callid":"xx","name":"xx"}...”：获取会议列表成功，返回callid和群组名<br>“OK:noGroupList”：当前appid没有会议<br>“OK:groupJoin”：加入会议成功<br>“OK:groupClose”：关闭会议成功<br>“OK:groupMic”：操作麦克成功<br>“OK:groupVideo”：操作画面成功<br>“ERROR:PARM_ERROR”：参数有误，调用接口失败<br>“ERROR:UNCALL”：未创建呼叫，不能操作此接口<br>“ERROR:UNREGISTER”：未注册至RTC平台<br>“ERROR:groupCreate,code=xx”：创建会议失败，返回错误码code<br>“ERROR:groupMember,code=xx”：查询成员失败，返回错误码code<br>“ERROR:groupInvite,code=xx”：邀请成员失败，返回错误码code<br>“ERROR:groupKick,code=xx”：踢出成员失败，返回错误码code<br>“ERROR:groupJoin,code=xx”：加入会议失败，返回错误码code<br>“ERROR:groupClose,code=xx”：关闭会议失败，返回错误码code<br>“ERROR:groupMic,code=xx”：操作麦克失败，返回错误码code<br>“ERROR:groupVideo,code=xx”：操作画面失败，返回错误码code|

**版本支持:**

iOS3.0.18+

**示例:**

```
uexESurfingRtc.cbGroupStatus = updateGroupStatus;

function updateGroupStatus(opCode, dataType, data){
        alert(data);
    } 
```
#### **3、iOS config.xml配置说明** <ignore>

###### **3.1、后台长连接**<ignore>
为保证iOS终端在后台能维持长连接，请在config文件中配置后台模式。

````
<config desc="backgroundConfig" type="AUTHORITY">
      <permission platform="iOS" info="backgroundMode" flag="5"/>
</config>
````
###### **3.2、log开关**<ignore>
iOS支持log开关，$uexESurfingRtc_enablelog$表示是否允许生成log。value值为“1”时表示打印log，“0”为关闭，不配置则默认关闭。开启后会在控制台输出RTC的log信息，并且保存在应用沙盒内的tmp文件夹下。此功能只为了方便开发者调试，正式发布时必须关闭log。

````
<config desc="uexESurfingRtc" type="KEY">
    <param platform="iOS"  name="$uexESurfingRtc_enablelog$" value="1"/>
</config>
````

###### **3.3、隐私权限**<ignore>
天翼RTC插件需要获取设备的麦克风、摄像头、相册权限，在iOS10系统上访问用户隐私数据必须要配置隐私权限。

````
<config desc="uexESurfingRtc" type="AUTHORITY">
    <permission platform="iOS" info="privacy" type="camera">
        <string>应用需要使用摄像头</string>
    </permission>
    <permission platform="iOS" info="privacy" type="microphone">
        <string>应用需要使用麦克风</string>
    </permission>
	<permission platform="iOS" info="privacy" type="photoLibrary">
        <string>应用需要访问相册</string>
    </permission>
</config>
````

###### **3.4、来电本地通知**<ignore>
$uexESurfingRtc_notification$表示当应用在后台时，来电通知的展现内容（仅支持iOS 9及其以下系统，若iOS 10及其以上系统需要支持后台通知，需要配置APNs证书，请参考后续文档说明）。value的取值与通知内容的对应关系如下：
（1）value="callName"，则通知内容="来电：xx"，xx为具体的来电号码（即call接口的callName参数）。
（2）value="callInfo"，则通知内容="来电：xx"，xx为呼叫附加信息（即call接口的callInfo参数，可以传入昵称等自定义信息）。
（3）value=任意字符串，则通知内容=value，若value中包含"callName"、"callInfo"或"nickName"字段，则通知内容的相应位置会替换为具体的"callName"、"callInfo"或"nickName"的值。
（4）value="hideNotification"，则通知内容不显示。
（5）若不配置则默认value为“callName”。
（6）多人通知name="notificationGrp"，value=任意字符串，若value中包含"grpName"字段，则通知内容的相应位置会替换为具体的"grpName"的值。

````
<config desc="uexESurfingRtc" type="KEY">
    <param platform="iOS"  name="$uexESurfingRtc_notification$" value="callName"/>
    <param platform="iOS"  name="$uexESurfingRtc_notificationGrp$" value="群组来电:grpName"/>
</config>
````
###### **3.5、APNs配置**<ignore>
若应用在后台时被清除进程，或是设备启动后没有启动应用，或是iOS 10及其以上系统的应用进入后台，那么此时只能通过苹果的APNs来接收推送。目前仅支持推送点对点来电通知，通知格式为“来自 xxx 的来电”，其中xxx内容为登录账号。若开发者需要自定义此消息，请联系RTC官方进行后台配置。
开发APNs推送功能流程如下：
（1）开发者首先需要在Apple Developer官网上申请推送证书，生成cer格式的文件。推送证书分为两种：开发证书和生产证书，分别用于测试应用和正式发布应用。
（2）不同应用有不同的推送证书，与应用的Bundle Identifier绑定，注意，生成推送证书后，对应的APP ID的Push Notifications项应变为enable，如果没有请手动编辑。
（3）将cer证书双击导入钥匙串，生成推送许可证书，右键证书导出为p12格式的文件，生成过程中需要设置密码，请牢记密码。
（4）将p12文件、导出密码、应用名称、应用的Bundle Identifier等信息发给RTC官方，可通过qq或邮件的形式发给开发者支持群里的” iOS SDK＠天翼RTC”(807382934@qq.com)。RTC官方审核后会生成推送所需要的pushId、pushKey、pushMaster，并将其发送给开发者，请妥善保存。不同的应用需要申请不同的pushId、pushKey、pushMaster，不能混用。
（5）在config.xml文件中配置以下内容，enableAPNs为1表示开启APNs功能，0为关闭：
```
<config desc="uexESurfingRtc" type="KEY">
    <param platform="iOS"  name="$uexESurfingRtc_enableAPNs$" value="1" />
    <param platform="iOS"  name="$uexESurfingRtc_pushid$" value="替换为RTC官方分配的pushid" />
    <param platform="iOS"  name="$uexESurfingRtc_pushkey$" value="替换为RTC官方分配的pushkey" />
    <param platform="iOS"  name="$uexESurfingRtc_pushmastersecret$" value="替换为RTC官方分配的pushmastersecret" />
</config>
<config desc="uexESurfingRtc" type="ENTITLEMENTS">
    <entitlement type="apns"></entitlement>
</config>
```
（6）使用配置了APNs的证书进行打包。
（7）需要保证应用安装后至少启动过一次，APNs才能开始推送。此后即使应用未启动，也能收到来电通知。
（8）以下三种情况会触发onGlobalStatus回调，回调内容为“APNs:xxx”，xxx为推送内容。a)当应用未启动的情况下收到通知后，点击或滑动通知会触发应用启动，并触发回调；b)当应用在前台运行的情况下收到来电，不会弹出通知，但会触发回调；c)当应用在后台运行的情况下收到通知后，点击或滑动通知会进入应用，并触发回调。
  
###### **4、常见错误码**<ignore>

- 400   //请求类错误4xx的下界
- 403   //被踢或token失效，请重新获取token，重新注册
- 404   //呼叫的号码不存在，被叫号码从未获取token登录过
- 408   //超时，请求服务器超时或被呼叫方网络异常
- 480   //对方不在线，对方未登陆，或网络异常断开一段时间
- 486   //正忙
- 487   //取消呼叫
- 488   //媒体协商失败
- 500   //服务器类错误5xx的下界
- 503   //网络不可用或服务器错误
- 600   //全局错误6xx的下界
- 603   //被叫拒接，或服务器拒绝请求
- 891   //已经在其他地方接听
- 1001  //内存错误；或网络断开，可选择是否挂断正在进行的通话
- 1002  //参数错误；或连接上了网络，可以继续呼叫
- 1003  //缺少参数；或网络闪断，可以忽略，不影响呼叫
- 1004  //重置参数失败；或重连失败应用可以选择重新登录，应限制呼叫
- 1005  //呼叫失败
- 1006  //呼叫结束
- 1007  //动作失败
- 1008  //sdk已初始化
- 1009  //sdk初始化不完整
- 1010  //容量溢出
- 1011  //无效函数
- 1500  同一账号在另一同类型终端登录，被踢，应限制呼叫
- 1501  同一账号在不同类型终端登录，不影响呼叫
- 3011  //操作失败
- 3012  //成员已在其他会议中
- 3059  //被控制的会议不存在
- 3061  //会议中不存在此成员
- 3062  //没有权限控制
- 3064  //要加入的会议不存在
- 
###### **5、更新历史**<ignore>

###### **iOS**<ignore>

API版本: `uexESurfingRtc-3.0.18`

最近更新时间:`2016-12-13`

|  历史发布版本 | 更新内容    |
| ------------ | ------------  |
| 3.0.18  | 新增多人会话接口，支持APNs推送，login增加昵称参数，优化网络重连机制  |
| 3.0.17  | xcode8编译支持iOS10，支持自定义通知   |
| 3.0.16  | 优化通话流畅性与稳定性，恢复onGlobalStatus的回调时间   |
| 3.0.15  | 优化多终端登录互踢机制，去掉onGlobalStatus的回调时间   |
| 3.0.14  | 增加hideLocalView接口，被叫接听时显示窗口，支持ipv6，支持自定义后台来电通知   |
| 3.0.13  | 优化视频窗口大小   |
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

###### **Android**<ignore>

API版本: `uexESurfingRtc-3.1.9`

最近更新时间:`2016-12-13`

| 历史发布版本 | 更新内容 |
| ------------ |  ------------ |
| 3.1.9    | 修复多页面 |
| 3.1.8    | 新增hideLocalView接口，原生sdk替换为2.7.0 |
| 3.1.7    | 新增切换和旋转摄像头、交换窗口接口，call接口增加callInfo参数 |
| 3.1.6    | 支持单例，全部回调到root页，增加文本聊天接口，完善事件通知，调整callType的值 |
| 3.1.5    | 升级sdk2.6 |
| 3.1.4    | 升级RTC sdk |
| 3.1.3    | 添加设置appKey和appId的回调 |
| 3.1.2    | 升级RTC sdk |
| 3.1.1    | 升级RTC sdk |
| 3.1.0    |  初始化插件。 ||
		
