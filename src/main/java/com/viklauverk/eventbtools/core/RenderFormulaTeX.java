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

public class RenderFormulaTeX extends RenderFormulaUnicode
{
    public RenderFormulaTeX(Canvas canvas)
    {
        super(canvas);
        limitToAscii();
    }

    @Override public Formula visit_BECOME_EQ(Formula i)
    {
        visitLeft(i); cnvs().append("\\bcmeq "); visitRight(i); return i;
    }

    @Override public Formula visit_BECOME_IN(Formula i)
    {
        visitLeft(i); cnvs().append("\\bcmin "); visitRight(i); return i;
    }

    @Override public Formula visit_BECOME_SUCH(Formula i)
    {
        visitLeft(i); cnvs().append("\\bcmsuch "); visitRight(i); return i;
    }

    @Override public Formula visit_FALSE(Formula i)
    {
        cnvs().append("\\bfalse "); return i;
    }

    @Override public Formula visit_TRUE(Formula i)
    {
        cnvs().append("\\btrue "); return i;
    }

    @Override public Formula visit_APPLICATION(Formula i)
    {
        visitLeft(i); visitRight(i); return i;
    }

    @Override public Formula visit_CONJUNCTION(Formula i)
    {
        visitLeft(i);
        checkNewLineBefore(i);
        cnvs().append(" \\land ");
        checkNewLineAfter(i);
        visitRight(i); return i;
    }

    @Override public Formula visit_DISJUNCTION(Formula i)
    {
        visitLeft(i);
        checkNewLineBefore(i);
        cnvs().append(" \\lor ");
        checkNewLineAfter(i);
        visitRight(i); return i;
    }

    @Override public Formula visit_NEGATION(Formula i)
    {
        cnvs().append(" \\lnot "); visitChild(i); return i;
    }

    @Override public Formula visit_IMPLICATION(Formula i)
    {
        visitLeft(i);
        checkNewLineBefore(i);
        cnvs().append(" \\limp ");
        checkNewLineAfter(i);
        visitRight(i); return i;
    }

    @Override public Formula visit_EQUALS(Formula i)
    {
        visitLeft(i); cnvs().append("="); visitRight(i); return i;
    }

    @Override public Formula visit_NOT_EQUALS(Formula i)
    {
        visitLeft(i); cnvs().append("\\neq "); visitRight(i); return i;
    }

    @Override public Formula visit_LESS_THAN_OR_EQUAL(Formula i)
    {
        visitLeft(i); cnvs().append("\\le "); visitRight(i); return i;
    }

    @Override public Formula visit_GREATER_THAN_OR_EQUAL(Formula i)
    {
        visitLeft(i); cnvs().append("\\ge "); visitRight(i); return i;
    }

    @Override public Formula visit_MEMBERSHIP(Formula i)
    {
        visitLeft(i); cnvs().append("\\in "); visitRight(i); return i;
    }

    @Override public Formula visit_EMPTY_SET(Formula i)
    {
        cnvs().append("\\emptyset "); return i;
    }

    @Override public Formula visit_NAT_SET(Formula i)
    {
        cnvs().append("\\nat "); return i;
    }

    @Override public Formula visit_NAT1_SET(Formula i)
    {
        cnvs().append("\\natn "); return i;
    }

    @Override public Formula visit_INT_SET(Formula i)
    {
        cnvs().append("\\intg "); return i;
    }

    @Override public Formula visit_SET_COMPREHENSION(Formula i)
    {
        cnvs().append("\\{"); visitChildNum(i, 0); cnvs().append("\\qdot "); visitChildNum(i, 1); cnvs().append("\\mid "); visitChildNum(i, 2); cnvs().append("\\}"); return i;
    }

    @Override public Formula visit_SET_COMPREHENSION_SPECIAL(Formula i)
    {
        cnvs().append("\\{"); visitLeft(i); cnvs().append("\\mid "); visitRight(i); cnvs().append("\\}"); return i;
    }

    @Override public Formula visit_ENUMERATED_SET(Formula i)
    {
        cnvs().append("\\{"); visitChildren(i, ()->{cnvs().append(",");}); cnvs().append("\\}"); return i;
    }

    @Override public Formula visit_POWER_SET(Formula i)
    {
        cnvs().append("\\mathbb{P}("); visitChild(i); cnvs().append(")"); return i;
    }

    @Override public Formula visit_POWER1_SET(Formula i)
    {
        cnvs().append("\\mathbb{P_1}("); visitChild(i); cnvs().append(")"); return i;
    }

    @Override public Formula visit_RELATION(Formula i)
    {
        visitLeft(i); cnvs().append("\\rel "); visitRight(i); return i;
    }

    @Override public Formula visit_TOTAL_RELATION(Formula i)
    {
        visitLeft(i); cnvs().append("\\trel "); visitRight(i); return i;
    }

    @Override public Formula visit_SURJECTIVE_RELATION(Formula i)
    {
        visitLeft(i); cnvs().append("\\srel "); visitRight(i); return i;
    }

    @Override public Formula visit_SURJECTIVE_TOTAL_RELATION(Formula i)
    {
        visitLeft(i); cnvs().append("\\strel "); visitRight(i); return i;
    }

    @Override public Formula visit_PARTIAL_FUNCTION(Formula i)
    {
        visitLeft(i); cnvs().append("\\pfun "); visitRight(i); return i;
    }

    @Override public Formula visit_TOTAL_FUNCTION(Formula i)
    {
        visitLeft(i); cnvs().append("\\tfun "); visitRight(i); return i;
    }

    @Override public Formula visit_PARTIAL_INJECTION(Formula i)
    {
        visitLeft(i); cnvs().append("\\pinj "); visitRight(i); return i;
    }

    @Override public Formula visit_TOTAL_INJECTION(Formula i)
    {
        visitLeft(i); cnvs().append("\\tinj "); visitRight(i); return i;
    }

    @Override public Formula visit_PARTIAL_SURJECTION(Formula i)
    {
        visitLeft(i); cnvs().append("\\psur "); visitRight(i); return i;
    }

    @Override public Formula visit_TOTAL_SURJECTION(Formula i)
    {
        visitLeft(i); cnvs().append("\\tsur "); visitRight(i); return i;
    }

    @Override public Formula visit_TOTAL_BIJECTION(Formula i)
    {
        visitLeft(i); cnvs().append("\\tbij "); visitRight(i); return i;
    }

    @Override public Formula visit_MAPSTO(Formula i)
    {
        visitLeft(i); cnvs().append("\\mapsto "); visitRight(i); return i;
    }

    @Override public Formula visit_CARTESIAN_PRODUCT(Formula i)
    {
        visitLeft(i); cnvs().append("\\cprod "); visitRight(i); return i;
    }

    @Override public Formula visit_NOT_MEMBERSHIP(Formula i)
    {
        visitLeft(i); cnvs().append("\\notin "); visitRight(i); return i;
    }

    @Override public Formula visit_SUBSET(Formula i)
    {
        visitLeft(i); cnvs().append("\\subseteq "); visitRight(i); return i;
    }

    @Override public Formula visit_STRICT_SUBSET(Formula i)
    {
        visitLeft(i); cnvs().append("\\subset "); visitRight(i); return i;
    }

    @Override public Formula visit_NOT_SUBSET(Formula i)
    {
        visitLeft(i); cnvs().append("\\not\\subseteq "); visitRight(i); return i;
    }

    @Override public Formula visit_NOT_STRICT_SUBSET(Formula i)
    {
        visitLeft(i); cnvs().append("\\not\\subset "); visitRight(i); return i;
    }

    @Override public Formula visit_SET_UNION(Formula i)
    {
        visitLeft(i); cnvs().append("\\bunion "); visitRight(i); return i;
    }

    @Override public Formula visit_SET_INTERSECTION(Formula i)
    {
        visitLeft(i); cnvs().append("\\binter "); visitRight(i); return i;
    }

    @Override public Formula visit_DIVISION(Formula i)
    {
        visitLeft(i); cnvs().append("\\div "); visitRight(i); return i;
    }

    @Override public Formula visit_BACKWARD_COMPOSITION(Formula i)
    {
        visitLeft(i); cnvs().append("\\bcomp "); visitRight(i); return i;
    }

    @Override public Formula visit_MODULO(Formula i)
    {
        visitLeft(i); cnvs().append("\\bmod "); visitRight(i); return i;
    }

    @Override public Formula visit_EXPONENTIATION(Formula i)
    {
        visitLeft(i); cnvs().append("\\expn "); visitRight(i); return i;
    }

    @Override public Formula visit_MINIMUM(Formula i)
    {
        cnvs().symbol("\\min("); visitChild(i); cnvs().symbol(")"); return i;
    }

    @Override public Formula visit_MAXIMUM(Formula i)
    {
        cnvs().symbol("\\max("); visitChild(i); cnvs().symbol(")"); return i;
    }

    @Override public Formula visit_TEST_BOOL(Formula i)
    {
        cnvs().append("\\bool ("); visitChild(i); cnvs().append(")"); return i;
    }

    @Override public Formula visit_CARDINALITY(Formula i)
    {
        cnvs().append("\\card ("); visitChild(i); cnvs().append(")"); return i;
    }

    @Override public Formula visit_ID_SET(Formula i)
    {
        cnvs().symbol("\\id "); return i;
    }

    @Override public Formula visit_DOMAIN(Formula i)
    {
        cnvs().append("\\dom ("); visitChild(i); cnvs().append(")"); return i;
    }

    @Override public Formula visit_RANGE(Formula i)
    {
        cnvs().append("\\ran ("); visitChild(i); cnvs().append(")"); return i;
    }

    @Override public Formula visit_DOMAIN_RESTRICTION(Formula i)
    {
        visitLeft(i); cnvs().append("\\domres "); visitRight(i); return i;
    }

    @Override public Formula visit_DOMAIN_SUBTRACTION(Formula i)
    {
        visitLeft(i); cnvs().append("\\domsub "); visitRight(i); return i;
    }

    @Override public Formula visit_RANGE_RESTRICTION(Formula i)
    {
        visitLeft(i); cnvs().append("\\ranres "); visitRight(i); return i;
    }

    @Override public Formula visit_RANGE_SUBTRACTION(Formula i)
    {
        visitLeft(i); cnvs().append("\\ransub "); visitRight(i); return i;
    }

    @Override public Formula visit_UNIVERSALQ(Formula i)
    {
        cnvs().append("\\forall "); visitLeft(i); cnvs().append("\\qdot "); visitRight(i); return i;
    }

    @Override public Formula visit_EXISTENTIALQ(Formula i)
    {
        cnvs().append("\\exists "); visitLeft(i); cnvs().append("\\qdot "); visitRight(i); return i;
    }

    @Override public Formula visit_DIRECT_PRODUCT(Formula i)
    {
        visitLeft(i); cnvs().append("\\dprod "); visitRight(i); return i;
    }

    @Override public Formula visit_FORWARD_COMPOSITION(Formula i)
    {
        visitLeft(i); cnvs().append("\\fcomp "); visitRight(i); return i;
    }

    @Override public Formula visit_FINITE(Formula i)
    {
        cnvs().append("\\finite ("); visitChild(i); cnvs().append(")"); return i;
    }

    @Override public Formula visit_PARTITION(Formula i)
    {
        cnvs().append("\\partition ("); visitLeft(i); cnvs().append(",\\allowbreak "); visitRight(i); cnvs().append(")"); return i;
    }

    @Override public Formula visit_G_UNION(Formula i)
    {
        cnvs().append("\\union ("); visitChild(i); cnvs().append(")"); return i;
    }

    @Override public Formula visit_G_INTER(Formula i)
    {
        cnvs().append("\\inter ("); visitChild(i); cnvs().append(")"); return i;
    }

    @Override public Formula visit_Q_UNION(Formula i)
    {
        cnvs().append("\\Union "); visitChildNum(i, 0); cnvs().append("\\qdot "); visitChildNum(i, 1); cnvs().append("\\mid "); visitChildNum(i, 2); return i;
    }

    @Override public Formula visit_Q_INTER(Formula i)
    {
        cnvs().append("\\Inter "); visitChildNum(i, 0); cnvs().append("\\qdot "); visitChildNum(i, 1); cnvs().append("\\mid "); visitChildNum(i, 2); return i;
    }

    @Override public Formula visit_LAMBDA_ABSTRACTION(Formula i)
    {
        cnvs().append("(\\lambda "); visitChildNum(i, 0); cnvs().append("\\qdot "); visitChildNum(i, 1); cnvs().append("\\mid "); visitChildNum(i, 2); cnvs().append(")"); return i;
    }

    @Override public Formula visit_EQUIVALENCE(Formula i)
    {
        visitLeft(i); cnvs().append("\\leqv "); visitRight(i); return i;
    }

    @Override public Formula visit_OF_TYPE(Formula i)
    {
        visitLeft(i); cnvs().append("\\oftype "); visitRight(i); return i;
    }

    @Override public Formula visit_OVERRIDE(Formula i)
    {
        visitLeft(i); cnvs().append("\\ovl "); visitRight(i); return i;
    }

    @Override public Formula visit_PARALLEL_PRODUCT(Formula i)
    {
        visitLeft(i); cnvs().append("\\pprod "); visitRight(i); return i;
    }

    @Override public Formula visit_PRJ1(Formula i)
    {
        cnvs().append("\\prjone ("); visitChild(i); cnvs().append(")"); return i;
    }

    @Override public Formula visit_PRJ2(Formula i)
    {
        cnvs().append("\\prjtwo ("); visitChild(i); cnvs().append(")"); return i;
    }

    @Override public Formula visit_SET_MINUS(Formula i)
    {
        visitLeft(i); cnvs().append("\\setminus "); visitRight(i); return i;
    }

    @Override public Formula visit_LIST_OF_EXPRESSIONS(Formula i)
    {
        visitChildren(i, ()->{cnvs().append(",\\allowbreak "); }); return i;
    }

}
