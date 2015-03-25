LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)


LOCAL_C_INCLUDES := $(LOCAL_PATH)/LOCAL_C_INCLUDES
LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -llog


LOCAL_MODULE    := SmartDownloadLibrary
LOCAL_SRC_FILES := \
packetizer.c \
SmartDownloadLibrary.c

include $(BUILD_SHARED_LIBRARY)
