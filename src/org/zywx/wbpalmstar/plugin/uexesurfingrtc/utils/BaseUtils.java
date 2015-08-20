package org.zywx.wbpalmstar.plugin.uexesurfingrtc.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.pm.PackageManager;

public class BaseUtils {
    
    public static boolean isStringEmpty(final String str)
    {
        boolean empty = false;
        if((null == str) || ("".equals(str)))
        {
            empty = true;
        }
        return empty;
    }
    
    public static String getAppName(Context mContext)
    {
        PackageManager pm = mContext.getPackageManager();
        return mContext.getApplicationInfo().loadLabel(pm).toString();
    }
    
    public static String getSpecialFormatTime(String format)
    {
        SimpleDateFormat tm = new SimpleDateFormat(format);
        return tm.format(new Date());
    }
}
