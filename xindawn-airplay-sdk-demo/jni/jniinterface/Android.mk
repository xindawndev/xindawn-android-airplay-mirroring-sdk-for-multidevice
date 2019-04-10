LOCAL_PATH:=$(call my-dir)


include $(CLEAR_VARS)
LOCAL_MODULE:=libjniinterface

LOCAL_C_INCLUDES   := \
  $(LOCAL_PATH)/../Include \

LOCAL_SRC_FILES := \
    com_xindawn_jni_PlatinumJniProxy.cpp \
    H264MediaCodec.cpp \
    sps_pps.cpp \
    Lock.cpp \
    videosource.cpp \
    XinDawnMultiScreen.cpp

LOCAL_CPPFLAGS := -fpermissive
LOCAL_CFLAGS   := -fpermissive
    
LOCAL_SHARED_LIBRARIES:= libAirPlay
  

LOCAL_LDLIBS += -llog -landroid -lmediandk


    
include $(BUILD_SHARED_LIBRARY)

