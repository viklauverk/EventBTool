## EVBT — an Event-B tool for code generation and documentation

The evbt (Event-B Tool) generates code and documentation from Event-B models
stored in a Rodin workspace. The models are developed using the Rodin tool.
[http://www.event-b.org/](http://www.event-b.org/)

The tool is still in beta (the current version 1.5.0, and any future version with an odd numbered
major release number, will be highly prone to radical changes and have a lot of bugs/missing features),
still it can be used to generate nice TeX documents.  It might generate the code you
are looking for, it might not, ie the tool is work in progress. :-)

## Download and install

The tool is a single binary `evbt`. It is distributed as a single executable jar file (ie
a jar file that starts with a Posix shell-script that starts java on the jar itself.
[Download latest release here!](https://github.com/viklauverk/EventBTool/releases/download/v1.5.0/evbt)

Do `chmod a+x evbt` and place evbt in your PATH and make sure you have Java 22 or later in your path as well.

You can also run: `java -jar evbt`

## To build from source.

Building is only tested in Ubuntu GNU/Linux. Clone then run `./configure ; make; sudo make install`

You need to have Java 22 or later installed and maven. To make the test-suite `make test` pass
you have to have xelatex installed and g++ and node (javascript).

# docgen - Generate documentation from your Rodin workspace directory

Run:
```
evbt docgen --tex workspace/YourGreatEventBSystem
```
to generate a tex document named `YourGreatEventBSystem.tex`

Then run:
```
xelatex YourGreatEventBSystem.tex
makeindex YourGreatEventBSystem.idx
xelatex YourGreatEventBSystem.tex
```
to generate a pdf with table of contents and an index.

Evbt parses the Event-B specifiction also for the document generation
and has therefore access to semantic knowledge about the
specification.  This knowledge is used to colorise and italicise the
parts of the components, such as constants, carrier sets and
variables.

You can tailor the output to remove labels:
```
evbt docgen --tex --docstyle=default,-labels workspace/YourGreatEventBSystem
```

## console - exploration of Event-B models.

The evbt tool also provides a console for exploration of Event-B models and formulas. You start
the console with:
```
evbt console
```
and you can now type for example: `yms.add.defaults` to fill the
symbol table with a few default symbols for predicates and variables.

You can now type:
```
eb.show.formula (P & x:BOOL) => Q
```
and it will parse and print:
```
(P ∧ x ∈ BOOL) ⇒ Q
```

If you start the console with a Rodin workspace as an argument:
`evbt console workspace/Library` then you can also print parts of the model with the command:
`eb.show.part "Library/events/whoBorrowsBook/guards"` or an invariant:
`eb.show.part "Library/invariants/inv3"`

## docmod - execute console commands within a tex document.

Run:
```
evbt docmod --tex your_article.tex updated_article.tex workspace/YourGreateEvebtBSystem
```

Then evbt will replace any occurences with `EVBT(...console command...)` with the
output from the console command and store this into updated_article.tex.

This is useful to generated rendered tex formulas without having to type actual tex commands,
for example: `EVBT(eb.show.formula x:BOOL)`

It is also useful when writing a document arguing why your requirements are fulfilled
by the invariants, since you do not need to copy the invariants/guards from the machine
into the document, you can simply link to the system, like this:
`EVBT(eb.show.part Elevator/events/enterDest/guards/grd_1)` and the particular guard
will be inserted into your document,

Read `tests/test_docmod.sh` for how it works.

## codegen - generate code from a specification.

The command `evbt codegen c++ workspace/Elevator/FinalRefinement.bum` will generate `Elevator.h` and `Elevator.cc`.
The header file declares a class with an interface to interact with the Event-B model. (Java and javascript support will be added as well.)

Event-B Tools comes with a seed of a library of contexts that can be automatically converted into code.
The EDK (Event-B Development Kit) will add support for floating point operations, strings and datetimes etc.

The contexts are named as such: EDK_FloatingPoint_v1 which means that if you use this context
in your Event-B specification, then evbt can generate proper code for its functions.

Odd version numbers of the contexts in the EDK are in development and flux, even version numbers supposed to be stable.

## License of the evbt source code.

```
Event-B Tools (EVBT) AGPL-3.0-or-later

Copyright (C) 2021 Viklauverk AB
Author Fredrik Öhrström

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
```

## License of the EDK when it is in included into your Event-B specification
and into any generated code.

```
Event-B Development Kit (EDK) BSD-2-Clause

Copyright 2021 Viklauverk AB
Author Fredrik Öhrström

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

1. Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
```
