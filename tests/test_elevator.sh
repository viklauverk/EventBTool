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

INFO="Generated elevator tex file."
$PROG docgen -q --outputdir=$OUTDIR tex models/Elevator
cp doc/bsymb.sty $OUTDIR
xelatex -output-directory=$OUTDIR -aux-directory=$OUTDIR -interaction=batchmode -halt-on-error $OUTDIR/Elevator.tex > $OUTDIR/tmp 2>&1

if [ "$?" != "0" ]
then
    cat $OUTDIR/tmp
    echo "ERROR $INFO"
    echo "To re-run test: echo $0 $1 $2"
    exit 1
else
    if [ -s $OUTDIR/Elevator.pdf ]
    then
        echo "OK $INFO"
    else
        echo "ERROR $INFO"
        echo "To re-run test: echo $0 $1 $2"
        exit 1
    fi
fi

INFO="Tested docmod of elevator tex file."
cat <<EOF > $OUTDIR/template.tex
DOCUMENT
EVBT(show part "Elevator/moveUp")
INFORMATION
EVBT(sp Elevator/moveDown)
Variables
EVBT(sp Elevator/variables)
Missing
EVBT(sp FinnsEj)
END
EOF
$PROG docmod -q --outputdir=$OUTDIR tex $OUTDIR/template.tex $OUTDIR/info.tex models/Elevator
if [ "$?" = "0" ]
then
    echo "OK $INFO"
else
    echo "ERROR $INFO"
    echo "To re-run test: echo $0 $1 $2"
    exit 1
fi

INFO="Tested generation of C++ code for elevator model."
$PROG codegen -q --outputdir=$OUTDIR c++ models/Elevator/Elevator.bum
if [ "$?" = "0" ]
then
    echo "OK $INFO"
else
    echo "ERROR $INFO"
    echo "To re-run test: echo $0 $1 $2"
    exit 1
fi

cat <<EOF > $OUTDIR/main.cc
#include"Elevator.h"

int main()
{
    auto elevator = createElevator();
    elevator->trace([](const char*msg) { printf("TRACE %s\n", msg); });
    elevator->enterDest(5);
    elevator->run();
    elevator->enterDest(1);
    elevator->run();
}
EOF

INFO="Tested compiling generated C++ code for elevator model."
g++ -o $OUTDIR/test $OUTDIR/*.cc
if [ "$?" = "0" ]
then
    echo "OK $INFO"
else
    echo "ERROR $INFO"
    exit 1
fi

INFO="Tested running compiled C++ code for elevator model."
$OUTDIR/test > $OUTDIR/c++output

cat <<EOF > $OUTDIR/expected_output
TRACE enterDest
TRACE moveUp
TRACE moveUp
TRACE moveUp
TRACE moveUp
TRACE enterDest
TRACE moveUp
TRACE moveUp
TRACE moveUp
TRACE moveUp
TRACE moveUp
TRACE startMovingDown
TRACE moveDown
TRACE moveDown
TRACE moveDown
TRACE moveDown
TRACE moveDown
TRACE moveDown
TRACE moveDown
TRACE moveDown
TRACE moveDown
EOF

diff $OUTDIR/c++output $OUTDIR/expected_output

if [ "$?" = "1" ]
then
    echo "ERROR $INFO"
    exit 1
else
    echo "OK $INFO"
fi

INFO="Tested generation of javascript code for elevator mode."
$PROG codegen -q --outputdir=$OUTDIR javascript models/Elevator/Elevator.bum
if [ "$?" = "0" ]
then
    echo "OK $INFO"
else
    echo "ERROR $INFO"
    exit 1
fi

cat <<EOF > $OUTDIR/test.js

var Elevator = require("./Elevator.js");
var elevator = Elevator.create();
elevator.trace((msg)=>{ console.log("TRACE "+msg); });

elevator.enterDest(5);
elevator.run();
elevator.enterDest(1);
elevator.run();
EOF

INFO="Tested running generated javascript code for elevator model."
(cd $OUTDIR ; node test.js) > $OUTDIR/jsoutput

diff $OUTDIR/jsoutput $OUTDIR/expected_output

if [ "$?" = "1" ]
then
    echo "ERROR $INFO"
    exit 1
else
    echo "OK $INFO"
fi

exit 0
