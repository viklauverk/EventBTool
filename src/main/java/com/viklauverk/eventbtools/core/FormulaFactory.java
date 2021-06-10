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
import java.util.ArrayList;

public class FormulaFactory
{

    public static
    Formula newBecomeEQ(Formula var, Formula f)
    {
        return new Formula(Node.BECOME_EQ,
                           var,
                           f);
    }

    public static
    Formula newBecomeEQFuncApp(Formula var, Formula left, Formula right)
    {
        List<Formula> inners = new ArrayList<>();
        inners.add(var);
        inners.add(left);
        inners.add(right);

        return new Formula(Node.BECOME_EQ_FUNC_APP, inners);
    }

    public static
    Formula newBecomeIN(Formula var, Formula f)
    {
        return new Formula(Node.BECOME_IN,
                           var,
                           f);
    }

    public static
    Formula newBecomeSUCH(Formula var, Formula f)
    {
        return new Formula(Node.BECOME_SUCH,
                           var,
                           f);
    }

    public static
    Formula newTrue()
    {
        return new Formula(Node.TRUE);
    }

    public static
    Formula newFalse()
    {
        return new Formula(Node.FALSE);
    }

    public static
    Formula newNumber (String number_string)
    {
        // Normalize any unicode − into ascii minus -.
        int n = Integer.parseInt(number_string.replace("−", "-"));
        return new Formula(Node.NUMBER, n);
    }

    public static
    Formula newAnySymbol (String p)
    {
        return new Formula(Node.ANY_SYMBOL, Symbols.intern(p));
    }

    public static
    Formula newNumberSymbol (String p)
    {
        return new Formula(Node.NUMBER_SYMBOL, Symbols.intern(p));
    }

    public static
    Formula newPredicateSymbol (String p)
    {
        return new Formula(Node.PREDICATE_SYMBOL, Symbols.intern(p));
    }

    public static
    Formula newVariableSymbol (String v)
    {
        return new Formula(Node.VARIABLE_SYMBOL, Symbols.intern(v));
    }

    public static
    Formula newVariablePrimSymbol (String v)
    {
        return new Formula(Node.VARIABLE_PRIM_SYMBOL, Symbols.intern(v));
    }

    public static
    Formula newVariableNonFreeSymbol (String v)
    {
        return new Formula(Node.VARIABLE_NONFREE_SYMBOL, Symbols.intern(v));
    }

    public static
    Formula newExpressionSymbol (String e)
    {
        return new Formula(Node.EXPRESSION_SYMBOL, Symbols.intern(e));
    }

    public static
    Formula newConstantSymbol (String e)
    {
        return new Formula(Node.CONSTANT_SYMBOL, Symbols.intern(e));
    }

    public static
    Formula newSetSymbol (String s)
    {
        return new Formula(Node.SET_SYMBOL, Symbols.intern(s));
    }

    public static
    Formula newEmptySet()
    {
        return new Formula(Node.EMPTY_SET);
    }

    public static
    Formula newIdSet()
    {
        return new Formula(Node.ID_SET);
    }

    public static
    Formula newPrj1(Formula inner)
    {
        return new Formula(Node.PRJ1, inner);
    }

    public static
    Formula newPrj2(Formula inner)
    {
        return new Formula(Node.PRJ2, inner);
    }

    public static
    Formula newNATSet()
    {
        return new Formula(Node.NAT_SET);
    }

    public static
    Formula newNAT1Set()
    {
        return new Formula(Node.NAT1_SET);
    }

    public static
    Formula newINTSet()
    {
        return new Formula(Node.INT_SET);
    }

    public static
    Formula newBOOLSet()
    {
        return new Formula(Node.BOOL_SET);
    }

    public static
    Formula newUniversalQ(Formula list_of_variables, Formula predicate)
    {
        return new Formula(Node.UNIVERSALQ,
                           list_of_variables,
                           predicate);
    }

    public static
    Formula newExistentialQ(Formula list_of_variables, Formula predicate)
    {
        return new Formula(Node.EXISTENTIALQ,
                           list_of_variables,
                           predicate);
    }

    public static
    Formula newLambda(Formula list_of_variables, Formula predicate, Formula expression)
    {
        return new Formula(Node.LAMBDA_ABSTRACTION,
                           list_of_variables,
                           predicate,
                           expression);
    }

    public static
    Formula newSetComprehension(Formula list_of_variables, Formula predicate, Formula expression)
    {
        return new Formula(Node.SET_COMPREHENSION,
                           list_of_variables,
                           predicate,
                           expression);
    }

    public static
    Formula newSetComprehensionSpecial(Formula var, Formula predicate)
    {
        return new Formula(Node.SET_COMPREHENSION_SPECIAL, var, predicate);
    }

    public static
    Formula newParenthesisedPredicate(Formula inner)
    {
        return new Formula(Node.PARENTHESISED_PREDICATE, inner);
    }

    public static
    Formula newParenthesisedExpression(Formula inner)
    {
        return new Formula(Node.PARENTHESISED_EXPRESSION, inner);
    }

    public static
    Formula newConjunction(Formula left, Formula right)
    {
        return new Formula(Node.CONJUNCTION, left, right);
    }

    public static
    Formula newImplication(Formula left, Formula right)
    {
        return new Formula(Node.IMPLICATION, left, right);
    }

    public static
    Formula newNegation(Formula left)
    {
        return new Formula(Node.NEGATION, left);
    }

    public static
    Formula newDisjunction (Formula left, Formula right)
    {
        return new Formula(Node.DISJUNCTION, left, right);
    }

    public static
    Formula newEquivalence (Formula left, Formula right)
    {
        return new Formula(Node.EQUIVALENCE, left, right);
    }

    public static
    Formula newEquals (Formula left, Formula right)
    {
        return new Formula(Node.EQUALS, left, right);
    }

    public static
    Formula newNotEquals (Formula left, Formula right)
    {
        return new Formula(Node.NOT_EQUALS, left, right);
    }

    public static
    Formula newLessThan(Formula left, Formula right)
    {
        return new Formula(Node.LESS_THAN, left, right);
    }

    public static
    Formula newGreaterThan(Formula left, Formula right)
    {
        return new Formula(Node.GREATER_THAN, left, right);
    }

    public static
    Formula newLessThanOrEqual(Formula left, Formula right)
    {
        return new Formula(Node.LESS_THAN_OR_EQUAL, left, right);
    }

    public static
    Formula newGreaterThanOrEqual(Formula left, Formula right)
    {
        return new Formula(Node.GREATER_THAN_OR_EQUAL, left, right);
    }

    public static
    Formula newApplication(Formula left, Formula right)
    {
        return new Formula(Node.APPLICATION, left, right);
    }

    public static
    Formula newSetMembership (Formula left, Formula right)
    {
        return new Formula(Node.MEMBERSHIP, left, right);
    }

    public static
    Formula newSetNotMembership (Formula left, Formula right)
    {
        return new Formula(Node.NOT_MEMBERSHIP, left, right);
    }

    public static
    Formula newSubSet(Formula left, Formula right)
    {
        return new Formula(Node.SUBSET, left, right);
    }

    public static
    Formula newNotSubSet(Formula left, Formula right)
    {
        return new Formula(Node.NOT_SUBSET, left, right);
    }

    public static
    Formula newStrictSubSet(Formula left, Formula right)
    {
        return new Formula(Node.STRICT_SUBSET, left, right);
    }

    public static
    Formula newNotStrictSubSet(Formula left, Formula right)
    {
        return new Formula(Node.NOT_STRICT_SUBSET, left, right);
    }

    public static
    Formula newFinite(Formula set)
    {
        Formula f = new Formula(Node.FINITE, set);
        return f;
    }

    public static
    Formula newPartition(String name, Formula elements)
    {
        return new Formula(Node.PARTITION, newSetSymbol(name), elements);
    }

    public static
    Formula newOfType(Formula left, Formula right)
    {
        return new Formula(Node.OF_TYPE, left, right);
    }

    public static
    Formula newChoice(Formula left)
    {
        return new Formula(Node.CHOICE, left);
    }

    public static
    Formula newCartesianProduct(Formula left, Formula right)
    {
        return new Formula(Node.CARTESIAN_PRODUCT, left, right);
    }

    public static
    Formula newRelation(Formula left, Formula right)
    {
        return new Formula(Node.RELATION, left, right);
    }

    public static
    Formula newTotalRelation(Formula left, Formula right)
    {
        return new Formula(Node.TOTAL_RELATION, left, right);
    }

    public static
    Formula newSurjectiveRelation(Formula left, Formula right)
    {
        return new Formula(Node.SURJECTIVE_RELATION, left, right);
    }

    public static
    Formula newSurjectiveTotalRelation(Formula left, Formula right)
    {
        return new Formula(Node.SURJECTIVE_TOTAL_RELATION, left, right);
    }

    public static
    Formula newPartialFunction(Formula left, Formula right)
    {
        return new Formula(Node.PARTIAL_FUNCTION, left, right);
    }

    public static
    Formula newTotalFunction(Formula left, Formula right)
    {
        return new Formula(Node.TOTAL_FUNCTION, left, right);
    }

    public static
    Formula newPartialInjection(Formula left, Formula right)
    {
        return new Formula(Node.PARTIAL_INJECTION, left, right);
    }

    public static
    Formula newTotalInjection(Formula left, Formula right)
    {
        return new Formula(Node.TOTAL_INJECTION, left, right);
    }

    public static
    Formula newPartialSurjection(Formula left, Formula right)
    {
        return new Formula(Node.PARTIAL_SURJECTION, left, right);
    }

    public static
    Formula newTotalSurjection(Formula left, Formula right)
    {
        return new Formula(Node.TOTAL_SURJECTION, left, right);
    }

    public static
    Formula newTotalBijection(Formula left, Formula right)
    {
        return new Formula(Node.TOTAL_BIJECTION, left, right);
    }

    public static
    Formula newForwardComposition(Formula left, Formula right)
    {
        return new Formula(Node.FORWARD_COMPOSITION, left, right);
    }

    public static
    Formula newBackwardComposition(Formula left, Formula right)
    {
        return new Formula(Node.BACKWARD_COMPOSITION, left, right);
    }

    public static
    Formula newDomainRestriction(Formula left, Formula right)
    {
        return new Formula(Node.DOMAIN_RESTRICTION, left, right);
    }

    public static
    Formula newDomainSubtraction(Formula left, Formula right)
    {
        return new Formula(Node.DOMAIN_SUBTRACTION, left, right);
    }

    public static
    Formula newRangeRestriction(Formula left, Formula right)
    {
        return new Formula(Node.RANGE_RESTRICTION, left, right);
    }

    public static
    Formula newRangeSubtraction(Formula left, Formula right)
    {
        return new Formula(Node.RANGE_SUBTRACTION, left, right);
    }

    public static
    Formula newInvert(Formula inner)
    {
        return new Formula(Node.INVERT, inner);
    }

    public static
    Formula newRelationImage(Formula left, Formula right)
    {
        return new Formula(Node.RELATION_IMAGE, left, right);
    }

    public static
    Formula newOverride(Formula left, Formula right)
    {
        return new Formula(Node.OVERRIDE, left, right);
    }

    public static
    Formula newDirectProduct(Formula left, Formula right)
    {
        return new Formula(Node.DIRECT_PRODUCT, left, right);
    }

    public static
    Formula newParallelProduct(Formula left, Formula right)
    {
        return new Formula(Node.PARALLEL_PRODUCT, left, right);
    }

    public static
    Formula newPowerSet(Formula left)
    {
        return new Formula(Node.POWER_SET, left);
    }

    public static
    Formula newPower1Set(Formula left)
    {
        return new Formula(Node.POWER1_SET, left);
    }

    public static
    Formula newGeneralizedUnion(Formula inner)
    {
        return new Formula(Node.G_UNION, inner);
    }

    public static
    Formula newGeneralizedIntersection(Formula inner)
    {
        return new Formula(Node.G_INTER, inner);
    }

    public static
    Formula newQuantifiedUnion(Formula list_of_variables, Formula predicate, Formula expression)
    {
        return new Formula(Node.Q_UNION,
                           list_of_variables,
                           predicate,
                           expression);
    }

    public static
    Formula newQuantifiedIntersection(Formula list_of_variables, Formula predicate, Formula expression)
    {
        return new Formula(Node.Q_INTER,
                           list_of_variables,
                           predicate,
                           expression);
    }

    public static
    Formula newDomain(Formula left)
    {
        return new Formula(Node.DOMAIN, left);
    }

    public static
    Formula newRange(Formula left)
    {
        return new Formula(Node.RANGE, left);
    }

    public static
    Formula newUpTo(Formula left, Formula right)
    {
        return new Formula(Node.UP_TO, left, right);
    }

    public static
    Formula newSetUnion(Formula left, Formula right)
    {
        return new Formula(Node.SET_UNION, left, right);
    }

    public static
    Formula newSetIntersection(Formula left, Formula right)
    {
        return new Formula(Node.SET_INTERSECTION, left, right);
    }

    public static
    Formula newSetMinus(Formula left, Formula right)
    {
        return new Formula(Node.SET_MINUS, left, right);
    }

    public static
    Formula newListOfVariables(List<Formula> elements)
    {
        return new Formula(Node.LIST_OF_VARIABLES, elements);
    }

    public static
    Formula newListOfNonFreeVariables(List<Formula> elements)
    {
        return new Formula(Node.LIST_OF_NONFREE_VARIABLES, elements);
    }

    public static
    Formula newListOfExpressions(List<Formula> elements)
    {
        return new Formula(Node.LIST_OF_EXPRESSIONS, elements);
    }

    public static
    Formula newEnumeratedSet(List<Formula> elements)
    {
        return new Formula(Node.ENUMERATED_SET, elements);
    }

    public static
    Formula newAddition (Formula left, Formula right)
    {
        return new Formula(Node.ADDITION, left, right);
    }

    public static
    Formula newSubtraction (Formula left, Formula right)
    {
        return new Formula(Node.SUBTRACTION, left, right);
    }

    public static
    Formula newTestBool(Formula inner)
    {
        return new Formula(Node.TEST_BOOL, inner);
    }

    public static
    Formula newCardinality(Formula inner)
    {
        return new Formula(Node.CARDINALITY, inner);
    }

    public static
    Formula newUnaryMinus(Formula right)
    {
        return new Formula(Node.UNARY_MINUS, right);
    }

    public static
    Formula newMultiplication (Formula left, Formula right)
    {
        return new Formula(Node.MULTIPLICATION, left, right);
    }

    public static
    Formula newDivision (Formula left, Formula right)
    {
        return new Formula(Node.DIVISION, left, right);
    }

    public static
    Formula newModulo(Formula left, Formula right)
    {
        return new Formula(Node.MODULO, left, right);
    }

    public static
    Formula newExponentiation(Formula left, Formula right)
    {
        return new Formula(Node.EXPONENTIATION, left, right);
    }

    public static
    Formula newMinimum(Formula inner)
    {
        return new Formula(Node.MINIMUM, inner);
    }

    public static
    Formula newMaximum(Formula inner)
    {
        return new Formula(Node.MAXIMUM, inner);
    }

    public static
    Formula newMapsTo (Formula left, Formula right)
    {
        return new Formula(Node.MAPSTO, left, right);
    }

    public static
    Formula newFunctionApplication (Formula left, Formula right)
    {
        return new Formula(Node.FUNC_APP, left, right);
    }

    public static
    Formula newFunctionInvertedApplication (Formula left, Formula right)
    {
        return new Formula(Node.FUNC_INV_APP, left, right);
    }

}
