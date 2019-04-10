/*
 * =====================================================================================
 *
 *       Filename:  com_xindawn_jni_PlatinumJniProxy.cpp
 *
 *    Description:  
 *
 *        Version:  1.0
 *        Created:  2013年02月12日 16时31分28秒
 *       Revision:  none
 *       Compiler:  gcc
 *
 *         Author:  xindawn
 *   Organization:  
 *
 * =====================================================================================
 */
#include <jni.h>
#include <stdlib.h>
#include <android/log.h>
#include <string.h>
#include <stdio.h>
#include "StdString.h"
#include <string>
#include <map>
#include <vector>
#include <string.h>
#include <stdio.h>
#include <pthread.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/ioctl.h>
#include <sys/time.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <errno.h>
#include <netdb.h>
#include <pthread.h>


#ifdef __cplusplus
extern "C"{
#endif
#include "addon.h"

#ifdef __cplusplus
}
#endif


#define  LOG_TAG    "XinDawn"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define printf(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__);


static JavaVM*   	g_vm = NULL;
static jclass 		g_inflectClass = NULL;
static jmethodID 	g_methodID = NULL;
static jmethodID 	g_methodID1 = NULL;
static jmethodID 	g_methodID2 = NULL;
static jmethodID 	g_methodID3 = NULL;
static jmethodID 	g_methodID4 = NULL;
//static jobject     g_activityObj = NULL;


JavaVM *get_jvm()
{

	return g_vm;
}


static const char* const kClassPathName = "com/xindawn/jni/PlatinumJniProxy";

static jint com_xindawn_jni_PlatinumJniProxy_startMediaRender(JNIEnv* env,jobject thiz, jstring friendname,jstring libpath,jstring activecode,int width,int height,int airtunes_port,int airplay_port,int rcvsize, jobject context);
static jint com_xindawn_jni_PlatinumJniProxy_stopMediaRender(JNIEnv *env, jobject thiz);
static jint com_xindawn_jni_PlatinumJniProxy_setSurfaceView(JNIEnv* env, jobject thiz, int id, jobject objSurface);
static jint com_xindawn_jni_PlatinumJniProxy_channelShutDown(JNIEnv* env, jobject thiz,int id);


static jint com_xindawn_jni_PlatinumJniProxy_responseGenaEvent(JNIEnv *env, jobject thiz,jint cmd,jbyteArray value,jbyteArray data)
{

	 jbyte       *pValue = NULL;
	 jbyte		 *pData  = NULL;
	 char 		 *cValue = NULL;
	 char		 *cData  = NULL;
	 int		 numVlaue  = 0;
	 int		 numData   = 0;



	 pValue   = value ? env->GetByteArrayElements(value, NULL):NULL;
	 numVlaue = value ? env->GetArrayLength(value): 0;
	 pData    = data?env->GetByteArrayElements(data, NULL):NULL;
	 numData =  data ? env->GetArrayLength(data): 0;

	 if(numVlaue > 0)
	 {
		 cValue = (char *) malloc(numVlaue+1);
		 memcpy(cValue,pValue,numVlaue);
		 cValue[numVlaue] = 0;
	 }

	 if(numData > 0)
	 {
		 cData = (char *)malloc(numData+1);
		 memcpy(cData,pData,numData);
		 cData[numData] = 0;
	 }


	 //UpdateState(cmd,cValue,cData);
     LOGI("native  responseGenaEvent is [cmd=%d][value=%s][data=%s]",cmd,cValue,cData);


    if(cValue)
    	free(cValue);

    if(cData)
    	free(cData);

	 if ( pValue != NULL)
	     env->ReleaseByteArrayElements(value, pValue,0);

	 if ( pData != NULL)
	     env->ReleaseByteArrayElements(data, pData, 0);

	 return 0;
}



int jniRegisterNativeMethods(JNIEnv* env,
							const char* className,
							const JNINativeMethod* gMethods,
							int numMethods){
	jclass clazz;



	jclass localClass = env->FindClass("com/xindawn/jni/PlatinumReflection");
	if(localClass == NULL)
	{

		LOGE("get localClass error!!");
		return -1;
	}

	g_inflectClass = (jclass)env->NewGlobalRef(localClass);
	if(g_inflectClass == NULL)
	{

		LOGE("get g_inflectClass error!!");
		return -1;
	}

	 env->DeleteLocalRef(localClass);


	g_methodID = env->GetStaticMethodID(g_inflectClass, "onActionReflection","(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
	if(g_methodID == NULL)
	{

		LOGE("get g_methodID error!!");
		return -1;
	}

	g_methodID1 = env->GetStaticMethodID(g_inflectClass, "onActionReflection","(ILjava/lang/String;[BLjava/lang/String;)V");
	if(g_methodID1 == NULL)
	{

		LOGE("get g_methodID1 error!!");
		return -1;
	}


	g_methodID2 = env->GetStaticMethodID(g_inflectClass, "audio_init","(IIIII)V");
	if(g_methodID2 == NULL)
	{

		LOGE("get g_methodID2 error!!");
		return -1;
	}

	g_methodID3 = env->GetStaticMethodID(g_inflectClass, "audio_process","(I[BDI)V");
	if(g_methodID3 == NULL)
	{

		LOGE("get g_methodID3 error!!");
		return -1;
	}

	g_methodID4 = env->GetStaticMethodID(g_inflectClass, "audio_destroy","(I)V");
	if(g_methodID4 == NULL)
	{

		LOGE("get g_methodID4 error!!");
		return -1;
	}

	clazz = env->FindClass(className);
	if(clazz == NULL){

		LOGE("findclass error!!");
		return -1;
	}



	LOGI("className = %s,numMethods =%d",className,numMethods);

	if(env->RegisterNatives(clazz, gMethods, numMethods) < 0){
		return -1;
	}

	return 0;
}

static JNINativeMethod gMethods[] = {
	{"startMediaRender", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IIIIILandroid/content/Context;)I", (void *)com_xindawn_jni_PlatinumJniProxy_startMediaRender },
	{"stopMediaRender", "()I", (void *)com_xindawn_jni_PlatinumJniProxy_stopMediaRender },
	{"responseGenaEvent", "(I[B[B)Z", (void *)com_xindawn_jni_PlatinumJniProxy_responseGenaEvent },
	{"setSurfaceView", "(ILandroid/view/Surface;)I", (void *)com_xindawn_jni_PlatinumJniProxy_setSurfaceView },
	{"channelShutDown", "(I)I", (void *)com_xindawn_jni_PlatinumJniProxy_channelShutDown },
};

int register_com_xindawn_jni_PlatinumJniProxy(JNIEnv* env){
	return jniRegisterNativeMethods(env, kClassPathName, gMethods, sizeof(gMethods) / sizeof(gMethods[0]));
}

jint JNI_OnLoad(JavaVM* vm, void* reserved){
	JNIEnv* env = NULL;
	jint result = JNI_ERR;

	g_vm = vm;

	if(vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK){		
		LOGE("jni init fialed!!");
		return result;
	}

	if(register_com_xindawn_jni_PlatinumJniProxy(env) != JNI_OK){
		LOGE("register PlatinumJniProxy failed");
		goto end;
	}

	result = JNI_VERSION_1_4;

end:
	return result;
}



extern "C" void ActionInflect(int cmd, const char* value, const char* data,const char *title)
{
	jclass inflectClass = NULL;
	jmethodID inflectMethod = NULL;
	//LOGI("ActionInflect 1");

	if (g_vm == NULL)
	{
		LOGE("g_vm = NULL!!!");
		return ;
	}
	//LOGI("ActionInflect 2");

	int status;
	JNIEnv *env = NULL;
	bool isAttach = false;
	status = g_vm->GetEnv((void **) &env, JNI_VERSION_1_4);
	if(status != JNI_OK)
	{
		status = g_vm->AttachCurrentThread(&env, NULL);
		if(status < 0)
		{
			LOGE("callback_handler: failed to attach , current thread, status = %d", status);
			return;
		}
		isAttach = true;
	}
	//LOGI("ActionInflect 3");

	jstring valueString		= NULL;
	jstring dataString 		= NULL;
	jstring titleString 	= NULL;

	inflectClass 	= g_inflectClass;//env->FindClass("com/xindawn/jni/PlatinumReflection");
	//LOGI("ActionInflect 4");
	if (inflectClass == NULL )
	{
		LOGE("inflectClass == NULL!!!");
		goto end;
	}

	//jmethodID inflectMethod = env->GetStaticMethodID(inflectClass, "onActionReflection","()I;Ljava/lang/String;Ljava/lang/String;");
	inflectMethod = g_methodID;//env->GetStaticMethodID(inflectClass, "onActionReflection","(I;Ljava/lang/String;Ljava/lang/String;)V");

	//LOGI("ActionInflect 5");
	//jclass inflectClass 	= g_inflectClass;
	//jmethodID inflectMethod = g_methodID;



	if (inflectMethod == NULL)
	{
		LOGE("inflectMethod == NULL!!!");
		goto end;
	}

	//LOGI("ActionInflect 6");


	valueString = env->NewStringUTF(value);
	dataString  = env->NewStringUTF(data);
	titleString = env->NewStringUTF(title);

	env->CallStaticVoidMethod(inflectClass, inflectMethod, cmd, valueString, dataString,titleString);

	env->DeleteLocalRef(valueString);
	env->DeleteLocalRef(dataString);
	env->DeleteLocalRef(titleString);

end:
	if (env->ExceptionOccurred())
	{
		env->ExceptionDescribe();
		env->ExceptionClear();
	}
	if (isAttach)
	{
		g_vm->DetachCurrentThread();
	}
}



extern "C" void ActionInflectByte(int cmd, const char* value, const char* data,unsigned int datasize,const char *title)
{
	jclass inflectClass = NULL;
	jmethodID inflectMethod = NULL;
	//LOGI("ActionInflect 1");

	if (g_vm == NULL)
	{
		LOGE("g_vm = NULL!!!");
		return ;
	}
	//LOGI("ActionInflect 2");

	int status;
	JNIEnv *env = NULL;
	bool isAttach = false;
	status = g_vm->GetEnv((void **) &env, JNI_VERSION_1_4);
	if(status != JNI_OK)
	{
		status = g_vm->AttachCurrentThread(&env, NULL);
		if(status < 0)
		{
			LOGE("callback_handler: failed to attach , current thread, status = %d", status);
			return;
		}
		isAttach = true;
	}
	//LOGI("ActionInflect 3");

	jstring valueString		= NULL;
	jbyteArray databyte 	= NULL;
	jstring titleString 	= NULL;

	inflectClass 	= g_inflectClass;//env->FindClass("com/xindawn/jni/PlatinumReflection");
	//LOGI("ActionInflect 4");
	if (inflectClass == NULL )
	{
		LOGE("inflectClass == NULL!!!");
		goto end;
	}

	//jmethodID inflectMethod = env->GetStaticMethodID(inflectClass, "onActionReflection","()I;Ljava/lang/String;Ljava/lang/String;");
	inflectMethod = g_methodID1;//env->GetStaticMethodID(inflectClass, "onActionReflection","(I;Ljava/lang/String;Ljava/lang/String;)V");

	//LOGI("ActionInflect 5");
	//jclass inflectClass 	= g_inflectClass;
	//jmethodID inflectMethod = g_methodID;



	if (inflectMethod == NULL)
	{
		LOGE("inflectMethod == NULL!!!");
		goto end;
	}


	valueString = env->NewStringUTF(value);
	databyte = env->NewByteArray(datasize);
	env->SetByteArrayRegion(databyte, 0, datasize, (jbyte*)data);
	titleString = env->NewStringUTF(title);

	env->CallStaticVoidMethod(inflectClass, inflectMethod, cmd, valueString, databyte,titleString);

	env->DeleteLocalRef(valueString);
	env->DeleteLocalRef(databyte);
	env->DeleteLocalRef(titleString);
end:
	if (env->ExceptionOccurred())
	{
		env->ExceptionDescribe();
		env->ExceptionClear();
	}
	if (isAttach)
	{
		g_vm->DetachCurrentThread();
	}
}






extern "C" void ActionInflect_AudioInit(int id,int bits, int channels, int samplerate,int isaudio)
{
	jclass inflectClass = NULL;
	jmethodID inflectMethod = NULL;

	if (g_vm == NULL)
	{
		LOGE("g_vm = NULL!!!");
		return ;
	}

	int status;
	JNIEnv *env = NULL;
	bool isAttach = false;
	status = g_vm->GetEnv((void **) &env, JNI_VERSION_1_4);
	if(status != JNI_OK)
	{
		status = g_vm->AttachCurrentThread(&env, NULL);
		if(status < 0)
		{
			LOGE("callback_handler: failed to attach , current thread, status = %d", status);
			return;
		}
		isAttach = true;
	}


	inflectClass 	= g_inflectClass;
	if (inflectClass == NULL )
	{
		LOGE("inflectClass == NULL!!!");
		goto end;
	}

	inflectMethod = g_methodID2;

	if (inflectMethod == NULL)
	{
		LOGE("inflectMethod == NULL!!!");
		goto end;
	}

	env->CallStaticVoidMethod(inflectClass, inflectMethod,id, bits, channels, samplerate,isaudio);


end:
	if (env->ExceptionOccurred())
	{
		env->ExceptionDescribe();
		env->ExceptionClear();
	}
	if (isAttach)
	{
		g_vm->DetachCurrentThread();
	}
}





extern "C" void ActionInflect_AudioProcess(int id,const char* data,unsigned int datasize,double timestamp,int seqnum)
{
	jclass inflectClass = NULL;
	jmethodID inflectMethod = NULL;

	if (g_vm == NULL)
	{
		LOGE("g_vm = NULL!!!");
		return ;
	}

	int status;
	JNIEnv *env = NULL;
	bool isAttach = false;
	status = g_vm->GetEnv((void **) &env, JNI_VERSION_1_4);
	if(status != JNI_OK)
	{
		status = g_vm->AttachCurrentThread(&env, NULL);
		if(status < 0)
		{
			LOGE("callback_handler: failed to attach , current thread, status = %d", status);
			return;
		}
		isAttach = true;
	}

	jbyteArray databyte 	= NULL;

	inflectClass 	= g_inflectClass;
	if (inflectClass == NULL )
	{
		LOGE("inflectClass == NULL!!!");
		goto end;
	}

	inflectMethod = g_methodID3;
	if (inflectMethod == NULL)
	{
		LOGE("inflectMethod == NULL!!!");
		goto end;
	}



	databyte = env->NewByteArray(datasize);
	env->SetByteArrayRegion(databyte, 0, datasize, (jbyte*)data);
	env->CallStaticVoidMethod(inflectClass, inflectMethod, id,databyte,timestamp,seqnum);
	env->DeleteLocalRef(databyte);

end:
	if (env->ExceptionOccurred())
	{
		env->ExceptionDescribe();
		env->ExceptionClear();
	}
	if (isAttach)
	{
		g_vm->DetachCurrentThread();
	}
}





extern "C" void ActionInflect_AudioDestroy(int id)
{
	jclass inflectClass = NULL;
	jmethodID inflectMethod = NULL;

	if (g_vm == NULL)
	{
		LOGE("g_vm = NULL!!!");
		return ;
	}

	int status;
	JNIEnv *env = NULL;
	bool isAttach = false;
	status = g_vm->GetEnv((void **) &env, JNI_VERSION_1_4);
	if(status != JNI_OK)
	{
		status = g_vm->AttachCurrentThread(&env, NULL);
		if(status < 0)
		{
			LOGE("callback_handler: failed to attach , current thread, status = %d", status);
			return;
		}
		isAttach = true;
	}


	inflectClass 	= g_inflectClass;
	if (inflectClass == NULL )
	{
		LOGE("inflectClass == NULL!!!");
		goto end;
	}

	inflectMethod = g_methodID4;

	if (inflectMethod == NULL)
	{
		LOGE("inflectMethod == NULL!!!");
		goto end;
	}

	env->CallStaticVoidMethod(inflectClass, inflectMethod,id);


end:
	if (env->ExceptionOccurred())
	{
		env->ExceptionDescribe();
		env->ExceptionClear();
	}
	if (isAttach)
	{
		g_vm->DetachCurrentThread();
	}
}



extern "C" void ActionInflect_destroy(void)
{


	LOGI("ActionInflect_destroy");

	JNIEnv *env = NULL;
	int status;
	bool isAttach = false;
	status = g_vm->GetEnv((void **) &env, JNI_VERSION_1_4);
	if(status != JNI_OK)
	{
		status = g_vm->AttachCurrentThread(&env, NULL);
		if(status < 0)
		{
			LOGE("callback_handler: failed to attach , current thread, status = %d", status);
			return;
		}
		isAttach = true;
	}


	 if(g_inflectClass)
	 {
		 env->DeleteGlobalRef(g_inflectClass);
		 g_inflectClass = NULL;
	 }

 	if (isAttach)
 	{
 		g_vm->DetachCurrentThread();
 	}
}

static jint com_xindawn_jni_PlatinumJniProxy_startMediaRender(JNIEnv* env,jobject thiz, jstring friendname,jstring libpath,jstring activecode,int width,int height,int airtunes_port,int airplay_port,int rcvsize, jobject context)
{


	char* pFriendname = (char *)env->GetStringUTFChars(friendname, NULL);
	char* pLibPath    = (char *)env->GetStringUTFChars(libpath, NULL);
	char* pActivecode = (char *)env->GetStringUTFChars(activecode, NULL);
	LOGI("native get friendname is %s,pActivecode is %s",pFriendname,pActivecode);
	int ret = XinDawn_StartMediaServer(g_vm,pFriendname,pLibPath,width,height,pActivecode);
	LOGI("ret=  %d",ret);
	env->ReleaseStringUTFChars(activecode,pActivecode);
	env->ReleaseStringUTFChars(libpath,pLibPath);
	env->ReleaseStringUTFChars(friendname,pFriendname);

	return 0;

}

static jint com_xindawn_jni_PlatinumJniProxy_stopMediaRender(JNIEnv *env, jobject thiz)
{



	XinDawn_StopMediaServer();

	return 0;

}


static jint com_xindawn_jni_PlatinumJniProxy_setSurfaceView(
		JNIEnv* env, jobject thiz, int id, jobject objSurface)
{
	XinDawn_SetVideoSurface(id,objSurface);

	return 0;
}


static jint com_xindawn_jni_PlatinumJniProxy_channelShutDown(
		JNIEnv* env, jobject thiz,int id)
{
	XinDawn_ShutDown(id);

	return 0;
}


