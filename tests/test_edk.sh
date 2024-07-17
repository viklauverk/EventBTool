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

INFO="Tested generation of tex from EDK model."
$PROG docgen --tex -q --outputdir=$OUTDIR src/event-b/EDK
cp doc/bsymb.sty $OUTDIR
xelatex -output-directory=$OUTDIR -aux-directory=$OUTDIR -interaction=batchmode -halt-on-error $OUTDIR/EDK.tex > $OUTDIR/tmp 2>&1

if [ "$?" != "0" ]
then
    cat $OUTDIR/tmp
    echo "ERROR $INFO"
    exit 1
else
    if [ -s $OUTDIR/EDK.pdf ]
    then
        echo "OK $INFO"
    else
        echo "ERROR $INFO"
        exit 1
    fi
fi

INFO="Tested generation of c++ EDK from example of use model."
$PROG codegen -q --outputdir=$OUTDIR c++ src/event-b/EDK/ExamplesOfUse.bum
if [ "$?" = "0" ]
then
    echo "OK $INFO"
else
    echo "ERROR $INFO"
    exit 1
fi

cat <<EOF > $OUTDIR/main.cc
#include"EDK.h"

int main()
{
    auto edk = createEDK();
    edk->trace([](const char*msg) { printf("TRACE %s\n", msg); });
    edk->setTemperature(234,-2);
    edk->raiseTemperature(4.5);
    float temp;
    edk->queryTemperature(&temp);
    printf("Temp %f\n", temp);
    edk->run();
}
EOF

INFO="Tested compiling c++ code generated from EDK example of use model."
g++ -o $OUTDIR/test $OUTDIR/*.cc
if [ "$?" = "0" ]
then
    echo "OK $INFO"
else
    echo "ERROR $INFO"
    exit 1
fi

INFO="Tested running generated c++ code for EDK example of use."
$OUTDIR/test > $OUTDIR/c++output

cat <<EOF > $OUTDIR/expected_output
TRACE setTemperature
TRACE raiseTemperature
TRACE queryTemperature
Temp 6.840000
EOF

diff $OUTDIR/c++output $OUTDIR/expected_output

if [ "$?" = "1" ]
then
    echo "ERROR $INFO"
    exit 1
else
    echo "OK $INFO"
fi

exit 0
