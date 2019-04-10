package com.xindawn.center;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class MediaControlBrocastFactory {

	public static interface IMediaControlListener {
		public void onPlayCommand(String data);
		public void onPauseCommand();
		public void onStopCommand(int type);
		public void onSeekCommand(int time);	
		public void onCoverCommand(byte data[]);	
		public void onMetaDataCommand(String data);
		public void onIPAddrCommand(String data);
		public void onSizeChangedCommand(String data);
	}
	
	
	private MediaControlBrocastReceiver mMediaControlReceiver;
	private Context mContext;
	
	public MediaControlBrocastFactory(Context context){
		mContext = context;
	}
	
	
	public void register(IMediaControlListener listener){
		if (mMediaControlReceiver == null){
			mMediaControlReceiver = new MediaControlBrocastReceiver();
			mMediaControlReceiver.setMediaControlListener(listener);	
			
			mContext.registerReceiver(mMediaControlReceiver, new IntentFilter(MediaControlBrocastFactory.MEDIA_RENDERER_CMD_PLAY));
			mContext.registerReceiver(mMediaControlReceiver, new IntentFilter(MediaControlBrocastFactory.MEDIA_RENDERER_CMD_PAUSE));
			mContext.registerReceiver(mMediaControlReceiver, new IntentFilter(MediaControlBrocastFactory.MEDIA_RENDERER_CMD_STOP));
			mContext.registerReceiver(mMediaControlReceiver, new IntentFilter(MediaControlBrocastFactory.MEDIA_RENDERER_CMD_SEEKPS));
			mContext.registerReceiver(mMediaControlReceiver, new IntentFilter(MediaControlBrocastFactory.MEDIA_RENDERER_CMD_COVER));
			mContext.registerReceiver(mMediaControlReceiver, new IntentFilter(MediaControlBrocastFactory.MEDIA_RENDERER_CMD_METADATA));
			mContext.registerReceiver(mMediaControlReceiver, new IntentFilter(MediaControlBrocastFactory.MEDIA_RENDERER_CMD_IPADDR));
			mContext.registerReceiver(mMediaControlReceiver, new IntentFilter(MediaControlBrocastFactory.MEDIA_RENDERER_CMD_SIZECHANGED));
		}
	}

	public void unregister()
	{
		if (mMediaControlReceiver != null){
			mContext.unregisterReceiver(mMediaControlReceiver);
			mMediaControlReceiver = null;
		}
	}
	
	
	
	public static final String MEDIA_RENDERER_CMD_PLAY="com.xindawn.control.play_command"; 
	public static final String MEDIA_RENDERER_CMD_PAUSE="com.xindawn.control.pause_command";
	public static final String MEDIA_RENDERER_CMD_STOP="com.xindawn.control.stop_command"; 
	public static final String MEDIA_RENDERER_CMD_SEEKPS="com.xindawn.control.seekps_command"; 
	public static final String MEDIA_RENDERER_CMD_COVER="com.xindawn.control.cover"; 
	public static final String MEDIA_RENDERER_CMD_METADATA="com.xindawn.control.metadata"; 
	public static final String MEDIA_RENDERER_CMD_IPADDR="com.xindawn.control.ipaddr"; 
	public static final String MEDIA_RENDERER_CMD_SIZECHANGED="com.xindawn.control.sizechanged"; 
		
	public static final String PARAM_CMD_SEEKPS="get_param_seekps";
	public static final String PARAM_CMD_STOPTYPE ="get_param_stoptype";
	public static final String PARAM_CMD_COVER ="get_param_cover";
	public static final String PARAM_CMD_METADATA="get_param_metadata";
	public static final String PARAM_CMD_IPADDR="get_param_ipaddr";
	public static final String PARAM_CMD_SIZECHANGED="get_param_sizechanged";
	public static final String PARAM_CMD_PLAY="get_param_play";
	
	public static void sendPlayBrocast(Context context, String data){
		Intent intent = new Intent(MEDIA_RENDERER_CMD_PLAY);
		intent.putExtra(PARAM_CMD_PLAY, data);
		context.sendBroadcast(intent);
	}
	
	public static void sendPauseBrocast(Context context){
		Intent intent = new Intent(MEDIA_RENDERER_CMD_PAUSE);
		context.sendBroadcast(intent);
	}
	
	public static void sendStopBorocast(Context context,int type){
		Intent intent = new Intent(MEDIA_RENDERER_CMD_STOP);
		intent.putExtra(PARAM_CMD_STOPTYPE, type);
		context.sendBroadcast(intent);
	}
	
	public static void sendSeekBrocast(Context context, int seekPos){
		Intent intent = new Intent(MEDIA_RENDERER_CMD_SEEKPS);
		intent.putExtra(PARAM_CMD_SEEKPS, seekPos);
		context.sendBroadcast(intent);
	}
	
	public static void sendCoverBrocast(Context context, byte data[]){
		Intent intent = new Intent(MEDIA_RENDERER_CMD_COVER);
		intent.putExtra(PARAM_CMD_COVER, data);
		context.sendBroadcast(intent);
	}
	
	public static void sendMetaDataBrocast(Context context, String data){
		Intent intent = new Intent(MEDIA_RENDERER_CMD_METADATA);
		intent.putExtra(PARAM_CMD_METADATA, data);
		context.sendBroadcast(intent);
	}
	
	public static void sendIPAddrBrocast(Context context, String data){
		Intent intent = new Intent(MEDIA_RENDERER_CMD_IPADDR);
		intent.putExtra(PARAM_CMD_IPADDR, data);
		context.sendBroadcast(intent);
	}
	
	public static void sendSizeChangedBrocast(Context context, String data){
		Intent intent = new Intent(MEDIA_RENDERER_CMD_SIZECHANGED);
		intent.putExtra(PARAM_CMD_SIZECHANGED, data);
		context.sendBroadcast(intent);
	}

}
