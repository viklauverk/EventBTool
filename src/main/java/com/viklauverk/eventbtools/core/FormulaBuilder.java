/*
 Copyright (C) 2021 Viklauverk AB

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
   public Formula visitAddition(EvBFormulaParser.AdditionContext ctx)
   {
       return FormulaFactory.newAddition(this.visit(ctx.left), this.visit(ctx.right), null);
   }

    @Override
    public Formula visitExpressionVariable(EvBFormulaParser.ExpressionVariableContext ctx)
    {
        if (ctx.PRIM() != null)
        {
            return FormulaFactory.newVariablePrimSymbol(ctx.variable.getText(), null);
        }
        else
        {
            return FormulaFactory.newVariableSymbol(ctx.variable.getText(), null);
        }
    }





    @Override
    public Formula visitOperatorInfixPredicate(EvBFormulaParser.OperatorInfixPredicateContext ctx)
    {
        Formula left = this.visit(ctx.left);
        Formula right = this.visit(ctx.right);

        return FormulaFactory.newOperatorInfixSymbol(ctx.operator.getText(),
                                                     OperatorType.PREDICATE,
                                                     left,
                                                     right,
                                                     null);
    }

    @Override
    public Formula visitOperatorInfixExpression(EvBFormulaParser.OperatorInfixExpressionContext ctx)
    {
        Formula left = this.visit(ctx.left);
        Formula right = this.visit(ctx.right);

        return FormulaFactory.newOperatorInfixSymbol(ctx.operator.getText(),
                                                     OperatorType.EXPRESSION,
                                                     left,
                                                     right,
                                                     null);
    }




    @Override
    public Formula visitExpressionTRUE(EvBFormulaParser.ExpressionTRUEContext ctx)
    {
        return FormulaFactory.newConstantSymbol("TRUE", null);
    }

    @Override
    public Formula visitExpressionFALSE(EvBFormulaParser.ExpressionFALSEContext ctx)
    {
        return FormulaFactory.newConstantSymbol("FALSE", null);
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
    public Formula visitConjunction(EvBFormulaParser.ConjunctionContext ctx)
    {
        Formula f = FormulaFactory.newConjunction(this.visit(ctx.left), this.visit(ctx.right), null);
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
   public Formula visitBOOLSet(EvBFormulaParser.BOOLSetContext ctx)
   {
       return FormulaFactory.newBOOLSet(null);
   }







}
