package org.zywx.wbpalmstar.plugin.uexesurfingrtc;

import org.zywx.wbpalmstar.engine.universalex.EUExBase;

import rtc.sdk.iface.Connection;
import android.content.Context;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;

public class SurfaceViewRtc{
    
    private EUExBase mEuExBase;
    private Context mContext;
    static RelativeLayout.LayoutParams lparm1, lparm2;
    
    public SurfaceViewRtc(EUExBase mEuExBase, Context mContext)
    {
        this.mEuExBase = mEuExBase;
        this.mContext = mContext;
    }
    
    private SurfaceView createVideoView(boolean isLocalView, Context ctx, boolean bOpenGL,
            Connection mCall, ViewConfig mViewConfig)
    {
        SurfaceView mSurfaceView = (SurfaceView) mCall
                .createVideoView(isLocalView, ctx, bOpenGL);
        mSurfaceView.setVisibility(View.INVISIBLE);
        if(isLocalView) {
            mSurfaceView.setKeepScreenOn(true);
            //mSurfaceView.setZOrderMediaOverlay(true);
            //mSurfaceView.setZOrderOnTop(true);
        }
        RelativeLayout.LayoutParams lparm 
                = new RelativeLayout.LayoutParams(mViewConfig.width, mViewConfig.hight);
        lparm.leftMargin = mViewConfig.axis;
        lparm.topMargin = mViewConfig.ordinate;
        mEuExBase.addViewToCurrentWindow(mSurfaceView, lparm);
        if(isLocalView)
        	lparm1 = lparm;
        else
        	lparm2 = lparm;
        return mSurfaceView;
    }
    
    /**
     * Inits the video views.
     * 10 800 144*3 176*3
     */
    public CallView initVideoViews(Connection mCall, 
            ViewConfig mLocalViewConfig, ViewConfig mRemoteViewConfig) 
    {
    	if(mCall == null) {
            lparm1 = new RelativeLayout.LayoutParams(mLocalViewConfig.width, mLocalViewConfig.hight);
            lparm1.leftMargin = mLocalViewConfig.axis;
            lparm1.topMargin = mLocalViewConfig.ordinate;
            lparm2 = new RelativeLayout.LayoutParams(mRemoteViewConfig.width, mRemoteViewConfig.hight);
            lparm2.leftMargin = mRemoteViewConfig.axis;
            lparm2.topMargin = mRemoteViewConfig.ordinate;
            return null;
        }
        CallView mCallView = new CallView();
        if(null == mCallView.mvLocal)
        {

            mCallView.mvLocal = createVideoView(true, mContext,
                    true, mCall, mLocalViewConfig);
            mCallView.mvRemote = createVideoView(false, mContext,
            		true, mCall, mRemoteViewConfig);
        }
        return mCallView;
    }
    
    /**
     * Sets the video surface visibility.
     *
     * @param visible the new video surface visibility
     */
    public void setVideoSurfaceVisibility(CallView mCallView, int visible) 
    {
        if(mCallView != null)
        {
            if(mCallView.mvLocal != null)
                mCallView.mvLocal.setVisibility(visible);
            if(mCallView.mvRemote != null)
                mCallView.mvRemote.setVisibility(visible);
        }
    }
    
}

class ViewConfig{
    int axis;
    int ordinate;
    int width;
    int hight;
}

class CallView{
    SurfaceView mvLocal;
    SurfaceView mvRemote;
}
        
