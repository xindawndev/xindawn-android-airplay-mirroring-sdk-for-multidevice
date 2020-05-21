
#include "XinDawnMultiScreen.h"


#include "addon.h"
#include "videosource.h"


#include <android/log.h>

#define  LOG_TAG    	"XinDawn"
#define  LOGI(...)  	__android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  	__android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define  printf(...)	__android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)



XinDawnMultiScreen::XinDawnMultiScreen()
{

	m_pAirplay = NULL;
	m_pAirPlayFactory = NULL;
	
	for (int i = 0; i < AIRPLAY_MAX_COUNT; i++)
	{
		m_pAirplayDataProcesser[i] = NULL;
	}
}

XinDawnMultiScreen::~XinDawnMultiScreen() {


	for (int i = 0; i < AIRPLAY_MAX_COUNT; i++)
	{
		if (m_pAirplayDataProcesser[i])
		{
			delete m_pAirplayDataProcesser[i];
			m_pAirplayDataProcesser[i] = NULL;
		}
	}
}




int XinDawnMultiScreen::StartAirplay(char *friendname, char *dllpath)
{
	StopAirplay();



	m_pAirPlayFactory = new CAirPlayFactory();
	if(!m_pAirPlayFactory)
		 return -1;

	m_pAirplay = m_pAirPlayFactory->Create();// new AirPlay();
	if (!m_pAirplay)
		return -1;

	m_pAirplay->Init("FF:FF:FF:FF:FF:F2",friendname,dllpath,"","000000000",1920,1080,47027,7027,7127,1,10,10,2);
	m_pAirplay->setAirPlayCallback(this);


	m_pAirplay->run(AIRPLAY_MAX_COUNT);


	return 0;
}
void XinDawnMultiScreen::StopAirplay()
{
	if (m_pAirplay) {
		m_pAirplay->stop();
		//delete m_pAirplay;
		m_pAirPlayFactory->Destroy(m_pAirplay);
		m_pAirplay = 0;
	}
	if(m_pAirPlayFactory)
	{
		delete m_pAirPlayFactory;
		m_pAirPlayFactory = 0x0;
	}
}

void XinDawnMultiScreen::ShutDown(int nId)
{

	if (m_pAirplayDataProcesser[nId])
	{

		const std::string& strDeviceMac = m_pAirplayDataProcesser[nId]->GetDeviceMac();
		if (!strDeviceMac.empty())
		{
			m_pAirplay->ShutDown((ChannelID)m_pAirplayDataProcesser[nId]);
		}

	}

}



void XinDawnMultiScreen::onAudioInit(ChannelID channelId,int bits, int channels, int samplerate,int isaudio)
{
	VideoSource* vs = (VideoSource*)channelId;
	vs->audio_init((void *)vs, bits, channels, samplerate, isaudio);

	printf("=====================%s=========================\n", __FUNCTION__);


}





void XinDawnMultiScreen::onAudioProcess(ChannelID channelId,const char* data,unsigned int datasize,double timestamp,int seqnum)
{

	VideoSource* vs = (VideoSource*)channelId;
	vs->audio_process((void *)vs, data, datasize, timestamp, seqnum);

	printf("=====================%s=========================\n", __FUNCTION__);

}





void XinDawnMultiScreen::onAudioDestroy(ChannelID channelId)
{

	VideoSource* vs = (VideoSource*)channelId;
	vs->audio_destory((void *)vs);

	printf("=====================%s=========================\n", __FUNCTION__);

}


void XinDawnMultiScreen::onAudioSetVolume(ChannelID channelId, int volume)
{
	VideoSource* vs = (VideoSource*)channelId;
	vs->audio_setvolume((void *)vs, volume);

	printf("=====================%s=========================\n", __FUNCTION__);
}
void XinDawnMultiScreen::onAudioSetMetadata(ChannelID channelId, const void *buffer, int buflen)
{
	VideoSource* vs = (VideoSource*)channelId;
	vs->audio_setmetadata((void *)vs, buffer,buflen);

	printf("=====================%s=========================\n", __FUNCTION__);

}

void XinDawnMultiScreen::onAudioSetCoverart(ChannelID channelId, const void *buffer, int buflen)
{
	VideoSource* vs = (VideoSource*)channelId;
	vs->audio_setmetadata((void *)vs, buffer, buflen);

	printf("=====================%s=========================\n", __FUNCTION__);
}


void XinDawnMultiScreen::onAudioFlush(ChannelID channelId)
{
	VideoSource* vs = (VideoSource*)channelId;
	vs->audio_flush((void *)vs);

	printf("=====================%s=========================\n", __FUNCTION__);
}




void XinDawnMultiScreen::onAirPlayOpen(ChannelID channelId,char *deviceip,char *url, float fPosition)
{
	VideoSource* vs = (VideoSource*)channelId;
	vs->airplay_open((void *)vs, url, fPosition);

	printf("=====================%s=========================\n", __FUNCTION__);


}
void XinDawnMultiScreen::onAirPlayPlay(ChannelID channelId)
{
	VideoSource* vs = (VideoSource*)channelId;
	vs->airplay_play((void *)vs);

	printf("=====================%s=========================\n", __FUNCTION__);

}
void XinDawnMultiScreen::onAirPlayStop(ChannelID channelId)
{
	VideoSource* vs = (VideoSource*)channelId;
	vs->airplay_stop((void *)vs);

	printf("=====================%s=========================\n", __FUNCTION__);


}
void XinDawnMultiScreen::onAirPlayPause(ChannelID channelId)
{
	VideoSource* vs = (VideoSource*)channelId;
	vs->airplay_pause((void *)vs);

	printf("=====================%s=========================\n", __FUNCTION__);

}
void XinDawnMultiScreen::onAirPlaySeek(ChannelID channelId,long fPosition)
{
	VideoSource* vs = (VideoSource*)channelId;
	vs->airplay_seek((void *)vs, fPosition);

	printf("=====================%s=========================\n", __FUNCTION__);
}
void XinDawnMultiScreen::onAirPlaySetVolume(ChannelID channelId,int volume)
{

	VideoSource* vs = (VideoSource*)channelId;
	vs->airplay_setvolume((void *)vs, volume);

	printf("=====================%s=========================\n", __FUNCTION__);

}
void XinDawnMultiScreen::onAirPlayShowPhoto(ChannelID channelId,unsigned char *data, long long size)
{
	VideoSource* vs = (VideoSource*)channelId;
	vs->airplay_showphoto((void *)vs, data,size);

	printf("=====================%s=========================\n", __FUNCTION__);

}

long XinDawnMultiScreen::onAirPlayGetDuration(ChannelID channelId)
{
	VideoSource* vs = (VideoSource*)channelId;
	return vs->airplay_getduration((void *)vs);

	printf("=====================%s=========================\n", __FUNCTION__);
}

long XinDawnMultiScreen::onAirPlayGetPosition(ChannelID channelId)
{
	VideoSource* vs = (VideoSource*)channelId;
	return vs->airplay_getpostion((void *)vs);

	printf("=====================%s=========================\n", __FUNCTION__);
}

int XinDawnMultiScreen::onAirPlayIsPlaying(ChannelID channelId)
{
	VideoSource* vs = (VideoSource*)channelId;
	return vs->airplay_isplaying((void *)vs);

	printf("=====================%s=========================\n", __FUNCTION__);
}

int XinDawnMultiScreen::onAirPlayIsPaused(ChannelID channelId)
{
	VideoSource* vs = (VideoSource*)channelId;
	return vs->airplay_ispaused((void *)vs);

	printf("=====================%s=========================\n", __FUNCTION__);
}


void XinDawnMultiScreen::updateState(int cmd,char *deviceip,char *value,char *data)
{
	if (!m_pAirplay)
		return ;
	m_pAirplay->updateState(cmd,deviceip,value,data);
}



ChannelID XinDawnMultiScreen::onAirPlayNewChannel(char *ipAddr)
{

	printf("=====================%s=========================\n", __FUNCTION__);

	for (int i = 0; i < AIRPLAY_MAX_COUNT; i++)
	{
		const std::string& strDeviceMac = m_pAirplayDataProcesser[i]->GetDeviceMac();
		if (strDeviceMac.empty())
		{
			m_pAirplayDataProcesser[i]->SetDeviceMac("Airplay");
			return (ChannelID)m_pAirplayDataProcesser[i];
		}
	}

	return NULL;
}


void XinDawnMultiScreen::OnAirPlayStopChannel(ChannelID channelId)
{
	printf("=====================%s=========================\n", __FUNCTION__);
	VideoSource* vs = (VideoSource*)channelId;
	vs->ClearDeviceMac();

}

void XinDawnMultiScreen::onDeviceName(ChannelID channelId,char *name)
{
	printf("=====================%s=========================\n", __FUNCTION__);
}


extern "C" void ActionInflect(int cmd, const char* value, const char* data,const char *title);

void XinDawnMultiScreen::onAirPlayMirroring_Play(ChannelID channelId, int width, int height, const void *buffer, int buflen, int payloadtype, double timestamp)
{

    VideoSource* vs = (VideoSource*)channelId;
    vs->mirroring_play((void *)vs, width, height, buffer, buflen, payloadtype, timestamp);

    printf( "=====================%s=========================\n", __FUNCTION__);

}

void XinDawnMultiScreen::onAirPlayMirroring_Process(ChannelID channelId, const void *buffer, int buflen, int payloadtype, double timestamp)
{
	VideoSource* vs = (VideoSource*)channelId;
	vs->mirroring_process((void *)vs,  buffer, buflen, payloadtype, timestamp);
	printf("=====================%s=========================\n", __FUNCTION__);
}


void XinDawnMultiScreen::onAirPlayMirroring_Stop(ChannelID channelId)
{
	VideoSource* vs = (VideoSource*)channelId;
	vs->mirroring_stop((void *)vs);
	printf("=====================%s=========================\n", __FUNCTION__);
}




#ifdef __cplusplus
extern "C"
{
#endif

XinDawnMultiScreen *cm = NULL;


void XinDawn_ShutDown(int nId)
{
	cm->ShutDown(nId);
}


void XinDawn_SetVideoSurface(int nId, jobject objSurface)
{

	cm->m_pAirplayDataProcesser[nId]->m_pVideoDataProcesser->setVideoSurface(objSurface);
}

void XinDawn_StopMediaServer()
{
	  if(cm)
	  {
		  cm->StopAirplay();
		  delete cm;
		  cm = NULL;
	  }
}

int  XinDawn_StartMediaServer(JavaVM* JVM,char *friendname, char *dllpath, int width, int height,char *activecode)
{
	cm = new XinDawnMultiScreen();

    for (int i = 0; i < AIRPLAY_MAX_COUNT; i++)
    {
    	cm->m_pAirplayDataProcesser[i] = new VideoSource(JVM,i);
    }


	cm->StartAirplay(friendname,dllpath);


	return 0;
}

#ifdef __cplusplus
}
#endif



