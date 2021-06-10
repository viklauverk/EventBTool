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

import com.viklauverk.eventbtools.core.Formula;

import java.util.List;
import java.util.LinkedList;

public class RenderFormulaUnicode extends RenderFormula
{
    public RenderFormulaUnicode(Canvas canvas)
    {
        super(canvas);
    }

    private boolean limit_to_ascii_ = false;


    @Override public void enterNode(Node p)
    {
        if (add_types_) cnvs().symbol("<"+p.name()+" ");
    }

    @Override public void exitNode(Node p)
    {
        if (add_types_) cnvs().symbol(">");
    }

    public void limitToAscii()
    {
        limit_to_ascii_ = true;
    }

    public String c(String unicode, String ascii)
    {
        if (limit_to_ascii_)
        {
            return ascii;
        }
        else
        {
            return unicode;
        }
    }

    @Override public Formula visit_BECOME_EQ(Formula i)
    {
        visitLeft(i); cnvs().symbol(c(" ≔ ", " := ")); visitRight(i); return i;
    }

    @Override public Formula visit_BECOME_EQ_FUNC_APP(Formula i)
    {
        visitChildNum(i, 0); cnvs().symbol("("); visitChildNum(i, 1); cnvs().symbol(c(") ≔ ", ") := ")); visitChildNum(i, 2); return i;
    }

    @Override public Formula visit_BECOME_IN(Formula i)
    {
        visitLeft(i); cnvs().symbol(c(" :∈ ", " :: ")); visitRight(i); return i;
    }

    @Override public Formula visit_BECOME_SUCH(Formula i)
    {
        visitLeft(i); cnvs().symbol(" :| "); visitRight(i); return i;
    }

    @Override public Formula visit_FALSE(Formula i)
    {
        cnvs().symbol(c("⊥", "false")); return i;
    }

    @Override public Formula visit_NUMBER(Formula i)
    {
        cnvs().number(""+i.intData()); return i;
    }

    @Override public Formula visit_TRUE(Formula i)
    {
        cnvs().symbol(c("⊤", "true")); return i;
    }

    @Override public Formula visit_APPLICATION(Formula i)
    {
        cnvs().symbol("["); visitLeft(i); cnvs().symbol("]"); visitRight(i); return i;
    }

    @Override public Formula visit_PARENTHESISED_PREDICATE(Formula i)
    {
        cnvs().symbol("(");
        visitChild(i);
        cnvs().symbol(")");
        return i;
    }

    @Override public Formula visit_PARENTHESISED_EXPRESSION(Formula i)
    {
        cnvs().symbol("(");
        visitChild(i);
        cnvs().symbol(")");
        return i;
    }

    @Override public Formula visit_CONJUNCTION(Formula i)
    {
        visitLeft(i);
        checkNewLineBefore(i);
        cnvs().symbol(c("∧","&"));
        checkNewLineAfter(i);
        visitRight(i); return i;
    }

    @Override public Formula visit_IMPLICATION(Formula i)
    {
        visitLeft(i); cnvs().symbol(c("⇒ ","=>")); visitRight(i); return i;
    }

    @Override public Formula visit_NEGATION(Formula i)
    {
        cnvs().symbol(c("¬","not ")); visitChild(i); return i;
    }

    @Override public Formula visit_DISJUNCTION(Formula i)
    {
        visitLeft(i);
        checkNewLineBefore(i);
        cnvs().symbol(c("∨"," or "));
        checkNewLineAfter(i);
        visitRight(i); return i;
    }

    @Override public Formula visit_EQUIVALENCE(Formula i)
    {
        visitLeft(i); cnvs().symbol(c("⇔ ", "<=>")); visitRight(i); return i;
    }

    @Override public Formula visit_UNIVERSALQ(Formula i)
    {
        cnvs().symbol(c("∀", "!")); visitLeft(i); cnvs().symbol(c("·",".")); visitRight(i); return i;
    }

    @Override public Formula visit_EXISTENTIALQ(Formula i)
    {
        cnvs().symbol(c("∃","#")); visitLeft(i); cnvs().symbol(c("·", ".")); visitRight(i); return i;
    }

    @Override public Formula visit_EQUALS(Formula i)
    {
        visitLeft(i); cnvs().symbol("="); visitRight(i); return i;
    }

    @Override public Formula visit_NOT_EQUALS(Formula i)
    {
        visitLeft(i); cnvs().symbol(c("≠", "/=")); visitRight(i); return i;
    }

    @Override public Formula visit_LESS_THAN(Formula i)
    {
        visitLeft(i); cnvs().symbol("<"); visitRight(i); return i;
    }

    @Override public Formula visit_GREATER_THAN(Formula i)
    {
        visitLeft(i); cnvs().symbol(">"); visitRight(i); return i;
    }

    @Override public Formula visit_LESS_THAN_OR_EQUAL(Formula i)
    {
        visitLeft(i); cnvs().symbol(c("≤", "<=")); visitRight(i); return i;
    }

    @Override public Formula visit_GREATER_THAN_OR_EQUAL(Formula i)
    {
        visitLeft(i); cnvs().symbol(c("≥", ">=")); visitRight(i); return i;
    }

    @Override public Formula visit_CHOICE(Formula i)
    {
        cnvs().symbol("choice("); visitChild(i); cnvs().symbol(")"); return i;
    }

    @Override public Formula visit_MEMBERSHIP(Formula i)
    {
        visitLeft(i); cnvs().symbol(c("∈", ":")); visitRight(i); return i;
    }

    @Override public Formula visit_NOT_MEMBERSHIP(Formula i)
    {
        visitLeft(i); cnvs().symbol(c("∉", "/:")); visitRight(i); return i;
    }

    @Override public Formula visit_SUBSET(Formula i)
    {
        visitLeft(i); cnvs().symbol(c("⊆", "<:")); visitRight(i); return i;
    }

    @Override public Formula visit_STRICT_SUBSET(Formula i)
    {
        visitLeft(i); cnvs().symbol(c("⊂", "<<:")); visitRight(i); return i;
    }

    @Override public Formula visit_NOT_SUBSET(Formula i)
    {
        visitLeft(i); cnvs().symbol(c("⊈", "/<:")); visitRight(i); return i;
    }

    @Override public Formula visit_NOT_STRICT_SUBSET(Formula i)
    {
        visitLeft(i); cnvs().symbol(c("⊄", "/<<:")); visitRight(i); return i;
    }

    @Override public Formula visit_FINITE(Formula i)
    {
        cnvs().symbol("finite("); visitChild(i); cnvs().symbol(")"); return i;
    }

    @Override public Formula visit_PARTITION(Formula i)
    {
        cnvs().symbol("partition("); visitLeft(i); cnvs().symbol(","); visitRight(i); cnvs().symbol(")"); return i;
    }

    @Override public Formula visit_OF_TYPE(Formula i)
    {
        visitLeft(i); cnvs().symbol(c(" ⦂ ", " oftype ")); visitRight(i); return i;
    }

    @Override public Formula visit_SET_UNION(Formula i)
    {
        visitLeft(i); cnvs().symbol(c("∪", "\\/")); visitRight(i); return i;
    }

    @Override public Formula visit_SET_INTERSECTION(Formula i)
    {
        visitLeft(i); cnvs().symbol(c("∩", "/\\")); visitRight(i); return i;
    }

    @Override public Formula visit_CARTESIAN_PRODUCT(Formula i)
    {
        visitLeft(i); cnvs().symbol(c("×", "**")); visitRight(i); return i;
    }

    @Override public Formula visit_RELATION(Formula i)
    {
        visitLeft(i); cnvs().symbol(c("↔ ", "<->")); visitRight(i); return i;
    }

    @Override public Formula visit_TOTAL_RELATION(Formula i)
    {
        visitLeft(i); cnvs().symbol(c(" ", "<<->")); visitRight(i); return i;
    }

    @Override public Formula visit_SURJECTIVE_RELATION(Formula i)
    {
        visitLeft(i); cnvs().symbol(c(" ", "<->>")); visitRight(i); return i;
    }

    @Override public Formula visit_SURJECTIVE_TOTAL_RELATION(Formula i)
    {
        visitLeft(i); cnvs().symbol(c(" ", "<<->>")); visitRight(i); return i;
    }

    @Override public Formula visit_PARTIAL_FUNCTION(Formula i)
    {
        visitLeft(i); cnvs().symbol(c("⇸ ", "+->")); visitRight(i); return i;
    }

    @Override public Formula visit_TOTAL_FUNCTION(Formula i)
    {
        visitLeft(i); cnvs().symbol(c("→ ", "-->")); visitRight(i); return i;
    }

    @Override public Formula visit_PARTIAL_INJECTION(Formula i)
    {
        visitLeft(i); cnvs().symbol(c("⤔ ", ">+>")); visitRight(i); return i;
    }

    @Override public Formula visit_TOTAL_INJECTION(Formula i)
    {
        visitLeft(i); cnvs().symbol(c("↣ ", ">->")); visitRight(i); return i;
    }

    @Override public Formula visit_PARTIAL_SURJECTION(Formula i)
    {
        visitLeft(i); cnvs().symbol(c("⤀ ", "+->>")); visitRight(i); return i;
    }

    @Override public Formula visit_TOTAL_SURJECTION(Formula i)
    {
        visitLeft(i); cnvs().symbol(c("↠ ", "-->>")); visitRight(i); return i;
    }

    @Override public Formula visit_TOTAL_BIJECTION(Formula i)
    {
        visitLeft(i); cnvs().symbol(c("⤖ ", ">->>")); visitRight(i); return i;
    }

    @Override public Formula visit_FORWARD_COMPOSITION(Formula i)
    {
        visitLeft(i); cnvs().symbol(";"); visitRight(i); return i;
    }

    @Override public Formula visit_BACKWARD_COMPOSITION(Formula i)
    {
        visitLeft(i); cnvs().symbol(c("∘", "circ")); visitRight(i); return i;
    }

    @Override public Formula visit_DOMAIN_RESTRICTION(Formula i)
    {
        visitLeft(i); cnvs().symbol(c("◁", " <| ")); visitRight(i); return i;
    }

    @Override public Formula visit_DOMAIN_SUBTRACTION(Formula i)
    {
        visitLeft(i); cnvs().symbol(c("⩤", " <<| ")); visitRight(i); return i;
    }

    @Override public Formula visit_RANGE_RESTRICTION(Formula i)
    {
        visitLeft(i); cnvs().symbol(c("▷", " |> ")); visitRight(i); return i;
    }

    @Override public Formula visit_RANGE_SUBTRACTION(Formula i)
    {
        visitLeft(i); cnvs().symbol(c("⩥", " |>> ")); visitRight(i); return i;
    }

    @Override public Formula visit_OVERRIDE(Formula i)
    {
        visitLeft(i); cnvs().symbol(c("", " <+ ")); visitRight(i); return i;
    }

    @Override public Formula visit_DIRECT_PRODUCT(Formula i)
    {
        visitLeft(i); cnvs().symbol(c("⊗", "><")); visitRight(i); return i;
    }

    @Override public Formula visit_PARALLEL_PRODUCT(Formula i)
    {
        visitLeft(i); cnvs().symbol(c("∥", "||")); visitRight(i); return i;
    }

    @Override public Formula visit_POWER_SET(Formula i)
    {
        cnvs().symbol(c("ℙ(", "POW(")); visitChild(i); cnvs().symbol(")"); return i;
    }

    @Override public Formula visit_POWER1_SET(Formula i)
    {
        cnvs().symbol(c("ℙ1(", "POW1(")); visitChild(i); cnvs().symbol(")"); return i;
    }

    @Override public Formula visit_Q_UNION(Formula i)
    {
        cnvs().symbol(c("⋃", "UNION ")); visitChildNum(i, 0); cnvs().symbol("."); visitChildNum(i, 1); cnvs().symbol("|"); visitChildNum(i, 2); return i;
    }

    @Override public Formula visit_Q_INTER(Formula i)
    {
        cnvs().symbol(c("⋂", "INTER ")); visitChildNum(i, 0); cnvs().symbol("."); visitChildNum(i, 1); cnvs().symbol("|"); visitChildNum(i, 2); return i;
    }

    @Override public Formula visit_LAMBDA_ABSTRACTION(Formula i)
    {
        cnvs().symbol(c("(λ", "(%")); visitChildNum(i, 0); cnvs().symbol(c("·",".")); visitChildNum(i, 1); cnvs().symbol("|"); visitChildNum(i, 2); cnvs().symbol(")"); return i;
    }

    @Override public Formula visit_SET_COMPREHENSION(Formula i)
    {
        cnvs().symbol("{"); visitChildNum(i, 0); cnvs().symbol(c("·", ".")); visitChildNum(i, 1); cnvs().symbol("|"); visitChildNum(i, 2); cnvs().symbol("}"); return i;
    }

    @Override public Formula visit_ENUMERATED_SET(Formula i)
    {
        cnvs().symbol("{"); visitChildren(i, ()->{cnvs().symbol(","); }); cnvs().symbol("}"); return i;
    }

    @Override public Formula visit_LIST_OF_VARIABLES(Formula i)
    {
        visitChildren(i, ()->{cnvs().symbol(","); }); return i;
    }

    @Override public Formula visit_LIST_OF_NONFREE_VARIABLES(Formula i)
    {
        visitChildren(i, ()->{cnvs().symbol(","); }); return i;
    }

    @Override public Formula visit_LIST_OF_EXPRESSIONS(Formula i)
    {
        visitChildren(i, ()->{cnvs().symbol(","); }); return i;
    }

    @Override public Formula visit_MULTIPLICATION(Formula i)
    {
        visitLeft(i); cnvs().symbol(c("∗", "*")); visitRight(i); return i;
    }

    @Override public Formula visit_DIVISION(Formula i)
    {
        visitLeft(i); cnvs().symbol(c("÷", "/")); visitRight(i); return i;
    }

    @Override public Formula visit_UP_TO(Formula i)
    {
        visitLeft(i); cnvs().symbol(c("‥", "..")); visitRight(i); return i;
    }

    @Override public Formula visit_EMPTY_SET(Formula i)
    {
        cnvs().symbol(c("∅", "{}")); return i;
    }

    @Override public Formula visit_NAT_SET(Formula i)
    {
        cnvs().primitiveSet(c("ℕ", "NAT")); return i;
    }

    @Override public Formula visit_NAT1_SET(Formula i)
    {
        cnvs().primitiveSet(c("ℕ1", "NAT1")); return i;
    }

    @Override public Formula visit_INT_SET(Formula i)
    {
        cnvs().primitiveSet(c("ℤ", "INT")); return i;
    }

    @Override public Formula visit_MAPSTO(Formula i)
    {
        visitLeft(i); cnvs().symbol(c("↦", "|->")); visitRight(i); return i;
    }

    @Override public Formula visit_ANY_SYMBOL(Formula i)
    {
        cnvs().any(Symbols.name(i.intData())); return i;
    }

    @Override public Formula visit_NUMBER_SYMBOL(Formula i)
    {
        cnvs().number(Symbols.name(i.intData())); return i;
    }

    @Override public Formula visit_PREDICATE_SYMBOL(Formula i)
    {
        cnvs().predicate(Symbols.name(i.intData())); return i;
    }

    @Override public Formula visit_EXPRESSION_SYMBOL(Formula i)
    {
        cnvs().expression(Symbols.name(i.intData())); return i;
    }

    @Override public Formula visit_SET_SYMBOL(Formula i)
    {
        cnvs().set(Symbols.name(i.intData())); return i;
    }

    @Override public Formula visit_CONSTANT_SYMBOL(Formula i)
    {
        cnvs().constant(Symbols.name(i.intData())); return i;
    }

    @Override public Formula visit_VARIABLE_SYMBOL(Formula i)
    {
        cnvs().variable(
            Symbols.name(
                i.intData()));

        return i;
    }

    @Override public Formula visit_VARIABLE_PRIM_SYMBOL(Formula i)
    {
        cnvs().variable(
            Symbols.name(
                i.intData()));
        cnvs().symbol("'");

        return i;
    }

    @Override public Formula visit_VARIABLE_NONFREE_SYMBOL(Formula i)
    {
        cnvs().nonFreeVariable(
            Symbols.name(
                i.intData()));

        return i;
    }

    @Override public Formula visit_INVERT(Formula i)
    {
        visitChild(i); cnvs().symbol("~"); return i;
    }

    @Override public Formula visit_RELATION_IMAGE(Formula i)
    {
        visitLeft(i); cnvs().symbol("["); visitRight(i); cnvs().symbol("]"); return i;
    }

    @Override public Formula visit_G_UNION(Formula i)
    {
        cnvs().symbol("union("); visitChild(i); cnvs().symbol(")"); return i;
    }

    @Override public Formula visit_G_INTER(Formula i)
    {
        cnvs().symbol("inter("); visitChild(i); cnvs().symbol(")"); return i;
    }

    @Override public Formula visit_DOMAIN(Formula i)
    {
        cnvs().symbol("dom("); visitChild(i); cnvs().symbol(")"); return i;
    }

    @Override public Formula visit_RANGE(Formula i)
    {
        cnvs().symbol("ran("); visitChild(i); cnvs().symbol(")"); return i;
    }

    @Override public Formula visit_SET_MINUS(Formula i)
    {
        visitLeft(i); cnvs().symbol("\\"); visitRight(i); return i;
    }

    @Override public Formula visit_SET_COMPREHENSION_SPECIAL(Formula i)
    {
        cnvs().symbol("{"); visitLeft(i); cnvs().symbol("|"); visitRight(i); cnvs().symbol("}"); return i;
    }

    @Override public Formula visit_ADDITION(Formula i)
    {
        visitLeft(i); cnvs().symbol("+"); visitRight(i); return i;
    }

    @Override public Formula visit_SUBTRACTION(Formula i)
    {
        visitLeft(i); cnvs().symbol("-"); visitRight(i); return i;
    }

    @Override public Formula visit_MODULO(Formula i)
    {
        visitLeft(i); cnvs().symbol(" mod "); visitRight(i); return i;
    }

    @Override public Formula visit_EXPONENTIATION(Formula i)
    {
        visitLeft(i); cnvs().symbol("^"); visitRight(i); return i;
    }

    @Override public Formula visit_MINIMUM(Formula i)
    {
        cnvs().symbol("min("); visitChild(i); cnvs().symbol(")"); return i;
    }

    @Override public Formula visit_MAXIMUM(Formula i)
    {
        cnvs().symbol("max("); visitChild(i); cnvs().symbol(")"); return i;
    }

    @Override public Formula visit_TEST_BOOL(Formula i)
    {
        cnvs().symbol("bool("); visitChild(i); cnvs().symbol(")"); return i;
    }

    @Override public Formula visit_CARDINALITY(Formula i)
    {
        cnvs().symbol("card("); visitChild(i); cnvs().symbol(")"); return i;
    }

    @Override public Formula visit_ID_SET(Formula i)
    {
        cnvs().symbol(" id "); return i;
    }

    @Override public Formula visit_PRJ1(Formula i)
    {
        cnvs().symbol("prj1("); visitChild(i); cnvs().symbol(")"); return i;
    }

    @Override public Formula visit_PRJ2(Formula i)
    {
        cnvs().symbol("prj2("); visitChild(i); cnvs().symbol(")"); return i;
    }

    @Override public Formula visit_BOOL_SET(Formula i)
    {
        cnvs().primitiveSet("BOOL"); return i;
    }

    @Override public Formula visit_FUNC_APP(Formula i)
    {
        visitLeft(i); cnvs().symbol("("); visitRight(i); cnvs().symbol(")"); return i;
    }

    @Override public Formula visit_FUNC_INV_APP(Formula i)
    {
        visitLeft(i); cnvs().symbol("~("); visitRight(i); cnvs().symbol(")"); return i;
    }

}
