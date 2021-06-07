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

## Library
This project tests extracting information out
from a machine through parameters prefixed with out_.

[Library.pdf](Library.pdf)

## SetComprehensions
Set comprehension syntax is the most complex part of the Event-B grammar.
This project tests all the ways of expressing set comprehension.

It also demonstrates that a machine variable can be overridden with
a non-free variable in a set comprehension.

[SetComprehensions.pdf](SetComprehensions.pdf)

## SquareRoot
This Event-B system is based on a model that appeared in the book:
System Modelling & Design Using Event-B by Ken Robinson.

This project implements an integer square root algorithm. The
algorithm performs a binary search of a value x such that
x*x = input, ie x will become the square root.


[SquareRoot.pdf](SquareRoot.pdf)

## TypingTests
I assume that typing can be made arbitrarily smart, however
I do not yet know the limits of how much typing Rodin can do.

For sure both evbt and Rodin does explicit typing based on
statements like: `x∈N` `alfa∈N→BOOL` or `p∈STAFF`

But Rodin also does implicit typing based on operations. For example:

```
@inv1 alfa ∈ ℕ⇸BOOL
@inv2 beta ∩ ran(alfa) = ∅
```

The disjunction forces the type of beta to be the same as the type of ran(alfa) ie ℕ.

```
@inv3 x ∈ ℕ
@inv4 x+y=7
```

The addition forces Rodin the type of y to be ℤ (not ℕ!!)

This projects tests the extent of implicit typing implemented so far in evbt.

[TypingTests.pdf](TypingTests.pdf)

## Vectors
This project tests code generation for vectors.

[Vectors.pdf](Vectors.pdf)

## WellCommented
This project is used to verify that rendering of complicated and long comments work.

[WellCommented.pdf](WellCommented.pdf)

