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

mkdir -p $OUTDIR

INFO="Tested C++ code generation for bridge model."
$PROG codegen -q --outputdir=$OUTDIR c++ tests/models/Bridge/WithDrawBridge.bum
if [ "$?" = "0" ]
then
    echo "OK $INFO"
else
    echo "ERROR $INFO"
    exit 1
fi

cat <<EOF > $OUTDIR/main.cc

#include"Bridge.h"

int main()
{
    auto bridge = createBridge();
    bridge->trace([](const char*msg) { printf("TRACE %s\n", msg); });
    bridge->enter(2);
    bridge->leave(2);
    bridge->setBridge(false);
    bridge->enter(3);
    bridge->leave(2);
    bridge->run();
}
EOF

INFO="Tested compiling generated c++ code for bridge model."
g++ -o $OUTDIR/test $OUTDIR/*.cc
if [ "$?" = "0" ]
then
    echo "OK $INFO"
else
    echo "ERROR $INFO"
    exit 1
fi

INFO="Tested running generated C++ code for bridge mode."
$OUTDIR/test > $OUTDIR/c++output

cat <<EOF > $OUTDIR/expected_output
TRACE setBridge
TRACE enter
TRACE leave
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
