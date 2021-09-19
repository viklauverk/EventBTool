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
    Formula newBecomeEQ(Formula syms, Formula exprs, Formula meta)
    {
        return new Formula(Node.BECOME_EQ,
                           syms,
                           exprs,
                           meta);
    }

    public static
    Formula newBecomeEQFuncApp(Formula var, Formula left, Formula right, Formula meta)
    {
        List<Formula> inners = new ArrayList<>();
        inners.add(var);
        inners.add(left);
        inners.add(right);

        return new Formula(Node.BECOME_EQ_FUNC_APP, inners, meta);
    }

    public static
    Formula newBecomeIN(Formula var, Formula f, Formula meta)
    {
        return new Formula(Node.BECOME_IN,
                           var,
                           f,
                           meta);
    }

    public static
    Formula newBecomeSUCH(Formula var, Formula f, Formula meta)
    {
        return new Formula(Node.BECOME_SUCH,
                           var,
                           f,
                           meta);
    }

    public static
    Formula newTrue()
    {
        return new Formula(Node.TRUE, null);
    }

    public static
    Formula newFalse()
    {
        return new Formula(Node.FALSE, null);
    }

    public static
    Formula newNumber (String number_string, Formula meta)
    {
        // Normalize any unicode − into ascii minus -.
        int n = Integer.parseInt(number_string.replace("−", "-"));
        return new Formula(Node.NUMBER, n, meta);
    }

    public static
    Formula newAnySymbol (String p, Formula meta)
    {
        return new Formula(Node.ANY_SYMBOL, Symbols.intern(p), meta);
    }

    public static
    Formula newNumberSymbol (String p, Formula meta)
    {
        return new Formula(Node.NUMBER_SYMBOL, Symbols.intern(p), meta);
    }

    public static
    Formula newPredicateSymbol (String p, Formula meta)
    {
        return new Formula(Node.PREDICATE_SYMBOL, Symbols.intern(p), meta);
    }

    public static
    Formula newVariableSymbol (String v, Formula meta)
    {
        return new Formula(Node.VARIABLE_SYMBOL, Symbols.intern(v), meta);
    }

    public static
    Formula newVariablePrimSymbol (String v, Formula meta)
    {
        return new Formula(Node.VARIABLE_PRIM_SYMBOL, Symbols.intern(v), meta);
    }

    public static
    Formula newVariableNonFreeSymbol (String v, Formula meta)
    {
        return new Formula(Node.VARIABLE_NONFREE_SYMBOL, Symbols.intern(v), meta);
    }

    public static
    Formula newExpressionSymbol (String e, Formula meta)
    {
        return new Formula(Node.EXPRESSION_SYMBOL, Symbols.intern(e), meta);
    }

    public static
    Formula newConstantSymbol (String e, Formula meta)
    {
        return new Formula(Node.CONSTANT_SYMBOL, Symbols.intern(e), meta);
    }

    public static
    Formula newSetSymbol (String s, Formula meta)
    {
        return new Formula(Node.SET_SYMBOL, Symbols.intern(s), meta);
    }

    public static
    Formula newEmptySet(Formula meta)
    {
        return new Formula(Node.EMPTY_SET, meta);
    }

    public static
    Formula newIdSet(Formula meta)
    {
        return new Formula(Node.ID_SET, meta);
    }

    public static
    Formula newPrj1(Formula meta)
    {
        return new Formula(Node.PRJ1, meta);
    }

    public static
    Formula newPrj2(Formula meta)
    {
        return new Formula(Node.PRJ2, meta);
    }

    public static
    Formula newNATSet(Formula meta)
    {
        return new Formula(Node.NAT_SET, meta);
    }

    public static
    Formula newNAT1Set(Formula meta)
    {
        return new Formula(Node.NAT1_SET, meta);
    }

    public static
    Formula newINTSet(Formula meta)
    {
        return new Formula(Node.INT_SET, meta);
    }

    public static
    Formula newBOOLSet(Formula meta)
    {
        return new Formula(Node.BOOL_SET, meta);
    }

    public static
    Formula newUniversalQ(Formula list_of_variables, Formula predicate, Formula meta)
    {
        return new Formula(Node.UNIVERSALQ,
                           list_of_variables,
                           predicate,
                           meta);
    }

    public static
    Formula newExistentialQ(Formula list_of_variables, Formula predicate, Formula meta)
    {
        return new Formula(Node.EXISTENTIALQ,
                           list_of_variables,
                           predicate,
                           meta);
    }

    public static
    Formula newLambda(Formula list_of_variables, Formula predicate, Formula expression, Formula meta)
    {
        return new Formula(Node.LAMBDA_ABSTRACTION,
                           list_of_variables,
                           predicate,
                           expression,
                           meta);
    }

    public static
    Formula newSetComprehension(Formula list_of_variables, Formula predicate, Formula expression, Formula meta)
    {
        return new Formula(Node.SET_COMPREHENSION,
                           list_of_variables,
                           predicate,
                           expression,
                           meta);
    }

    public static
    Formula newSetComprehensionSpecial(Formula var, Formula predicate, Formula meta)
    {
        return new Formula(Node.SET_COMPREHENSION_SPECIAL, var, predicate, meta);
    }

    public static
    Formula newParenthesisedPredicate(Formula inner, Formula meta)
    {
        return new Formula(Node.PARENTHESISED_PREDICATE, inner, meta);
    }

    public static
    Formula newParenthesisedExpression(Formula inner, Formula meta)
    {
        return new Formula(Node.PARENTHESISED_EXPRESSION, inner, meta);
    }

    public static
    Formula newConjunction(Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.CONJUNCTION, left, right, meta);
    }

    public static
    Formula newImplication(Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.IMPLICATION, left, right, meta);
    }

    public static
    Formula newNegation(Formula left, Formula meta)
    {
        return new Formula(Node.NEGATION, left, meta);
    }

    public static
    Formula newDisjunction (Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.DISJUNCTION, left, right, meta);
    }

    public static
    Formula newEquivalence (Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.EQUIVALENCE, left, right, meta);
    }

    public static
    Formula newEquals (Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.EQUALS, left, right, meta);
    }

    public static
    Formula newNotEquals (Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.NOT_EQUALS, left, right, meta);
    }

    public static
    Formula newLessThan(Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.LESS_THAN, left, right, meta);
    }

    public static
    Formula newGreaterThan(Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.GREATER_THAN, left, right, meta);
    }

    public static
    Formula newLessThanOrEqual(Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.LESS_THAN_OR_EQUAL, left, right, meta);
    }

    public static
    Formula newGreaterThanOrEqual(Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.GREATER_THAN_OR_EQUAL, left, right, meta);
    }

    public static
    Formula newApplication(Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.APPLICATION, left, right, meta);
    }

    public static
    Formula newSetMembership (Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.MEMBERSHIP, left, right, meta);
    }

    public static
    Formula newSetNotMembership (Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.NOT_MEMBERSHIP, left, right, meta);
    }

    public static
    Formula newSubSet(Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.SUBSET, left, right, meta);
    }

    public static
    Formula newNotSubSet(Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.NOT_SUBSET, left, right, meta);
    }

    public static
    Formula newStrictSubSet(Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.STRICT_SUBSET, left, right, meta);
    }

    public static
    Formula newNotStrictSubSet(Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.NOT_STRICT_SUBSET, left, right, meta);
    }

    public static
    Formula newFinite(Formula set)
    {
        Formula f = new Formula(Node.FINITE, set, null);
        return f;
    }

    public static
    Formula newPartition(String name, Formula elements)
    {
        return new Formula(Node.PARTITION, newSetSymbol(name, Formula.NO_META), elements, Formula.NO_META);
    }

    public static
    Formula newOfType(Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.OF_TYPE, left, right, meta);
    }

    public static
    Formula newChoice(Formula left, Formula meta)
    {
        return new Formula(Node.CHOICE, left, meta);
    }

    public static
    Formula newCartesianProduct(Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.CARTESIAN_PRODUCT, left, right, meta);
    }

    public static
    Formula newRelation(Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.RELATION, left, right, meta);
    }

    public static
    Formula newTotalRelation(Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.TOTAL_RELATION, left, right, meta);
    }

    public static
    Formula newSurjectiveRelation(Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.SURJECTIVE_RELATION, left, right, meta);
    }

    public static
    Formula newSurjectiveTotalRelation(Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.SURJECTIVE_TOTAL_RELATION, left, right, meta);
    }

    public static
    Formula newPartialFunction(Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.PARTIAL_FUNCTION, left, right, meta);
    }

    public static
    Formula newTotalFunction(Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.TOTAL_FUNCTION, left, right, meta);
    }

    public static
    Formula newPartialInjection(Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.PARTIAL_INJECTION, left, right, meta);
    }

    public static
    Formula newTotalInjection(Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.TOTAL_INJECTION, left, right, meta);
    }

    public static
    Formula newPartialSurjection(Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.PARTIAL_SURJECTION, left, right, meta);
    }

    public static
    Formula newTotalSurjection(Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.TOTAL_SURJECTION, left, right, meta);
    }

    public static
    Formula newTotalBijection(Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.TOTAL_BIJECTION, left, right, meta);
    }

    public static
    Formula newForwardComposition(Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.FORWARD_COMPOSITION, left, right, meta);
    }

    public static
    Formula newBackwardComposition(Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.BACKWARD_COMPOSITION, left, right, meta);
    }

    public static
    Formula newDomainRestriction(Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.DOMAIN_RESTRICTION, left, right, meta);
    }

    public static
    Formula newDomainSubtraction(Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.DOMAIN_SUBTRACTION, left, right, meta);
    }

    public static
    Formula newRangeRestriction(Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.RANGE_RESTRICTION, left, right, meta);
    }

    public static
    Formula newRangeSubtraction(Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.RANGE_SUBTRACTION, left, right, meta);
    }

    public static
    Formula newInvert(Formula inner, Formula meta)
    {
        return new Formula(Node.INVERT, inner, meta);
    }

    public static
    Formula newRelationImage(Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.RELATION_IMAGE, left, right, meta);
    }

    public static
    Formula newOverride(Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.OVERRIDE, left, right, meta);
    }

    public static
    Formula newDirectProduct(Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.DIRECT_PRODUCT, left, right, meta);
    }

    public static
    Formula newParallelProduct(Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.PARALLEL_PRODUCT, left, right, meta);
    }

    public static
    Formula newPowerSet(Formula left, Formula meta)
    {
        return new Formula(Node.POWER_SET, left, meta);
    }

    public static
    Formula newPower1Set(Formula left, Formula meta)
    {
        return new Formula(Node.POWER1_SET, left, meta);
    }

    public static
    Formula newGeneralizedUnion(Formula inner, Formula meta)
    {
        return new Formula(Node.G_UNION, inner, meta);
    }

    public static
    Formula newGeneralizedIntersection(Formula inner, Formula meta)
    {
        return new Formula(Node.G_INTER, inner, meta);
    }

    public static
    Formula newQuantifiedUnion(Formula list_of_variables, Formula predicate, Formula expression, Formula meta)
    {
        return new Formula(Node.Q_UNION,
                           list_of_variables,
                           predicate,
                           expression,
                           meta);
    }

    public static
    Formula newQuantifiedIntersection(Formula list_of_variables, Formula predicate, Formula expression, Formula meta)
    {
        return new Formula(Node.Q_INTER,
                           list_of_variables,
                           predicate,
                           expression,
                           meta);
    }

    public static
    Formula newDomain(Formula left, Formula meta)
    {
        return new Formula(Node.DOMAIN, left, meta);
    }

    public static
    Formula newRange(Formula left, Formula meta)
    {
        return new Formula(Node.RANGE, left, meta);
    }

    public static
    Formula newUpTo(Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.UP_TO, left, right, meta);
    }

    public static
    Formula newSetUnion(Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.SET_UNION, left, right, meta);
    }

    public static
    Formula newSetIntersection(Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.SET_INTERSECTION, left, right, meta);
    }

    public static
    Formula newSetMinus(Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.SET_MINUS, left, right, meta);
    }

    public static
    Formula newListOfVariables(List<Formula> elements)
    {
        return new Formula(Node.LIST_OF_VARIABLES, elements, null);
    }

    public static
    Formula newListOfNonFreeVariables(List<Formula> elements)
    {
        return new Formula(Node.LIST_OF_NONFREE_VARIABLES, elements, null);
    }

    public static
    Formula newListOfExpressions(List<Formula> elements)
    {
        return new Formula(Node.LIST_OF_EXPRESSIONS, elements, null);
    }

    public static
    Formula newEnumeratedSet(List<Formula> elements)
    {
        return new Formula(Node.ENUMERATED_SET, elements, null);
    }

    public static
    Formula newAddition (Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.ADDITION, left, right, meta);
    }

    public static
    Formula newSubtraction (Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.SUBTRACTION, left, right, meta);
    }

    public static
    Formula newTestBool(Formula inner, Formula meta)
    {
        return new Formula(Node.TEST_BOOL, inner, meta);
    }

    public static
    Formula newCardinality(Formula inner, Formula meta)
    {
        return new Formula(Node.CARDINALITY, inner, meta);
    }

    public static
    Formula newUnaryMinus(Formula right, Formula meta)
    {
        return new Formula(Node.UNARY_MINUS, right, meta);
    }

    public static
    Formula newMultiplication (Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.MULTIPLICATION, left, right, meta);
    }

    public static
    Formula newDivision (Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.DIVISION, left, right, meta);
    }

    public static
    Formula newModulo(Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.MODULO, left, right, meta);
    }

    public static
    Formula newExponentiation(Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.EXPONENTIATION, left, right, meta);
    }

    public static
    Formula newMinimum(Formula inner, Formula meta)
    {
        return new Formula(Node.MINIMUM, inner, meta);
    }

    public static
    Formula newMaximum(Formula inner, Formula meta)
    {
        return new Formula(Node.MAXIMUM, inner, meta);
    }

    public static
    Formula newMapsTo (Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.MAPSTO, left, right, meta);
    }

    public static
    Formula newFunctionApplication (Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.FUNC_APP, left, right, meta);
    }

    public static
    Formula newFunctionInvertedApplication (Formula left, Formula right, Formula meta)
    {
        return new Formula(Node.FUNC_INV_APP, left, right, meta);
    }

}
