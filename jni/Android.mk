LOCAL_PATH:= $(call my-dir)
APP_PLATFORM:= android-8
APP_ABI:= armeabi armeabi-v7a x86

include $(CLEAR_VARS)

LOCAL_SRC_FILES:= \
 libusb-andro/libusb/core.c \
 libusb-andro/libusb/descriptor.c \
 libusb-andro/libusb/io.c \
 libusb-andro/libusb/sync.c \
 libusb-andro/libusb/os/linux_usbfs.c \
 libusb-andro/libusb/os/threads_posix.c\
 rtl-sdr/src/rtl_fm.c \
 rtl-sdr/src/convenience/convenience.c \
 rtl-sdr/src/librtlsdr.c \
 rtl-sdr/src/tuner_e4k.c \
 rtl-sdr/src/tuner_fc0012.c \
 rtl-sdr/src/tuner_fc0013.c \
 rtl-sdr/src/tuner_fc2580.c \
 rtl-sdr/src/tuner_r82xx.c

LOCAL_C_INCLUDES += \
libusb-andro \
libusb-andro/libusb \
libusb-andro/libusb/os \
libusb-andro/libusb \
rtl-sdr/include \
rtl-sdr/src \
rtl-sdr/src/convenience

LOCAL_CFLAGS += -DLIBUSB_DESCRIBE=""  -O3 
LOCAL_MODULE:= rtl_fm
LOCAL_PRELINK_MODULE:= true
include $(BUILD_EXECUTABLE)

all: $(LOCAL_PATH)/../assets/nativeFolder/$(notdir $(LOCAL_BUILT_MODULE))

$(LOCAL_PATH)/../assets/nativeFolder/$(notdir $(LOCAL_BUILT_MODULE)): $(LOCAL_BUILT_MODULE)
	cp $< $@

include $(CLEAR_VARS)

LOCAL_SRC_FILES:= \
multimon-ng-master/unixinput.c \
multimon-ng-master/uart.c \
multimon-ng-master/pocsag.c \
multimon-ng-master/selcall.c \
multimon-ng-master/hdlc.c \
multimon-ng-master/demod_zvei1.c \
multimon-ng-master/demod_zvei2.c \
multimon-ng-master/demod_zvei3.c \
multimon-ng-master/demod_pzvei.c \
multimon-ng-master/demod_dzvei.c \
multimon-ng-master/demod_ccir.c \
multimon-ng-master/demod_eia.c \
multimon-ng-master/demod_eea.c \
multimon-ng-master/demod_ufsk12.c \
multimon-ng-master/demod_poc24.c \
multimon-ng-master/demod_poc12.c \
multimon-ng-master/demod_poc5.c \
multimon-ng-master/demod_hapn48.c \
multimon-ng-master/demod_fsk96.c \
multimon-ng-master/demod_dtmf.c \
multimon-ng-master/demod_clipfsk.c \
multimon-ng-master/demod_afsk24.c \
multimon-ng-master/demod_afsk24_3.c \
multimon-ng-master/demod_afsk24_2.c \
multimon-ng-master/demod_afsk12.c \
multimon-ng-master/costabi.c \
multimon-ng-master/costabf.c \
multimon-ng-master/clip.c \
multimon-ng-master/demod_eas.c \
multimon-ng-master/demod_morse.c \
multimon-ng-master/demod_dumpcsv.c

LOCAL_C_INCLUDES += \
multimon-ng-master/ \

LOCAL_CFLAGS += -O3 -DMAX_VERBOSE_LEVEL=1 -DDUMMY_AUDIO -DNO_X11 -DCHARSET_UTF8 -std=gnu99
LOCAL_MODULE:= multimon-ng
LOCAL_PRELINK_MODULE:= true
include $(BUILD_EXECUTABLE)

all: $(LOCAL_PATH)/../assets/nativeFolder/$(notdir $(LOCAL_BUILT_MODULE))

$(LOCAL_PATH)/../assets/nativeFolder/$(notdir $(LOCAL_BUILT_MODULE)): $(LOCAL_BUILT_MODULE)
	cp $< $@
