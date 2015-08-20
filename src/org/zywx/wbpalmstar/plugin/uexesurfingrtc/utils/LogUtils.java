package org.zywx.wbpalmstar.plugin.uexesurfingrtc.utils;
import android.util.Log;

public class LogUtils{
    private static String TAG = "uexESurfingRtc";
    
    public static String getTAG()
    {
        return TAG;
    }
    
    public static void logWlDebug(boolean dubug, String info)
    {
        if(dubug)
        {
            Log.i(TAG, info);
        }
    }
    
    public static void logError(String info)
    {
        Log.e(TAG, info);
    }
    
    public static String getLineInfo()
    {  
        StackTraceElement ste = new Throwable().getStackTrace()[1];
        return (ste.getFileName() + ": Line " + ste.getLineNumber() + ": ");
    }
}
