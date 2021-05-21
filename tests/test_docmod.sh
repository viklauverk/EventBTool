#!/bin/bash

PROG="$1"
DIR="$2"
RC="0"

OUTDIR="$DIR/test_docmod"
mkdir -p $OUTDIR

INFO="Tested docmod with formulas and the Elevator model."

cat <<EOF > $OUTDIR/template.txt
DOCUMENT
EVBT(add vars x y)
EVBT(add sets S T)
EVBT(add consts c f)
EVBT(add exprs E F)
+++++
EVBT(show formula plain "x:BOOL & y:S => E = F")
+++++
EVBT(read "tests/models/Elevator")
Elevator enterDest grd_1:
EVBT(show part framed plain "Elevator/events/enterDest/guards/grd_1")
+++++
END
EOF

$PROG docmod -q --outputdir=$OUTDIR plain $OUTDIR/template.txt $OUTDIR/info.txt

cat <<EOF > $OUTDIR/expected.txt
DOCUMENT
OK
OK
OK
OK
+++++
x∈BOOL∧y∈S⇒ E=F
+++++
read 1 contexts and 1 machines
Elevator enterDest grd_1:
┌──────────┐
│grd_1: d∈ℕ│
└──────────┘
+++++
END
EOF

diff -Z $OUTDIR/info.txt $OUTDIR/expected.txt
if [ "$?" = "0" ]
then
    echo "OK $INFO"
else
    echo "ERROR $INFO"
    echo "To re-run test: echo $0 $1 $2"
    echo "xxxxxxxxxxxxxxxxxx"
    cat $OUTDIR/info.txt
    echo "xxxxxxxxxxxxxxxxxx"
    cat $OUTDIR/expected.txt
    echo "xxxxxxxxxxxxxxxxxx"
    exit 1
fi

exit 0
