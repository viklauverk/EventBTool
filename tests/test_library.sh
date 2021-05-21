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

INFO="Tested generation of C++ code for library model."
$PROG codegen -q --outputdir=$OUTDIR c++  tests/models/Library/Library.bum
if [ "$?" = "0" ]
then
    echo "OK $INFO"
else
    echo "ERROR $INFO"
    exit 1
fi

cat <<EOF > $OUTDIR/main.cc

#include"Library.h"
#include<stdio.h>
#include<memory>
#include<inttypes.h>

int main()
{
    auto library = createLibrary();
    library->trace([](const char*msg) { printf("TRACE %s\n", msg); });
    library->addBook(17);
    library->addBook(4711);
    library->addBook(32768);
    library->addBorrower(1);
    library->addBorrower(2);
    library->addLoan(1,17);
    library->addLoan(2,4711);

    bool on_loan = false;
    library->isBookOnLoan(4711, &on_loan);

    printf("isBookOnLoan(4711)=%d\n", on_loan);

    uint64_t who = 0;
    library->whoBorrowsBook(4711, &who);

    printf("whoBorrowsBook(4711)=%" PRIu64 "\n", who);

    library->returnBook(4711);

    library->isBookOnLoan(4711, &on_loan);

    printf("isBookOnLoan(4711)=%d\n", on_loan);

    library->run();
}
EOF

INFO="Tested running generated c++ code for library model."
g++ -o $OUTDIR/test $OUTDIR/*.cc

$OUTDIR/test > $OUTDIR/c++output

cat <<EOF > $OUTDIR/expected_output
TRACE addBook
TRACE addBook
TRACE addBook
TRACE addBorrower
TRACE addBorrower
TRACE addLoan
TRACE addLoan
TRACE isBookOnLoan
isBookOnLoan(4711)=1
TRACE whoBorrowsBook
whoBorrowsBook(4711)=2
TRACE returnBook
TRACE isBookOnLoan
isBookOnLoan(4711)=0
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
