package org.zywx.wbpalmstar.plugin.uexesurfingrtc;

import java.io.File;

import org.json.JSONObject;
import org.zywx.wbpalmstar.plugin.uexesurfingrtc.utils.BaseUtils;
import org.zywx.wbpalmstar.plugin.uexesurfingrtc.utils.ConstantUtils;

import android.content.Context;
import android.os.Environment;
import rtc.sdk.common.RtcConst;

public class RtcBase{

    private static String APP_ID = "123";//
    private static String APP_KEY ="123456";//
    
    public static String getAppId()
    {
        return APP_ID;
    }
    
    public static String getAppKey()
    {
        return APP_KEY;
    }  
    public static void setAppId(String appId)
    {
        APP_ID = appId;
    }
    
    public static void setAppKey(String appKey)
    {
        APP_KEY = appKey;
    }
    
    public static boolean isVideoAttr(int attr)
    {
        return ((attr >= RtcConst.Video_SD) && (attr <= RtcConst.Video_HD));
    }
    
    public static String getPicPath(Context mContext)
    {
        return (Environment.getExternalStorageDirectory()
                .getAbsolutePath()
                + File.separator
                + BaseUtils.getAppName(mContext)
                + ConstantUtils.PIC_FOLDER_NAME);
    }
    
    public static String createRemotePicFloder(Context mContext, String path)
    {
        if(BaseUtils.isStringEmpty(path))
        {
            path = getPicPath(mContext);
        }
        File file = new File(path);
        if (!file.exists())
        {
            file.mkdirs();
        }
        return path;
    }
    
    public static ViewConfig parseViewConfigJson(JSONObject json)
    {
        ViewConfig mViewConfig = new ViewConfig();
        
        mViewConfig.axis = (int)Float
                .parseFloat(json.optString(ConstantUtils.JK_VIEW_CON_X));
        mViewConfig.ordinate = (int)Float
                .parseFloat(json.optString(ConstantUtils.JK_VIEW_CON_Y));
        mViewConfig.width = (int)Float
                .parseFloat(json.optString(ConstantUtils.JK_VIEW_CON_W));
        mViewConfig.hight = (int)Float
                .parseFloat(json.optString(ConstantUtils.JK_VIEW_CON_H));
        
        return mViewConfig;
    }
    
}
