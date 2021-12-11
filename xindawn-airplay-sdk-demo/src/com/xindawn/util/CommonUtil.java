package com.xindawn.util;



import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import org.apache.http.conn.util.InetAddressUtils;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.TrafficStats;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Environment;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

public class CommonUtil {

	private static final CommonLog log = LogFactory.createLog();
	
    static JmDNS  mDNS;
    static ServiceInfo mAirPlayService;
    static ServiceInfo mRoapService;
    static Timer mRepublishTimer;
    static InetAddress S_ADDR_ANY ;
	
    public static boolean isPortOpen(final int port ) {
        final int timeout=100;
        try {
            Socket socket = new Socket();
            if(S_ADDR_ANY == null)
                S_ADDR_ANY = InetAddress.getByName("0.0.0.0");
            socket.connect(new InetSocketAddress(S_ADDR_ANY, port), timeout);
            socket.close();
            return true;
        }

        catch(ConnectException ce){
            return false;
        }

        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     *
     * @param context Android上下文
     * @param name Mirror名称
     * @param roapPort 服务发布端口
     * @param airPlayPort AirPlay数据端口
     * @return 0,jmdns对象还未销毁；-2，WiFI开启，但是无法获取网络连接信息；-3，WiFi开启，但是无法获取有效IP地址；
     * -6，-5，WiFi未开启，WiFi热点也未开启；
     */
    public static int RegisterMyService(Context context, String name, int[] roapPort, int[] airPlayPort) {
     
    	while (isPortOpen(roapPort[0])) {
            roapPort[0]++;
        }
        while(isPortOpen(airPlayPort[0])) {
            airPlayPort[0]++;
        }
        try {
            JmDNS cur = mDNS;
            if (cur != null) {
                return 0;
            }

            WifiManager mWifiManager = (WifiManager) context.getSystemService(android.content.Context.WIFI_SERVICE);
            String macAddress = "FF:FF:FF:FF:FF:F2";
            byte[] myAddressBytes ;
            InetAddress myAddress = null;
            
            try
            {
                List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
                for (NetworkInterface intf : interfaces) {
                	
                    log.e("intf.getDisplayName(): " + intf.getDisplayName());
                    //只索引WiFi网络的IP，一般手机只会有一个wlan，即wlan0（少数手机WiFi Ap模式下的接口为“ap0”或者“swlan0"）
                    if(!intf.getDisplayName().startsWith("wlan") && !intf.getDisplayName().startsWith("ap") && !intf.getDisplayName().startsWith("eth") &&  !intf.getDisplayName().startsWith("swlan") )
                        continue;
                    List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                    for (InetAddress addr : addrs) {
                        if (!addr.isLoopbackAddress()) {
                            String sAddr = addr.getHostAddress().toUpperCase();
                            boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                            if (isIPv4) {
                                myAddress = addr;
                                log.e("myAddress: " + myAddress  );
                                byte[] macBytes = intf.getHardwareAddress();
                                macAddress = "";
                                if (macBytes == null) continue;
                                for (int i = 0; i < macBytes.length; i++) {
                                    String b = Integer.toHexString(macBytes[i] >= 0 ? macBytes[i] : 256 + macBytes[i]);
                                    macAddress = macAddress + (b.length() == 1 ? "0" + b : b) + (i < macBytes.length - 1 ? ":" : "");
                                }
                                log.e("macAddress: " + macAddress  );
                                break;
                            }
                        }
                    }
                }
            }
            catch(Exception e)
            {
            	e.printStackTrace();
            }

            if (myAddress == null) {
                return -6;
            }
  
            log.e("macAddress: " + macAddress);

            final String Feature = "0x5A7FFFF6,0x1E";
            final String Model = "AppleTV3,2";
            final String ServerVersion = "220.68";
            String pk = "99FD4299889422515FBD27949E4E1E21B2AF50A454499E3D4BE75A4E0F55FE63";
            String pi = "b08f5a79-db29-4384-b456-a4784d9e6055";
            mDNS = JmDNS.create(myAddress, macAddress + "-jmdns");

            final LinkedHashMap<String, String> mRoapLocalHashMap = new LinkedHashMap<String, String>();
			/*
			*
			    ch=2
                cn=0,1,2,3
                da=true
                et=0,3,5
                vv=2
                ft=0x5A7FFFF7,0x1E
                am=AppleTV3,1
                md=0,1,2
                rhd=2.0.0.3
                pw=false
                sr=44100
                ss=16
                sv=false
                tp=UDP
                txtvers=1
                sf=0x44
                vs=220.68
                vn=65537
                pk=24ca5cacb9bcb4aaf1aaa822d6c8d4b924ac59df6987cf04ac7c4c56dfe7a361
			* */
            mRoapLocalHashMap.put("ch", "2");
            mRoapLocalHashMap.put("cn", "0,1,2,3");
            mRoapLocalHashMap.put("da", "true");
            mRoapLocalHashMap.put("et", "0,3,5");
            mRoapLocalHashMap.put("vv", "2");
            mRoapLocalHashMap.put("ft", Feature);
            mRoapLocalHashMap.put("am", Model);
            mRoapLocalHashMap.put("md", "0,1,2");
            mRoapLocalHashMap.put("rhd", "2.0.0.3");
            mRoapLocalHashMap.put("pw", "false");
            mRoapLocalHashMap.put("sr", "44100");
            mRoapLocalHashMap.put("ss", "16");
            mRoapLocalHashMap.put("sv", "false");
            mRoapLocalHashMap.put("tp", "UDP");
            mRoapLocalHashMap.put("txtvers", "1");
            mRoapLocalHashMap.put("sf", "44");
            mRoapLocalHashMap.put("vn", "65537");
            mRoapLocalHashMap.put("vs", ServerVersion);

            mRoapLocalHashMap.put("pk", pk);

            mRoapService = javax.jmdns.ServiceInfo.create("_raop._tcp.local.", macAddress.replace(":", "") + "@" + name, "", roapPort[0], 0, 0,
                    true, mRoapLocalHashMap);

        /*
                    deviceid=c2:d6:37:96:14:68
                    features=0x5A7FFFF7,0x1E
                    srcvers=220.68
                    flags=0x44
                    vv=2
                    model=AppleTV3,1
                    pw=false
                    rhd=2.0.0.3
                    pk=24ca5cacb9bcb4aaf1aaa822d6c8d4b924ac59df6987cf04ac7c4c56dfe7a361
                    pi=2e388006-13ba-4041-9a67-25dd4a43d536
        * */
            final LinkedHashMap<String, String> mAirPlayLocalHashMap = new LinkedHashMap<String, String>();
            mAirPlayLocalHashMap.put("deviceid", macAddress);
            mAirPlayLocalHashMap.put("features", Feature);
            mAirPlayLocalHashMap.put("srcvers", ServerVersion);
            mAirPlayLocalHashMap.put("flags", "0x44");
            mAirPlayLocalHashMap.put("vv", "2");
            mAirPlayLocalHashMap.put("model", Model);
            mAirPlayLocalHashMap.put("pw", "false");
            mRoapLocalHashMap.put("rhd", "2.0.0.3");
            mRoapLocalHashMap.put("pi", pi);
            mRoapLocalHashMap.put("pk", pk);
            //airPlayLocalHashMap.put("vv", "1");

            mAirPlayService = javax.jmdns.ServiceInfo.create("_airplay._tcp.local.", name, "", airPlayPort[0], 0, 1, true, mAirPlayLocalHashMap);


            //mDNS.addServiceListener(DACP_TYPE, this);
            //mDNS.addServiceTypeListener(this);
            //_services._dns-sd._udp.local: type PTR, class IN, _airplay._tcp.local
            //final javax.jmdns.ServiceInfo dnssdService = javax.jmdns.ServiceInfo.create("_raop._tcp.local.","_services._dns-sd._udp.local", airPlayPort,"");
            if (mDNS.isClosing() || mDNS.isClosed()) {
                return 0;
            }
            mDNS.registerService(mRoapService);
            if (mDNS.isClosing() || mDNS.isClosed()) {
                return 0;
            }
            mDNS.registerService(mAirPlayService);

            mRepublishTimer = new Timer(true);
            mRepublishTimer.schedule(new TimerTask() {
                boolean dummay = false;

                @Override
                public void run() {
                    dummay = !dummay;
                    mRoapLocalHashMap.put("dummay", String.valueOf(dummay));
                    mRoapService.setText(mRoapLocalHashMap);
                    mAirPlayLocalHashMap.put("dummay", String.valueOf(dummay));
                    mAirPlayService.setText(mAirPlayLocalHashMap);
                    log.d( "run....");


                  //  mRepublishTimer.cancel();
                }

            }, 2000, 2000);
            log.d( myAddress+"   " + macAddress + " roap Port port:" + roapPort[0] + " airPlayPort port:" + airPlayPort[0]);

        }catch (Throwable e) {
            log.e("Failed to registerService: " + e.toString());
            mDNS = null;

            return -4;
        }

        return 1;
    }

    public synchronized static void closeMDNS() {

        log.e("closeMDNS ");
        if (mDNS != null) {
            mDNS.unregisterAllServices();
            try {
                mDNS.close();
                mDNS = null;
            } catch (IOException e) {
            }
        }

        if (mRepublishTimer != null)
            mRepublishTimer.cancel();
    }
	
	
	public static boolean hasSDCard() {
		String status = Environment.getExternalStorageState();
		if (!status.equals(Environment.MEDIA_MOUNTED)) {
			return false;
		} 
		return true;
	}
	
	public static String getRootFilePath() {
		if (hasSDCard()) {
			return Environment.getExternalStorageDirectory().getAbsolutePath() + "/";// filePath:/sdcard/
		} else {
			return Environment.getDataDirectory().getAbsolutePath() + "/data/"; // filePath: /data/data/
		}
	}
	
	public static boolean checkNetworkState(Context context){
    	boolean netstate = false;
		ConnectivityManager connectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connectivity != null)
		{
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++)
				{
					if (info[i].getState() == NetworkInfo.State.CONNECTED) 
					{
						netstate = true;
						break;
					}
				}
			}
		}
		return netstate;
    }

	public static String getLocalMacAddress(Context mc){
		String defmac = "00:00:00:00:00:00";
		InputStream   input   =   null;
		String wifimac = getWifiMacAddress(mc);
		if(null != wifimac){
			if(!wifimac.equals(defmac))
				return wifimac;
		}
		try{

			ProcessBuilder builder = new ProcessBuilder( "busybox","ifconfig");
			Process process = builder.start();
			input = process.getInputStream();



			byte[] b = new byte[1024];
			StringBuffer buffer = new StringBuffer();
			while(input.read(b)>0){
				buffer.append(new String(b));
			}
			String value = buffer.substring(0);
			String systemFlag ="HWaddr ";
			int index = value.indexOf(systemFlag);
			//List <String> address   = new ArrayList <String> ();
			if(0<index){
				value = buffer.substring(index+systemFlag.length());
				//address.add(value.substring(0,18));
				defmac=value.substring(0,17);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return defmac;
	}
	
	public static String getWifiMacAddress(Context mc) {   
        WifiManager wifi = (WifiManager) mc.getSystemService(Context.WIFI_SERVICE);   
        WifiInfo info = wifi.getConnectionInfo();   
        return info.getMacAddress();   
    } 
	
	public static MulticastLock openWifiBrocast(Context context){
		WifiManager wifiManager=(WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		MulticastLock  multicastLock=wifiManager.createMulticastLock("MediaRender");
		if (multicastLock != null){
			multicastLock.acquire();
		}
		return multicastLock;
	}
	
	
	public static void setCurrentVolume(int percent,Context mc){
		AudioManager am=(AudioManager)mc.getSystemService(Context.AUDIO_SERVICE);
		int maxvolume=am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		am.setStreamVolume(AudioManager.STREAM_MUSIC, (maxvolume*percent)/100, 
				AudioManager.FLAG_PLAY_SOUND|AudioManager.FLAG_SHOW_UI);
		am.setMode(AudioManager.MODE_INVALID);
	}
	
	public static void setVolumeMute(Context mc){
		AudioManager am=(AudioManager)mc.getSystemService(Context.AUDIO_SERVICE);
		am.setStreamMute(AudioManager.STREAM_MUSIC, true);
	}
	public static void setVolumeUnmute(Context mc){
		AudioManager am=(AudioManager)mc.getSystemService(Context.AUDIO_SERVICE);
		am.setStreamMute(AudioManager.STREAM_MUSIC, false);
	}
	
	public static void showToask(Context context, String tip){
		Toast.makeText(context, tip, Toast.LENGTH_SHORT).show();
	}

	public static int getScreenWidth(Context context) {
		WindowManager manager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return display.getWidth();
	}
	
	public static int getScreenHeight(Context context) {
		WindowManager manager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return display.getHeight();
	}
	
	public static ViewSize getFitSize(Context context, MediaPlayer mediaPlayer)
	{
		int videoWidth = mediaPlayer.getVideoWidth();  
	    int videoHeight = mediaPlayer.getVideoHeight();  	    
	    double fit1 = videoWidth * 1.0 / videoHeight;	 
	    
	    int width2 = getScreenWidth(context);
	    int height2 = getScreenHeight(context);      
	    double fit2 = width2 * 1.0 / height2;  
	    
	    double fit = 1;
	    if (fit1 > fit2)
	    {
	    	fit = width2 * 1.0 / videoWidth;
	    }else{
	    	fit = height2 * 1.0 / videoHeight;
	    }

	    ViewSize viewSize = new ViewSize();
	    viewSize.width = (int) (fit * videoWidth);
	    viewSize.height = (int) (fit * videoHeight);

	    return viewSize;
	}

	public static class ViewSize
	{
		public int width = 0;
		public int height = 0;
	}
    
	  public static boolean getWifiState(Context context){
		  ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
		  State wifistate = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		  if (wifistate != State.CONNECTED){
			  return false;
		  }
		  
		  State mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		  boolean ret = State.CONNECTED != mobileState;
		  return ret;
	  }
	  
	  
	  public static boolean getMobileState(Context context){
		  ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
		  State wifistate = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		  if (wifistate != State.CONNECTED){
			  return false;
		  }
		  
		  State mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		  boolean ret = State.CONNECTED == mobileState;
		  return ret;
	  }
	  
	  
	  
	  private static long m_lSysNetworkSpeedLastTs = 0;
		private static long m_lSystNetworkLastBytes = 0;
		private static float m_fSysNetowrkLastSpeed = 0.0f;
		public static float getSysNetworkDownloadSpeed() {
			long nowMS = System.currentTimeMillis();
			long nowBytes = TrafficStats.getTotalRxBytes();

			long timeinterval = nowMS - m_lSysNetworkSpeedLastTs;
			long bytes = nowBytes - m_lSystNetworkLastBytes;

			if(timeinterval > 0) m_fSysNetowrkLastSpeed = (float)bytes * 1.0f / (float)timeinterval;

			m_lSysNetworkSpeedLastTs = nowMS;
			m_lSystNetworkLastBytes = nowBytes;

			return m_fSysNetowrkLastSpeed;
		}
}


