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

INFO="Tested C++ code generation for quare root model."
$PROG codegen -q --outputdir=$OUTDIR c++ models/SquareRoot/SquareRoot_R4_WithMiddleInVariable.bum
if [ "$?" = "0" ]
then
    echo "OK $INFO"
else
    echo "ERROR $INFO"
    exit 1
fi

cat <<EOF > $OUTDIR/main.cc

#include"SquareRoot.h"
#include<stdio.h>
#include<memory>
#include<inttypes.h>

int main()
{
    auto squareroot = createSquareRoot();
    squareroot->trace([](const char*msg) { printf("TRACE %s\n", msg); });

    uint64_t num = 49;
    squareroot->setInput(num);
    squareroot->run();

    uint64_t result = 0;
    squareroot->getResult(&result);

    printf("Squareroot of %zu is %zu\n", num, result);
}
EOF

INFO="Tested compiling generated c++ code for square root."
g++ -o $OUTDIR/test $OUTDIR/*.cc
if [ "$?" = "0" ]
then
    echo "OK $INFO"
else
    echo "ERROR $INFO"
    exit 1
fi

INFO="Tested running generated C++ code for square root."
$OUTDIR/test > $OUTDIR/c++output

cat <<EOF > $OUTDIR/expected_output
TRACE setInput
TRACE ImproveUpperBound
TRACE ImproveUpperBound
TRACE ImproveLowerBound
TRACE ImproveUpperBound
TRACE ImproveLowerBound
TRACE ImproveUpperBound
TRACE SquareRoot
TRACE getResult
Squareroot of 49 is 7
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
