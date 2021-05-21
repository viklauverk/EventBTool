#!/bin/bash

PROG="$1"
DIR="$2"
DEBUG="$3"

if [ "$PROG" = "" ] || [ "$DIR" = "" ]
then
    echo "Usage: test <evbt_executable> <output_dir>"
    exit 1
fi

RC="0"

TESTNAME="${0##*/}"
TESTNAME="${TESTNAME%.*}"
OUTDIR="$DIR/$TESTNAME"

mkdir -p $OUTDIR

INFO="Test parsing of set comprehension."
$PROG docgen -q --outputdir=$OUTDIR tex  tests/models/SetComprehensions
if [ "$?" = "0" ]
then
    echo "OK $INFO"
else
    echo "ERROR $INFO"
    exit 1
fi

exit 0
