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

package com.viklauverk.evbt.core.visitors;

import com.viklauverk.evbt.core.sys.Formula;

public interface FormulaVisitor
{
    void enterNode(Formula f);
    void exitNode(Formula f);
    Formula visit_BECOME_EQ(Formula i);
    Formula visit_BECOME_EQ_FUNC_APP(Formula i);
    Formula visit_BECOME_IN(Formula i);
    Formula visit_BECOME_SUCH(Formula i);
    Formula visit_FALSE(Formula i);
    Formula visit_NUMBER(Formula i);
    Formula visit_TRUE(Formula i);
    Formula visit_ANY_SYMBOL(Formula i);
    Formula visit_NUMBER_SYMBOL(Formula i);
    Formula visit_PREDICATE_SYMBOL(Formula i);
    Formula visit_EXPRESSION_SYMBOL(Formula i);
    Formula visit_SET_SYMBOL(Formula i);
    Formula visit_VARIABLE_SYMBOL(Formula i);
    Formula visit_VARIABLE_PRIM_SYMBOL(Formula i);
    Formula visit_VARIABLE_NONFREE_SYMBOL(Formula i);
    Formula visit_CONSTANT_SYMBOL(Formula i);
    Formula visit_POLYMORPHIC_DATA_TYPE_SYMBOL(Formula i);
    Formula visit_CONSTRUCTOR_SYMBOL(Formula i);
    Formula visit_DESTRUCTOR_SYMBOL(Formula i);
    Formula visit_OPERATOR_INFIX_PREDICATE_SYMBOL(Formula i);
    Formula visit_OPERATOR_INFIX_EXPRESSION_SYMBOL(Formula i);
    Formula visit_OPERATOR_PREFIX_PREDICATE_SYMBOL(Formula i);
    Formula visit_OPERATOR_PREFIX_EXPRESSION_SYMBOL(Formula i);
    Formula visit_APPLICATION(Formula i);
    Formula visit_PARENTHESISED_PREDICATE(Formula i);
    Formula visit_PARENTHESISED_EXPRESSION(Formula i);
    Formula visit_CONJUNCTION(Formula i);
    Formula visit_IMPLICATION(Formula i);
    Formula visit_NEGATION(Formula i);
    Formula visit_DISJUNCTION(Formula i);
    Formula visit_EQUIVALENCE(Formula i);
    Formula visit_UNIVERSALQ(Formula i);
    Formula visit_EXISTENTIALQ(Formula i);
    Formula visit_EQUALS(Formula i);
    Formula visit_NOT_EQUALS(Formula i);
    Formula visit_LESS_THAN(Formula i);
    Formula visit_GREATER_THAN(Formula i);
    Formula visit_LESS_THAN_OR_EQUAL(Formula i);
    Formula visit_GREATER_THAN_OR_EQUAL(Formula i);
    Formula visit_CHOICE(Formula i);
    Formula visit_MEMBERSHIP(Formula i);
    Formula visit_NOT_MEMBERSHIP(Formula i);
    Formula visit_SUBSET(Formula i);
    Formula visit_STRICT_SUBSET(Formula i);
    Formula visit_NOT_SUBSET(Formula i);
    Formula visit_NOT_STRICT_SUBSET(Formula i);
    Formula visit_FINITE(Formula i);
    Formula visit_PARTITION(Formula i);
    Formula visit_OF_TYPE(Formula i);
    Formula visit_CARTESIAN_PRODUCT(Formula i);
    Formula visit_RELATION(Formula i);
    Formula visit_TOTAL_RELATION(Formula i);
    Formula visit_SURJECTIVE_RELATION(Formula i);
    Formula visit_SURJECTIVE_TOTAL_RELATION(Formula i);
    Formula visit_PARTIAL_FUNCTION(Formula i);
    Formula visit_TOTAL_FUNCTION(Formula i);
    Formula visit_PARTIAL_INJECTION(Formula i);
    Formula visit_TOTAL_INJECTION(Formula i);
    Formula visit_PARTIAL_SURJECTION(Formula i);
    Formula visit_TOTAL_SURJECTION(Formula i);
    Formula visit_TOTAL_BIJECTION(Formula i);
    Formula visit_FORWARD_COMPOSITION(Formula i);
    Formula visit_BACKWARD_COMPOSITION(Formula i);

    Formula visit_DOMAIN_RESTRICTION(Formula i);
    Formula visit_DOMAIN_SUBTRACTION(Formula i);
    Formula visit_RANGE_RESTRICTION(Formula i);
    Formula visit_RANGE_SUBTRACTION(Formula i);

    Formula visit_INVERT(Formula i);
    Formula visit_RELATION_IMAGE(Formula i);
    Formula visit_OVERRIDE(Formula i);
    Formula visit_DIRECT_PRODUCT(Formula i);
    Formula visit_PARALLEL_PRODUCT(Formula i);

    Formula visit_POWER_SET(Formula i);
    Formula visit_POWER1_SET(Formula i);
    Formula visit_G_UNION(Formula i);
    Formula visit_G_INTER(Formula i);
    Formula visit_Q_UNION(Formula i);
    Formula visit_Q_INTER(Formula i);
    Formula visit_DOMAIN(Formula i);
    Formula visit_RANGE(Formula i);
    Formula visit_SET_UNION(Formula i);
    Formula visit_SET_INTERSECTION(Formula i);
    Formula visit_SET_MINUS(Formula i);
    Formula visit_LAMBDA_ABSTRACTION(Formula i);
    Formula visit_SET_COMPREHENSION(Formula i);
    Formula visit_SET_COMPREHENSION_SPECIAL(Formula i);
    Formula visit_ENUMERATED_SET(Formula i);
    Formula visit_LIST_OF_VARIABLES(Formula i);
    Formula visit_LIST_OF_NONFREE_VARIABLES(Formula i);
    Formula visit_LIST_OF_EXPRESSIONS(Formula i);
    Formula visit_LIST_OF_PREDICATES(Formula i);
    Formula visit_ADDITION(Formula i);
    Formula visit_SUBTRACTION(Formula i);
    Formula visit_MULTIPLICATION(Formula i);
    Formula visit_DIVISION(Formula i);
    Formula visit_MODULO(Formula i);
    Formula visit_EXPONENTIATION(Formula i);
    Formula visit_MINIMUM(Formula i);
    Formula visit_MAXIMUM(Formula i);
    Formula visit_TEST_BOOL(Formula i);
    Formula visit_UP_TO(Formula i);
    Formula visit_CARDINALITY(Formula i);
    Formula visit_EMPTY_SET(Formula i);
    Formula visit_ID_SET(Formula i);
    Formula visit_PRJ1(Formula i);
    Formula visit_PRJ2(Formula i);
    Formula visit_NAT_SET(Formula i);
    Formula visit_NAT1_SET(Formula i);
    Formula visit_INT_SET(Formula i);
    Formula visit_BOOL_SET(Formula i);
    Formula visit_MAPSTO(Formula i);
    Formula visit_FUNC_APP(Formula i);
    Formula visit_FUNC_INV_APP(Formula i);
    Formula visit_META(Formula i);
}
