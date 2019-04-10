package com.xindawn.mediacodec;


import com.xindawn.center.DlnaMediaModel;
import com.xindawn.center.DlnaMediaModelFactory;
import com.xindawn.center.MediaControlBrocastFactory;
import com.xindawn.center.MediaRenderProxy;
import com.xindawn.util.CommonLog;
import com.xindawn.util.CommonUtil;
import com.xindawn.util.DlnaUtils;
import com.xindawn.util.LogFactory;


import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.xindawn.center.MediaControlBrocastFactory;
import com.xindawn.jni.PlatinumJniProxy;
import com.xindawn.jni.PlatinumReflection;

import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.LinearLayout;
import com.xindawn.R;

import com.xindawn.jni.PlatinumJniProxy;
import android.widget.Toast;

public class VideoDecoderActivity extends Activity implements SurfaceHolder.Callback,MediaControlBrocastFactory.IMediaControlListener{

	private static final CommonLog log = LogFactory.createLog();
	private MediaControlBrocastFactory mMediaControlBorcastFactor;
	public static Handler mHandler;
	
	private static final int REFRESH_SPEED= 0x0001;
	private static final int EXIT_ACTIVITY = 0x0002;
	
	private SurfaceView[] surfaceView;

	//private int[] surfaceWidth;
	//private int[] surfaceHeight;
	
	private long exitTime = 0;
	
	public static final int AIRPLAY_MAX_COUNT = 16;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
	
		
		mMediaControlBorcastFactor = new MediaControlBrocastFactory(this);
		mMediaControlBorcastFactor.register(this);
		
		mHandler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				switch(msg.what){
				case EXIT_ACTIVITY:
					log.d("EXIT_ACTIVITY");
					finish();
					break;
				}
			}
			
		};
		
		

		
	    Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        window.setAttributes(params);
		   
		setContentView(R.layout.activity_main_mediacodec);

		surfaceView    = new SurfaceView[AIRPLAY_MAX_COUNT];
		//surfaceWidth   = new int[AIRPLAY_MAX_COUNT];
		//surfaceHeight  = new int[AIRPLAY_MAX_COUNT];
		
	    surfaceView[0] = (SurfaceView) findViewById(R.id.layoutAirPlay1).findViewById(R.id.video1);
	    surfaceView[0].getHolder().addCallback(this);
	   


	    surfaceView[1] = (SurfaceView) findViewById(R.id.layoutAirPlay1).findViewById(R.id.video2);
	    surfaceView[1].getHolder().addCallback(this);
	    
	    surfaceView[2] = (SurfaceView) findViewById(R.id.layoutAirPlay1).findViewById(R.id.video3);
	    surfaceView[2].getHolder().addCallback(this);


	    surfaceView[3] = (SurfaceView) findViewById(R.id.layoutAirPlay1).findViewById(R.id.video4);
	    surfaceView[3].getHolder().addCallback(this);
	    
	    
	    surfaceView[4] = (SurfaceView) findViewById(R.id.layoutAirPlay2).findViewById(R.id.video1);
	    surfaceView[4].getHolder().addCallback(this);


	    surfaceView[5] = (SurfaceView) findViewById(R.id.layoutAirPlay2).findViewById(R.id.video2);
	    surfaceView[5].getHolder().addCallback(this);
	    
	    surfaceView[6] = (SurfaceView) findViewById(R.id.layoutAirPlay2).findViewById(R.id.video3);
	    surfaceView[6].getHolder().addCallback(this);


	    surfaceView[7] = (SurfaceView) findViewById(R.id.layoutAirPlay2).findViewById(R.id.video4);
	    surfaceView[7].getHolder().addCallback(this);
	    
	    surfaceView[8] = (SurfaceView) findViewById(R.id.layoutAirPlay3).findViewById(R.id.video1);
	    surfaceView[8].getHolder().addCallback(this);


	    surfaceView[9] = (SurfaceView) findViewById(R.id.layoutAirPlay3).findViewById(R.id.video2);
	    surfaceView[9].getHolder().addCallback(this);
	    
	    surfaceView[10] = (SurfaceView) findViewById(R.id.layoutAirPlay3).findViewById(R.id.video3);
	    surfaceView[10].getHolder().addCallback(this);


	    surfaceView[11] = (SurfaceView) findViewById(R.id.layoutAirPlay3).findViewById(R.id.video4);
	    surfaceView[11].getHolder().addCallback(this);
	    
	    
	    
	    surfaceView[12] = (SurfaceView) findViewById(R.id.layoutAirPlay4).findViewById(R.id.video1);
	    surfaceView[12].getHolder().addCallback(this);


	    surfaceView[13] = (SurfaceView) findViewById(R.id.layoutAirPlay4).findViewById(R.id.video2);
	    surfaceView[13].getHolder().addCallback(this);
	    
	    surfaceView[14] = (SurfaceView) findViewById(R.id.layoutAirPlay4).findViewById(R.id.video3);
	    surfaceView[14].getHolder().addCallback(this);


	    surfaceView[15] = (SurfaceView) findViewById(R.id.layoutAirPlay4).findViewById(R.id.video4);
	    surfaceView[15].getHolder().addCallback(this);
   


	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		
		for (int i = 0; i < AIRPLAY_MAX_COUNT; i++)
		{
			if (holder == surfaceView[i].getHolder())
			{
				PlatinumJniProxy.setSurfaceView(i,holder.getSurface());
			
			
//				if(getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
//				{
//					surfaceWidth[i]  = surfaceView[i].getWidth();
//					surfaceHeight[i] = surfaceView[i].getHeight();
//				}
//				else
//				{
//					surfaceWidth[i]  = surfaceView[i].getHeight();
//					surfaceHeight[i] = surfaceView[i].getWidth();
//				}				
				break;
			}
		}
		


		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}
	
	
	private void removeExitMessage(){
		mHandler.removeMessages(EXIT_ACTIVITY);
	}
	
	private static final int EXIT_DELAY_TIME = 200;
	private void delayToExit(){
		removeExitMessage();
		//mHandler.sendEmptyMessageDelayed(EXIT_ACTIVITY, EXIT_DELAY_TIME);
		mHandler.sendEmptyMessage(EXIT_ACTIVITY);
	}
	
	
	@Override
	protected void onDestroy() {
		
		log.d("onDestroy 1");
		mMediaControlBorcastFactor.unregister();
		super.onDestroy();
	}
	
	
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		
		for(int i = 0; i< AIRPLAY_MAX_COUNT; i++)
		{
			PlatinumJniProxy.channelShutDown(i);
		}
		
		super.finish();
		
		MediaRenderProxy.getInstance().stopEngine();

	}
	
    public void onBackPressed() {

        if((System.currentTimeMillis()-exitTime) > 2000){
           Toast.makeText(getApplicationContext(), "Please click BACK again to exit.", Toast.LENGTH_SHORT).show();
           exitTime = System.currentTimeMillis();
        } else {
           finish();
        }
     }

	
	@Override
	public void onPlayCommand(String data) {
		// TODO Auto-generated method stub
		//surfaceView[Integer.valueOf(data)].setVisibility(View.VISIBLE);
	}

	@Override
	public void onPauseCommand() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopCommand(int type) {

		
		//surfaceView[type].setVisibility(View.GONE);
		
	}

	@Override
	public void onSeekCommand(int time) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onCoverCommand(byte data[])
	{
		
	}
	
	@Override
	public void onMetaDataCommand(String data)
	{
		
	}
	
	@Override
	public void onIPAddrCommand(String data)
	{

	}
	
	@Override
	public void onSizeChangedCommand(String data)
	{

		
//		String[] videosize = data.split("X");
//
//		int id 			 = Integer.parseInt(videosize[0]);
//	    int videoWidth   = Integer.parseInt(videosize[1]);
//        int videoHeight  = Integer.parseInt(videosize[2]);
//        
//    	log.d("onSizeChangedCommand " + videoWidth + "  " + videoHeight);
//	    
//	    float max;
//        if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
//            max = Math.max((float) videoWidth / (float) surfaceWidth[id],(float) videoHeight / (float) surfaceHeight[id]);
//        } else{
//            max = Math.max(((float) videoWidth/(float) surfaceHeight[id]),(float) videoHeight/(float) surfaceWidth[id]);
//        }
//
//
//        videoWidth  = (int) Math.ceil((float) videoWidth  / max);
//        videoHeight = (int) Math.ceil((float) videoHeight / max);
//
//		surfaceView[id].setLayoutParams(new LinearLayout.LayoutParams(videoWidth, videoHeight));
	}

}
