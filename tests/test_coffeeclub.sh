#!/bin/bash

PROG="$1"
DIR="$2"

if [ "$PROG" = "" ] || [ "$DIR" = "" ]
then
    echo "Usage: test <evbt_executable> <output_dir>"
    exit 1
fi

RC="0"

TESTNAME="${0##*/}"
TESTNAME="${TESTNAME%.*}"
OUTDIR="$DIR/$TESTNAME"

rm -rf $OUTDIR
mkdir -p $OUTDIR

INFO="Generated coffee club tex file."
$PROG docgen --tex -q --outputdir=$OUTDIR models/CoffeeClub
cp doc/bsymb.sty $OUTDIR
xelatex -output-directory=$OUTDIR -interaction=batchmode -halt-on-error $OUTDIR/CoffeeClub.tex > $OUTDIR/tmp 2>&1

if [ "$?" != "0" ]
then
    cat $OUTDIR/tmp
    echo "ERROR $INFO"
    echo "To re-run test: echo $0 $1 $2"
    exit 1
else
    if [ -s $OUTDIR/CoffeeClub.pdf ]
    then
        echo "OK $INFO"
    else
        echo "ERROR $INFO"
        echo "To re-run test: echo $0 $1 $2"
        exit 1
    fi
fi
