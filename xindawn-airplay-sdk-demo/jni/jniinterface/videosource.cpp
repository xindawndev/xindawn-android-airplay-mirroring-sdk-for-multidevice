
#include "videosource.h"


VideoSource::VideoSource(JavaVM* pJVM, int channel)
{

	m_channel = channel;
	m_pVideoDataProcesser = new H264MediaCodec(pJVM,channel);
}

VideoSource::~VideoSource()
{ 

	if (m_pVideoDataProcesser)
	{
		delete m_pVideoDataProcesser;
		m_pVideoDataProcesser = NULL;
	}
}




void VideoSource::airplay_open(void *cls, char *url, float fPosition)
{


}

void VideoSource::airplay_play(void *cls)
{

}

void VideoSource::airplay_pause(void *cls)
{

}

void VideoSource::airplay_stop(void *cls)
{

}

void VideoSource::airplay_seek(void *cls, long fPosition)
{

}
void VideoSource::airplay_setvolume(void *cls, int volume)
{

}
void VideoSource::airplay_showphoto(void *cls, unsigned char *data, long long size)
{

}



long VideoSource::airplay_getduration(void *cls)
{
	return ((VideoSource *)cls)->totalDuration;
}

long VideoSource::airplay_getpostion(void *cls)
{
	return ((VideoSource *)cls)->currentPosition;
}
int VideoSource::airplay_isplaying(void *cls)
{
	return ((VideoSource *)cls)->isPlaying;
}
int VideoSource::airplay_ispaused(void *cls)
{
	return ((VideoSource *)cls)->isPaused;
}


extern "C" void ActionInflect_AudioInit(int id,int bits, int channels, int samplerate,int isaudio);
extern "C" void ActionInflect_AudioProcess(int id,const char* data,unsigned int datasize,double timestamp,int seqnum);
extern "C" void ActionInflect_AudioDestroy(int id);
extern "C" void ActionInflect(int cmd, const char* value, const char* data,const char *title);

void VideoSource::audio_init(void *cls, int bits, int channels, int samplerate, int isaudio)
{

	ActionInflect_AudioInit(m_channel,bits,channels,samplerate,isaudio);

}
void VideoSource::audio_process(void *cls, const void *buffer, int buflen, double timestamp, uint32_t seqnum)
{
	ActionInflect_AudioProcess(m_channel,(const char *)buffer,buflen,timestamp,seqnum);

}

void VideoSource::audio_destory(void *cls)
{
	ActionInflect_AudioDestroy(m_channel);
}


void VideoSource::audio_setvolume(void *cls, int volume)
{

}
void VideoSource::audio_setmetadata(void *cls, const void *buffer, int buflen)
{

}
void VideoSource::audio_setcoverart(void *cls, const void *buffer, int buflen)
{

}

void VideoSource::audio_flush(void *cls)
{

}



void VideoSource::mirroring_play(void *cls, int width, int height, const void *buffer, int buflen, int payloadtype, double timestamp)
{


	char data[10];
	memset(data,0,sizeof(data));
	sprintf(data,"%d",m_channel);
    ActionInflect(0x102,NULL,data,NULL);



	if (m_pVideoDataProcesser)
		m_pVideoDataProcesser->setSpsPpSData((char *)buffer, buflen);



}







void VideoSource::mirroring_process(void *cls, const void *buffer, int buflen, int payloadtype, double timestamp)
{
	if(payloadtype == 1)
	{
		if (m_pVideoDataProcesser)
			m_pVideoDataProcesser->setSpsPpSData((char *)buffer, buflen);

	}
	else
	{
//		  int		    rLen       = 0;
//		  unsigned    char *head = (unsigned char *)buffer + rLen;
//
//        while (rLen < buflen)
//        {
//            rLen += 4;
//            rLen += (((uint32_t)head[0] << 24) | ((uint32_t)head[1] << 16) | ((uint32_t)head[2] << 8) | (uint32_t)head[3]);
//
//            head[0] = 0;
//            head[1] = 0;
//            head[2] = 0;
//            head[3] = 1;
//
//            head = (unsigned char *)buffer + rLen;
//        }

		if (m_pVideoDataProcesser)
			m_pVideoDataProcesser->setData((char *)buffer, buflen);

	}


}
void VideoSource::mirroring_stop(void *cls)
{
	if (m_pVideoDataProcesser)
		m_pVideoDataProcesser->onStop();

	char data[10];
	memset(data,0,sizeof(data));
	sprintf(data,"%d",m_channel);
	ActionInflect(0x101,NULL,data,NULL);
}

void VideoSource::SetDeviceMac(const std::string& strDeviceMac)
{
	m_strDeviceMac = strDeviceMac;
}
const std::string& VideoSource::GetDeviceMac()
{
	return m_strDeviceMac;
}

const void VideoSource::ClearDeviceMac()
{
	 m_strDeviceMac.clear();
}

const int VideoSource::GetChannelId()
{

	return m_channel;
}
