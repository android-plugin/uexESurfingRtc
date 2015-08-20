/**
 * 
 * login 包括init和register
 * */

package org.zywx.wbpalmstar.plugin.uexesurfingrtc;

import jni.http.HttpManager;
import jni.http.HttpResult;
import jni.http.RtcHttpClient;
import jni.util.Utils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.zywx.wbpalmstar.plugin.uexesurfingrtc.EUExesurfingRtc.CbHandler;
import org.zywx.wbpalmstar.plugin.uexesurfingrtc.utils.ConstantUtils;
import org.zywx.wbpalmstar.plugin.uexesurfingrtc.utils.LogUtils;

import rtc.sdk.clt.RtcClientImpl;
import rtc.sdk.common.RtcConst;
import rtc.sdk.common.SdkSettings;
import rtc.sdk.iface.ClientListener;
import rtc.sdk.iface.Device;
import rtc.sdk.iface.DeviceListener;
import rtc.sdk.iface.RtcClient;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class RtcLogin{

    /** The LOGTAG. */
    private String LOGTAG = LogUtils.getTAG();
    private final boolean DEBUG = true;

    private Context mContext = null;
    private CbHandler mCbhandler = null;
    private Handler mHandler = null;
    
    public RtcLogin(Context mContext, Handler mHandler, CbHandler mCbhandler) 
    {
        this.mContext = mContext;
        this.mHandler = mHandler;
        this.mCbhandler = mCbhandler;
    }
    
    private String getRegisterJson(String initStr, String userName)
    {
        String jsonStr = "";
        try {
            jsonStr = new JSONStringer().object()
                    .key(ConstantUtils.REGISTER_INIT_KEY).value(ConstantUtils.TRUE_STR)
                    .key(ConstantUtils.REGISTER_USER_NAME_KEY).value(userName).endObject().toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonStr;
    }
    
    /**
     * Inits the rtc client impl.
     */
    public RtcClient initRtcClientImpl(final String userName)
    {
        final RtcClient mClt = new RtcClientImpl();
        try {

            mClt.initialize(mContext.getApplicationContext(), new ClientListener() {
                @Override   //初始化结果回调
                public void onInit(int result) {
                    Utils.PrintLog(5,"ClientListener","onInit,result="+result);
                    mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GLOBAL_STATUS, 
                            "ClientListener:onInit,result="+result);
                    String jsonStr = "";
                    if(result == 0) {
                        mClt.setAudioCodec(RtcConst.ACodec_OPUS);
                        mClt.setVideoCodec(RtcConst.VCodec_VP8);
                        mClt.setVideoAttr(RtcConst.Video_SD);
                        
                        jsonStr = getRegisterJson(ConstantUtils.TRUE_STR, userName);
                    }
                    else
                    {
                        jsonStr = getRegisterJson(ConstantUtils.FALSE_STR, "");
                    }
                    
                    Message msg = new Message();
                    msg.what = ConstantUtils.MSG_RTC_CLIENT_ON_INIT;
                    msg.obj = jsonStr;
                    mHandler.sendMessage(msg);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mClt;
    }
    
    public void register(RegisterConfig mRegister)
    {
        LogUtils.logWlDebug(DEBUG, LogUtils.getLineInfo() + "into register");
        if((mRegister.mClt != null) && mRegister.mInit)
        {
            if (mRegister.mAcc == null)
            {
                //获取副uwqi
                restRegister(mRegister.userName);
            } 
        }
        else
        {
            mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_LOG_STATUS, 
                    ConstantUtils.ERROR_MSG_UNINIT);
        }
    }
    
    /**
     * Rest register.
     */
    private void restRegister(final String userName)
    {
        new Thread(){
            public void run()
            {
                optGetToken(userName);
            }
        }.start();  

        //如果直接注册  
    }
    
    /**
     * 终端直接从rtc平台获取token，应用产品需要通过自己的服务器来获取，rtc平台接口请参考开发文档<2.5>章节.
     */
    private void optGetToken(String userName) 
    {
        RtcConst.UEAPPID_Current = RtcConst.UEAPPID_Self;//账号体系，包括私有、微博、QQ等，必须在获取token之前确定。
        JSONObject jsonobj = HttpManager.getInstance()
                .CreateTokenJson(0, userName, RtcHttpClient.grantedCapabiltyID, "");
        HttpResult ret = HttpManager
                .getInstance().getCapabilityToken(jsonobj,
                        RtcBase.getAppId(), RtcBase.getAppKey());
        mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GLOBAL_STATUS,
                "获取token:"+ret.getStatus()+" reason:"+ret.getErrorMsg());
        Message msg = new Message();
        RegisterInfo mRegisterInfo = new RegisterInfo();
        mRegisterInfo.userName = userName;
        mRegisterInfo.mHttpResult = ret;
        msg.what = ConstantUtils.MSG_GETTOKEN;
        msg.obj = mRegisterInfo;
        mHandler.sendMessage(msg);
    }
    
    /**
     * response_get token.
     *
     * @param msg the msg
     * @param mAListener 
     * @param mClt 
     * @param mAcc 
     */
    public Device onResponseGetToken(Message msg, Device mAcc, 
            RtcClient mClt, DeviceListener mAListener) 
    {
        RegisterInfo mRegisterInfo = (RegisterInfo)msg.obj;
        HttpResult ret = mRegisterInfo.mHttpResult;
        String userName = mRegisterInfo.userName;
        Utils.PrintLog(5, LOGTAG, 
                "handleMessage getCapabilityToken status:" + ret.getStatus());
        JSONObject jsonrsp = (JSONObject)ret.getObject();
        String capabailitytoken = "";
        if((jsonrsp != null) && (false == jsonrsp.isNull("code")))
        {
            try {
                String code = jsonrsp.getString(RtcConst.kcode);
                String reason = jsonrsp.getString(RtcConst.kreason);
                Utils.PrintLog(5, LOGTAG, 
                        "Response getCapabilityToken code:" + code + " reason:" + reason);
                if("0".equals(code))
                {
                    capabailitytoken =jsonrsp.getString(RtcConst.kcapabilityToken);

                    Utils.PrintLog(5, LOGTAG,
                            "handleMessage getCapabilityToken:" + capabailitytoken);
                    mAcc = onRegister(mAcc, mClt, mAListener, userName, capabailitytoken);
                }
                else
                {
                    Utils.PrintLog(5, LOGTAG, 
                            "获取token失败 [status:" + ret.getStatus()+"]" + ret.getErrorMsg());
                    mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_LOG_STATUS, ConstantUtils.ERROR_MSG_ERROR 
                            + "获取token失败 [status:"+ret.getStatus()+"]"+ret.getErrorMsg());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return mAcc;
    }
    
    /**
     * register.
     * @param mAListener 
     * @param mClt 
     * @param mAcc 
     *
     * @param sname the sname
     * @param spwd the spwd
     */
    private Device onRegister(Device mAcc, RtcClient mClt, 
            DeviceListener mAListener, String sname ,String spwd) {
        Utils.PrintLog(5, LOGTAG, "OnRegister:"+sname+"spwd:"+spwd);
        try {
            JSONObject jargs = SdkSettings.defaultDeviceSetting();
            jargs.put(RtcConst.kAccPwd,spwd);
            
            //账号格式形如“账号体系-号码~应用id~终端类型”，以下主要设置账号内各部分内容，其中账号体系的值要在获取token之前确定，默认为私有账号
            jargs.put(RtcConst.kAccAppID, RtcBase.getAppId());//应用id
            //jargs.put(RtcConst.kAccName,"逍遥神龙");
            jargs.put(RtcConst.kAccUser,sname); //号码
            jargs.put(RtcConst.kAccType,RtcConst.UEType_Current);//终端类型
            
            mAcc = mClt.createDevice(jargs.toString(), mAListener); //注册
            mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_LOG_STATUS, 
                    ConstantUtils.LOG_STATUS_LOGIN);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mAcc;
    }
    
    public static int getCallType(int typeId)
    {
        int callType = typeId;
        switch (typeId) 
        {
        case ConstantUtils.CALL_TYPE_RECV_ONLY:
            callType = RtcConst.CallType_A_V_M;
            break;
        case ConstantUtils.CALL_TYPE_SEND_ONLY:
            callType = RtcConst.CallType_A_V_L;
            break;
        default:
            break;
        }
        return callType;
    }
}

class RegisterConfig{
    boolean mInit;
    String userName;
    RtcClient mClt;
    Device mAcc;
}

class RegisterInfo{
    String userName;
    HttpResult mHttpResult;
}
