#!/bin/bash

PROG="$1"
MODE="$2"

OUT=build/test_$MODE

rm -rf $OUT
mkdir -p $OUT

# Test parsing and plain rendering of formulas read from a file and a model.
./tests/test_docmod.sh $PROG $OUT
if [ "$?" != "0" ]; then exit 1; fi

# Test tex rendering of formulas inside a tex document.
./tests/test_basic_formulas.sh $PROG $OUT
if [ "$?" != "0" ]; then exit 1; fi

./tests/test_setcomprehensions.sh $PROG $OUT
if [ "$?" != "0" ]; then exit 1; fi

./tests/test_projections.sh $PROG $OUT
if [ "$?" != "0" ]; then exit 1; fi

# Test that safe (as safe as possible) latex output is generated from
# comments, even though the comments contain Event-B unicode chars
# and tex chars and some other unicode code points.
./tests/test_wellcommented.sh $PROG $OUT
if [ "$?" != "0" ]; then exit 1; fi

#tests/test_vectors.sh $PROG $OUT
#if [ "$?" != "0" ]; then exit 1; fi

./tests/test_elevator.sh $PROG $OUT
if [ "$?" != "0" ]; then exit 1; fi

./tests/test_bridge.sh $PROG $OUT
if [ "$?" != "0" ]; then exit 1; fi

./tests/test_library.sh $PROG $OUT
if [ "$?" != "0" ]; then exit 1; fi

./tests/test_coffeeclub.sh $PROG $OUT
if [ "$?" != "0" ]; then exit 1; fi

./tests/test_squareroot.sh $PROG $OUT
if [ "$?" != "0" ]; then exit 1; fi

#tests/test_trivial_atc.sh $PROG $OUT
#if [ "$?" != "0" ]; then RC="1"; fi

./tests/test_edk.sh $PROG $OUT
if [ "$?" != "0" ]; then exit 1; fi

exit 0
