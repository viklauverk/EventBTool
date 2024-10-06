/*
 Copyright (C) 2021 Viklauverk AB (agpl-3.0-or-later)

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

package com.viklauverk.evbt.core;

import static com.viklauverk.evbt.core.NodeType.*;

public enum Node
{
    // 1) is_predicate 2) is_expression 3) is_set 4) is_data_type 5) is_variable 6) is_constant 7) is_symbol 8) is_relation 9) is_function

    NONE(0), // No element.
    TRUE(PRE.bit),  // The predicate true.
    FALSE(PRE.bit), // The predicate false.

    // First basic symbols are expressions
    NUMBER(EXP.bit), // 1, 2, 3 etc
    PREDICATE_SYMBOL(PRE.bit | SYM.bit), // A symbol representing a predicate, eg P,Q or R
    EXPRESSION_SYMBOL(EXP.bit | SYM.bit), // A symbol representing an expression, eg E,F or G
    SET_SYMBOL(SET.bit | SYM.bit), // A symbol representing any set, eg S,T or U
    VARIABLE_SYMBOL(EXP.bit | VAR.bit | SYM.bit), // A symbol representing a variable, eg x,y,z,w,x1,floor,speed
    VARIABLE_PRIM_SYMBOL(EXP.bit | VAR.bit | SYM.bit), // A prim variable symbol, eg x',y',floor'
    VARIABLE_NONFREE_SYMBOL(EXP.bit | VAR.bit | SYM.bit), // A symbol capture by ! or #, ie declared inside the predicate.
    CONSTANT_SYMBOL(CON.bit | SYM.bit), // A constant symbol: set element, constant function, eg c,d,f
    ANY_SYMBOL(SYM.bit), // Symbol that matches any other element, eg A,B,C.
    NUMBER_SYMBOL(SYM.bit), // Symbol that matches a number.
    POLYMORPHIC_DATA_TYPE_SYMBOL(SET.bit | PDT.bit | SYM.bit), // A symbol representing a data type, eg H List, Seq
    CONSTRUCTOR_SYMBOL(CNS.bit | SYM.bit), // A constructor symbol: nil cons
    DESTRUCTOR_SYMBOL(DES.bit | SYM.bit), // A destructor symbol: head tail
    OPERATOR_INFIX_PREDICATE_SYMBOL(OPE.bit | SYM.bit), // An operator symbol: same lessThan
    OPERATOR_INFIX_EXPRESSION_SYMBOL(OPE.bit | SYM.bit), // An operator symbol: merge minus
    OPERATOR_PREFIX_PREDICATE_SYMBOL(OPE.bit | SYM.bit), // An operator symbol: same(a,b) lessThan(a,b)
    OPERATOR_PREFIX_EXPRESSION_SYMBOL(OPE.bit | SYM.bit), // An operator symbol: merge(a,b) minus(a,b)

    LIST_OF_VARIABLES(0), // x,y,z := 1,2,3
    LIST_OF_NONFREE_VARIABLES(0), // In a forall ∀x,y,z· or exists ∃a,b· the variables are such a list.
    LIST_OF_EXPRESSIONS(0), // In a partition {a},{b},{c} is a list of expressions (sets).
    LIST_OF_PREDICATES(0), // For the api formila for a constructor or operator, such as: cx(x:NAT,y:BOOL)
    LIST_OF_TYPES(0), // In a DataType Seq(NAT) Map(NAT,INT)

    PARENTHESISED_PREDICATE(PRE.bit), // A predicate wrapped in parenthesis.
    PARENTHESISED_EXPRESSION(EXP.bit), // An expression wrapped in parenthesis.

    // Propositional logic
    CONJUNCTION(PRE.bit),
    IMPLICATION(PRE.bit),
    NEGATION(PRE.bit),
    DISJUNCTION(PRE.bit),
    EQUIVALENCE(PRE.bit),

    // Addition of substitution to preds, vars and expressions
    BECOME_EQ(0),    // x:=E
    BECOME_EQ_FUNC_APP(0), // x(t):=E
    BECOME_IN(0),    // x::S
    BECOME_SUCH(0),  // x:|=P
    APPLICATION(0),  // [x:=E]P

    // Adding quantifiers for predicate logic
    UNIVERSALQ(PRE.bit),
    EXISTENTIALQ(PRE.bit),

    // Expressions can be equals in predicates
    EQUALS(PRE.bit),
    NOT_EQUALS(PRE.bit),
    LESS_THAN(PRE.bit),
    GREATER_THAN(PRE.bit),
    LESS_THAN_OR_EQUAL(PRE.bit),
    GREATER_THAN_OR_EQUAL(PRE.bit),

    // A choice from a set makes an expression
    CHOICE(EXP.bit),

    // Set membership is a predicate
    MEMBERSHIP(PRE.bit),
    NOT_MEMBERSHIP(PRE.bit),
    SUBSET(PRE.bit),
    NOT_SUBSET(PRE.bit),
    STRICT_SUBSET(PRE.bit),
    NOT_STRICT_SUBSET(PRE.bit),
    FINITE(PRE.bit),
    OF_TYPE(PRE.bit),

    // Partition is a special case that generates a lot of predicates.
    PARTITION(0),

    // Cardinality is a natural number aka an expression
    CARDINALITY(EXP.bit),

    // These create new sets, and thus expressions.
    CARTESIAN_PRODUCT(EXP.bit | SET.bit),
    POWER_SET(EXP.bit | SET.bit),
    POWER1_SET(EXP.bit | SET.bit),
    G_UNION(EXP.bit | SET.bit),
    G_INTER(EXP.bit | SET.bit),
    Q_UNION(EXP.bit | SET.bit),
    Q_INTER(EXP.bit | SET.bit),
    DOMAIN(EXP.bit | SET.bit),
    RANGE(EXP.bit | SET.bit),
    SET_UNION(EXP.bit | SET.bit),
    SET_INTERSECTION(EXP.bit | SET.bit),
    SET_MINUS(EXP.bit | SET.bit),
    SET_COMPREHENSION(EXP.bit | SET.bit),
    SET_COMPREHENSION_SPECIAL(EXP.bit | SET.bit),

    LAMBDA_ABSTRACTION(EXP.bit | SET.bit),

    // S <-> T   Any ordered pair is allowed.
    RELATION(EXP.bit | SET.bit | REL.bit),

    // S <<-> T  All elements in S are part of at least one pair.
    TOTAL_RELATION(EXP.bit | SET.bit | REL.bit),

    // S <->> T  All elements in T are part of at least one pair.
    SURJECTIVE_RELATION(EXP.bit | SET.bit | REL.bit),

    // S <<->> T All elements in S and T are part of at least one pair.
    SURJECTIVE_TOTAL_RELATION(EXP.bit | SET.bit | REL.bit),

    // S +-> T   An element in S can only be part of a single pair.
    PARTIAL_FUNCTION(EXP.bit | SET.bit | REL.bit | FUN.bit),

    // S --> T   Every element in S is in a single pair.
    TOTAL_FUNCTION(EXP.bit | SET.bit | REL.bit | FUN.bit),

    // S >+> T   A partial function where each element in T can only be part of a single pair. One-to-One relations.
    PARTIAL_INJECTION(EXP.bit | SET.bit | REL.bit | FUN.bit),

    // S >-> T   A partial function where all elements in S are part of a single pair.
    TOTAL_INJECTION(EXP.bit | SET.bit | REL.bit | FUN.bit),

    // S +->> T  A partial function where all elements in T are part of a single pair. Onto relations
    PARTIAL_SURJECTION(EXP.bit | SET.bit | REL.bit | FUN.bit),

    // S -->> T  A partial function where all elements in T are part of a single pair.
    TOTAL_SURJECTION(EXP.bit | SET.bit | REL.bit | FUN.bit),

    // S >->> T  All elements in S and T are in uniqe pairs. Every element has its own inverse.
    TOTAL_BIJECTION(EXP.bit | SET.bit | REL.bit | FUN.bit),

    // S ; T  Forward composition
    FORWARD_COMPOSITION(EXP.bit | SET.bit | REL.bit | FUN.bit),

    // S circ T  Backward composition
    BACKWARD_COMPOSITION(EXP.bit | SET.bit | REL.bit | FUN.bit),

    // S <| r  Domain restriction
    DOMAIN_RESTRICTION(EXP.bit | SET.bit | REL.bit | FUN.bit),

    // S <<| r  Domain subtraction
    DOMAIN_SUBTRACTION(EXP.bit | SET.bit | REL.bit | FUN.bit),

    // r |> S  Range restriction
    RANGE_RESTRICTION(EXP.bit | SET.bit | REL.bit | FUN.bit),

    // r |>> S Range subtraction
    RANGE_SUBTRACTION(EXP.bit | SET.bit | REL.bit | FUN.bit),

    INVERT(EXP.bit | SET.bit | REL.bit | FUN.bit),
    RELATION_IMAGE(EXP.bit | SET.bit | REL.bit | FUN.bit),
    OVERRIDE(EXP.bit | SET.bit | REL.bit | FUN.bit),
    DIRECT_PRODUCT(EXP.bit | SET.bit | REL.bit | FUN.bit),
    PARALLEL_PRODUCT(EXP.bit | SET.bit | REL.bit | FUN.bit),

    UP_TO(EXP.bit | SET.bit),

    FUNC_APP(EXP.bit),
    FUNC_INV_APP(EXP.bit),

    // These are pre-defined sets
    EMPTY_SET(EXP.bit | SET.bit),
    ID_SET(EXP.bit | SET.bit),
    NAT_SET(EXP.bit | SET.bit),
    NAT1_SET(EXP.bit | SET.bit),
    INT_SET(EXP.bit | SET.bit),
    BOOL_SET(EXP.bit | SET.bit),
    ENUMERATED_SET(EXP.bit | SET.bit), // { alfa, beta }

    // These are operations on numbers.
    UNARY_MINUS(EXP.bit),
    ADDITION(EXP.bit),
    SUBTRACTION(EXP.bit),
    MULTIPLICATION(EXP.bit),
    DIVISION(EXP.bit),
    MODULO(EXP.bit),
    EXPONENTIATION(EXP.bit),
    MINIMUM(EXP.bit),
    MAXIMUM(EXP.bit),

    // Translate a predicate into a BOOL expression.
    TEST_BOOL(EXP.bit),

    MAPSTO(EXP.bit),
    PRJ1(EXP.bit),
    PRJ2(EXP.bit);

    private final int bits_;

    Node(int bits)
    {
        bits_ = bits;
    }

    boolean isPredicate() { return (bits_ & NodeType.PRE.bit) != 0; }
    boolean isExpression() { return (bits_ & NodeType.EXP.bit) != 0; }
    boolean isSet() { return (bits_ & NodeType.SET.bit) != 0; }
    boolean isVariable() { return (bits_ & NodeType.VAR.bit) != 0; }
    boolean isConstant() { return (bits_ & NodeType.CON.bit) != 0; }
    boolean isSymbol() { return (bits_ & NodeType.SYM.bit) != 0; }
    boolean isRelation() { return (bits_ & NodeType.REL.bit) != 0; }
    boolean isFunction() { return (bits_ & NodeType.FUN.bit) != 0; }
    boolean isPolymorphicDataType() { return (bits_ & NodeType.PDT.bit) != 0; }
    boolean isConstructor() { return (bits_ & NodeType.CNS.bit) != 0; }
    boolean isDestructor() { return (bits_ & NodeType.DES.bit) != 0; }
    boolean isOperator() { return (bits_ & NodeType.OPE.bit) != 0; }
}
