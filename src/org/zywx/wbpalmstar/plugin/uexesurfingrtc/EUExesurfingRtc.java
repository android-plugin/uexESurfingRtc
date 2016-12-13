package org.zywx.wbpalmstar.plugin.uexesurfingrtc;

import java.util.ArrayList;

import jni.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;
import org.zywx.wbpalmstar.engine.universalex.EUExCallback;
import org.zywx.wbpalmstar.plugin.uexesurfingrtc.utils.BaseUtils;
import org.zywx.wbpalmstar.plugin.uexesurfingrtc.utils.ConstantUtils;
import org.zywx.wbpalmstar.plugin.uexesurfingrtc.utils.LogUtils;

import rtc.sdk.common.RtcConst;
import rtc.sdk.core.RtcRules;
import rtc.sdk.iface.Connection;
import rtc.sdk.iface.ConnectionListener;
import rtc.sdk.iface.Device;
import rtc.sdk.iface.DeviceListener;
import rtc.sdk.iface.RtcClient;
import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

/*入口类*/
@SuppressLint("ShowToast")
public class EUExesurfingRtc extends EUExBase {
    
    /** The LOGTAG. */
    private String LOGTAG = LogUtils.getTAG();
	/*服务器端已定义*/
    public static final String CALLBACK_GLOBAL_STATUS = "uexESurfingRtc.onGlobalStatus";
    public static final String CALLBACK_LOG_STATUS = "uexESurfingRtc.cbLogStatus";
    public static final String CALLBACK_CALL_STATUS = "uexESurfingRtc.cbCallStatus";
    public static final String CALLBACK_REMOTE_PIC_PATH = "uexESurfingRtc.cbRemotePicPath";
    public static final String CALLBACK_SET_APPKEY_ID = "uexESurfingRtc.cbSetAppKeyAndAppId";
    public static final String CALLBACK_MSG_STATUS = "uexESurfingRtc.cbMessageStatus";

    final int mMyActivityRequestCode = 10000;
    /**uexESurfingRtc use*/
    private final boolean DEBUG = true;
    private CbHandler mCbhandler = new CbHandler();
    /** The m clt. */
    private static RtcClient mClt;
    /** The m init. */
    private static boolean mInit = false; //init
    private static SurfaceViewRtc mSurfaceViewRtc = null;
    private static CallView mCallView = null;
    private static RtcLogin mRtcLogin = null;
    /** The m acc. */
    private static Device mAcc = null;  //reg 
    /** The m call. */
    private static Connection mCall = null;
    private String remotePicPathString = "";
    private static ViewConfig mLocalViewConfig = null;
    private static ViewConfig mRemoteViewConfig = null;
    private static boolean switchFlag = false;
    private static ArrayList<EUExesurfingRtc> sRtc = new ArrayList<EUExesurfingRtc>();
    
    /** The m a listener. */
    private DeviceListener mAListener = new DeviceListener() {
        @Override
        public void onDeviceStateChanged(int result) {
            Utils.PrintLog(5,"DeviceListener","onDeviceStateChanged,result="+result);
            mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GLOBAL_STATUS,
                    "StateChanged,result=" + result);
            if(result == RtcConst.CallCode_Success) { //注销也存在此处
                mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_LOG_STATUS, 
                        ConstantUtils.LOG_STATUS_LOGIN);
            }
            else if(result == RtcConst.NoNetwork) {
                onNoNetWork();
            }
            else if(result == RtcConst.ChangeNetwork) {
                ChangeNetWork();
            }
            else if(result == RtcConst.PoorNetwork) {
                onPoorNetWork();
            }
            else if(result == RtcConst.ReLoginNetwork) {
            // 网络原因导致多次登陆不成功，由用户选择是否继续，如想继续尝试，可以重建device
                Utils.PrintLog(5,"DeviceListener","onDeviceStateChanged,ReLoginNetwork");
            }
            else if(result == RtcConst.DeviceEvt_KickedOff) {
            // 被另外一个终端踢下线，由用户选择是否继续，如果再次登录，需要重新获取token，重建device
                Utils.PrintLog(5,"DeviceListener","onDeviceStateChanged,DeviceEvt_KickedOff");
            }
            else if(result == RtcConst.DeviceEvt_MultiLogin) {
                Utils.PrintLog(5,"DeviceListener","onDeviceStateChanged,DeviceEvt_MultiLogin");
            }
            else {
            }
        }
        
        private void onPoorNetWork() {
            Utils.PrintLog(5, LOGTAG, "onPoorNetWork");
        }
        
        private void onNoNetWork() {
            Utils.PrintLog(5, LOGTAG, "onNoNetWork");
            Utils.DisplayToast(mContext, "onNoNetWork");
            //断网销毁 
			setViewVisibilityByHandler(View.INVISIBLE);
            if (mCall!=null) {
                mCall.disconnect();
                mCall = null;
				mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_CALL_STATUS,
                            ConstantUtils.CALL_STATUS_NORMAL);
            }
        }
        private void ChangeNetWork() {
            Utils.PrintLog(5, LOGTAG, "ChangeNetWork");
            //自动重连接  
        }
        @Override
        public void onNewCall(Connection call) {

            Utils.PrintLog(5,"DeviceListener","onNewCall,call="+call.info());
            if (mCall!=null) {
            	mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GLOBAL_STATUS, 
                        "DeviceListener:rejectIncomingCall call="+call.info());
                call.reject();
                call = null;
                Utils.PrintLog(5,"DeviceListener","onNewCall,reject call");
                return;
            }
            mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GLOBAL_STATUS, 
                    "DeviceListener:onNewCall,call="+call.info());
            mCall = call;
            call.setIncomingListener(mCListener);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_CALL_STATUS, 
                            ConstantUtils.CALL_STATUS_INCOMING);
                }
            });
        }
        @Override
        public void onQueryStatus(int status, String paramers) {
        }

        @Override
        public void onReceiveIm(String from,String mime,String content) {
        	mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_MSG_STATUS, ConstantUtils.MSG_STATUS_RECEIVE);
        	mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GLOBAL_STATUS, "onReceiveIm:from:"+from+",msg:"+content);
        }

        @Override
        public void onSendIm(int nStatus) {
        	if(nStatus == RtcConst.CallCode_Success)
        		mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_MSG_STATUS, ConstantUtils.MSG_STATUS_SEND);
            else
            	mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_MSG_STATUS, ConstantUtils.ERROR_MSG_ERROR + nStatus);
        }
    };
    
    /** The m c listener. */
    ConnectionListener mCListener = new ConnectionListener() {
        @Override
        public void onConnecting() {
            mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GLOBAL_STATUS, 
                    "ConnectionListener:onConnecting");
        }
        @Override
        public void onConnected() {
            mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GLOBAL_STATUS, 
                    "ConnectionListener:onConnected");
        }
        @Override
        public void onDisconnected(int code) {
            mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GLOBAL_STATUS, 
                    "ConnectionListener:onDisconnect,code=" + code);
            Utils.PrintLog(5, LOGTAG, "onDisconnected timerDur"+mCall.getCallDuration());
            mCall = null;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_CALL_STATUS,
                            ConstantUtils.CALL_STATUS_NORMAL);
                    setViewVisibilityByHandler(View.INVISIBLE);
                }
            });
        }
        @Override
        public void onVideo() {
            mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GLOBAL_STATUS, 
                    "ConnectionListener:onVideo");
            initSurfaceViewRtc();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if((null == mCallView) && (mCall != null))
                    {
                        mCallView = mSurfaceViewRtc
                                .initVideoViews(mCall, mLocalViewConfig, mRemoteViewConfig);
                    }
                    mCall.buildVideo(mCallView.mvRemote);
                    setViewVisibilityByHandler(View.VISIBLE);
                }
            });
        }
        @Override
        public void onNetStatus(int msg, String info) {
        }
    };

    public static void onActivityResume() {
    	if(mCall != null)
    		mCall.resetVideoViews();
    };

    /** The UI handler. */
    private Handler mUIHandler = new Handler() {
        @Override 
        public void handleMessage(Message msg) {
            switch(msg.what) {
            case ConstantUtils.MSG_GETTOKEN:
                mAcc = mRtcLogin.onResponseGetToken(msg, mAcc, mClt, mAListener);
                break;
            case ConstantUtils.MSG_SET_SURFACE_VIEW_VISIBILITUY:
            	if(mCallView != null && mSurfaceViewRtc != null) {
            	    mSurfaceViewRtc.setVideoSurfaceVisibility(mCallView, msg.arg1);
            	    if(msg.arg1 == View.INVISIBLE) {
            	    	mCallView = null;
            	    	mSurfaceViewRtc = null;
            	    }
            	}
                break;
            case ConstantUtils.MSG_REMOVE_SURFACE_VIEW:
                destroySurfaceView();
                break;
            case ConstantUtils.MSG_RTC_CLIENT_ON_INIT:
                rtcClientOnInit(msg);
                break;
            case ConstantUtils.MSG_REGISTER:
//                register((String)msg.obj);
                break;
            case ConstantUtils.MSG_SET_LOCAL_VIEW_VISIBILITUY:
            	mCallView.mvLocal.setVisibility(msg.arg1);
            	if(msg.arg1 == View.VISIBLE) {
            		mCall.resetVideoViews();
            		mCall.fillSend(0);
            	} else {
            		mCall.fillSend(1);
            	}
                break;
            default:
                break;
            }
        }

    };
    
	/**
	 * 构造方法
	 * */
    public EUExesurfingRtc(Context context, EBrowserView view)
    {
        super(context, view);
        //sRtc = this;
        Log.e("appcan","先看看咋回事吧！");
    }
    
    /**
     * initESurfingRtc.
     *
     * 
     */
    public void setAppKeyAndAppId(String[] parm)
    {
        LogUtils.logWlDebug(true, "into setAppKeyAndAppId");
        String errorMsg = ConstantUtils.ERROR_MSG_ERROR;
        if(parm.length >= 2 &&
                !TextUtils.isEmpty(parm[ConstantUtils.SET_APP_KEY_ID_KEY_OFFSET])
                && !TextUtils.isEmpty(parm[ConstantUtils.SET_APP_KEY_ID_ID_OFFSET]))
        {
            RtcBase.setAppKey(parm[ConstantUtils.SET_APP_KEY_ID_KEY_OFFSET]);
            RtcBase.setAppId(parm[ConstantUtils.SET_APP_KEY_ID_ID_OFFSET]);
            errorMsg = ConstantUtils.ERROR_MSG_OK;
        }
        else
        {
            LogUtils.logError(LogUtils.getLineInfo() + "setAppKeyAndAppId ERROR");
            errorMsg = ConstantUtils.ERROR_MSG_PARM_ERROR;
        }

        mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_SET_APPKEY_ID, errorMsg);
    }
    
    /**
     * initESurfingRtc.
    *
    * 
    */
   public void initESurfingRtc(final String userName)
   {
       LogUtils.logWlDebug(DEBUG, "into initESurfingRtc");

       mInit = !mInit;

       if(mInit)
       {
           new Thread(new Runnable() {
            @Override
            public void run() {
                mClt = mRtcLogin.initRtcClientImpl(userName);
            }
        }).start();
       }
   }
    
   
   public void initSurfaceViewRtc()
   {
       if(null == mSurfaceViewRtc)
       {
           mSurfaceViewRtc = new SurfaceViewRtc(sRtc.get(sRtc.size()-1), mContext);
       }
   }
   

    /**
     * login.
     *
     * @param 
     */
    public void login(String[] parm)
    {
        LogUtils.logWlDebug(true, "into login");
        if(parm.length >= 2)
        {
            LogUtils.logWlDebug(DEBUG, parm[0]);
            remotePicPathString = RtcBase.createRemotePicFloder(mContext, remotePicPathString);
            if(null == mRtcLogin)
            {
                mRtcLogin = new RtcLogin(mContext, mUIHandler, mCbhandler);
            }

            JSONObject json = null;
            try {
                json = new JSONObject(parm[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mLocalViewConfig = RtcBase
                    .parseViewConfigJson(json.optJSONObject(ConstantUtils.JK_LOCA_VIEW_CON));
            mRemoteViewConfig = RtcBase
                    .parseViewConfigJson(json.optJSONObject(ConstantUtils.JK_REMOTE_VIEW_CON));
            initESurfingRtc(parm[1]);
        }
        else
        {
            mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_LOG_STATUS, 
                    ConstantUtils.ERROR_MSG_PARM_ERROR);
        }
    }
    
    /**
     * logout.
     *
     * @param 
     */
    public void logout(String[] parm)
    {
        LogUtils.logWlDebug(true, "into logout");

        if (mCall != null)
        {
            mCall.disconnect();
            mCall = null;
            mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_CALL_STATUS,
                    ConstantUtils.CALL_STATUS_NORMAL);
        }
        if(mAcc != null) 
        {
            mAcc.release();
            mAcc = null;
        }
        if(mClt != null)
        {
            mClt.release();
        }
        mClt = null;
        mInit = false;
        mUIHandler.sendEmptyMessage(ConstantUtils.MSG_REMOVE_SURFACE_VIEW);
        mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_LOG_STATUS, 
                ConstantUtils.LOG_STATUS_LOGOUT);
    }
    
    private void setViewVisibilityByHandler(int visible)
    {
        Message msg = new Message(); 
        msg.what = ConstantUtils.MSG_SET_SURFACE_VIEW_VISIBILITUY;
        msg.arg1 = visible;
        mUIHandler.sendMessage(msg);
    }
    
    private void destroySurfaceView()
    {
        if(mCallView != null && mSurfaceViewRtc != null)
        {
            mSurfaceViewRtc.setVideoSurfaceVisibility(mCallView, View.INVISIBLE);
            if(mCallView.mvLocal != null)
            {
                removeViewFromCurrentWindow(mCallView.mvLocal);
                mCallView.mvLocal = null;
            }
            if(mCallView.mvRemote != null)
            {
                removeViewFromCurrentWindow(mCallView.mvRemote);
                mCallView.mvRemote = null;
            }
            mCallView = null;
        }
    }
    
    /**
     * call.
     *
     * @param 
     */
    public void call(String[] parm)
    {
        LogUtils.logWlDebug(DEBUG, LogUtils.getLineInfo() 
                + "into call transtype = " + RtcConst.TransType);
        String errorMsg = ConstantUtils.ERROR_MSG_ERROR;
        boolean startCall = false;
        if(parm.length >= 3)
        {
            if(mAcc == null)
			{
                errorMsg = ConstantUtils.ERROR_MSG_UNREGISTER;
            }
            else if (mCall == null)
            {
                int mCallType = ConstantUtils.CALL_TYPE_AUDIO_AND_VIDED;
                mCallType = RtcLogin.getCallType(Integer
                        .parseInt(parm[ConstantUtils.CALL_TYPE_ID_OFFSET]));
                startCall = true;
                try {
                    String remoteUserName = RtcRules.UserToRemoteUri_new(parm[ConstantUtils.CALL_USER_NAME_OFFSET],
                            RtcConst.UEType_Any);
                    
                    JSONObject jinfo = new JSONObject();
                    jinfo.put(RtcConst.kCallRemoteUri, remoteUserName);
                    if(parm[ConstantUtils.CALL_INFO_OFFSET].length() > 0)
                    	jinfo.put(RtcConst.kCallInfo, parm[ConstantUtils.CALL_INFO_OFFSET]); //opt
                    jinfo.put(RtcConst.kCallType, mCallType);
                    mCall = mAcc.connect(jinfo.toString(), mCListener);
                    mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_CALL_STATUS, 
                            ConstantUtils.CALL_STATUS_CALLING);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            errorMsg = ConstantUtils.ERROR_MSG_PARM_ERROR;
        }
        if(!startCall)
        {
            mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_CALL_STATUS, errorMsg);
        }
    }
    
    /**
     * 
     * accept
     * 
     * */
    public void acceptCall(String[] parm)
    {
        LogUtils.logWlDebug(DEBUG, LogUtils.getLineInfo() + "into acceptCall");
        String errorMsg = ConstantUtils.ERROR_MSG_ERROR;
        if(parm.length >= 1)
        {
            int mCallType = ConstantUtils.CALL_TYPE_AUDIO_AND_VIDED;
            mCallType = RtcLogin.getCallType(Integer.parseInt(parm[0]));
            if (mCall != null)
            {
                mCall.accept(mCallType); //视频来电可以选择仅音频接听
                Utils.PrintLog(5,LOGTAG, "incoming accept(mCT)");
                errorMsg = ConstantUtils.CALL_STATUS_CALLING;
            }
        }
        else
        {
            errorMsg = ConstantUtils.ERROR_MSG_PARM_ERROR;
        }
        mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_CALL_STATUS, errorMsg);
    }
    
    public void hangUp(String[] parm)
    {
        LogUtils.logWlDebug(DEBUG, LogUtils.getLineInfo() + "into hangUp");
        if (mCall != null)
        {
            mCall.disconnect();
            Utils.PrintLog(5, LOGTAG, "onBtnHangup timerDur" + mCall.getCallDuration());
            setViewVisibilityByHandler(View.INVISIBLE);
            mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_CALL_STATUS,
                    ConstantUtils.CALL_STATUS_NORMAL);
          mCall = null;
        }
    }
    
    public void setVideoAttr(String[] parm)
    {
        LogUtils.logWlDebug(DEBUG, LogUtils.getLineInfo() + "into setVideoAttr");
        if((parm.length >= 1) && (RtcBase.isVideoAttr(Integer.parseInt(parm[0]))))
        {
            if(mClt != null)
            {
                mClt.setVideoAttr(Integer.parseInt(parm[0]));
            }
        }
        else
        {
            LogUtils.logError(LogUtils.getLineInfo() + "setVideoAttr parm error");
        }
    }
    
    public void takeRemotePicture(String[] parm)
    {
        LogUtils.logWlDebug(DEBUG, LogUtils.getLineInfo() + "into takeRemotePicture");
        if(mCall != null)
        {
            String picPath = remotePicPathString 
                    + BaseUtils.getSpecialFormatTime(ConstantUtils.TIME_FORMAT_PIC_NAME) 
                    + ConstantUtils.PIC_FORMAT;
            mCall.takeRemotePicture(picPath);
            mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_REMOTE_PIC_PATH, picPath);
        }
    }
    
    public void mute(String[] parm)
    {
        LogUtils.logWlDebug(DEBUG, LogUtils.getLineInfo() + "into mute");
        if((parm.length >= 1) && (mCall != null))
        {
            mCall.setMuted((ConstantUtils.TRUE_STR.equals(parm[0])) ? true : false);
        }
    }
    
    public void loudSpeaker(String[] parm)
    {
        LogUtils.logWlDebug(DEBUG, LogUtils.getLineInfo() + "into loudSpeaker");
        if((parm.length >= 1) && (mClt != null))
        {
            AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            mClt.enableSpeaker(audioManager, (ConstantUtils.TRUE_STR.equals(parm[0])) ? true : false);
        }
    }
    
    public void sendMessage(String[] parm)
    {
        LogUtils.logWlDebug(DEBUG, LogUtils.getLineInfo() 
                + "into sendMessage content-type = " + RtcConst.ImText);
        if(parm.length >= 2)
        {
            if (mAcc != null)
            {
                String remoteUserName = RtcRules.UserToRemoteUri_new(parm[ConstantUtils.MSG_USER_NAME_OFFSET],
                                RtcConst.UEType_Any);
                
                mAcc.sendIm(remoteUserName, RtcConst.ImText, parm[ConstantUtils.MSG_TYPE_MSG_OFFSET]);
            }
            else
            {
            	mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_MSG_STATUS, ConstantUtils.ERROR_MSG_UNREGISTER);
            }
        }
        else
        {
        	mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_MSG_STATUS, ConstantUtils.ERROR_MSG_PARM_ERROR);
        }
    }
    
    public void switchCamera(String[] parm)
    {
        LogUtils.logWlDebug(DEBUG, LogUtils.getLineInfo() + "into switchCamera");
        if((parm.length >= 1) && (mCall != null))
        {
        	if(parm[0].equals(ConstantUtils.CAMERA_BACK))
        		mCall.setCamera(0);
        	else
        		mCall.setCamera(1);
        }
    }
    
    public void rotateCamera(String[] parm)
    {
        LogUtils.logWlDebug(DEBUG, LogUtils.getLineInfo() + "into rotateCamera");
        if((parm.length >= 1) && (mCall != null))
        {
        	int angle = Integer.parseInt(parm[0]);
        	if(angle >= 0 && angle <=3)
    			mCall.setCameraAngle(angle);
        }
    }
    
    public void switchView(String[] parm)
    {
        LogUtils.logWlDebug(DEBUG, LogUtils.getLineInfo() + "into switchView");
        if(mCall != null && mCallView != null && mSurfaceViewRtc != null)
        {
        	removeViewFromCurrentWindow(mCallView.mvLocal);
        	removeViewFromCurrentWindow(mCallView.mvRemote);
        	try{
        		Thread.sleep(200);
        	}catch(InterruptedException e){}
        	if(switchFlag) {
        		addViewToCurrentWindow(mCallView.mvLocal, mSurfaceViewRtc.lparm1);
        		addViewToCurrentWindow(mCallView.mvRemote, mSurfaceViewRtc.lparm2);
        	} else {
        		addViewToCurrentWindow(mCallView.mvRemote, mSurfaceViewRtc.lparm1);
        		addViewToCurrentWindow(mCallView.mvLocal, mSurfaceViewRtc.lparm2);
        	}
        	mCall.resetVideoViews();
			switchFlag = !switchFlag;
        }
    }
    
    public void hideLocalView(String[] parm)
    {
        LogUtils.logWlDebug(DEBUG, LogUtils.getLineInfo() + "into hideLocalView");
        if(mCall != null && mCallView != null)
        {
        	Message msg = new Message(); 
            msg.what = ConstantUtils.MSG_SET_LOCAL_VIEW_VISIBILITUY;
        	if(parm[0].equals(ConstantUtils.VIEW_SHOW))
        		msg.arg1 = View.VISIBLE;
        	else if(parm[0].equals(ConstantUtils.VIEW_HIDE))
        		msg.arg1 = View.INVISIBLE;
            mUIHandler.sendMessage(msg);
        }
    }
    
    private void rtcClientOnInit(Message msg)
    {
        JSONObject json = null;
        try {
            json = new JSONObject((String) msg.obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mInit = (ConstantUtils.TRUE_STR
                .endsWith(json.optString(ConstantUtils.REGISTER_INIT_KEY)) ? true : false);
        if(mInit)
        {
            RegisterConfig mRegisterConfig = new RegisterConfig();
            mRegisterConfig.mInit = mInit;
            mRegisterConfig.userName = json.optString(ConstantUtils.REGISTER_USER_NAME_KEY);
            mRegisterConfig.mClt = mClt;
            mRegisterConfig.mAcc = mAcc;
            mRtcLogin.register(mRegisterConfig);
        }
    }

    // clean something
    @Override
    protected boolean clean()
    {
    	if(sRtc.size() == 0) {
    		sRtc.add(this);
    	}
    	else if(sRtc.get(sRtc.size()-1) == this) {
    		sRtc.remove(sRtc.size()-1);
    	}
    	else {
    		sRtc.add(this);
    	}
    	Log.e("appcan", "clean "+this);
        return true;
    }

    public class CbHandler extends Handler {

        public void send2Callback(int what, String result)
        {
            Message msg = Message.obtain();
            msg.what = what;
            msg.obj = result;
            this.sendMessage(msg);
        }

        @Override
        public void handleMessage(Message msg)
        {
        	String js;
            switch (msg.what)
            {
            case ConstantUtils.WHAT_CALLBACK_GLOBAL_STATUS:
                String outStr = BaseUtils
                        .getSpecialFormatTime(ConstantUtils.TIME_FORMAT_GLOBAL_STATUS) 
                        + ": " + (String)msg.obj;
                js = SCRIPT_HEADER + "if(" + CALLBACK_GLOBAL_STATUS + "){"
                        + CALLBACK_GLOBAL_STATUS + "(" + 0 + "," + EUExCallback.F_C_TEXT + ",'" +outStr + "');}";
                evaluateScript("root", 0, js);
                break;
            case ConstantUtils.WHAT_CALLBACK_LOG_STATUS:
            	js = SCRIPT_HEADER + "if(" + CALLBACK_LOG_STATUS + "){"
                        + CALLBACK_LOG_STATUS + "(" + 0 + "," + EUExCallback.F_C_TEXT + ",'" +(String)msg.obj + "');}";
                evaluateScript("root", 0, js);
                break;
            case ConstantUtils.WHAT_CALLBACK_CALL_STATUS:
            	js = SCRIPT_HEADER + "if(" + CALLBACK_CALL_STATUS + "){"
                        + CALLBACK_CALL_STATUS + "(" + 0 + "," + EUExCallback.F_C_TEXT + ",'" +(String)msg.obj + "');}";
                evaluateScript("root", 0, js);
                break;
            case ConstantUtils.WHAT_CALLBACK_REMOTE_PIC_PATH:
            	js = SCRIPT_HEADER + "if(" + CALLBACK_REMOTE_PIC_PATH + "){"
                        + CALLBACK_REMOTE_PIC_PATH + "(" + 0 + "," + EUExCallback.F_C_TEXT + ",'" +(String)msg.obj + "');}";
                evaluateScript("root", 0, js);
                break;
            case ConstantUtils.WHAT_CALLBACK_SET_APPKEY_ID:
            	js = SCRIPT_HEADER + "if(" + CALLBACK_SET_APPKEY_ID + "){"
                        + CALLBACK_SET_APPKEY_ID + "(" + 0 + "," + EUExCallback.F_C_TEXT + ",'" +(String)msg.obj + "');}";
                evaluateScript("root", 0, js);
                break;
            case ConstantUtils.WHAT_CALLBACK_MSG_STATUS:
            	js = SCRIPT_HEADER + "if(" + CALLBACK_MSG_STATUS + "){"
                        + CALLBACK_MSG_STATUS + "(" + 0 + "," + EUExCallback.F_C_TEXT + ",'" +(String)msg.obj + "');}";
                evaluateScript("root", 0, js);
                break;
            }
        }
    }
}
