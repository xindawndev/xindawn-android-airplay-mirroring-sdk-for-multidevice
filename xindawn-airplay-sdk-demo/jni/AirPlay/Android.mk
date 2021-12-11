LOCAL_PATH:= $(call my-dir)


include $(CLEAR_VARS)
LOCAL_MODULE := libAirPlay
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/libAirPlay.so
include $(PREBUILT_SHARED_LIBRARY)


#include $(CLEAR_VARS)
#LOCAL_MODULE := libmdnsserver
#LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/libmdnsserver.so
#include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libShairplay
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/libShairplay.so
include $(PREBUILT_SHARED_LIBRARY)
