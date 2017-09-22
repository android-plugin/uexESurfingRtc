package org.zywx.wbpalmstar.plugin.uexesurfingrtc;

import java.util.ArrayList;

import jni.util.Utils;

import org.json.JSONArray;
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
import rtc.sdk.iface.GroupCallListener;
import rtc.sdk.iface.GroupMgr;
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
    public static final String CALLBACK_GROUP_STATUS = "uexESurfingRtc.cbGroupStatus";

    
    /** The Constant MSG_GRP_ZHU_CREATE. */
    public static final int MSG_GRP_ZHU_CREATE = 10004;

    /** The m_grpname. */
    private static boolean b_creator; //是否为创建者，供主动加入会议接口使用
    
    /** The m_grpname. */
    private static String m_grpid; //当前会议ID，供主动加入会议接口使用
    
    /** The m_grpname. */
    public static String m_grpname; //当前会议组名
    
    /** The m_grptype. */
    public static int m_grptype; //当前会议类型
    
    /** The m_grpname. */
    private static String coming_grpid; //来电id
    
    /** The m_grpname. */
    public static String coming_grpname; //来电组名
    
    /** The m_grptype. */
    public static int coming_grptype; //来电会议类型

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
    private static GroupMgr grpmgr;
    private static int grptype;
    /** The m call. */
    private static Connection mCall = null;
    private static Connection mGroupCall = null;
    private String remotePicPathString = "";
    private static ViewConfig mLocalViewConfig = null;
    private static ViewConfig mRemoteViewConfig = null;
    private static boolean switchFlag = false;
    private static ArrayList<EUExesurfingRtc> sRtc = new ArrayList<EUExesurfingRtc>();
    
    
    /** The m grp listener. */
    ConnectionListener mGrpListener = new ConnectionListener() {
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
            Utils.PrintLog(5, LOGTAG, "onDisconnected timerDur"+mGroupCall.getCallDuration());
            mGroupCall = null;
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
                    if((null == mCallView) && (mGroupCall != null)) {
                        mCallView = mSurfaceViewRtc
                                .initVideoViews(mGroupCall, mLocalViewConfig, mRemoteViewConfig);
                        mGroupCall.buildVideo(mCallView.mvRemote);
                        setViewVisibilityByHandler(View.VISIBLE);
                    } else {
                    	mGroupCall.buildVideo(mCallView.mvRemote);
                    	removeViewFromCurrentWindow(mCallView.mvRemote);
                        addViewToCurrentWindow(mCallView.mvRemote, mSurfaceViewRtc.lparm2);
                    }
                }
            });
        }
        @Override
        public void onNetStatus(int msg, String info) {
            // TODO Auto-generated method stub
        }
    };

    /** The m grp voice listener. */
    GroupCallListener mGrpCallListener = new GroupCallListener() {
    	
        private void onResponse_grpcreate(String parameters) {
        	Utils.PrintLog(5, LOGTAG, parameters+"");
            //RestMgr中对应groupVoice_optCreate
            try {
                if(parameters==null || parameters.equals("")) {
                    Utils.PrintLog(5, LOGTAG, "onResponse_grpcreate resopnse strResponse null");
                    return;
                }
                JSONObject jsonrsp = new JSONObject(parameters);
                int code = jsonrsp.getInt(RtcConst.kcode);
                String callid = jsonrsp.getString(RtcConst.kcallId);
                String reason = jsonrsp.getString(RtcConst.kreason);
                Utils.PrintLog(5, LOGTAG, "onResponse_grpcreate code:"+code+" reason:"+reason);
                if(code == 0) {
                    b_creator = true;
                    m_grpid = jsonrsp.getString(RtcConst.kcallId);
                    //“OK:groupCreate,callid="xx"”：创建会议成功，返回callid
                	mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, 
                    		"OK:groupCreate,callid="+ "\"" + callid + "\"");
                    Utils.PrintLog(5, LOGTAG, "会议创建成功:"+parameters);
                }
                else {                
                	//“ERROR:groupCreate,code=xx”：创建会议失败，返回平台的code
                	mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, 
                		"ERROR:groupCreate,code="+code);
                	Utils.PrintLog(5, LOGTAG, "会议创建失败:"+code+" reason:"+reason);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
            	Utils.PrintLog(5, LOGTAG, "会议创建失败 JSONException:"+e.getMessage());
                e.printStackTrace();
            }
        }
        
        
        private void onResponse_grpgetMemberLis(String parameters) {
            try {
                if(parameters == null || parameters.equals("")) {
                    Utils.PrintLog(5, LOGTAG, "onResponse_grpgetMemberLis fail result: null");
                    return;
                }
                JSONObject jsonrsp = new JSONObject(parameters);
                String code = jsonrsp.getString(RtcConst.kcode);
                String reason = jsonrsp.getString(RtcConst.kreason);
                Utils.PrintLog(5, LOGTAG, "onResponse_grpgetMemberLis code:"+code+" reason:"+reason);
                if(code.equals("0")) {
                	Utils.PrintLog(5, LOGTAG, "获取成员列表成功:"+parameters);
                	mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, 
                    		"OK:groupMember,list=" + jsonrsp.getJSONArray("memberInfoList"));
                }
                else {
                	mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, 
                			"ERROR:groupMember" + ",code=" + code);
                	Utils.PrintLog(5, LOGTAG, "获取成员列表失败:"+code+" reason:"+reason);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
            	Utils.PrintLog(5, LOGTAG, "获取成员列表失败 JSONException:"+e.getMessage());
                e.printStackTrace();
            }

        }
        
        private void onResponse_grpInvitedMemberList(String parameters) {
            try {
                Utils.PrintLog(5, LOGTAG, "onResponse_grpInvitedMemberList:"+parameters);
                if(parameters == null || parameters.equals(""))
                    return;
                JSONObject jsonrsp = new JSONObject(parameters);
                if(jsonrsp.isNull("code")==false) {
                    String code = jsonrsp.getString(RtcConst.kcode);
                    String reason = jsonrsp.getString(RtcConst.kreason);
                    Utils.PrintLog(5, LOGTAG, "onResponse_grpInvitedMemberList code:"+code+" reason:"+reason);
                    if(code.equals("0")) {
                    	mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, 
                        		"OK:groupInvite");
                    	Utils.PrintLog(5, LOGTAG, "邀请成员参与群组会话成功:"+parameters);
                    }
                    else {
                    	mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, 
                        		"ERROR:groupInvite,code=" + code);
                    	Utils.PrintLog(5, LOGTAG, "邀请成员参与群组会话失败:"+code+" reason:"+reason);
                    }
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
            	Utils.PrintLog(5, LOGTAG, "邀请成员参与群组会话失败 JSONException:"+e.getMessage());
                e.printStackTrace();
            }
        }     
        
        private void onResponse_grpkickedMemberList(String parameters) {
            try {
                if(parameters == null || parameters.equals(""))
                    return;
                JSONObject jsonrsp = new JSONObject(parameters);
                if(jsonrsp.isNull("code")==false) {
                    String code = jsonrsp.getString(RtcConst.kcode);
                    String reason = jsonrsp.getString(RtcConst.kreason);
                    if(code.equals("0") || code.equals("200")) {
                    	mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, 
                        		"OK:groupKick");
                    	Utils.PrintLog(5, LOGTAG, "踢出成员成功:"+parameters);
                    }
                    else {
                    	mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, 
                        		"ERROR:groupKick,code=" + code);
                    	Utils.PrintLog(5, LOGTAG, "踢出成员失败:"+code+" reason:"+reason);
                    }
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
            	Utils.PrintLog(5, LOGTAG, "踢出成员失败 JSONException:"+e.getMessage());
                e.printStackTrace();
            }
        }
              
        private void onResponse_grpClose(String parameters) {
            try {
                if(parameters == null || parameters.equals(""))
                    return;
                JSONObject jsonrsp = new JSONObject(parameters);
                if(jsonrsp.isNull("code")==false) {
                    String code = jsonrsp.getString(RtcConst.kcode);
                    String reason = jsonrsp.getString(RtcConst.kreason);
                    Utils.PrintLog(5, LOGTAG, "onResponse_grpClose code:"+code+" reason:"+reason);
                    if(code.equals("0") || code.equals("200")) {
                    	mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, 
                        		"OK:groupClose");
                    	Utils.PrintLog(5, LOGTAG, "关闭群组成功:"+parameters);
                    }
                    else {
                    	mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, 
                        		"ERROR:groupClose,code=" + code);
                    	Utils.PrintLog(5, LOGTAG, "关闭群组失败:"+code+" reason:"+reason);
                    }
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
            	Utils.PrintLog(5, LOGTAG, "关闭群组失败 JSONException:"+e.getMessage());
                e.printStackTrace();
            }
        }
        
        
        private void onResponse_grpStreamManagement(String parameters) {
            try {
                if(parameters == null || parameters.equals(""))
                    return;
                JSONObject jsonrsp = new JSONObject(parameters);
                if(jsonrsp.isNull("code")==false) {
                    String code = jsonrsp.getString(RtcConst.kcode);
                    String reason = jsonrsp.getString(RtcConst.kreason);
                    Utils.PrintLog(5, LOGTAG, "onResponse_grpStreamManagement code:"+code+" reason:"+reason);
                    if(code.equals("0")) {
                    	mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, 
                        		"OK:groupMic");
                    	Utils.PrintLog(5, LOGTAG, "媒体流控制成功:"+parameters);
                    }
                    else {
                    	mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, 
                        		"ERROR:groupMic,code=" + code);
                    	Utils.PrintLog(5, LOGTAG, "媒体流控制失败:"+code+" reason:"+reason);
                    }
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
            	Utils.PrintLog(5, LOGTAG, "媒体流控制失败 JSONException:"+e.getMessage());
                e.printStackTrace();
            }
        }
        
        
        private void onResponse_grpJoin(String parameters) {
            try {
                Utils.PrintLog(5, LOGTAG, "onResponse_grpJoin:"+parameters);
                if(parameters == null || parameters.equals(""))
                    return;
                JSONObject jsonrsp = new JSONObject(parameters);
                if(jsonrsp.isNull("code")==false) {
                    String code = jsonrsp.getString(RtcConst.kcode);
                    String reason = jsonrsp.getString(RtcConst.kreason);
                    Utils.PrintLog(5, LOGTAG, "onResponse_grpJoin code:"+code+" reason:"+reason);
                    if(code.equals("0")) {
        	            mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, 
        	            		"OK:groupJoin");
                    	Utils.PrintLog(5, LOGTAG, "主动加入群组会话成功:"+parameters);
                    }
                    else {
                    	mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, 
                        		"ERROR:groupJoin,code=" + code);
                    	Utils.PrintLog(5, LOGTAG, "主动加入群组会话失败:"+code+" reason:"+reason);
                    }
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block

            	Utils.PrintLog(5, LOGTAG, "主动加入群组会话失败 JSONException:"+e.getMessage());
                e.printStackTrace();
            }
        }
        
        private void onResponse_grpQList(String parameters) {
            try {
                Utils.PrintLog(5, LOGTAG, "onResponse_grpQList:"+parameters);
                if(parameters == null || parameters.equals(""))
                    return;
                JSONObject jsonrsp = new JSONObject(parameters);
                if(jsonrsp.isNull("code")==false) {
                    String code = jsonrsp.getString(RtcConst.kcode);
                    String reason = jsonrsp.getString(RtcConst.kreason);
                    Utils.PrintLog(5, LOGTAG, "onResponse_grpQList code:"+code+" reason:"+reason);
                    if(code.equals("0")||code.equals("200")) {
                        //“OK:groupList,list={"callid":"xx","name":"xx"},
                    	JSONArray transitListArray = jsonrsp.getJSONArray("gvcList");
                    	if(transitListArray.length() == 0){
                        	//“OK:noGroupList”：当前appid没有会议
                        	mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, 
                        			"OK:noGroupList");
                            return;
                    	}
                        String tempcallID = null;
                        String tempName = null;
                        for(int i = 0 ; i < transitListArray.length(); i++) {
                        	JSONObject jsonObject = (JSONObject)transitListArray.get(i);
                        	tempcallID = jsonObject.getString(RtcConst.kcallId);
                        	tempName = jsonObject.getString(RtcConst.kgvcname);
                        	jsonObject.remove("gvcattendingPolicy");
                        	jsonObject.put("callid", tempcallID);
                        	jsonObject.put("name", tempName);
                        	jsonObject.remove(RtcConst.kcallId);
                        	jsonObject.remove(RtcConst.kgvcname);
                        }
                        //“OK:groupList,list={"callid":"xx","name":"xx"},
                    	mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, 
                        		"OK:groupList,list=" + transitListArray.toString());
                    	Utils.PrintLog(5, LOGTAG, "查询群组列表成功:"+parameters);
                    }
                    else {
                    	Utils.PrintLog(5, LOGTAG, "查询群组列表失败:"+code+" reason:"+reason);
                    }
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
            	Utils.PrintLog(5, LOGTAG, "查询群组列表失败 JSONException:"+e.getMessage());
                e.printStackTrace();
            }
        }
        
       
        private void onResponse_grpMDisp(String parameters) {
            try {
                Utils.PrintLog(5, LOGTAG, "onResponse_grpMDisp:"+parameters);
                if(parameters == null || parameters.equals(""))
                    return;
                JSONObject jsonrsp = new JSONObject(parameters);
                if(jsonrsp.isNull("code")==false) {
                    String code = jsonrsp.getString(RtcConst.kcode);
                    String reason = jsonrsp.getString(RtcConst.kreason);
                    Utils.PrintLog(5, LOGTAG, "onResponse_grpMDisp code:"+code+" reason:"+reason);
                    if(code.equals("0")||code.equals("200")) {
                    	mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, 
                        		"OK:groupVideo" + parameters);
                    	Utils.PrintLog(5, LOGTAG, "分屏设置成功:"+parameters);
                    }
                    else {
                    	mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, 
                        		"ERROR:groupVideo,code=" + code);
                    	Utils.PrintLog(5, LOGTAG, "分屏设置失败:"+code+" reason:"+reason);
                    }
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
            	Utils.PrintLog(5, LOGTAG, "分屏设置失败 JSONException:"+e.getMessage());
                e.printStackTrace();
            }
        }
        


   
        @Override //用于处理会议结果返回的提示 
        public void onResponse(int action, String parameters) {
            // TODO Auto-generated method stub
            //parameters 为请求和返回的json
            Utils.PrintLog(5, LOGTAG, "onResponse action["+action +"]  parameters:"+parameters);
            switch(action) {
                case RtcConst.groupcall_opt_create:
                    onResponse_grpcreate(parameters);
                    break;
                case RtcConst.groupcall_opt_getmemberlist:
                    onResponse_grpgetMemberLis(parameters);
                    break;
                case RtcConst.groupcall_opt_invitedmemberlist:
                    onResponse_grpInvitedMemberList(parameters);
                    break;
                case RtcConst.groupcall_opt_kickedmemberlist:
                    onResponse_grpkickedMemberList(parameters);
                    break;
                case RtcConst.groupcall_opt_close:
                    onResponse_grpClose(parameters);
                    break;
                case RtcConst.groupcall_opt_strm:
                    onResponse_grpStreamManagement(parameters);
                    break;
                case RtcConst.groupcall_opt_join:
                    onResponse_grpJoin(parameters);
                    break;
                case RtcConst.groupcall_opt_qlist:
                    onResponse_grpQList(parameters);
                    break;
                case RtcConst.groupcall_opt_mdisp:
                    onResponse_grpMDisp(parameters);
                    break;     
            }
            //
        }
        
        @Override 
        public void onCreate(Connection call) {
            // TODO Auto-generated method stub       	
           	if(mCall != null || mGroupCall != null) {
                JSONObject jsonTemp;
				try {
					jsonTemp = new JSONObject(call.info());
					coming_grpname = jsonTemp.getString(RtcConst.kGrpname);
					coming_grptype = jsonTemp.getInt(RtcConst.kGrpType);
					coming_grpid = jsonTemp.getString(RtcConst.kGrpID); 
	            	mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, 
	            			"rejectIncomingCall,call={\"callid\":\"" + coming_grpid + "\",\"name\":\"" + coming_grpname
	            			+ "\",\"type\"=" + coming_grptype + "}");

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	call.reject();
            	return;
           	}
           	
            mGroupCall = call;
            mGroupCall.setIncomingListener(mGrpListener);
            Utils.PrintLog(5, LOGTAG, "GroupCallListener onCreate info:"+call.info());
            try {
                JSONObject json = new JSONObject(call.info());
                b_creator = json.getBoolean(RtcConst.kGrpInviter);
                int grptype = json.getInt(RtcConst.kGrpType);
                if(json.has(RtcConst.kGrpname))
                    m_grpname = json.getString(RtcConst.kGrpname);
                m_grptype = json.getInt(RtcConst.kGrpType);
                m_grpid = json.getString(RtcConst.kGrpID);
                int calltype = json.getInt(RtcConst.kCallType);
                Utils.PrintLog(5, LOGTAG, "GroupCallListener onCreate m_grptype:"+m_grptype+" grptype:"+grptype + "m_grpid:" + m_grpid+" calltype:"+calltype);
                if(b_creator == false) {//非创建者接听选择是否接听
                	mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, 
                			"onNewGroupCall,call=" + "{\"callid\":\"" + m_grpid + "\",\"name\":\"" + m_grpname
                			+ "\",\"type\":" + m_grptype + "}");
                	Utils.PrintLog(5,LOGTAG, "有会议邀请"+RtcConst.getGrpType(grptype)+"grpname:"+m_grpname);
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }                   
        }

       
        @Override
        public void onNotify(String parameters) {
        	
            // TODO Auto-generated method stub
            Utils.PrintLog(5, LOGTAG, "GroupCallListener onNotify");
            try {
            	JSONArray jsonarr = new JSONArray(parameters);
            	Utils.PrintLog(5, LOGTAG, "成员变化:"+jsonarr);
            	JSONObject member = (JSONObject)jsonarr.get(0);
            	if(member.has("memberStatus"))
            		mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, 
            				"statusChangedInfo="+member);
            	else if(member.has("upAudioState"))
            		mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, 
            				"micChangedInfo="+member);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };
    
    /** The m a listener. */
    private DeviceListener mAListener = new DeviceListener() {
        @Override
        public void onDeviceStateChanged(int result) {
            Utils.PrintLog(5,"DeviceListener","onDeviceStateChanged,result="+result);
            mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GLOBAL_STATUS,
                    "StateChanged,result=" + result);
            if(result == RtcConst.CallCode_Success) { //注销也存在此处
                if(mAcc!=null) {
                    grpmgr = mAcc.getGroup();
                    grpmgr.setGroupCallListener(mGrpCallListener);
                }
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
            if (mGroupCall!=null) {
            	mGroupCall.disconnect();
            	mGroupCall = null;
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
            if (mCall != null || mGroupCall != null) {
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
                    if((null == mCallView) && (mCall != null)) {
                        mCallView = mSurfaceViewRtc
                                .initVideoViews(mCall, mLocalViewConfig, mRemoteViewConfig);
                        mCall.buildVideo(mCallView.mvRemote);
                        setViewVisibilityByHandler(View.VISIBLE);
                    } else {
                    	mCall.buildVideo(mCallView.mvRemote);
                    	removeViewFromCurrentWindow(mCallView.mvRemote);
                        addViewToCurrentWindow(mCallView.mvRemote, mSurfaceViewRtc.lparm2);
                    }
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
    	if(mGroupCall != null)
    		mGroupCall.resetVideoViews();
    };

    /** The UI handler. */
    private Handler mUIHandler = new Handler() {
        @Override 
        public void handleMessage(Message msg) {
            switch(msg.what) {
            case ConstantUtils.MSG_GETTOKEN:
                mAcc = mRtcLogin.onResponseGetToken(msg, mAcc, mClt, mAListener);
                break;
            case ConstantUtils.MSG_SET_SURFACE_VIEW_VISIBILITY:
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
            case ConstantUtils.MSG_SET_LOCAL_VIEW_VISIBILITY:
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
        if(parm.length >= 2 && parm[1].length() > 0)
        {
            LogUtils.logWlDebug(DEBUG, parm[0]);
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
                    .parseViewConfigJson(json.optJSONObject(ConstantUtils.JK_LOCAL_VIEW_CON));
            mRemoteViewConfig = RtcBase
                    .parseViewConfigJson(json.optJSONObject(ConstantUtils.JK_REMOTE_VIEW_CON));
            if(mSurfaceViewRtc != null)
            	mSurfaceViewRtc.initVideoViews(null, mLocalViewConfig, mRemoteViewConfig);
            if(mInit == false)
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
        if (mGroupCall != null)
        {
        	mGroupCall.disconnect();
        	mGroupCall = null;
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
        msg.what = ConstantUtils.MSG_SET_SURFACE_VIEW_VISIBILITY;
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
        if(parm.length >= 2 && parm[0].length() > 0 && parm[1].length() > 0)
        {
            if(mAcc == null) {
                errorMsg = ConstantUtils.ERROR_MSG_UNREGISTER;
            } else if (mCall == null) {
                int mCallType = ConstantUtils.CALL_TYPE_AUDIO_AND_VIDED;
                mCallType = RtcLogin.getCallType(Integer
                        .parseInt(parm[ConstantUtils.CALL_TYPE_ID_OFFSET]));
                startCall = true;
                try {
                    String remoteUserName = RtcRules.UserToRemoteUri_new(parm[ConstantUtils.CALL_USER_NAME_OFFSET],
                            RtcConst.UEType_Any);
                    
                    JSONObject jinfo = new JSONObject();
                    jinfo.put(RtcConst.kCallRemoteUri, remoteUserName);
                    if(parm.length >= 3 && parm[ConstantUtils.CALL_INFO_OFFSET].length() > 0)
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
            if(mGroupCall != null){
            	mGroupCall.accept(mCallType);
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
        if (mGroupCall != null)
        {
        	mGroupCall.disconnect();
            Utils.PrintLog(5, LOGTAG, "onBtnHangup timerDur" + mGroupCall.getCallDuration());
            setViewVisibilityByHandler(View.INVISIBLE);
            mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_CALL_STATUS,
                    ConstantUtils.CALL_STATUS_NORMAL);
            mGroupCall = null;
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
    	Connection con = null;
        LogUtils.logWlDebug(DEBUG, LogUtils.getLineInfo() + "into takeRemotePicture");
        if(mCall != null)
        	con = mCall;
        if(mGroupCall != null)
        	con = mGroupCall;
        if(con != null)
        {
        	remotePicPathString = RtcBase.createRemotePicFloder(mContext, remotePicPathString);
            String picPath = remotePicPathString 
                    + BaseUtils.getSpecialFormatTime(ConstantUtils.TIME_FORMAT_PIC_NAME) 
                    + ConstantUtils.PIC_FORMAT;
            con.takeRemotePicture(picPath);
            mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_REMOTE_PIC_PATH, picPath);
        }
    }
    
    public void mute(String[] parm)
    {
    	Connection con = null;
        LogUtils.logWlDebug(DEBUG, LogUtils.getLineInfo() + "into mute");
        if(mCall != null)
        	con = mCall;
        if(mGroupCall != null)
        	con = mGroupCall;
        if((parm.length >= 1) && (con != null))
        {
        	con.setMuted((ConstantUtils.TRUE_STR.equals(parm[0])) ? true : false);
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
    	Connection con = null;
        LogUtils.logWlDebug(DEBUG, LogUtils.getLineInfo() + "into switchCamera");
        if(mCall != null)
        	con = mCall;
        if(mGroupCall != null)
        	con = mGroupCall;
        if((parm.length >= 1) && (con != null))
        {
        	if(parm[0].equals(ConstantUtils.CAMERA_BACK))
        		con.setCamera(0);
        	else if(parm[0].equals("beauty"))
        		con.ctbriVideoProcessing(0, 6, 0);
        	else
        		con.setCamera(1);
        }
    }
    
    public void rotateCamera(String[] parm)
    {
    	Connection con = null;
        LogUtils.logWlDebug(DEBUG, LogUtils.getLineInfo() + "into rotateCamera");
        if(mCall != null)
        	con = mCall;
        if(mGroupCall != null)
        	con = mGroupCall;
        if((parm.length >= 1) && (con != null))
        {
        	int angle = Integer.parseInt(parm[0]);
        	if(angle >= 0 && angle <=3)
        		con.setCameraAngle(angle);
        }
    }
    
    public void switchView(String[] parm)
    {
    	Connection con = null;
        LogUtils.logWlDebug(DEBUG, LogUtils.getLineInfo() + "into switchView");
        if(mCall != null)
        	con = mCall;
        if(mGroupCall != null)
        	con = mGroupCall;
        if(con != null && mCallView != null && mSurfaceViewRtc != null)
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
        	con.resetVideoViews();
			switchFlag = !switchFlag;
        }
    }
    
    public void hideLocalView(String[] parm)
    {
        LogUtils.logWlDebug(DEBUG, LogUtils.getLineInfo() + "into hideLocalView");
        if(mCall != null && mCallView != null)
        {
        	Message msg = new Message(); 
            msg.what = ConstantUtils.MSG_SET_LOCAL_VIEW_VISIBILITY;
        	if(parm[0].equals(ConstantUtils.VIEW_SHOW))
        		msg.arg1 = View.VISIBLE;
        	else if(parm[0].equals(ConstantUtils.VIEW_HIDE))
        		msg.arg1 = View.INVISIBLE;
            mUIHandler.sendMessage(msg);
        }
    }
    
    //群组功能
    //groupCreate 创建群组
    public void groupCreate(String[] parm) {
    	Utils.PrintLog(5, LOGTAG, "groupCreate 创建群组");
    	String errorMsg = ConstantUtils.ERROR_MSG_ERROR;
        if(parm[0].length() == 0) {
            //“ERROR:PARM_ERROR”：参数有误，调用接口失败
            mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, ConstantUtils.ERROR_MSG_PARM_ERROR);
            return;
        }
        //用户未登录
        if(mAcc == null) {
        	errorMsg = ConstantUtils.ERROR_MSG_UNREGISTER;
        	//“ERROR:UNREGISTER”：未注册至RTC平台
        	mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, 
        			errorMsg);
        	Utils.PrintLog(5, LOGTAG, "groupCreate 未注册至RTC平台");
        	return;
        }
        grpmgr = mAcc.getGroup();
        try {
        	JSONObject jsonObj = new JSONObject(parm[0]);
        	if(jsonObj.has("groupType") == false || jsonObj.has("groupName") == false ||
        			jsonObj.has("passWord") == false || jsonObj.has("members") == false) {
        		mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, ConstantUtils.ERROR_MSG_PARM_ERROR);
                return;
        	}
        	if(jsonObj.get("groupType").toString().length() == 0 || jsonObj.get("groupName").toString().length() == 0 ||
        			jsonObj.get("passWord").toString().length() == 0 || jsonObj.get("members").toString().length() == 0) {
        		mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, ConstantUtils.ERROR_MSG_PARM_ERROR);
                return;
        	}
        	
        	jsonObj.put(RtcConst.kgvctype, jsonObj.get("groupType"));
    		//群组名
    		jsonObj.put(RtcConst.kgvcname, jsonObj.get("groupName"));
    		//密码
    		jsonObj.put(RtcConst.kgvcpassword, jsonObj.get("passWord"));
    		//被邀请人
    		jsonObj.put(RtcConst.kgvcinvitedList, jsonObj.get("members"));
    		//opt 与视频微直播相关，需要与setVideoCodec的设置一致，不设置则默认为H264
    		jsonObj.put(RtcConst.klivecodec, RtcConst.VCodec_VP8);
    		jsonObj.remove("groupType");
    		jsonObj.remove("groupName");
    		jsonObj.remove("passWord");
    		jsonObj.remove("members");
    		//mAcc = mClt.createDevice(jsonObj.toString(), mAListener);
    		Utils.PrintLog(5, LOGTAG, "createGroupCallJson:"+jsonObj.toString());
            //重复创建
        	if(grpmgr.groupCall(RtcConst.groupcall_opt_create,jsonObj.toString()) == -1){
        		mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, ConstantUtils.ERROR_MSG_UNCALL);
        	}
    	}catch (JSONException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    }
    
    //groupMember 获取会议成员列表
    public void groupMember(String[] parm) {
    	Utils.PrintLog(5, LOGTAG, "groupMember 获取会议成员列表");
    	String errorMsg = ConstantUtils.ERROR_MSG_ERROR;
        if(mAcc == null) {
    		errorMsg = ConstantUtils.ERROR_MSG_UNREGISTER;
    		//“ERROR:UNREGISTER”：未注册至RTC平台
            mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, 
            		errorMsg);
    		Utils.PrintLog(5, LOGTAG, "groupMember 未注册至RTC平台");
        	return;
        }
        grpmgr = mAcc.getGroup();
        if(grpmgr == null)
            return;
        if(grpmgr.groupCall(RtcConst.groupcall_opt_getmemberlist , null) == -1) {
    		//ERROR:INVALIDOPERATION
            mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, ConstantUtils.ERROR_MSG_UNCALL);
        	Utils.PrintLog(5, LOGTAG, "未创建呼叫，不能操作此接口");
        }
    }
    
    //groupInvite 邀请成员加入会议
    public void groupInvite(String[] parm) {
        Utils.PrintLog(5, LOGTAG, "groupInvite 邀请成员加入会议");
        String errorMsg = ConstantUtils.ERROR_MSG_ERROR;
        if(parm[0].length() == 0) {
        	errorMsg = ConstantUtils.ERROR_MSG_PARM_ERROR;
            //“ERROR:PARM_ERROR”：参数有误，调用接口失败
            mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, 
            		errorMsg);
            return;
        }
        if(mAcc == null) {
    		errorMsg = ConstantUtils.ERROR_MSG_UNREGISTER;
    		//“ERROR:UNREGISTER”：未注册至RTC平台
            mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, 
            		errorMsg);
    		Utils.PrintLog(5, LOGTAG, "groupMember 未注册至RTC平台");
        	return;
        }
        grpmgr = mAcc.getGroup();
        if(grpmgr==null)
            return;
        String remoteuri = parm[0];
        //多人列表取被叫 逗号 间隔 
        if(grpmgr.groupCall(RtcConst.groupcall_opt_invitedmemberlist,remoteuri) == -1) {
    		//“ERROR:UNCALL”：未创建呼叫，不能操作此接口
            mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, ConstantUtils.ERROR_MSG_UNCALL);
        	Utils.PrintLog(5, LOGTAG, "未创建呼叫，不能操作此接口");
        }
    }
    
    //groupList 查询当前appid的应用所有正在进行的会议
    public void  groupList(String[] parm) {
        Utils.PrintLog(5, LOGTAG, "groupList 查询当前appid的应用所有正在进行的会议");
        String errorMsg = ConstantUtils.ERROR_MSG_ERROR;
        if(mAcc == null) {
    		errorMsg = ConstantUtils.ERROR_MSG_UNREGISTER;
    		//“ERROR:UNREGISTER”：未注册至RTC平台
            mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, 
            		errorMsg);
    		Utils.PrintLog(5, LOGTAG, "groupMember 未注册至RTC平台");
        	return;
        }
        grpmgr = mAcc.getGroup();
        if(grpmgr==null)
            return;
        grpmgr.groupCall(RtcConst.groupcall_opt_qlist, null);
    }
    
    //groupJoin 主动加入会议
    public void  groupJoin(String[] parm){
        Utils.PrintLog(5, LOGTAG, "onBtnGrpCall_join 会议类型："+grptype);
        String errorMsg = ConstantUtils.ERROR_MSG_ERROR;
        if(parm[0].length() == 0 || parm[1].length() == 0){
        	errorMsg = ConstantUtils.ERROR_MSG_PARM_ERROR;
            //“ERROR:PARM_ERROR”：参数有误，调用接口失败
            mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, 
            		errorMsg);
            return;
        }
        if(mAcc == null) {
    		errorMsg = ConstantUtils.ERROR_MSG_UNREGISTER;
    		//“ERROR:UNREGISTER”：未注册至RTC平台
    		mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, 
        		errorMsg);
    		Utils.PrintLog(5, LOGTAG, "groupMember 未注册至RTC平台");
    	    return;
    	}
    	grpmgr = mAcc.getGroup();
	    JSONObject jsonObj = new JSONObject();
	    try {
	    	jsonObj.put(RtcConst.kGrpID , parm[0]); //yes
            jsonObj.put(RtcConst.kgvccreator, false); //yes
            //opt：收到过邀请，后续再加入时不需要密码；而未被邀请的要加入必须携带密码
            jsonObj.put(RtcConst.kgvcpassword, parm[1]);
            Utils.PrintLog(5, LOGTAG, "createGroupJoinJson:"+jsonObj.toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	    if(grpmgr.groupCall(RtcConst.groupcall_opt_join,jsonObj.toString()) == -1) {
    		mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, ConstantUtils.ERROR_MSG_UNCALL);
    	}
    }
    
    //groupKick 踢出会议成员
    public void groupKick(String[] parm){
        String errorMsg = ConstantUtils.ERROR_MSG_ERROR;
        if(parm[0].length() == 0) {
        	errorMsg = ConstantUtils.ERROR_MSG_PARM_ERROR;
            //“ERROR:PARM_ERROR”：参数有误，调用接口失败
            mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, 
            		errorMsg);
            return;
        }
    	if(mAcc == null) {
    		errorMsg = ConstantUtils.ERROR_MSG_UNREGISTER;
    		//“ERROR:UNREGISTER”：未注册至RTC平台
    		mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, 
        		errorMsg);
    		Utils.PrintLog(5, LOGTAG, "groupMember 未注册至RTC平台");
    	    return;
    	}
    	grpmgr = mAcc.getGroup();
    	if(grpmgr==null){
            return;
        }
        String remoteuri = parm[0];
        //remoteuri =  remoteuri.replace("@", RtcConst.char_key);
        //多人列表取被叫 逗号 间隔 
        if(grpmgr.groupCall(RtcConst.groupcall_opt_kickedmemberlist,remoteuri) == -1) {
    		mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, ConstantUtils.ERROR_MSG_UNCALL);
    	}

    }
    
    
    public void groupClose(String[] parm) {
    	String errorMsg = ConstantUtils.ERROR_MSG_ERROR;
        Utils.PrintLog(5, LOGTAG, "onBtnGrpV_CloseGrpv:");
    	if(mAcc == null) {
    		errorMsg = ConstantUtils.ERROR_MSG_UNREGISTER;
    		//“ERROR:UNREGISTER”：未注册至RTC平台
    		mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, 
        		errorMsg);
    		Utils.PrintLog(5, LOGTAG, "groupMember 未注册至RTC平台");
    	    return;
    	}
    	grpmgr = mAcc.getGroup();   
    	if(grpmgr==null){
            return;  	
        }
        if(grpmgr.groupCall(RtcConst.groupcall_opt_close,null) == -1) {
            mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, ConstantUtils.ERROR_MSG_UNCALL);
    	}
    }
    
    //抢麦和释麦操作
    public void groupMic(String[] parm) {
    	String errorMsg = ConstantUtils.ERROR_MSG_ERROR;
        Utils.PrintLog(5, LOGTAG, "onBtnGrpCall_MDisp:");
        if(parm[0].length() == 0 || parm[1].length() == 0 || parm[2].length() == 0){
        	errorMsg = ConstantUtils.ERROR_MSG_PARM_ERROR;
            //“ERROR:PARM_ERROR”：参数有误，调用接口失败
            mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, 
            		errorMsg);
            return;
        } else {
        	OnStreamManagement(parm[0] , parm[1] , parm[2]);          
        }  
    }    
    
    //视频管理
    public void groupVideo(String[] parm){
    	String errorMsg = ConstantUtils.ERROR_MSG_ERROR;
        Utils.PrintLog(5, LOGTAG, "onBtnGrpCall_MDisp:");
        if(parm[0].length() == 0) {
        	errorMsg = ConstantUtils.ERROR_MSG_PARM_ERROR;
            //“ERROR:PARM_ERROR”：参数有误，调用接口失败
            mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, 
            		errorMsg);
            return;
        }
       	if(mAcc == null) {
    		errorMsg = ConstantUtils.ERROR_MSG_UNREGISTER;
    		//“ERROR:UNREGISTER”：未注册至RTC平台
    		mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, 
        		errorMsg);
    		Utils.PrintLog(5, LOGTAG, "groupMember 未注册至RTC平台");
    	    return;
    	}
        grpmgr = mAcc.getGroup();
        if(grpmgr==null) {
            return;  	
        }
        try {
        	//分屏数设置，该参数也可以在刚开始创建多人视频时就指定
            //0：由后台指定
            //1：1*1
        	//2：1*2 需要平台单独配置生效
            //3：2*2
            //4：2*3
            //5：3*3 需要平台单独配置生效
        	JSONObject jsonObj = new JSONObject(parm[0]);
        	if(jsonObj.has("mode"))
        		jsonObj.remove("mode");
        	if(jsonObj.has("memberToSet"))
        		jsonObj.remove("memberToSet");
        	if(jsonObj.has("memberToShow"))
        		jsonObj.remove("memberToShow");
        	if(grpmgr.groupCall(RtcConst.groupcall_opt_mdisp,jsonObj.toString()) == -1) {
        		mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, ConstantUtils.ERROR_MSG_UNCALL);
        	}
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            	e.printStackTrace();
        }
    }
    
    private void OnStreamManagement(String members , String upMode , String downMode) {
        Utils.PrintLog(5, LOGTAG, "OnStreamManagement:");
    	String errorMsg = ConstantUtils.ERROR_MSG_ERROR;
    	if(mAcc == null){
    		errorMsg = ConstantUtils.ERROR_MSG_UNREGISTER;
    		//“ERROR:UNREGISTER”：未注册至RTwC平台
    		mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, 
    				errorMsg);
    		Utils.PrintLog(5, LOGTAG, "groupMember 未注册至RTC平台");
    		return;
	    }
        grpmgr = mAcc.getGroup();
    	if(grpmgr == null){
    		return;  	
    	}
    	String[] memList = members.split(",");
        int up = Integer.parseInt(upMode);
        int down = Integer.parseInt(downMode);
        try {
            JSONArray jsonArr = new JSONArray();
            for(int i=0; i<memList.length; i++) {
            	JSONObject jsonObj1 = new JSONObject();
                jsonObj1.put(RtcConst.kGrpMember, memList[i]);
                jsonObj1.put(RtcConst.kGrpUpOpType, up);
                jsonObj1.put(RtcConst.kGrpDownOpType, down);   
                jsonArr.put(i, jsonObj1);
            }
            if(grpmgr.groupCall(RtcConst.groupcall_opt_strm,jsonArr.toString()) == -1) {
	    		mCbhandler.send2Callback(ConstantUtils.WHAT_CALLBACK_GROUP_STATUS, ConstantUtils.ERROR_MSG_UNCALL);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
                
            case ConstantUtils.WHAT_CALLBACK_GROUP_STATUS:
            	js = SCRIPT_HEADER + "if(" + CALLBACK_GROUP_STATUS + "){"
                        + CALLBACK_GROUP_STATUS + "(" + 0 + "," + EUExCallback.F_C_TEXT + ",'" +(String)msg.obj + "');}";
                evaluateScript("root", 0, js);
                break;
            }
        }
    }
}
