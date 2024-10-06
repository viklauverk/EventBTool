#!/bin/bash

if [ "$1" = "" ]
then
    echo "Usage: collect_copyrights.sh [output_file]"
    exit 1
fi

TMP=$(mktemp -t evbt.copyrights.XXXXXXXXXX)
TMP_AUTHORS=$(mktemp -t spraul.authors.XXXXXXXXXX)
TMP_OTHER_AUTHORS=$(mktemp -t evbt.other.authors.XXXXXXXXXX)

function finish {
    rm -f $TMP $TMP_AUTHORS $TMP_OTHER_AUTHORS
}
trap finish EXIT

cat > $TMP <<EOF
Format: https://www.debian.org/doc/packaging-manuals/copyright-format/1.0/
Upstream-Name: evbt
Source: https://github.com/viklauverk/evbt
Upstream-Contact: Fredrik Öhrström <fredrik.ohrstrom@viklauverk.com>

Files: *
Copyright: 2023-2024 Viklauverk AB <fredrik.ohrstrom@viklauverk.com>
License: AGPL-3+

EOF

SOURCES="$(find src/main -type f | sort)"

for f in $SOURCES
do
    cat $f | grep "Copyright (C)" | sed 's/.*Copyright (C) *//g' > $TMP_AUTHORS
    cat $TMP_AUTHORS | grep -v Viklauverk > $TMP_OTHER_AUTHORS
    cops=$(cat $TMP_AUTHORS | tr '\n' '|' | \
               sed 's/[0-9][0-9][0-9][0-9]-//' | \
               sed 's/ ([^)]*)//g' | \
               sed -z 's/|$//g' | \
               sed 's/|/\n           /g')
    if grep -q -i "copyright (c) .*agpl-3.0-or-later" $f
    then
        license="AGPL-3+"
    elif grep -q -i "copyright (C) .*gpl-3.0-or-later" $f
    then
        license="GPL-3+"
    elif grep -q -i "copyright (C) .*CC0" $f
    then
        license="CC0"
    elif grep -q -i "copyright (C) .*MIT" $f
    then
        license="MIT"
    else
        echo "Unknown license in file: $f"
        exit 1
    fi

    if [ -s $TMP_OTHER_AUTHORS ]
    then
        {
        echo "Files: $f"
        echo "Copyright: $cops"
        echo "License: $license"
        echo ""
        } >> $TMP
    fi
done

cat >> $TMP <<EOF
License: GPL-3+
 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 3 of the License, or
 (at your option) any later version.
 .
 On Debian systems, the complete text of the GNU General Public License
 version 3 can be found in file "/usr/share/common-licenses/GPL-3".

License: AGPL-3+
/*
 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 .
 On Debian systems, the complete text of the GNU General Public License
 version 3 can be found in file "/usr/share/common-licenses/AGPL-3".

License: MIT
 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.

License: CC0
 The authors, and therefore would be copyright holders, have as much
 as possible relinguished their copyright to the public domain.
EOF

mv $TMP $1
chmod 644 $1

echo "Created copyright file: $1"
