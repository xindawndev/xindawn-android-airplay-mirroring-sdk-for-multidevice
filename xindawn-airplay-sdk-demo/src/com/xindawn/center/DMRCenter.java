package com.xindawn.center;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.xindawn.image.ImageActivity;
import com.xindawn.jni.PlatinumReflection;
import com.xindawn.jni.PlatinumReflection.ActionReflectionListener;
import com.xindawn.music.MusicActivity;
import com.xindawn.util.CommonLog;
import com.xindawn.util.CommonUtil;
import com.xindawn.util.DlnaUtils;
import com.xindawn.util.LogFactory;
import com.xindawn.video.VideoActivity;
import com.xindawn.airgl.AirPlayerGLActivity;
import com.xindawn.mediacodec.VideoDecoderActivity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.AudioTrack;
import java.nio.ByteBuffer;


public class DMRCenter implements ActionReflectionListener, IDMRAction{

	private static final CommonLog log = LogFactory.createLog();
	
	private Context mContext;
	
	private DlnaMediaModel mMusicMediaInfo;
	private DlnaMediaModel mVideoMediaInfo;
	private DlnaMediaModel mImageMediaInfo;
	private DlnaMediaModel mScreenMediaInfo;
	
	public static AudioManager N;
	public static AudioTrack[] track = null;

	
	private int mCurMediaInfoType = -1;
	public static final int CUR_MEDIA_TYPE_MUSCI = 0x0001;
	public static final int CUR_MEDIA_TYPE_VIDEO = 0x0000;
	public static final int CUR_MEDIA_TYPE_PICTURE = 0x0002;
	public static final int CUR_MEDIA_TYPE_SCREEN = 0x0003;//0x0003;
	
	public DMRCenter(Context context){
		mContext = context;
		track = new AudioTrack[16];
	}
	
	@Override
	public synchronized void onActionInvoke(int cmd, String value, String data,String title) {
	
		switch(cmd){		
			case PlatinumReflection.MEDIA_RENDER_CTL_MSG_SET_AV_URL:		
				onRenderAvTransport(value, data);
				break;
			case PlatinumReflection.MEDIA_RENDER_CTL_MSG_PLAY:				
				onRenderPlay(value, data);
				break;
			case PlatinumReflection.MEDIA_RENDER_CTL_MSG_PAUSE:
				onRenderPause(value, data);
				break;
			case PlatinumReflection.MEDIA_RENDER_CTL_MSG_STOP:
				onRenderStop(value, data);
				break;
			case PlatinumReflection.MEDIA_RENDER_CTL_MSG_SEEK:
				onRenderSeek(value, data);
				break;
			case PlatinumReflection.MEDIA_RENDER_CTL_MSG_SETMUTE:
				onRenderSetMute(value, data);
				break;
			case PlatinumReflection.MEDIA_RENDER_CTL_MSG_SETVOLUME:
				onRenderSetVolume(value, data);
				break;
			case PlatinumReflection.MEDIA_RENDER_CTL_MSG_SETMETADATA:
				onRenderSetMetaData(value, data);
				break;
			case PlatinumReflection.MEDIA_RENDER_CTL_MSG_SETIPADDR:
				onRenderSetIPAddr(value, data);
				log.e("ipaddr = " + value );
				break;	
			case PlatinumReflection.MEDIA_RENDER_CTL_MSG_SIZECHANGED:
				onRenderSizeChanged(value, data);
				log.e("videosize = " + value );
				break;	
			default:
				log.e("unrognized cmd!!!");
				break;
		}
	}
	
	 public synchronized void onActionInvoke(int cmd, String value, byte data[],String title) {

		onRenderSetCover(value,data);
	}
	 
	 
	 public synchronized void audio_init(int id,int bits, int channels, int samplerate,int isaudio) {

		 if(track[id] == null)
		 {
			   int minBufferSize = AudioTrack.getMinBufferSize(samplerate, AudioFormat.CHANNEL_CONFIGURATION_STEREO, AudioFormat.ENCODING_PCM_16BIT);
			
			   track[id] = new AudioTrack(AudioManager.STREAM_MUSIC,(int)samplerate,AudioFormat.CHANNEL_CONFIGURATION_STEREO,AudioFormat.ENCODING_PCM_16BIT,minBufferSize,AudioTrack.MODE_STREAM);
			    
			   track[id].play();
		 }
	 }
	 
	 public synchronized void audio_process(int id,byte data[],double timestamp,int seqnum) {

		 
			log.e("timestamp   " + timestamp + "   "+seqnum);
		   ByteBuffer bytedata = ByteBuffer.wrap(data);
		   track[id].write(bytedata.array(), 0, bytedata.capacity());
		 
	}
	 
	 public synchronized void audio_destroy(int id) {

		 if(track[id] != null)
		 {
			 track[id].flush();
			 track[id].stop();
			 track[id] = null;
		 }

	}
	
	@Override
	public void onRenderAvTransport(String value, String data) {
		if (data == null){
			log.e("meteData = null!!!");
			return ;
		}
		
		if (value == null || value.length() < 2){
			log.e("url = " + value + ", it's invalid...");
			return ;
		}

		DlnaMediaModel mediaInfo = DlnaMediaModelFactory.createFromMetaData(data);	
		mediaInfo.setUrl(value);
		if (DlnaUtils.isAudioItem(mediaInfo)){
			mMusicMediaInfo = mediaInfo;
			mCurMediaInfoType = CUR_MEDIA_TYPE_MUSCI;
		}else if (DlnaUtils.isVideoItem(mediaInfo)){
			mVideoMediaInfo = mediaInfo;
			mCurMediaInfoType = CUR_MEDIA_TYPE_VIDEO;
		}else if (DlnaUtils.isImageItem(mediaInfo)){
			mImageMediaInfo = mediaInfo;
			mCurMediaInfoType = CUR_MEDIA_TYPE_PICTURE;
    }else if (DlnaUtils.isScreenItem(mediaInfo)){
    	mScreenMediaInfo = mediaInfo;
        mCurMediaInfoType = CUR_MEDIA_TYPE_SCREEN;
    }else{
            log.e("unknow media type!!! mediainfo.objectclass = \n"  + mediaInfo.getObjectClass());
    }

	
	}

	@Override
	public void onRenderPlay(String value, String data) {
	
		MediaControlBrocastFactory.sendPlayBrocast(mContext,data);
	
	}

	@Override
	public void onRenderPause(String value, String data) {
		MediaControlBrocastFactory.sendPauseBrocast(mContext);
	}

	@Override
	public void onRenderStop(String value, String data) {
		//delayToStop(Integer.valueOf(data));
	    log.d("MediaControlBrocastFactory stop" );
		MediaControlBrocastFactory.sendStopBorocast(mContext,Integer.valueOf(data));
	}

	@Override
	public void onRenderSeek(String value, String data) {
		int seekPos = 0;
		try {
			seekPos = DlnaUtils.parseSeekTime(value);
			MediaControlBrocastFactory.sendSeekBrocast(mContext, seekPos);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onRenderSetMute(String value, String data) {
		
		 if ("1".equals(value)){
			CommonUtil.setVolumeMute(mContext);
		}else if("0".equals(value)){
			CommonUtil.setVolumeUnmute(mContext);
		}
	}

	@Override
	public void onRenderSetVolume(String value, String data) {
		try {
			int volume = Integer.valueOf(value);
			if(volume < 101){				
				CommonUtil.setCurrentVolume(volume, mContext);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onRenderSetCover(String value, byte data[]) {
		
		MediaControlBrocastFactory.sendCoverBrocast(mContext, data);
	}

	@Override
	public void onRenderSetMetaData(String value, String data) {
		
		MediaControlBrocastFactory.sendMetaDataBrocast(mContext, data);
	}
	
	@Override
	public void onRenderSetIPAddr(String value, String data) {
		
		MediaControlBrocastFactory.sendIPAddrBrocast(mContext, value);
	}
	
	@Override
	public void onRenderSizeChanged(String value, String data) {
		
		MediaControlBrocastFactory.sendSizeChangedBrocast(mContext, value);
	}



	
	private void clearState(){
		mMusicMediaInfo = null;
		mVideoMediaInfo = null;
		mImageMediaInfo = null;
		mScreenMediaInfo= null;
	}
	

	
	private static final int DELAYTIME = 200;
	private void delayToPlayMusic(DlnaMediaModel mediaInfo){
		if (mediaInfo != null)
		{
			clearDelayMsg();
			Message msg = mHandler.obtainMessage(MSG_START_MUSICPLAY, mediaInfo);
			//mHandler.sendMessageDelayed(msg, DELAYTIME);
			mHandler.sendMessage(msg);
		}
	}
	
	private void delayToPlayVideo(DlnaMediaModel mediaInfo){
		if (mediaInfo != null)
		{
			clearDelayMsg();
			Message msg = mHandler.obtainMessage(MSG_START_VIDOPLAY, mediaInfo);
			//mHandler.sendMessageDelayed(msg, DELAYTIME);
			mHandler.sendMessage(msg);
		}
	}
	
	private void delayToPlayImage(DlnaMediaModel mediaInfo){
		if (mediaInfo != null){
			clearDelayMsg();
			Message msg = mHandler.obtainMessage(MSG_START_PICPLAY, mediaInfo);
			//mHandler.sendMessageDelayed(msg, DELAYTIME);
			mHandler.sendMessage(msg);
		}
	}
	
	private void delayToPlayScreen(DlnaMediaModel mediaInfo){
		if (mediaInfo != null)
		{
			clearDelayMsg();
			Message msg = mHandler.obtainMessage(MSG_START_SCREENPLAY, mediaInfo);
			//mHandler.sendMessageDelayed(msg, DELAYTIME);
			mHandler.sendMessage(msg);
		}
	}
	private void delayToStop(int type){
		clearDelayMsg();
		mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SEND_STOPCMD,type), DELAYTIME);
		//mHandler.sendMessage(mHandler.obtainMessage(MSG_SEND_STOPCMD,type));
	}
	
	private void clearDelayMsg(){
		clearDelayMsg(MSG_START_MUSICPLAY);
		clearDelayMsg(MSG_START_PICPLAY);
		clearDelayMsg(MSG_START_VIDOPLAY);
		clearDelayMsg(MSG_SEND_STOPCMD);
		clearDelayMsg(MSG_START_SCREENPLAY);
	}
	
	private void clearDelayMsg(int num){
		mHandler.removeMessages(num);
	}
	
	
	
	
	
	
	
	
	

	
	
	private static final int MSG_START_MUSICPLAY = 0x0001;
	private static final int MSG_START_PICPLAY = 0x0002;
	private static final int MSG_START_VIDOPLAY = 0x0003;
	private static final int MSG_SEND_STOPCMD = 0x0004;
	private static final int MSG_START_SCREENPLAY = 0x0005;
	
	private Handler mHandler = new Handler(){

		@Override
		public void dispatchMessage(Message msg) {
			
			try {
				switch(msg.what){
				case MSG_START_MUSICPLAY:
					DlnaMediaModel mediaInfo1 = (DlnaMediaModel) msg.obj;
					startPlayMusic(mediaInfo1);
					break;
				case MSG_START_PICPLAY:
					DlnaMediaModel mediaInfo2 = (DlnaMediaModel) msg.obj;
					startPlayPicture(mediaInfo2);
					break;
				case MSG_START_VIDOPLAY:
					DlnaMediaModel mediaInfo3 = (DlnaMediaModel) msg.obj;
					startPlayVideo(mediaInfo3);
					break;
				case MSG_SEND_STOPCMD:
					int type =  (int)msg.arg1;
					MediaControlBrocastFactory.sendStopBorocast(mContext,type);
					break;
				case MSG_START_SCREENPLAY:
					DlnaMediaModel mediaInfo4 = (DlnaMediaModel) msg.obj;
					startPlayScreen(mediaInfo4);
					break;
			}
			} catch (Exception e) {
				e.printStackTrace();
				log.e("DMRCenter transdel msg catch Exception!!! msgID = " + msg.what);
			}
			
		}
		
	};

	
	private void startPlayMusic(DlnaMediaModel mediaInfo){
			log.d("startPlayMusic");
			Intent intent = new Intent();
			intent.setClass(mContext, MusicActivity.class);
	        DlnaMediaModelFactory.pushMediaModelToIntent(intent, mediaInfo);		
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
			mContext.startActivity(intent);
	}
	


	private void startPlayVideo(DlnaMediaModel mediaInfo){
			log.d("startPlayVideo");
			Intent intent = new Intent();
			intent.setClass(mContext, VideoActivity.class);
	        DlnaMediaModelFactory.pushMediaModelToIntent(intent, mediaInfo);		
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
			mContext.startActivity(intent);

	}
	
	private void startPlayPicture(DlnaMediaModel mediaInfo){
			log.d("startPlayPicture");
			Intent intent = new Intent();
			intent.setClass(mContext, ImageActivity.class);
		    DlnaMediaModelFactory.pushMediaModelToIntent(intent, mediaInfo);		
	        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);    
	        mContext.startActivity(intent);	
	}

	
	private void startPlayScreen(DlnaMediaModel mediaInfo){
		log.d("startPlayScreen");
		Intent intent = new Intent();
		//intent.setClass(mContext, AirPlayerActivity.class);
		intent.setClass(mContext, VideoDecoderActivity.class);
		//intent.setClass(mContext, AirPlayerActivityUI.class);
        DlnaMediaModelFactory.pushMediaModelToIntent(intent, mediaInfo);		
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
		mContext.startActivity(intent);
		
		//Intent intent = new Intent();
		//intent.putExtra(DlnaMediaModelFactory.PARAM_GET_URL, mediaInfo.getUrl());
        //final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        //String uriString = uri.toString();
       // String extension = uriString.substring(uriString.lastIndexOf('.') + 1);
       // intent.setClass(mContext, VideoActivity.class);
        //mContext.startActivity(intent);

}

}
