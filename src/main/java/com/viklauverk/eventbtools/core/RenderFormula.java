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

import java.util.List;
import java.util.LinkedList;

import java.util.Map;
import java.util.HashMap;

import com.viklauverk.eventbtools.core.Formula;

public class RenderFormula implements FormulaVisitor
{
    private static Log log = LogModule.lookup("renderformula");

    private static Map<Formula,Boolean> new_line_before_ = new HashMap<>();
    private static Map<Formula,Boolean> new_line_after_ = new HashMap<>();

    private SymbolTable symbols_;
    private Canvas canvas_ = null;

    RenderFormula(Canvas canvas)
    {
        canvas_ = canvas;
    }

    public Canvas cnvs() { return canvas_; }

    public void enterNode(Node p) {}
    public void exitNode(Node p) {}
    public void setSymbolTable(SymbolTable st) { symbols_ = st; }

    public SymbolTable symbols() { return symbols_; }

    public Formula visit_BECOME_EQ(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_BECOME_EQ_FUNC_APP(Formula i) { visitChildren(i, ()->{}); return i; }
    public Formula visit_BECOME_IN(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_BECOME_SUCH(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_FALSE(Formula i) { return i; }
    public Formula visit_NUMBER(Formula i) { return i; }
    public Formula visit_TRUE(Formula i) { return i; }
    public Formula visit_ANY_SYMBOL(Formula i) { return i; }
    public Formula visit_NUMBER_SYMBOL(Formula i) { return i; }
    public Formula visit_PREDICATE_SYMBOL(Formula i) { return i; }
    public Formula visit_EXPRESSION_SYMBOL(Formula i) { return i; }
    public Formula visit_SET_SYMBOL(Formula i) { return i; }
    public Formula visit_VARIABLE_SYMBOL(Formula i) { return i; }
    public Formula visit_VARIABLE_PRIM_SYMBOL(Formula i) { return i; }
    public Formula visit_VARIABLE_NONFREE_SYMBOL(Formula i) { return i; }
    public Formula visit_CONSTANT_SYMBOL(Formula i) { return i; }
    public Formula visit_APPLICATION(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_PARENTHESISED_PREDICATE(Formula i) { visitChild(i); return i; }
    public Formula visit_PARENTHESISED_EXPRESSION(Formula i) { visitChild(i); return i; }
    public Formula visit_CONJUNCTION(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_IMPLICATION(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_NEGATION(Formula i) { visitChild(i); return i; }
    public Formula visit_DISJUNCTION(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_EQUIVALENCE(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_UNIVERSALQ(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_EXISTENTIALQ(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_EQUALS(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_NOT_EQUALS(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_LESS_THAN(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_GREATER_THAN(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_LESS_THAN_OR_EQUAL(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_GREATER_THAN_OR_EQUAL(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_CHOICE(Formula i) { visitChild(i); return i; }
    public Formula visit_MEMBERSHIP(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_NOT_MEMBERSHIP(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_SUBSET(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_STRICT_SUBSET(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_NOT_SUBSET(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_NOT_STRICT_SUBSET(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_FINITE(Formula i) { visitChild(i); return i; }
    public Formula visit_PARTITION(Formula i) { visitChild(i); return i; }
    public Formula visit_OF_TYPE(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_CARTESIAN_PRODUCT(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_RELATION(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_TOTAL_RELATION(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_SURJECTIVE_RELATION(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_SURJECTIVE_TOTAL_RELATION(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_PARTIAL_FUNCTION(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_TOTAL_FUNCTION(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_PARTIAL_INJECTION(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_TOTAL_INJECTION(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_PARTIAL_SURJECTION(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_TOTAL_SURJECTION(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_TOTAL_BIJECTION(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_FORWARD_COMPOSITION(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_BACKWARD_COMPOSITION(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_DOMAIN_RESTRICTION(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_DOMAIN_SUBTRACTION(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_RANGE_RESTRICTION(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_RANGE_SUBTRACTION(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_INVERT(Formula i) { visitChild(i);  return i; }
    public Formula visit_RELATION_IMAGE(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_OVERRIDE(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_DIRECT_PRODUCT(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_PARALLEL_PRODUCT(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_POWER_SET(Formula i) { visitChild(i);  return i; }
    public Formula visit_POWER1_SET(Formula i) { visitChild(i); return i; }
    public Formula visit_G_UNION(Formula i) { visitChild(i); return i; }
    public Formula visit_G_INTER(Formula i) { visitChild(i); return i; }
    public Formula visit_Q_UNION(Formula i) { visitChild(i); return i; }
    public Formula visit_Q_INTER(Formula i) { visitChild(i); return i; }
    public Formula visit_DOMAIN(Formula i) { visitChild(i); return i; }
    public Formula visit_RANGE(Formula i) { visitChild(i); return i; }
    public Formula visit_SET_UNION(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_SET_INTERSECTION(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_SET_MINUS(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_LAMBDA_ABSTRACTION(Formula i) { visitChildNum(i,0);visitChildNum(i,1);visitChildNum(i,2); return i; }
    public Formula visit_SET_COMPREHENSION(Formula i) { visitChildNum(i,0);visitChildNum(i,1);visitChildNum(i,2); return i; }
    public Formula visit_SET_COMPREHENSION_SPECIAL(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_SET_COMPREHENSION_SPECIAL_CASE(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_ENUMERATED_SET(Formula i) { visitChildren(i, ()->{}); return i; }
    public Formula visit_LIST_VARIABLES(Formula i) { visitChildren(i, ()->{}); return i; }
    public Formula visit_LIST_OF_VARIABLES(Formula i) { visitChildren(i, ()->{}); return i; }
    public Formula visit_LIST_OF_NONFREE_VARIABLES(Formula i) { visitChildren(i, ()->{}); return i; }
    public Formula visit_LIST_OF_EXPRESSIONS(Formula i) { visitChildren(i, ()->{}); return i; }
    public Formula visit_ADDITION(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_SUBTRACTION(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_UNARY_MINUS(Formula i) { visitChild(i); return i; }
    public Formula visit_MULTIPLICATION(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_DIVISION(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_MODULO(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_EXPONENTIATION(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_MINIMUM(Formula i) { visitChild(i); return i; }
    public Formula visit_MAXIMUM(Formula i) { visitChild(i); return i; }
    public Formula visit_TEST_BOOL(Formula i) { visitChild(i); return i; }
    public Formula visit_CARDINALITY(Formula i) { visitChild(i); return i; }
    public Formula visit_UP_TO(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_EMPTY_SET(Formula i) { return i; }
    public Formula visit_ID_SET(Formula i) { return i; }
    public Formula visit_PRJ1(Formula i) { return i; }
    public Formula visit_PRJ2(Formula i) { return i; }
    public Formula visit_NAT_SET(Formula i) {  return i; }
    public Formula visit_NAT1_SET(Formula i) { return i; }
    public Formula visit_INT_SET(Formula i) { return i; }
    public Formula visit_BOOL_SET(Formula i) { return i; }
    public Formula visit_MAPSTO(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_FUNC_APP(Formula i) { visitLeft(i); visitRight(i); return i; }
    public Formula visit_FUNC_INV_APP(Formula i) { visitLeft(i); visitRight(i); return i; }

    public boolean add_types_ = false;

    public void addTypes()
    {
        add_types_ = true;
    }

    Formula startVisiting(Formula i)
    {
        Formula ii = innerVisit(i);
        return ii;
    }

    Formula innerVisit(Formula i)
    {
        if (i == null) return null;
        Node s = i.node();

        enterNode(s);
        switch (s)
        {
        case BECOME_EQ: i = visit_BECOME_EQ(i); break;
        case BECOME_EQ_FUNC_APP: i = visit_BECOME_EQ_FUNC_APP(i); break;
        case BECOME_IN: i = visit_BECOME_IN(i); break;
        case BECOME_SUCH: i = visit_BECOME_SUCH(i); break;
        case FALSE: i = visit_FALSE(i); break;
        case NUMBER: i = visit_NUMBER(i); break;
        case TRUE: i = visit_TRUE(i); break;
        case ANY_SYMBOL: i = visit_ANY_SYMBOL(i); break;
        case NUMBER_SYMBOL: i = visit_NUMBER_SYMBOL(i); break;
        case PREDICATE_SYMBOL: i = visit_PREDICATE_SYMBOL(i); break;
        case EXPRESSION_SYMBOL: i = visit_EXPRESSION_SYMBOL(i); break;
        case SET_SYMBOL: i = visit_SET_SYMBOL(i); break;
        case VARIABLE_SYMBOL: i = visit_VARIABLE_SYMBOL(i); break;
        case VARIABLE_PRIM_SYMBOL: i = visit_VARIABLE_PRIM_SYMBOL(i); break;
        case VARIABLE_NONFREE_SYMBOL: i = visit_VARIABLE_NONFREE_SYMBOL(i); break;
        case CONSTANT_SYMBOL: i = visit_CONSTANT_SYMBOL(i); break;
        case APPLICATION: i = visit_APPLICATION(i); break;
        case PARENTHESISED_PREDICATE: i = visit_PARENTHESISED_PREDICATE(i); break;
        case PARENTHESISED_EXPRESSION: i = visit_PARENTHESISED_EXPRESSION(i); break;
        case CONJUNCTION: i = visit_CONJUNCTION(i); break;
        case IMPLICATION: i = visit_IMPLICATION(i); break;
        case NEGATION: i = visit_NEGATION(i); break;
        case DISJUNCTION: i = visit_DISJUNCTION(i); break;
        case EQUIVALENCE: i = visit_EQUIVALENCE(i); break;
        case UNIVERSALQ: i = visit_UNIVERSALQ(i); break;
        case EXISTENTIALQ: i = visit_EXISTENTIALQ(i); break;
        case EQUALS: i = visit_EQUALS(i); break;
        case NOT_EQUALS: i = visit_NOT_EQUALS(i); break;
        case LESS_THAN: i = visit_LESS_THAN(i); break;
        case GREATER_THAN: i = visit_GREATER_THAN(i); break;
        case LESS_THAN_OR_EQUAL: i = visit_LESS_THAN_OR_EQUAL(i); break;
        case GREATER_THAN_OR_EQUAL: i = visit_GREATER_THAN_OR_EQUAL(i); break;
        case CHOICE: i = visit_CHOICE(i); break;
        case MEMBERSHIP: i = visit_MEMBERSHIP(i); break;
        case NOT_MEMBERSHIP: i = visit_NOT_MEMBERSHIP(i); break;
        case SUBSET: i = visit_SUBSET(i); break;
        case STRICT_SUBSET: i = visit_STRICT_SUBSET(i); break;
        case NOT_SUBSET: i = visit_NOT_SUBSET(i); break;
        case NOT_STRICT_SUBSET: i = visit_NOT_STRICT_SUBSET(i); break;
        case FINITE: i = visit_FINITE(i); break;
        case PARTITION: i = visit_PARTITION(i); break;
        case OF_TYPE: i = visit_OF_TYPE(i); break;
        case CARTESIAN_PRODUCT: i = visit_CARTESIAN_PRODUCT(i); break;
        case RELATION: i = visit_RELATION(i); break;
        case TOTAL_RELATION: i = visit_TOTAL_RELATION(i); break;
        case SURJECTIVE_RELATION: i = visit_SURJECTIVE_RELATION(i); break;
        case SURJECTIVE_TOTAL_RELATION: i = visit_SURJECTIVE_TOTAL_RELATION(i); break;
        case PARTIAL_FUNCTION: i = visit_PARTIAL_FUNCTION(i); break;
        case TOTAL_FUNCTION: i = visit_TOTAL_FUNCTION(i); break;
        case PARTIAL_INJECTION: i = visit_PARTIAL_INJECTION(i); break;
        case TOTAL_INJECTION: i = visit_TOTAL_INJECTION(i); break;
        case PARTIAL_SURJECTION: i = visit_PARTIAL_SURJECTION(i); break;
        case TOTAL_SURJECTION: i = visit_TOTAL_SURJECTION(i); break;
        case TOTAL_BIJECTION: i = visit_TOTAL_BIJECTION(i); break;
        case FORWARD_COMPOSITION: i = visit_FORWARD_COMPOSITION(i); break;
        case BACKWARD_COMPOSITION: i = visit_BACKWARD_COMPOSITION(i); break;
        case DOMAIN_RESTRICTION: i = visit_DOMAIN_RESTRICTION(i); break;
        case DOMAIN_SUBTRACTION: i = visit_DOMAIN_SUBTRACTION(i); break;
        case RANGE_RESTRICTION: i = visit_RANGE_RESTRICTION(i); break;
        case RANGE_SUBTRACTION: i = visit_RANGE_SUBTRACTION(i); break;
        case INVERT: i = visit_INVERT(i); break;
        case RELATION_IMAGE: i = visit_RELATION_IMAGE(i); break;
        case OVERRIDE: i = visit_OVERRIDE(i); break;
        case DIRECT_PRODUCT: i = visit_DIRECT_PRODUCT(i); break;
        case PARALLEL_PRODUCT: i = visit_PARALLEL_PRODUCT(i); break;
        case POWER_SET: i = visit_POWER_SET(i); break;
        case POWER1_SET: i = visit_POWER1_SET(i); break;
        case G_UNION: i = visit_G_UNION(i); break;
        case G_INTER: i = visit_G_INTER(i); break;
        case Q_UNION: i = visit_Q_UNION(i); break;
        case Q_INTER: i = visit_Q_INTER(i); break;
        case DOMAIN: i = visit_DOMAIN(i); break;
        case RANGE: i = visit_RANGE(i); break;
        case SET_UNION: i = visit_SET_UNION(i); break;
        case SET_INTERSECTION: i = visit_SET_INTERSECTION(i); break;
        case SET_MINUS: i = visit_SET_MINUS(i); break;
        case LAMBDA_ABSTRACTION: i = visit_LAMBDA_ABSTRACTION(i); break;
        case SET_COMPREHENSION: i = visit_SET_COMPREHENSION(i); break;
        case SET_COMPREHENSION_SPECIAL: i = visit_SET_COMPREHENSION_SPECIAL(i); break;
        case ENUMERATED_SET: i = visit_ENUMERATED_SET(i); break;
        case CARDINALITY: i = visit_CARDINALITY(i); break;
        case LIST_OF_VARIABLES: i = visit_LIST_OF_VARIABLES(i); break;
        case LIST_OF_NONFREE_VARIABLES: i = visit_LIST_OF_NONFREE_VARIABLES(i); break;
        case LIST_OF_EXPRESSIONS: i = visit_LIST_OF_EXPRESSIONS(i); break;
        case ADDITION: i = visit_ADDITION(i); break;
        case UNARY_MINUS: i = visit_UNARY_MINUS(i); break;
        case SUBTRACTION: i = visit_SUBTRACTION(i); break;
        case MULTIPLICATION: i = visit_MULTIPLICATION(i); break;
        case DIVISION: i = visit_DIVISION(i); break;
        case MODULO: i = visit_MODULO(i); break;
        case EXPONENTIATION: i = visit_EXPONENTIATION(i); break;
        case MINIMUM: i = visit_MINIMUM(i); break;
        case MAXIMUM: i = visit_MAXIMUM(i); break;
        case TEST_BOOL: i = visit_TEST_BOOL(i); break;
        case UP_TO: i = visit_UP_TO(i); break;
        case EMPTY_SET: i = visit_EMPTY_SET(i); break;
        case ID_SET: i = visit_ID_SET(i); break;
        case PRJ1: i = visit_PRJ1(i); break;
        case PRJ2: i = visit_PRJ2(i); break;
        case NAT_SET: i = visit_NAT_SET(i); break;
        case NAT1_SET: i = visit_NAT1_SET(i); break;
        case INT_SET: i = visit_INT_SET(i); break;
        case BOOL_SET: i = visit_BOOL_SET(i); break;
        case MAPSTO: i = visit_MAPSTO(i); break;
        case FUNC_APP: i = visit_FUNC_APP(i); break;
        case FUNC_INV_APP: i = visit_FUNC_INV_APP(i); break;
        default: System.err.println("NOT IMPLEMENTED SWITCH FOR "+s); System.exit(1); break;
        }
        exitNode(s);
        return i;
    }

    Formula visitLeft(Formula f)
    {
        return innerVisit(f.left());
    }

    Formula visitRight(Formula f)
    {
        return innerVisit(f.right());
    }

    Formula visitChild(Formula f)
    {
        return innerVisit(f.child());
    }

    Formula visitChildNum(Formula f, int n)
    {
        return innerVisit(f.child(n));
    }

    Formula visitChildren(Formula f, Runnable inbetween)
    {
        for (int i = 0; i < f.numChildren(); ++i)
        {
            if (i > 0) inbetween.run();
            innerVisit(f.child(i));
        }
        return f;
    }

    public static void addNewLineHintBefore(Formula f)
    {
        log.trace("add new line hint before: %s", f);
        new_line_before_.put(f, true);
    }

    public static void addNewLineHintAfter(Formula f)
    {
        log.trace("add new line hint after: %s", f);
        new_line_after_.put(f, true);
    }

    public void insertNewLineBefore(Formula i)
    {
        cnvs().newlineInFormula();
    }

    public void insertNewLineAfter(Formula i)
    {
        cnvs().newlineInFormula();
    }

    public static boolean shouldAddNewLineAfter(Formula f)
    {
        Boolean b = new_line_after_.get(f);
        if (b == null || b == false) return false;
        return true;
    }

    public static boolean shouldAddNewLineBefore(Formula f)
    {
        Boolean b = new_line_before_.get(f);
        if (b == null || b == false) return false;
        return true;
    }

    void checkNewLineBefore(Formula i)
    {
        if (shouldAddNewLineBefore(i))
        {
            insertNewLineBefore(i);
        }
    }

    void checkNewLineAfter(Formula i)
    {
        if (shouldAddNewLineAfter(i))
        {
            insertNewLineAfter(i);
        }
    }

}
