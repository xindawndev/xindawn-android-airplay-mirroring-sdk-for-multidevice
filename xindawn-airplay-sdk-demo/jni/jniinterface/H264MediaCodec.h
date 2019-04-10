
#ifndef H264MEDIACODEC_H_
#define H264MEDIACODEC_H_

#include <jni.h>
#include <android/native_window_jni.h>
#include <media/NdkMediaCodec.h>

#include "Lock.h"
#include "threads.h"




class H264MediaCodec {
public:
	H264MediaCodec(JavaVM* jvm,int channel);
	virtual ~H264MediaCodec();


	void setSpsPpSData(char *buffer, int buflen);
	void setData(char *buffer, int buflen);

	void setVideoSurface(jobject jsurface);
	void onStop();


protected:

	static void *thread_helper(void *arg);
	void * mediacodec_thread_loop();


	int  InitCodec();
	void DeInitCodec();

private:
	JavaVM* m_pJvm;

	bool m_bStop;
	int m_width;
	int m_height;
	thread_handle_t m_hThreadId;
	AMediaCodec *m_decoder;
	AMediaFormat* m_format;

	ANativeWindow* m_window;

	int m_channel;

	CMutex m_codec_lock;
	CMutex m_window_lock;

};

#endif /* H264MEDIACODEC_H_ */
