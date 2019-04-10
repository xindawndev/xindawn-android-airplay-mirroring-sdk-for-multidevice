#pragma once
#include <string>
#include <string.h>
#include <ctype.h>

#include "H264MediaCodec.h"



class VideoSource
{

public:
	VideoSource(JavaVM* pJVM, int channel);
    ~VideoSource();


public:
    H264MediaCodec* m_pVideoDataProcesser;

public:

	void SetDeviceMac(const std::string& strDeviceMac);
	const std::string& GetDeviceMac();
	const void ClearDeviceMac();

	const int GetChannelId();

	std::string m_strDeviceMac;

	int m_channel;

public:

    long long totalDuration;
    long currentPosition;
    int isPlaying;
    int isPaused;

	
public:
		void airplay_open(void *cls, char *url, float fPosition);
		void airplay_play(void *cls);
		void airplay_pause(void *cls);
		void airplay_stop(void *cls);
		void airplay_seek(void *cls, long fPosition);
		void airplay_setvolume(void *cls, int volume);
		void airplay_showphoto(void *cls, unsigned char *data, long long size);
		long airplay_getduration(void *cls);
		long airplay_getpostion(void *cls);
		int  airplay_isplaying(void *cls);
		int  airplay_ispaused(void *cls);
		void audio_init(void *cls, int bits, int channels, int samplerate, int isaudio);
		void audio_process(void *cls, const void *buffer, int buflen, double timestamp, uint32_t seqnum);
		void audio_destory(void *cls);
		void audio_setvolume(void *cls, int volume);//1-100
		void audio_setmetadata(void *cls, const void *buffer, int buflen);
		void audio_setcoverart(void *cls, const void *buffer, int buflen);
		void audio_flush(void *cls);
		void mirroring_play(void *cls, int width, int height, const void *buffer, int buflen, int payloadtype, double timestamp);
		void mirroring_process(void *cls, const void *buffer, int buflen, int payloadtype, double timestamp);
		void mirroring_stop(void *cls);


};



