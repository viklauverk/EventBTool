# Models used for testing evbt.

## Bridge
This project tests extending events.

[Bridge.pdf](Bridge.pdf)

## CoffeeClub
This Event-B system is based on a model that appeared in the book:
System Modelling & Design Using Event-B by Ken Robinson.

It illustrates how a an abstract machine describes
the consistency of a single bank account. The refinement
then add multiple membership accounts while still
maintaining the single bank account.

It also illustrates the use of a witness to drop (or replace)
a variable when refining an event.


[CoffeeClub.pdf](CoffeeClub.pdf)

## Elevator
A machine for testing the basics of code generation.
It can move an elevator up and down between limits
specified by constants.

[Elevator.pdf](Elevator.pdf)

## ExtendsMultipleContexts
Test extending from multiple contexts.

[ExtendsMultipleContexts.pdf](ExtendsMultipleContexts.pdf)

## Library
This project tests extracting information out
from a machine through parameters prefixed with out_.

[Library.pdf](Library.pdf)

## Projections
Test using projections to get the left and right parts of pairs.

[Projections.pdf](Projections.pdf)

## ProofFailures
A machine with unproven, reviewed and manually proven POs.

[ProofFailures.pdf](ProofFailures.pdf)

## SetComprehensions
Set comprehension syntax is the most complex part of the Event-B grammar.
This project tests all the ways of expressing set comprehension.

It also demonstrates that a machine variable can be overridden with
a non-free variable in a set comprehension.

[SetComprehensions.pdf](SetComprehensions.pdf)

## SimpleTheoryTest
This model tests a machine that uses a theory that in turn uses another theory.


[SimpleTheoryTest.pdf](SimpleTheoryTest.pdf)

## SquareRoot
This Event-B system is based on a model that appeared in the book:
System Modelling & Design Using Event-B by Ken Robinson.

This project implements an integer square root algorithm. The
algorithm performs a binary search of a value x such that
x*x = input, ie x will become the square root.


[SquareRoot.pdf](SquareRoot.pdf)

## TypingTests
Event-B performs type checking of formulas and variables. Within evbt
these types are called checked types. Such checked types can be used for
implementing a variable but they have neither limits on integers nor
restrictions on the sets. Thus the generated code is not very efficient.

Therefore evbt will try to deduce suitable implementation types to be
able use an efficient map for a partial function or a vector for a
full function with a domain 1..100.

If no implementation type can be found, then evbt will fall back
to using the checked type for implementation.

[TypingTests.pdf](TypingTests.pdf)

## Vectors
This project tests code generation for vectors.

[Vectors.pdf](Vectors.pdf)

## WellCommented
This project is used to verify that rendering of complicated and long comments work.

[WellCommented.pdf](WellCommented.pdf)

