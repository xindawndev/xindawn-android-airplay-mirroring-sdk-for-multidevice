#include "H264MediaCodec.h"
#include <jni.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <android/log.h>
#include "sps_pps.h"


#define  LOG_TAG    "H264MediaCodec"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)





uint64_t timeGetTime()
{
	uint64_t uptime = 0;
	struct timespec on;
	//if (clock_gettime(CLOCK_THREAD_CPUTIME_ID, &on) == 0)
	if (clock_gettime(CLOCK_MONOTONIC, &on) == 0)
		uptime = on.tv_sec * 1000 + on.tv_nsec / 1000000;
	return uptime;
}


H264MediaCodec::H264MediaCodec(JavaVM* jvm,int channel)
{

	m_pJvm 		= jvm;
	m_channel 	= channel;
	m_hThreadId = 0;
	m_decoder   = NULL;
	m_format    = NULL;
	m_width     = 0;
	m_height    = 0;
	m_window    = NULL;
	m_bStop     = true;

}

H264MediaCodec::~H264MediaCodec() {

	DeInitCodec();


}

void H264MediaCodec::setData(char *buffer, int buflen)
{
	int64_t timeoutUs = 200000;
	CMyLock lock(m_codec_lock);
	if (m_decoder)
	{
		ssize_t iBufidx = AMediaCodec_dequeueInputBuffer(m_decoder,timeoutUs);
		if (iBufidx >= 0)
		{
			size_t bufsize;
			uint8_t* buf = (uint8_t *) AMediaCodec_getInputBuffer(m_decoder,iBufidx, &bufsize);
			memcpy(buf, buffer, buflen);
			uint64_t time = 0; //timeGetTime();
			AMediaCodec_queueInputBuffer(m_decoder, iBufidx, 0,buflen, time, 0);
		}
		else
		{
			LOGI(  "setData timeout");
		}
	}
}

void H264MediaCodec::setSpsPpSData(char *buffer, int buflen)
{

	int spscnt     = 0;
	int spsnalsize = 0;
	int ppscnt     = 0;
	int ppsnalsize = 0;
	int spsWidth   = 0;
	int spsHeight  = 0;

	unsigned char *head = (unsigned char *)buffer;
	spscnt 	   = head[5] & 0x1f;
	spsnalsize = ((uint32_t)head[6] << 8) | ((uint32_t)head[7]);
	ppscnt     = head[8 + spsnalsize];
	ppsnalsize = ((uint32_t)head[9 + spsnalsize] << 8) | ((uint32_t)head[10 + spsnalsize]);

	SPS spsHandle;

	get_bit_context *bitbuf = (get_bit_context *)malloc(sizeof(get_bit_context));
	bitbuf->buf = (uint8_t *)malloc(spsnalsize -1);
	memcpy(bitbuf->buf, head + 9, spsnalsize -1);
	bitbuf->buf_size = spsnalsize -1;
	bitbuf->bit_pos = 0;
	bitbuf->cur_bit_pos = 0;

	h264dec_seq_parameter_set((void *)bitbuf, &spsHandle);

	spsWidth   = h264_get_width(&spsHandle);
	spsHeight  = h264_get_height(&spsHandle);


	free(bitbuf->buf);
	free(bitbuf);


	if(m_width != spsWidth  ||  m_height != spsHeight)
	{
		DeInitCodec();

		m_width  = spsWidth;
		m_height = spsHeight;
		
		InitCodec();

		LOGI("width = %d height =%d ", m_width,m_height);

	}




     char *data = (char *)malloc(4 + spsnalsize + 4 + ppsnalsize);


     data[0] = 0;
     data[1] = 0;
     data[2] = 0;
     data[3] = 1;

     memcpy(data + 4, head + 8, spsnalsize);

     data[4 + spsnalsize] = 0;
     data[5 + spsnalsize] = 0;
     data[6 + spsnalsize] = 0;
     data[7 + spsnalsize] = 1;

     memcpy(data + 8 + spsnalsize, head + 11 + spsnalsize, ppsnalsize);


     setData(data,4 + spsnalsize + 4 + ppsnalsize);

     free(data);

}



void H264MediaCodec::setVideoSurface(jobject jsurface)
{
	CMyLock lock(m_window_lock);

	if (m_window)
	{
		ANativeWindow_release(m_window);
		m_window = NULL;
	}

	if (!jsurface)
	{
		return;
	}


	JNIEnv *env = NULL;
	bool isAttach = false;
	int status = m_pJvm->GetEnv((void **) &env, JNI_VERSION_1_4);
	if(status != JNI_OK)
	{
		status = m_pJvm->AttachCurrentThread(&env, NULL);
		if(status < 0)
		{
			LOGE("callback_handler: failed to attach , current thread, status = %d", status);
			return;
		}
		isAttach = true;
	}

	m_window = ANativeWindow_fromSurface(env, jsurface);


	if (isAttach)
	{
		m_pJvm->DetachCurrentThread();
	}

	return ;
}




void H264MediaCodec::onStop()
{
	DeInitCodec();
}


void* H264MediaCodec::thread_helper(void* arg)
{
	H264MediaCodec* ptr = (H264MediaCodec*) arg;
	return ptr->mediacodec_thread_loop();
}



void* H264MediaCodec::mediacodec_thread_loop()
{
	int64_t timeoutUs = 100000;

	while (!m_bStop) {

		if (m_decoder)
		{
			AMediaCodecBufferInfo info;
			ssize_t oBufidx = AMediaCodec_dequeueOutputBuffer(m_decoder,&info, timeoutUs);
			if (oBufidx >= 0)
			{
				CMyLock lock(m_window_lock);

				if (m_window)
				{
					AMediaCodec_releaseOutputBuffer(m_decoder, oBufidx, true);
				}
				else
				{
					AMediaCodec_releaseOutputBuffer(m_decoder, oBufidx, false);
				}

			} else if (oBufidx == AMEDIACODEC_INFO_OUTPUT_BUFFERS_CHANGED)
			{


			} else if (oBufidx == AMEDIACODEC_INFO_OUTPUT_FORMAT_CHANGED)
			{


			} else if (oBufidx == AMEDIACODEC_INFO_TRY_AGAIN_LATER)
			{

			}
			else
			{

			}
		}
	}

	return NULL;
}



int H264MediaCodec::InitCodec()
{
	{
		CMyLock lockA(m_codec_lock);

		const char* mime = "video/avc";
		m_decoder = AMediaCodec_createDecoderByType(mime);
		if (m_decoder)
		{
			m_format = AMediaFormat_new();
			if (m_format)
			{
				AMediaFormat_setString(m_format, AMEDIAFORMAT_KEY_MIME,   mime);
				AMediaFormat_setInt32(m_format,  AMEDIAFORMAT_KEY_WIDTH,  m_width);
				AMediaFormat_setInt32(m_format,  AMEDIAFORMAT_KEY_HEIGHT, m_height);
				//AMediaFormat_setInt32(m_format, AMEDIAFORMAT_KEY_COLOR_FORMAT, OMX_COLOR_FormatYUV420Planar);

				CMyLock lockB(m_window_lock);

				if(AMediaCodec_configure(m_decoder, m_format, m_window, NULL, 0) != AMEDIA_OK)
				{
					LOGI("AMediaCodec_configure failed");
			        return -1;

				}

			    if( AMediaCodec_start(m_decoder) != AMEDIA_OK)
			    {
			    	LOGI("AMediaCodec_start failed");
			        return -1;
			    }

			}
		}
	}


	if (m_decoder)
	{
		m_bStop = false;
		THREAD_CREATE(m_hThreadId, thread_helper, this);
		return 0;
	}

	return -1;
}

void H264MediaCodec::DeInitCodec()
{
	m_bStop = true;


	if (0 != m_hThreadId)
	{
		THREAD_JOIN(m_hThreadId);
		m_hThreadId = 0;
	}

	{
		CMyLock lockA(m_codec_lock);
		if (m_decoder)
		{
			CMyLock lockB(m_window_lock);
			AMediaCodec_stop(m_decoder);
			AMediaCodec_delete(m_decoder);
			m_decoder = NULL;
		}

		if (m_format)
		{
			AMediaFormat_delete(m_format);
			m_format = NULL;
		}
	}

	m_width  = 0;
	m_height = 0;

}
