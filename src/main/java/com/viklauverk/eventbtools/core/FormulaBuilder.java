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

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.TerminalNode;

public class FormulaBuilder extends EvBFormulaBaseVisitor<Formula>
{
    static Log log = LogModule.lookup("parser");

    private CommonTokenStream tokens_;

    FormulaBuilder(CommonTokenStream tokens)
    {
        tokens_ = tokens;
    }


    public Formula visitOptionalMeta(EvBFormulaParser.MetaContext ctx)
    {
        if (ctx == null) return null;
        return this.visit(ctx);
    }

    void checkNewLineHints(Token t, Formula f)
    {
        int p = t.getTokenIndex();

        if (p-1 >= 0)
        {
            Token prev = tokens_.get(p-1);
            if (prev.getType() == EvBFormulaLexer.WHITESPACE)
            {
                String ws = prev.getText();
                if (Util.hasNewLine(ws))
                {
                    log.debug("found newline before %s in %s", t.getText(), f);
                    RenderFormula.addNewLineHintBefore(f);
                }
            }
        }

        if (tokens_.size() > p+1)
        {
            Token next = tokens_.get(p+1);
            if (next.getType() == EvBFormulaLexer.WHITESPACE)
            {
                String ws = next.getText();
                if (Util.hasNewLine(ws))
                {
                    log.debug("found newline after: %s in %s", t.getText(), f);
                    RenderFormula.addNewLineHintAfter(f);
                }
            }
        }
    }

    @Override
    public Formula visitDone(EvBFormulaParser.DoneContext ctx)
    {
        EvBFormulaParser.SubstitutionContext sc = ctx.substitution();
        EvBFormulaParser.PredicateContext pc = ctx.predicate();
        EvBFormulaParser.ExpressionContext ec = ctx.expression();
		TerminalNode eof = ctx.EOF();

        if (eof == null) return null;

        if (sc != null) return this.visit(sc);
        if (pc != null) return this.visit(pc);
        if (ec != null) return this.visit(ec);

        return null;
    }

    @Override
    public Formula visitAnyPredicateSymbol(EvBFormulaParser.AnyPredicateSymbolContext ctx)
    {
        return FormulaFactory.newAnySymbol(ctx.sym.getText(), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitAnyExpressionSymbol(EvBFormulaParser.AnyExpressionSymbolContext ctx)
    {
        return FormulaFactory.newAnySymbol(ctx.sym.getText(), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitNumberSymbol(EvBFormulaParser.NumberSymbolContext ctx)
    {
        return FormulaFactory.newNumberSymbol(ctx.sym.getText(), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitAnySetSymbol(EvBFormulaParser.AnySetSymbolContext ctx)
    {
        return FormulaFactory.newAnySymbol(ctx.sym.getText(), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitPredicateSymbol(EvBFormulaParser.PredicateSymbolContext ctx)
    {
        return FormulaFactory.newPredicateSymbol(ctx.sym.getText(), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitExpressionSymbol(EvBFormulaParser.ExpressionSymbolContext ctx)
    {
        return FormulaFactory.newExpressionSymbol(ctx.sym.getText(), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitSetSymbol(EvBFormulaParser.SetSymbolContext ctx)
    {
        return FormulaFactory.newSetSymbol(ctx.sym.getText(), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitExpressionVariable(EvBFormulaParser.ExpressionVariableContext ctx)
    {
        if (ctx.PRIM() != null)
        {
            return FormulaFactory.newVariablePrimSymbol(ctx.variable.getText(), visitOptionalMeta(ctx.meta()));
        }
        else
        {
            return FormulaFactory.newVariableSymbol(ctx.variable.getText(), visitOptionalMeta(ctx.meta()));
        }
    }

    @Override
    public Formula visitSetComprehensionNonFreeExpressionVariable(EvBFormulaParser.SetComprehensionNonFreeExpressionVariableContext ctx)
    {
        return FormulaFactory.newVariableNonFreeSymbol(ctx.variable.getText(), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitNonFreeExpressionVariable(EvBFormulaParser.NonFreeExpressionVariableContext ctx)
    {
        return FormulaFactory.newVariableNonFreeSymbol(ctx.variable.getText(), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitExpressionConstant(EvBFormulaParser.ExpressionConstantContext ctx)
    {
        return FormulaFactory.newConstantSymbol(ctx.constant.getText(), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitVariableFunctionApplication(EvBFormulaParser.VariableFunctionApplicationContext ctx)
    {
        Formula var;
        if (ctx.PRIM() != null)
        {
            var = FormulaFactory.newVariablePrimSymbol(ctx.variable.getText(), visitOptionalMeta(ctx.meta()));
        }
        else
        {
            var = FormulaFactory.newVariableSymbol(ctx.variable.getText(), visitOptionalMeta(ctx.meta()));
        }
        if (ctx.INV() != null)
        {
            return FormulaFactory.newFunctionInvertedApplication(var, this.visit(ctx.inner), visitOptionalMeta(ctx.meta()));
        }
        else
        {
            return FormulaFactory.newFunctionApplication(var, this.visit(ctx.inner), visitOptionalMeta(ctx.meta()));
        }
    }

    @Override
    public Formula visitConstantFunctionApplication(EvBFormulaParser.ConstantFunctionApplicationContext ctx)
    {
        Formula constant = FormulaFactory.newConstantSymbol(ctx.constant.getText(), visitOptionalMeta(ctx.meta()));
        return FormulaFactory.newFunctionApplication(constant, this.visit(ctx.inner), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitGenericFunctionApplication(EvBFormulaParser.GenericFunctionApplicationContext ctx)
    {
        return FormulaFactory.newFunctionApplication(this.visit(ctx.function), this.visit(ctx.inner), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitExpressionTRUE(EvBFormulaParser.ExpressionTRUEContext ctx)
    {
        return FormulaFactory.newConstantSymbol("TRUE", visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitExpressionFALSE(EvBFormulaParser.ExpressionFALSEContext ctx)
    {
        return FormulaFactory.newConstantSymbol("FALSE", visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitSetVariable(EvBFormulaParser.SetVariableContext ctx)
    {
        if (ctx.PRIM() != null)
        {
            return FormulaFactory.newVariablePrimSymbol(ctx.variable.getText(), visitOptionalMeta(ctx.meta()));
        }
        else
        {
            return FormulaFactory.newVariableSymbol(ctx.variable.getText(), visitOptionalMeta(ctx.meta()));
        }
    }

    @Override
    public Formula visitNonFreeSetVariable(EvBFormulaParser.NonFreeSetVariableContext ctx)
    {
        return FormulaFactory.newVariableNonFreeSymbol(ctx.variable.getText(), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitSetConstant(EvBFormulaParser.SetConstantContext ctx)
    {
        return FormulaFactory.newConstantSymbol(ctx.constant.getText(), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitListOfVariables(EvBFormulaParser.ListOfVariablesContext ctx)
    {
        List<Formula> elements = new LinkedList<>();
        for (org.antlr.v4.runtime.tree.TerminalNode sec : ctx.SYMBOL())
        {
            // Prim variables not allowed here?
            elements.add(FormulaFactory.newVariableSymbol(sec.getText(), Formula.NO_META));
        }
        return FormulaFactory.newListOfVariables(elements);
    }

    @Override
    public Formula visitListOfNonFreeVariables(EvBFormulaParser.ListOfNonFreeVariablesContext ctx)
    {
        List<Formula> elements = new LinkedList<>();
        for (org.antlr.v4.runtime.tree.TerminalNode sec : ctx.SYMBOL())
        {
            // Prim variables not allowed here?
            elements.add(FormulaFactory.newVariableNonFreeSymbol(sec.getText(), Formula.NO_META));
        }
        return FormulaFactory.newListOfNonFreeVariables(elements);
    }

    @Override
    public Formula visitListOfExpressions(EvBFormulaParser.ListOfExpressionsContext ctx)
    {
        List<Formula> elements = new LinkedList<>();
        for (EvBFormulaParser.ExpressionContext sec : ctx.expression())
        {
            elements.add(this.visit(sec));
        }
        return FormulaFactory.newListOfExpressions(elements);
    }

    @Override
    public Formula visitEnumeratedSet(EvBFormulaParser.EnumeratedSetContext ctx)
    {
        List<Formula> elements = new LinkedList<>();
        for (EvBFormulaParser.ExpressionContext sec : ctx.expression())
        {
            elements.add(this.visit(sec));
        }
        return FormulaFactory.newEnumeratedSet(elements);
    }

    @Override
    public Formula visitLambdaAbstractionExpression(EvBFormulaParser.LambdaAbstractionExpressionContext ctx)
    {
        return FormulaFactory.newLambda(this.visit(ctx.vars), this.visit(ctx.pred), this.visit(ctx.formula),
                                        visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitLambdaAbstractionSet(EvBFormulaParser.LambdaAbstractionSetContext ctx)
    {
        return FormulaFactory.newLambda(this.visit(ctx.vars), this.visit(ctx.pred), this.visit(ctx.formula),
                                        visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitSetComprehension(EvBFormulaParser.SetComprehensionContext ctx)
    {
        return FormulaFactory.newSetComprehension(this.visit(ctx.vars), this.visit(ctx.pred), this.visit(ctx.formula),
                                                  visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitSetComprehensionSpecial(EvBFormulaParser.SetComprehensionSpecialContext ctx)
    {
        Formula var = this.visit(ctx.formula);
        Formula pred = this.visit(ctx.pred);
        return FormulaFactory.newSetComprehensionSpecial(var, pred,
                                                         visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitBecomeEQ(EvBFormulaParser.BecomeEQContext ctx)
    {
        Formula left = this.visit(ctx.left);
        Formula right = this.visit(ctx.right);
        return FormulaFactory.newBecomeEQ(left, right,
                                          visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitBecomeEQFuncApp(EvBFormulaParser.BecomeEQFuncAppContext ctx)
    {
        Formula var = FormulaFactory.newVariableSymbol(ctx.variable.getText(), Formula.NO_META);
        Formula left = this.visit(ctx.left);
        Formula right = this.visit(ctx.right);
        return FormulaFactory.newBecomeEQFuncApp(var, left, right, visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitBecomeIN(EvBFormulaParser.BecomeINContext ctx)
    {
        Formula var = FormulaFactory.newVariableSymbol(ctx.variable.getText(), Formula.NO_META);
        Formula set = this.visit(ctx.inner);
        return FormulaFactory.newBecomeIN(var, set, visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitBecomeSUCH(EvBFormulaParser.BecomeSUCHContext ctx)
    {
        Formula var = FormulaFactory.newVariableSymbol(ctx.variable.getText(), Formula.NO_META);
        Formula set = this.visit(ctx.inner);
        return FormulaFactory.newBecomeSUCH(var, set, visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitAlwaysTrue(EvBFormulaParser.AlwaysTrueContext ctx)
    {
        return FormulaFactory.newTrue();
    }

    @Override
    public Formula visitAlwaysFalse(EvBFormulaParser.AlwaysFalseContext ctx)
    {
        return FormulaFactory.newFalse();
    }

    @Override
    public Formula visitNumber(EvBFormulaParser.NumberContext ctx)
    {
        return FormulaFactory.newNumber(ctx.NUMBER().getText(), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitConjunction(EvBFormulaParser.ConjunctionContext ctx)
    {
        Formula f = FormulaFactory.newConjunction(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
        checkNewLineHints(ctx.operator, f);
        return f;
    }

    @Override
    public Formula visitImplication(EvBFormulaParser.ImplicationContext ctx)
    {
        Formula f = FormulaFactory.newImplication(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
        checkNewLineHints(ctx.operator, f);
        return f;
    }

    @Override
    public Formula visitEquivalence(EvBFormulaParser.EquivalenceContext ctx)
    {
        return FormulaFactory.newEquivalence(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitNegation(EvBFormulaParser.NegationContext ctx)
    {
        return FormulaFactory.newNegation(this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitDisjunction(EvBFormulaParser.DisjunctionContext ctx)
    {
        Formula f = FormulaFactory.newDisjunction(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
        checkNewLineHints(ctx.operator, f);
        return f;
    }

    @Override
    public Formula visitPredicateParentheses(EvBFormulaParser.PredicateParenthesesContext ctx)
    {
        return FormulaFactory.newParenthesisedPredicate(this.visit(ctx.inner), Formula.NO_META);
    }

    @Override
    public Formula visitExpressionParentheses(EvBFormulaParser.ExpressionParenthesesContext ctx)
    {
        return FormulaFactory.newParenthesisedExpression(this.visit(ctx.inner), Formula.NO_META);
    }

    @Override
    public Formula visitMapsToExpression(EvBFormulaParser.MapsToExpressionContext ctx)
    {
        return FormulaFactory.newMapsTo(this.visit(ctx.left), this.visit(ctx.right), Formula.NO_META);
    }

    @Override
    public Formula visitMapsToSet(EvBFormulaParser.MapsToSetContext ctx)
    {
        return FormulaFactory.newMapsTo(this.visit(ctx.left), this.visit(ctx.right), Formula.NO_META);
    }

    @Override
    public Formula visitUnaryMinus(EvBFormulaParser.UnaryMinusContext ctx)
    {
        return FormulaFactory.newUnaryMinus(this.visit(ctx.right), Formula.NO_META);
    }

    @Override
    public Formula visitMultiplication(EvBFormulaParser.MultiplicationContext ctx)
    {
        return FormulaFactory.newMultiplication(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitDivision(EvBFormulaParser.DivisionContext ctx)
    {
        return FormulaFactory.newDivision(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitAddition(EvBFormulaParser.AdditionContext ctx)
    {
        return FormulaFactory.newAddition(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitSubtraction(EvBFormulaParser.SubtractionContext ctx)
    {
        return FormulaFactory.newSubtraction(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitModulo(EvBFormulaParser.ModuloContext ctx)
    {
        return FormulaFactory.newModulo(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitExponentiation(EvBFormulaParser.ExponentiationContext ctx)
    {
        return FormulaFactory.newExponentiation(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitMinimum(EvBFormulaParser.MinimumContext ctx)
    {
        return FormulaFactory.newMinimum(this.visit(ctx.inner), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitMaximum(EvBFormulaParser.MaximumContext ctx)
    {
        return FormulaFactory.newMaximum(this.visit(ctx.inner), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitTestBool(EvBFormulaParser.TestBoolContext ctx)
    {
        return FormulaFactory.newTestBool(this.visit(ctx.inner), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitCardinality(EvBFormulaParser.CardinalityContext ctx)
    {
        return FormulaFactory.newCardinality(this.visit(ctx.inner), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitSetMembership(EvBFormulaParser.SetMembershipContext ctx)
    {
        return FormulaFactory.newSetMembership(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitSetNotMembership(EvBFormulaParser.SetNotMembershipContext ctx)
    {
        return FormulaFactory.newSetNotMembership(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitSubSet(EvBFormulaParser.SubSetContext ctx)
    {
        Formula left = this.visit(ctx.left);
        Formula right = this.visit(ctx.right);
        return FormulaFactory.newSubSet(left, right, visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitNotSubSet(EvBFormulaParser.NotSubSetContext ctx)
    {
        Formula left = this.visit(ctx.left);
        Formula right = this.visit(ctx.right);
        return FormulaFactory.newNotSubSet(left, right, visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitStrictSubSet(EvBFormulaParser.StrictSubSetContext ctx)
    {
        Formula left = this.visit(ctx.left);
        Formula right = this.visit(ctx.right);
        return FormulaFactory.newStrictSubSet(left, right, visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitNotStrictSubSet(EvBFormulaParser.NotStrictSubSetContext ctx)
    {
        Formula left = this.visit(ctx.left);
        Formula right = this.visit(ctx.right);
        return FormulaFactory.newNotStrictSubSet(left, right, visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitFiniteSet(EvBFormulaParser.FiniteSetContext ctx)
    {
        Formula set = this.visit(ctx.inner);
        return FormulaFactory.newFinite(set);
    }

    @Override
    public Formula visitPartitionSet(EvBFormulaParser.PartitionSetContext ctx)
    {
        Formula elements = this.visit(ctx.right);
        return FormulaFactory.newPartition(ctx.left.getText(), elements);
    }

    @Override
    public Formula visitEmptySet(EvBFormulaParser.EmptySetContext ctx)
    {
        return FormulaFactory.newEmptySet(visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitIdSet(EvBFormulaParser.IdSetContext ctx)
    {
        return FormulaFactory.newIdSet(visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitPrj1(EvBFormulaParser.Prj1Context ctx)
    {
        return FormulaFactory.newPrj1(visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitPrj2(EvBFormulaParser.Prj2Context ctx)
    {
        return FormulaFactory.newPrj2(visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitNATSet(EvBFormulaParser.NATSetContext ctx)
    {
        return FormulaFactory.newNATSet(visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitNAT1Set(EvBFormulaParser.NAT1SetContext ctx)
    {
        return FormulaFactory.newNAT1Set(visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitINTSet(EvBFormulaParser.INTSetContext ctx)
    {
        return FormulaFactory.newINTSet(visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitBOOLSet(EvBFormulaParser.BOOLSetContext ctx)
    {
        return FormulaFactory.newBOOLSet(visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitPowerSet(EvBFormulaParser.PowerSetContext ctx)
    {
        Formula inner_set = this.visit(ctx.inner);
        return FormulaFactory.newPowerSet(inner_set, visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitPower1Set(EvBFormulaParser.Power1SetContext ctx)
    {
        Formula inner_set = this.visit(ctx.inner);
        return FormulaFactory.newPower1Set(inner_set, visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitGeneralizedUnion(EvBFormulaParser.GeneralizedUnionContext ctx)
    {
        Formula inner_set = this.visit(ctx.inner);
        return FormulaFactory.newGeneralizedUnion(inner_set, visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitGeneralizedIntersection(EvBFormulaParser.GeneralizedIntersectionContext ctx)
    {
        Formula inner_set = this.visit(ctx.inner);
        return FormulaFactory.newGeneralizedIntersection(inner_set, visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitQuantifiedUnion(EvBFormulaParser.QuantifiedUnionContext ctx)
    {
        return FormulaFactory.newQuantifiedUnion(this.visit(ctx.vars), this.visit(ctx.pred), this.visit(ctx.inner),
                                                 visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitQuantifiedIntersection(EvBFormulaParser.QuantifiedIntersectionContext ctx)
    {
        return FormulaFactory.newQuantifiedIntersection(this.visit(ctx.vars), this.visit(ctx.pred), this.visit(ctx.inner),
                                                        visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitDomain(EvBFormulaParser.DomainContext ctx)
    {
        Formula inner_set = this.visit(ctx.inner);
        return FormulaFactory.newDomain(inner_set, visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitRange(EvBFormulaParser.RangeContext ctx)
    {
        Formula inner_set = this.visit(ctx.inner);
        return FormulaFactory.newRange(inner_set, visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitCartesianProduct(EvBFormulaParser.CartesianProductContext ctx)
    {
        return FormulaFactory.newCartesianProduct(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitUpTo(EvBFormulaParser.UpToContext ctx)
    {
        return FormulaFactory.newUpTo(this.visit(ctx.from), this.visit(ctx.to), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitSetUnion(EvBFormulaParser.SetUnionContext ctx)
    {
        return FormulaFactory.newSetUnion(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitSetIntersection(EvBFormulaParser.SetIntersectionContext ctx)
    {
        return FormulaFactory.newSetIntersection(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitSetMinus(EvBFormulaParser.SetMinusContext ctx)
    {
        return FormulaFactory.newSetMinus(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitRelation(EvBFormulaParser.RelationContext ctx)
    {
        return FormulaFactory.newRelation(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitTotalRelation(EvBFormulaParser.TotalRelationContext ctx)
    {
        return FormulaFactory.newTotalRelation(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitSurjectiveRelation(EvBFormulaParser.SurjectiveRelationContext ctx)
    {
        return FormulaFactory.newSurjectiveRelation(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitSurjectiveTotalRelation(EvBFormulaParser.SurjectiveTotalRelationContext ctx)
    {
        return FormulaFactory.newSurjectiveTotalRelation(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitPartialFunction(EvBFormulaParser.PartialFunctionContext ctx)
    {
        return FormulaFactory.newPartialFunction(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitTotalFunction(EvBFormulaParser.TotalFunctionContext ctx)
    {
        return FormulaFactory.newTotalFunction(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitPartialInjection(EvBFormulaParser.PartialInjectionContext ctx)
    {
        return FormulaFactory.newPartialInjection(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitTotalInjection(EvBFormulaParser.TotalInjectionContext ctx)
    {
        return FormulaFactory.newTotalInjection(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitPartialSurjection(EvBFormulaParser.PartialSurjectionContext ctx)
    {
        return FormulaFactory.newPartialSurjection(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitTotalSurjection(EvBFormulaParser.TotalSurjectionContext ctx)
    {
        return FormulaFactory.newTotalSurjection(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitTotalBijection(EvBFormulaParser.TotalBijectionContext ctx)
    {
        return FormulaFactory.newTotalBijection(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitForwardComposition(EvBFormulaParser.ForwardCompositionContext ctx)
    {
        return FormulaFactory.newForwardComposition(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitBackwardComposition(EvBFormulaParser.BackwardCompositionContext ctx)
    {
        return FormulaFactory.newBackwardComposition(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitDomainRestriction(EvBFormulaParser.DomainRestrictionContext ctx)
    {
        return FormulaFactory.newDomainRestriction(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitDomainSubtraction(EvBFormulaParser.DomainSubtractionContext ctx)
    {
        return FormulaFactory.newDomainSubtraction(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitRangeRestriction(EvBFormulaParser.RangeRestrictionContext ctx)
    {
        return FormulaFactory.newRangeRestriction(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitRangeSubtraction(EvBFormulaParser.RangeSubtractionContext ctx)
    {
        return FormulaFactory.newRangeSubtraction(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitInvertSet(EvBFormulaParser.InvertSetContext ctx)
    {
        return FormulaFactory.newInvert(this.visit(ctx.inner), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitRelationImage(EvBFormulaParser.RelationImageContext ctx)
    {
        return FormulaFactory.newRelationImage(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitOverride(EvBFormulaParser.OverrideContext ctx)
    {
        return FormulaFactory.newOverride(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitDirectProduct(EvBFormulaParser.DirectProductContext ctx)
    {
        return FormulaFactory.newDirectProduct(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitParallelProduct(EvBFormulaParser.ParallelProductContext ctx)
    {
        return FormulaFactory.newParallelProduct(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitOfType(EvBFormulaParser.OfTypeContext ctx)
    {
        return FormulaFactory.newOfType(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitEquals(EvBFormulaParser.EqualsContext ctx)
    {
        return FormulaFactory.newEquals(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitNotEquals(EvBFormulaParser.NotEqualsContext ctx)
    {
        return FormulaFactory.newNotEquals(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitLessThan(EvBFormulaParser.LessThanContext ctx)
    {
        return FormulaFactory.newLessThan(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitGreaterThan(EvBFormulaParser.GreaterThanContext ctx)
    {
        return FormulaFactory.newGreaterThan(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitLessThanOrEqual(EvBFormulaParser.LessThanOrEqualContext ctx)
    {
        return FormulaFactory.newLessThanOrEqual(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitGreaterThanOrEqual(EvBFormulaParser.GreaterThanOrEqualContext ctx)
    {
        return FormulaFactory.newGreaterThanOrEqual(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitUniversal(EvBFormulaParser.UniversalContext ctx)
    {
        return FormulaFactory.newUniversalQ(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitExistential(EvBFormulaParser.ExistentialContext ctx)
    {
        return FormulaFactory.newExistentialQ(this.visit(ctx.left), this.visit(ctx.right), visitOptionalMeta(ctx.meta()));
    }

    @Override
    public Formula visitMetaFormula(EvBFormulaParser.MetaFormulaContext ctx)
    {
        EvBFormulaParser.SubstitutionContext sc = ctx.substitution();
        EvBFormulaParser.PredicateContext pc = ctx.predicate();
        EvBFormulaParser.ExpressionContext ec = ctx.expression();

        if (sc != null) return this.visit(sc);
        if (pc != null) return this.visit(pc);
        if (ec != null) return this.visit(ec);

        return null;
    }

}
