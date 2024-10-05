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

package com.viklauverk.evbt.core;

import com.viklauverk.evbt.core.Formula;

import java.util.List;
import java.util.LinkedList;

public class RenderFormulaTeX extends RenderFormulaUnicode
{
    @SuppressWarnings("this-escape")
    public RenderFormulaTeX(Canvas canvas)
    {
        super(canvas);
        limitToAscii();
    }

    @Override public Formula visit_BECOME_EQ(Formula i)
    {
        visitLeft(i); cnvs().append("\\bcmeq "); visitMeta(i); visitRight(i); return i;
    }

    @Override public Formula visit_BECOME_IN(Formula i)
    {
        visitLeft(i); cnvs().append("\\bcmin "); visitMeta(i); visitRight(i); return i;
    }

    @Override public Formula visit_BECOME_SUCH(Formula i)
    {
        visitLeft(i); cnvs().append("\\bcmsuch "); visitMeta(i); visitRight(i); return i;
    }

    @Override public Formula visit_FALSE(Formula i)
    {
        cnvs().append("\\bfalse "); visitMeta(i); return i;
    }

    @Override public Formula visit_TRUE(Formula i)
    {
        cnvs().append("\\btrue "); visitMeta(i); return i;
    }

    @Override public Formula visit_APPLICATION(Formula i)
    {
        visitLeft(i); cnvs().symbol("["); visitRight(i);  cnvs().symbol("]"); visitMeta(i); return i;
    }

    @Override public Formula visit_CONJUNCTION(Formula i)
    {
        visitLeft(i);
        checkNewLineBefore(i);
        cnvs().append(" \\land ");
        visitMeta(i);
        checkNewLineAfter(i);
        visitRight(i); return i;
    }

    @Override public Formula visit_DISJUNCTION(Formula i)
    {
        visitLeft(i);
        checkNewLineBefore(i);
        cnvs().append(" \\lor ");
        visitMeta(i);
        checkNewLineAfter(i);
        visitRight(i); return i;
    }

    @Override public Formula visit_NEGATION(Formula i)
    {
        cnvs().append(" \\lnot "); visitMeta(i); visitChild(i); return i;
    }

    @Override public Formula visit_IMPLICATION(Formula i)
    {
        visitLeft(i);
        checkNewLineBefore(i);
        cnvs().append(" \\limp ");
        visitMeta(i);
        checkNewLineAfter(i);
        visitRight(i); return i;
    }

    @Override public Formula visit_EQUALS(Formula i)
    {
        visitLeft(i); cnvs().append("="); visitMeta(i); visitRight(i); return i;
    }

    @Override public Formula visit_NOT_EQUALS(Formula i)
    {
        visitLeft(i); cnvs().append("\\neq "); visitMeta(i); visitRight(i); return i;
    }

    @Override public Formula visit_LESS_THAN_OR_EQUAL(Formula i)
    {
        visitLeft(i); cnvs().append("\\le "); visitMeta(i); visitRight(i); return i;
    }

    @Override public Formula visit_GREATER_THAN_OR_EQUAL(Formula i)
    {
        visitLeft(i); cnvs().append("\\ge "); visitMeta(i); visitRight(i); return i;
    }

    @Override public Formula visit_MEMBERSHIP(Formula i)
    {
        visitLeft(i); cnvs().append("\\in "); visitMeta(i); visitRight(i); return i;
    }

    @Override public Formula visit_EMPTY_SET(Formula i)
    {
        cnvs().append("\\emptyset "); visitMeta(i); return i;
    }

    @Override public Formula visit_NAT_SET(Formula i)
    {
        cnvs().append("\\nat "); visitMeta(i); return i;
    }

    @Override public Formula visit_NAT1_SET(Formula i)
    {
        cnvs().append("\\natn "); visitMeta(i); return i;
    }

    @Override public Formula visit_INT_SET(Formula i)
    {
        cnvs().append("\\intg "); visitMeta(i); return i;
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
        cnvs().append("\\mathbb{P}"); visitMeta(i); cnvs().append("("); visitChild(i); cnvs().append(")"); return i;
    }

    @Override public Formula visit_POWER1_SET(Formula i)
    {
        cnvs().append("\\mathbb{P_1}"); visitMeta(i); cnvs().append("("); visitChild(i); cnvs().append(")"); return i;
    }

    @Override public Formula visit_RELATION(Formula i)
    {
        visitLeft(i); cnvs().append("\\rel "); visitMeta(i); visitRight(i); return i;
    }

    @Override public Formula visit_TOTAL_RELATION(Formula i)
    {
        visitLeft(i); cnvs().append("\\trel "); visitMeta(i); visitRight(i); return i;
    }

    @Override public Formula visit_SURJECTIVE_RELATION(Formula i)
    {
        visitLeft(i); cnvs().append("\\srel "); visitMeta(i); visitRight(i); return i;
    }

    @Override public Formula visit_SURJECTIVE_TOTAL_RELATION(Formula i)
    {
        visitLeft(i); cnvs().append("\\strel "); visitMeta(i); visitRight(i); return i;
    }

    @Override public Formula visit_PARTIAL_FUNCTION(Formula i)
    {
        visitLeft(i); cnvs().append("\\pfun "); visitMeta(i); visitRight(i); return i;
    }

    @Override public Formula visit_TOTAL_FUNCTION(Formula i)
    {
        visitLeft(i); cnvs().append("\\tfun "); visitMeta(i); visitRight(i); return i;
    }

    @Override public Formula visit_PARTIAL_INJECTION(Formula i)
    {
        visitLeft(i); cnvs().append("\\pinj "); visitMeta(i); visitRight(i); return i;
    }

    @Override public Formula visit_TOTAL_INJECTION(Formula i)
    {
        visitLeft(i); cnvs().append("\\tinj "); visitMeta(i); visitRight(i); return i;
    }

    @Override public Formula visit_PARTIAL_SURJECTION(Formula i)
    {
        visitLeft(i); cnvs().append("\\psur "); visitMeta(i); visitRight(i); return i;
    }

    @Override public Formula visit_TOTAL_SURJECTION(Formula i)
    {
        visitLeft(i); cnvs().append("\\tsur "); visitMeta(i); visitRight(i); return i;
    }

    @Override public Formula visit_TOTAL_BIJECTION(Formula i)
    {
        visitLeft(i); cnvs().append("\\tbij "); visitMeta(i); visitRight(i); return i;
    }

    @Override public Formula visit_MAPSTO(Formula i)
    {
        visitLeft(i); cnvs().append("\\mapsto "); visitMeta(i); visitRight(i); return i;
    }

    @Override public Formula visit_CARTESIAN_PRODUCT(Formula i)
    {
        visitLeft(i); cnvs().append("\\cprod "); visitMeta(i); visitRight(i); return i;
    }

    @Override public Formula visit_NOT_MEMBERSHIP(Formula i)
    {
        visitLeft(i); cnvs().append("\\notin "); visitMeta(i); visitRight(i); return i;
    }

    @Override public Formula visit_SUBSET(Formula i)
    {
        visitLeft(i); cnvs().append("\\subseteq "); visitMeta(i); visitRight(i); return i;
    }

    @Override public Formula visit_STRICT_SUBSET(Formula i)
    {
        visitLeft(i); cnvs().append("\\subset "); visitMeta(i); visitRight(i); return i;
    }

    @Override public Formula visit_NOT_SUBSET(Formula i)
    {
        visitLeft(i); cnvs().append("\\not\\subseteq "); visitMeta(i); visitRight(i); return i;
    }

    @Override public Formula visit_NOT_STRICT_SUBSET(Formula i)
    {
        visitLeft(i); cnvs().append("\\not\\subset "); visitMeta(i); visitRight(i); return i;
    }

    @Override public Formula visit_SET_UNION(Formula i)
    {
        visitLeft(i); cnvs().append("\\bunion "); visitMeta(i); visitRight(i); return i;
    }

    @Override public Formula visit_SET_INTERSECTION(Formula i)
    {
        visitLeft(i); cnvs().append("\\binter "); visitMeta(i); visitRight(i); return i;
    }

    @Override public Formula visit_DIVISION(Formula i)
    {
        visitLeft(i); cnvs().append("\\div "); visitMeta(i); visitRight(i); return i;
    }

    @Override public Formula visit_BACKWARD_COMPOSITION(Formula i)
    {
        visitLeft(i); cnvs().append("\\bcomp "); visitMeta(i); visitRight(i); return i;
    }

    @Override public Formula visit_MODULO(Formula i)
    {
        visitLeft(i); cnvs().append("\\bmod "); visitMeta(i); visitRight(i); return i;
    }

    @Override public Formula visit_EXPONENTIATION(Formula i)
    {
        visitLeft(i); cnvs().append("\\expn "); visitMeta(i); visitRight(i); return i;
    }

    @Override public Formula visit_MINIMUM(Formula i)
    {
        cnvs().symbol("\\min "); visitMeta(i); cnvs().append("("); visitChild(i); cnvs().symbol(")"); return i;
    }

    @Override public Formula visit_MAXIMUM(Formula i)
    {
        cnvs().symbol("\\max "); visitMeta(i); cnvs().append("("); visitChild(i); cnvs().symbol(")"); return i;
    }

    @Override public Formula visit_TEST_BOOL(Formula i)
    {
        cnvs().append("\\bool "); visitMeta(i); cnvs().append("("); visitChild(i); cnvs().append(")"); return i;
    }

    @Override public Formula visit_CARDINALITY(Formula i)
    {
        cnvs().append("\\card "); visitMeta(i); cnvs().append("("); visitChild(i); cnvs().append(")"); return i;
    }

    @Override public Formula visit_ID_SET(Formula i)
    {
        cnvs().symbol("\\id "); visitMeta(i); return i;
    }

    @Override public Formula visit_DOMAIN(Formula i)
    {
        cnvs().append("\\dom "); visitMeta(i); cnvs().append("("); visitChild(i); cnvs().append(")"); return i;
    }

    @Override public Formula visit_RANGE(Formula i)
    {
        cnvs().append("\\ran "); visitMeta(i); cnvs().append("("); visitChild(i); cnvs().append(")"); return i;
    }

    @Override public Formula visit_DOMAIN_RESTRICTION(Formula i)
    {
        visitLeft(i); cnvs().append("\\domres "); visitMeta(i); visitRight(i); return i;
    }

    @Override public Formula visit_DOMAIN_SUBTRACTION(Formula i)
    {
        visitLeft(i); cnvs().append("\\domsub "); visitMeta(i); visitRight(i); return i;
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
        cnvs().append("\\lambda "); visitChildNum(i, 0); cnvs().append("\\qdot "); visitChildNum(i, 1); cnvs().append("\\mid "); visitChildNum(i, 2); return i;
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
        cnvs().append("\\prjone "); return i;
    }

    @Override public Formula visit_PRJ2(Formula i)
    {
        cnvs().append("\\prjtwo "); return i;
    }

    @Override public Formula visit_SET_MINUS(Formula i)
    {
        visitLeft(i); cnvs().append("\\setminus "); visitRight(i); return i;
    }

    @Override public Formula visit_LIST_OF_EXPRESSIONS(Formula i)
    {
        visitChildren(i, ()->{cnvs().append(",\\allowbreak "); }); return i;
    }

    @Override public Formula visit_LIST_OF_PREDICATES(Formula i)
    {
        visitChildren(i, ()->{cnvs().append(",\\allowbreak "); }); return i;
    }

}
