#!/system/bin/sh

APPROOT=$1
FREQ=$2
GAIN=$3
SQUELCH=$4

$APPROOT/nativeFolder/rtl_fm -f ${FREQ}M -s 22050 -M fm -F 0 -E dc -p 37 -g $GAIN -l $SQUELCH | tee $APPROOT/pipe | $APPROOT/nativeFolder/multimon-ng -a EAS -q -t raw -