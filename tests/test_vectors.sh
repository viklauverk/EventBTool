#!/bin/bash

PROG="$1"
DIR="$2"
RC="0"

OUTDIR="$DIR/test_vectors"
mkdir -p $OUTDIR

echo Generate vectors C++
$PROG codegen -q --outputdir=$OUTDIR c++  tests/Vectors/Vectors.bum
if [ "$?" = "0" ]
then
    echo OK
else
    echo ERROR
    exit 1
fi

cat <<EOF > $OUTDIR/main.cc
#include"Vectors.h"
#include<stdio.h>
#include<memory>
#include<inttypes.h>

int main()
{
    auto vectors = createVectors();
    vectors->trace([](const char*msg) { printf("TRACE %s\n", msg); });
    vectors->setHeight(17, 42);
    std::vector<uint64_t> alfa;
    alfa.assign(100,99);
    vectors->setHeights(alfa);
    vectors->run();
}
EOF

echo Running vectors C++
g++ -o $OUTDIR/test $OUTDIR/*.cc

$OUTDIR/test > $OUTDIR/c++output

cat <<EOF > $OUTDIR/expected_output
TRACE setHeight
TRACE setHeights
EOF

diff $OUTDIR/c++output $OUTDIR/expected_output

if [ "$?" = "1" ]
then
    echo ERROR!
    exit 1
else
    echo OK
fi

exit 0
