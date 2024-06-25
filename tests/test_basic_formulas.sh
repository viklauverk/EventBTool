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

rm -rf "$OUTDIR"
mkdir -p $OUTDIR

INFO="Tested docmod to generate list of formulas for tex typesetting."

$PROG docmod -q tex tests/test_basic_formulas.tex $OUTDIR/modded_test_basic_formulas.tex
if [ "$?" = "0" ]
then
    echo "OK $INFO"
else
    echo "ERROR $INFO"
    echo "To re-run test: echo $0 $1 $2"
    exit 1
fi


INFO="Tested xelatex on generated document."
cp doc/bsymb.sty $OUTDIR
xelatex -output-directory=$OUTDIR -interaction=batchmode -halt-on-error $OUTDIR/modded_test_basic_formulas.tex > $OUTDIR/xelatex.output 2>&1

if [ "$?" = "0" ]
then
    echo "OK $INFO"
else
    echo "ERROR $INFO"
    echo "To re-run test: echo $0 $1 $2"
    exit 1
fi
