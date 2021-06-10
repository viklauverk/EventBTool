/*
 Copyright (C) 2021 Viklauverk AB
 Author Fredrik Öhrström

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.viklauverk.eventbtools.core;

public enum Node
{
    // 1) is_predicate 2) is_expression 3) is_set 4) is_variable 5) is_constant 6) is_symbol 7) is_relation 8) is_function

    NONE(false, false, false, false, false, false, false, false), // No element.
    TRUE(true, false, false, false, false, false, false, false),  // The predicate true.
    FALSE(true, false, false, false, false, false, false, false), // The predicate false.

    // First basic symbols are expressions
    NUMBER(false, true, false, false, false, false, false, false),          // 1, 2, 3 etc
    PREDICATE_SYMBOL(true, false, false, false, false, true, false, false), // A symbol representing a predicate, eg P,Q or R
    EXPRESSION_SYMBOL(false, true, false, false, false, true, false, false),// A symbol representing an expression, eg E,F or G
    SET_SYMBOL(false, false, true, false, false, true, false, false),       // A symbol representing any set, eg S,T or U
    VARIABLE_SYMBOL(false, true, false, true, false, true, false, false),   // A symbol representing a variable, eg x,y,z,w,x1,floor,speed
    VARIABLE_PRIM_SYMBOL(false, true, false, true, false, true, false, false),   // A prim variable symbol, eg x',y',floor'
    VARIABLE_NONFREE_SYMBOL(false, true, false, true, false, true, false, false), // A symbol capture by ! or #, ie declared inside the predicate.
    CONSTANT_SYMBOL(false, false, false, false, true, true, false, false),  // A constant symbol: set element, constant function, eg c,d,f
    ANY_SYMBOL(false, false, false, false, false, true, false, false),      // Symbol that matches any other element, eg A,B,C.
    NUMBER_SYMBOL(false, false, false, false, false, true, false, false),   // Symbol that matches a number.

    LIST_OF_VARIABLES(false, false, false, false, false, false, false, false), // x,y,z := 1,2,3
    LIST_OF_NONFREE_VARIABLES(false, false, false, false, false, false, false, false), // In a forall ∀x,y,z· or exists ∃a,b· the variables are such a list.
    LIST_OF_EXPRESSIONS(false, false, false, false, false, false, false, false),      // In a partition {a},{b},{c} is a list of sets.

    PARENTHESISED_PREDICATE(true, false, false, false, false, false, false, false), // A predicate wrapped in parenthesis.
    PARENTHESISED_EXPRESSION(false, true, false, false, false, false, false, false), // An expression wrapped in parenthesis.

    // Propositional logic
    CONJUNCTION(true, false, false, false, false, false, false, false),
    IMPLICATION(true, false, false, false, false, false, false, false),
    NEGATION(true, false, false, false, false, false, false, false),
    DISJUNCTION(true, false, false, false, false, false, false, false),
    EQUIVALENCE(true, false, false, false, false, false, false, false),

    // Addition of substitution to preds, vars and expressions
    BECOME_EQ(false, false, false, false, false, false, false, false),    // x:=E
    BECOME_EQ_FUNC_APP(false, false, false, false, false, false, false, false), // x(t):=E
    BECOME_IN(false, false, false, false, false, false, false, false),    // x::S
    BECOME_SUCH(false, false, false, false, false, false, false, false),  // x:|=P
    APPLICATION(false, false, false, false, false, false, false, false),  // [x:=E]P

    // Adding quantifiers for predicate logic
    UNIVERSALQ(true, false, false, false, false, false, false, false),
    EXISTENTIALQ(true, false, false, false, false, false, false, false),

    // Expressions can be equals in predicates
    EQUALS(true, false, false, false, false, false, false, false),
    NOT_EQUALS(true, false, false, false, false, false, false, false),
    LESS_THAN(true, false, false, false, false, false, false, false),
    GREATER_THAN(true, false, false, false, false, false, false, false),
    LESS_THAN_OR_EQUAL(true, false, false, false, false, false, false, false),
    GREATER_THAN_OR_EQUAL(true, false, false, false, false, false, false, false),

    // A choice from a set makes an expression
    CHOICE(false, true, false, false, false, false, false, false),

    // Set membership is a predicate
    MEMBERSHIP(true, false, false, false, false, false, false, false),
    NOT_MEMBERSHIP(true, false, false, false, false, false, false, false),
    SUBSET(true, false, false, false, false, false, false, false),
    NOT_SUBSET(true, false, false, false, false, false, false, false),
    STRICT_SUBSET(true, false, false, false, false, false, false, false),
    NOT_STRICT_SUBSET(true, false, false, false, false, false, false, false),
    FINITE(true, false, false, false, false, false, false, false),
    OF_TYPE(true, false, false, false, false, false, false, false),

    // Partition is a special case that generates a lot of predicates.
    PARTITION(false, false, false, false, false, false, false, false),

    // Cardinality is a natural number aka an expression
    CARDINALITY(false, true, false, false, false, false, false, false),

    // These create new sets, and thus expressions.
    CARTESIAN_PRODUCT(false, true, true, false, false, false, false, false),
    POWER_SET(false, true, true, false, false, false, false, false),
    POWER1_SET(false, true, true, false, false, false, false, false),
    G_UNION(false, true, true, false, false, false, false, false),
    G_INTER(false, true, true, false, false, false, false, false),
    Q_UNION(false, true, true, false, false, false, false, false),
    Q_INTER(false, true, true, false, false, false, false, false),
    DOMAIN(false, true, true, false, false, false, false, false),
    RANGE(false, true, true, false, false, false, false, false),
    SET_UNION(false, true, true, false, false, false, false, false),
    SET_INTERSECTION(false, true, true, false, false, false, false, false),
    SET_MINUS(false, true, true, false, false, false, false, false),
    SET_COMPREHENSION(false, true, true, false, false, false, false, false),
    SET_COMPREHENSION_SPECIAL(false, true, true, false, false, false, false, false),

    LAMBDA_ABSTRACTION(false, true, true, false, false, false, false, false),

    // S <-> T   Any ordered pair is allowed.
    RELATION(false, true, true, false, false, false, true, false),

    // S <<-> T  All elements in S are part of at least one pair.
    TOTAL_RELATION(false, true, true, false, false, false, true, false),

    // S <->> T  All elements in T are part of at least one pair.
    SURJECTIVE_RELATION(false, true, true, false, false, false, true, false),

    // S <<->> T All elements in S and T are part of at least one pair.
    SURJECTIVE_TOTAL_RELATION(false, true, true, false, false, false, true, false),

    // S +-> T   An element in S can only be part of a single pair.
    PARTIAL_FUNCTION(false, true, true, false, false, false, true, true),

    // S --> T   Every element in S is in a single pair.
    TOTAL_FUNCTION(false, true, true, false, false, false, true, true),

    // S >+> T   A partial function where each element in T can only be part of a single pair. One-to-One relations.
    PARTIAL_INJECTION(false, true, true, false, false, false, true, true),

    // S >-> T   A partial function where all elements in S are part of a single pair.
    TOTAL_INJECTION(false, true, true, false, false, false, true, true),

    // S +->> T  A partial function where all elements in T are part of a single pair. Onto relations
    PARTIAL_SURJECTION(false, true, true, false, false, false, true, true),

    // S -->> T  A partial function where all elements in T are part of a single pair.
    TOTAL_SURJECTION(false, true, true, false, false, false, true, true),

    // S >->> T  All elements in S and T are in uniqe pairs. Every element has its own inverse.
    TOTAL_BIJECTION(false, true, true, false, false, false, true, true),

    // S ; T  Forward composition
    FORWARD_COMPOSITION(false, true, true, false, false, false, true, true),

    // S circ T  Backward composition
    BACKWARD_COMPOSITION(false, true, true, false, false, false, true, true),

    // S <| r  Domain restriction
    DOMAIN_RESTRICTION(false, true, true, false, false, false, true, true),

    // S <<| r  Domain subtraction
    DOMAIN_SUBTRACTION(false, true, true, false, false, false, true, true),

    // r |> S  Range restriction
    RANGE_RESTRICTION(false, true, true, false, false, false, true, true),

    // r |>> S Range subtraction
    RANGE_SUBTRACTION(false, true, true, false, false, false, true, true),

    INVERT(false, true, true, false, false, false, true, true),
    RELATION_IMAGE(false, true, true, false, false, false, true, true),
    OVERRIDE(false, true, true, false, false, false, true, true),
    DIRECT_PRODUCT(false, true, true, false, false, false, true, true),
    PARALLEL_PRODUCT(false, true, true, false, false, false, true, true),

    UP_TO(false, true, true, false, false, false, false, false),

    FUNC_APP(false, true, false, false, false, false, false, false),
    FUNC_INV_APP(false, true, false, false, false, false, false, false),

    // These are pre-defined sets
    EMPTY_SET(false, true, true, false, false, false, false, false),
    ID_SET(false, true, true, false, false, false, false, false),
    NAT_SET(false, true, true, false, false, false, false, false),
    NAT1_SET(false, true, true, false, false, false, false, false),
    INT_SET(false, true, true, false, false, false, false, false),
    BOOL_SET(false, true, true, false, false, false, false, false),
    ENUMERATED_SET(false, true, true, false, false, false, false, false), // { alfa, beta }

    // These are operations on numbers.
    UNARY_MINUS(false, true, false, false, false, false, false, false),
    ADDITION(false, true, false, false, false, false, false, false),
    SUBTRACTION(false, true, false, false, false, false, false, false),
    MULTIPLICATION(false, true, false, false, false, false, false, false),
    DIVISION(false, true, false, false, false, false, false, false),
    MODULO(false, true, false, false, false, false, false, false),
    EXPONENTIATION(false, true, false, false, false, false, false, false),
    MINIMUM(false, true, false, false, false, false, false, false),
    MAXIMUM(false, true, false, false, false, false, false, false),

    // Translate a predicate into a BOOL expression.
    TEST_BOOL(false, true, false, false, false, false, false, false),

    MAPSTO(false, true, false, false, false, false, false, false),
    PRJ1(false, true, false, false, false, false, false, false),
    PRJ2(false, true, false, false, false, false, false, false);

    private final boolean is_predicate_, is_expression_, is_set_, is_variable_, is_constant_, is_symbol_, is_relation_, is_function_;

    Node(boolean ip, boolean ie, boolean is, boolean iv, boolean ic, boolean iss, boolean ir, boolean isf)
    {
        is_predicate_ = ip;
        is_expression_ = ie;
        is_set_ = is;
        is_variable_ = iv;
        is_constant_ = ic;
        is_symbol_ = iss;
        is_relation_ = ir;
        is_function_ = isf;
    }

    boolean isPredicate() { return is_predicate_; }
    boolean isExpression() { return is_expression_; }
    boolean isSet() { return is_set_; }
    boolean isVariable() { return is_variable_; }
    boolean isConstant() { return is_constant_; }
    boolean isSymbol() { return is_symbol_; }
    boolean isRelation() { return is_relation_; }
    boolean isFunction() { return is_function_; }
}
