APP := multimon-ng
ROOT := /Users/keithconger/Development/
INSTALL_DIR := /data/local/tmp
NDK_PLATFORM_VER := 18

ANDROID_NDK_ROOT := $(ROOT)/ndk
ANDROID_NDK_HOST := darwin-x86_64
ANDROID_SDK_ROOT := $(ROOT)/adt/sdk
PREBUILD := $(ANDROID_NDK_ROOT)/toolchains/arm-linux-androideabi-4.8/prebuilt/$(ANDROID_NDK_HOST)

BIN := $(PREBUILD)/bin
LIB := $(ANDROID_NDK_ROOT)/platforms/android-$(NDK_PLATFORM_VER)/arch-arm/usr/lib
INCLUDE := $(ANDROID_NDK_ROOT)/platforms/android-$(NDK_PLATFORM_VER)/arch-arm/usr/include

CC := $(BIN)/arm-linux-androideabi-gcc
LINK := $(BIN)/arm-linux-androideabi-g++
GDB_CLIENT := $(BIN)/arm-linux-androideabi-gdb

LIBCRT := $(LIB)/crtbegin_dynamic.o
LIBCRTEND := $(LIB)/crtend_android.o
LINKER := /system/bin/linker

DEBUG := -g

DEFINES := -DMAX_VERBOSE_LEVEL=1 -DDUMMY_AUDIO -DNO_X11 -DCHARSET_UTF8

CFLAGS := $(DEBUG) -fno-short-enums -I$(INCLUDE)
CFLAGS += -Wl,-rpath-link=$(LIB),-dynamic-linker=$(LINKER) -L$(LIB)
CFLAGS += -nostdlib -lc
CFLAGS += -lm -lgcc -std=gnu99 $(DEFINES)

LDFLAGS += -nostdlib -lc -lm
LDFLAGS += -disable-multilib

OBJECTS       = unixinput.o \
		uart.o \
		pocsag.o \
		selcall.o \
		hdlc.o \
		demod_zvei1.o \
		demod_zvei2.o \
		demod_zvei3.o \
		demod_pzvei.o \
		demod_dzvei.o \
		demod_ccir.o \
		demod_eia.o \
		demod_eea.o \
		demod_ufsk12.o \
		demod_poc24.o \
		demod_poc12.o \
		demod_poc5.o \
		demod_hapn48.o \
		demod_fsk96.o \
		demod_dtmf.o \
		demod_clipfsk.o \
		demod_afsk24.o \
		demod_afsk24_3.o \
		demod_afsk24_2.o \
		demod_afsk12.o \
		costabi.o \
		costabf.o \
		clip.o \
		demod_eas.o \
		demod_morse.o \
		demod_dumpcsv.o

all: $(APP)

$(APP):  $(OBJECTS)  
	$(LINK) $(LDFLAGS) -o $(APP) $(OBJECTS) $(OBJCOMP) $(LIBS) $(CFLAGS) $(LIBCRT) $(LIBCRTEND)

unixinput.o: unixinput.c multimon.h \
		msvc_support.h \
		win32_getopt.h
	$(CC) -c $(CFLAGS) $(INCLUDE) -o unixinput.o unixinput.c

uart.o: uart.c multimon.h \
		msvc_support.h \
		win32_getopt.h
	$(CC) -c $(CFLAGS) $(INCLUDE) -o uart.o uart.c

pocsag.o: pocsag.c multimon.h \
		msvc_support.h \
		win32_getopt.h
	$(CC) -c $(CFLAGS) $(INCLUDE) -o pocsag.o pocsag.c

selcall.o: selcall.c multimon.h \
		msvc_support.h \
		win32_getopt.h \
		filter.h \
		filter-i386.h
	$(CC) -c $(CFLAGS) $(INCLUDE) -o selcall.o selcall.c

hdlc.o: hdlc.c multimon.h \
		msvc_support.h \
		win32_getopt.h
	$(CC) -c $(CFLAGS) $(INCLUDE) -o hdlc.o hdlc.c

demod_zvei1.o: demod_zvei1.c multimon.h \
		msvc_support.h \
		win32_getopt.h
	$(CC) -c $(CFLAGS) $(INCLUDE) -o demod_zvei1.o demod_zvei1.c

demod_zvei2.o: demod_zvei2.c multimon.h \
		msvc_support.h \
		win32_getopt.h
	$(CC) -c $(CFLAGS) $(INCLUDE) -o demod_zvei2.o demod_zvei2.c

demod_zvei3.o: demod_zvei3.c multimon.h \
		msvc_support.h \
		win32_getopt.h
	$(CC) -c $(CFLAGS) $(INCLUDE) -o demod_zvei3.o demod_zvei3.c

demod_pzvei.o: demod_pzvei.c multimon.h \
		msvc_support.h \
		win32_getopt.h
	$(CC) -c $(CFLAGS) $(INCLUDE) -o demod_pzvei.o demod_pzvei.c

demod_dzvei.o: demod_dzvei.c multimon.h \
		msvc_support.h \
		win32_getopt.h
	$(CC) -c $(CFLAGS) $(INCLUDE) -o demod_dzvei.o demod_dzvei.c

demod_ccir.o: demod_ccir.c multimon.h \
		msvc_support.h \
		win32_getopt.h
	$(CC) -c $(CFLAGS) $(INCLUDE) -o demod_ccir.o demod_ccir.c

demod_eia.o: demod_eia.c multimon.h \
		msvc_support.h \
		win32_getopt.h
	$(CC) -c $(CFLAGS) $(INCLUDE) -o demod_eia.o demod_eia.c

demod_eea.o: demod_eea.c multimon.h \
		msvc_support.h \
		win32_getopt.h
	$(CC) -c $(CFLAGS) $(INCLUDE) -o demod_eea.o demod_eea.c

demod_ufsk12.o: demod_ufsk12.c multimon.h \
		msvc_support.h \
		win32_getopt.h \
		filter.h \
		filter-i386.h
	$(CC) -c $(CFLAGS) $(INCLUDE) -o demod_ufsk12.o demod_ufsk12.c

demod_poc24.o: demod_poc24.c multimon.h \
		msvc_support.h \
		win32_getopt.h \
		filter.h \
		filter-i386.h
	$(CC) -c $(CFLAGS) $(INCLUDE) -o demod_poc24.o demod_poc24.c

demod_poc12.o: demod_poc12.c multimon.h \
		msvc_support.h \
		win32_getopt.h \
		filter.h \
		filter-i386.h
	$(CC) -c $(CFLAGS) $(INCLUDE) -o demod_poc12.o demod_poc12.c

demod_poc5.o: demod_poc5.c multimon.h \
		msvc_support.h \
		win32_getopt.h \
		filter.h \
		filter-i386.h
	$(CC) -c $(CFLAGS) $(INCLUDE) -o demod_poc5.o demod_poc5.c

demod_hapn48.o: demod_hapn48.c multimon.h \
		msvc_support.h \
		win32_getopt.h \
		filter.h \
		filter-i386.h
	$(CC) -c $(CFLAGS) $(INCLUDE) -o demod_hapn48.o demod_hapn48.c

demod_fsk96.o: demod_fsk96.c multimon.h \
		msvc_support.h \
		win32_getopt.h \
		filter.h \
		filter-i386.h
	$(CC) -c $(CFLAGS) $(INCLUDE) -o demod_fsk96.o demod_fsk96.c

demod_dtmf.o: demod_dtmf.c multimon.h \
		msvc_support.h \
		win32_getopt.h \
		filter.h \
		filter-i386.h
	$(CC) -c $(CFLAGS) $(INCLUDE) -o demod_dtmf.o demod_dtmf.c

demod_clipfsk.o: demod_clipfsk.c multimon.h \
		msvc_support.h \
		win32_getopt.h \
		filter.h \
		filter-i386.h
	$(CC) -c $(CFLAGS) $(INCLUDE) -o demod_clipfsk.o demod_clipfsk.c

demod_afsk24.o: demod_afsk24.c multimon.h \
		msvc_support.h \
		win32_getopt.h \
		filter.h \
		filter-i386.h
	$(CC) -c $(CFLAGS) $(INCLUDE) -o demod_afsk24.o demod_afsk24.c

demod_afsk24_3.o: demod_afsk24_3.c multimon.h \
		msvc_support.h \
		win32_getopt.h \
		filter.h \
		filter-i386.h
	$(CC) -c $(CFLAGS) $(INCLUDE) -o demod_afsk24_3.o demod_afsk24_3.c

demod_afsk24_2.o: demod_afsk24_2.c multimon.h \
		msvc_support.h \
		win32_getopt.h \
		filter.h \
		filter-i386.h
	$(CC) -c $(CFLAGS) $(INCLUDE) -o demod_afsk24_2.o demod_afsk24_2.c

demod_afsk12.o: demod_afsk12.c multimon.h \
		msvc_support.h \
		win32_getopt.h \
		filter.h \
		filter-i386.h
	$(CC) -c $(CFLAGS) $(INCLUDE) -o demod_afsk12.o demod_afsk12.c

costabi.o: costabi.c 
	$(CC) -c $(CFLAGS) $(INCLUDE) -o costabi.o costabi.c

costabf.o: costabf.c 
	$(CC) -c $(CFLAGS) $(INCLUDE) -o costabf.o costabf.c

clip.o: clip.c multimon.h \
		msvc_support.h \
		win32_getopt.h
	$(CC) -c $(CFLAGS) $(INCLUDE) -o clip.o clip.c

demod_eas.o: demod_eas.c multimon.h \
		msvc_support.h \
		win32_getopt.h \
		filter.h \
		filter-i386.h
	$(CC) -c $(CFLAGS) $(INCLUDE) -o demod_eas.o demod_eas.c

demod_morse.o: demod_morse.c multimon.h \
		msvc_support.h \
		win32_getopt.h
	$(CC) -c $(CFLAGS) $(INCLUDE) -o demod_morse.o demod_morse.c

demod_dumpcsv.o: demod_dumpcsv.c multimon.h \
		msvc_support.h \
		win32_getopt.h
	$(CC) -c $(CFLAGS) $(INCLUDE) -o demod_dumpcsv.o demod_dumpcsv.c

install: $(APP)
	$(ANDROID_SDK_ROOT)/platform-tools/adb push $(APP) $(INSTALL_DIR)/$(APP) 
	$(ANDROID_SDK_ROOT)/platform-tools/adb shell su -c chmod 777 $(INSTALL_DIR)/$(APP)

shell:
	$(ANDROID_SDK_ROOT)/platform-tools/adb shell

run:
	$(ANDROID_SDK_ROOT)/platform-tools/adb shell $(INSTALL_DIR)/$(APP)

debug-install:
	$(ANDROID_SDK_ROOT)/platform-tools/adb push $(PREBUILD)/../gdbserver $(INSTALL_DIR)/gdbserver
	$(ANDROID_SDK_ROOT)/platform-tools/adb shell chmod 777 $(INSTALL_DIR)/gdbserver

debug-go:
	$(ANDROID_SDK_ROOT)/platform-tools/adb forward tcp:1234: tcp:1234
	$(ANDROID_SDK_ROOT)/platform-tools/adb shell $(INSTALL_DIR)/gdbserver :1234 $(INSTALL_DIR)/$(APP)

debug:
	$(GDB_CLIENT) $(APP)

clean:
	@rm -f $(APP).o $(APP)
