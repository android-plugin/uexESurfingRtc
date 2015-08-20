package org.zywx.wbpalmstar.plugin.uexesurfingrtc.utils;

public class ConstantUtils 
{
    public static final int SET_APP_KEY_ID_KEY_OFFSET = 0;
    public static final int SET_APP_KEY_ID_ID_OFFSET = 1;
    
    public static final int CALL_TYPE_ID_OFFSET = 0;
    public static final int CALL_USEER_NAME_OFFSET = 1;

    public static final int CALL_TYPE_AUDIO = 1;
    public static final int CALL_TYPE_VIDEO = 2;
    public static final int CALL_TYPE_AUDIO_AND_VIDED = 3;
    public static final int CALL_TYPE_RECV_ONLY = 4;
    public static final int CALL_TYPE_SEND_ONLY = 5;
    
    public static final String ERROR_MSG_OK = "OK";
    public static final String ERROR_MSG_ERROR = "ERROR:";
    public static final String ERROR_MSG_PARM_ERROR = ERROR_MSG_ERROR + "PARM_ERROR";
    public static final String ERROR_MSG_UNINIT = ERROR_MSG_ERROR + "UNINIT";
    public static final String ERROR_MSG_UNREGISTER = ERROR_MSG_ERROR + "UNREGISTER";
    public static final String LOG_STATUS_LOGIN = ERROR_MSG_OK + ":LOGIN";
    public static final String LOG_STATUS_LOGOUT = ERROR_MSG_OK + ":LOGOUT";
    public static final String CALL_STATUS_NORMAL = ERROR_MSG_OK + ":NORMAL";
    public static final String CALL_STATUS_INCOMING = ERROR_MSG_OK + ":INCOMING";
    public static final String CALL_STATUS_CALLING = ERROR_MSG_OK + ":CALLING";

    public static final int MSG_GETTOKEN = 10001;
    public static final int MSG_SET_SURFACE_VIEW_VISIBILITUY = 10002;
    public static final int MSG_REMOVE_SURFACE_VIEW = 10003;
    public static final int MSG_RTC_CLIENT_ON_INIT = 10004;
    public static final int MSG_REGISTER = 10005;
    
    public static final int WHAT_CALLBACK_GLOBAL_STATUS = 0;
    public static final int WHAT_CALLBACK_LOG_STATUS = 1;
    public static final int WHAT_CALLBACK_CALL_STATUS = 2;
    public static final int WHAT_CALLBACK_REMOTE_PIC_PATH = 3;
    public static final int WHAT_CALLBACK_SET_APPKEY_ID = 4;

    public static final String TRUE_STR = "true";
    public static final String FALSE_STR = "false";
    public static final String REGISTER_INIT_KEY = "REGISTER_INIT_KEY";
    public static final String REGISTER_USER_NAME_KEY = "REGISTER_USER_NAME_KEY";

    public static final String TIME_FORMAT_GLOBAL_STATUS = "hh:mm:ss";
    public static final String TIME_FORMAT_PIC_NAME = "yyyyMMddhhmmss";
    public static final String PIC_FOLDER_NAME = "/photo/";
    public static final String PIC_FORMAT = ".png";
    
    public static final String JK_LOCA_VIEW_CON = "localView";
    public static final String JK_REMOTE_VIEW_CON = "remoteView";
    public static final String JK_VIEW_CON_X = "x";
    public static final String JK_VIEW_CON_Y = "y";
    public static final String JK_VIEW_CON_W = "w";
    public static final String JK_VIEW_CON_H = "h";
    
}
