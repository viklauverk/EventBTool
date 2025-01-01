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

package com.viklauverk.evbt.core.codegen;

import com.viklauverk.evbt.core.implementation.PlanImplementation;
import com.viklauverk.evbt.core.sys.Formula;

public class CodeGenFormulaJava extends CodeGenFormula
{
    CodeGenFormulaJava(CodeGenJava codegen, PlanImplementation plan)
    {
        super(codegen, plan);
    }

    @Override public Formula visit_CONJUNCTION(Formula i)
    {
        visitLeft(i); cnvs().append(" && "); visitRight(i); return i;
    }

    @Override public Formula visit_NEGATION(Formula i)
    {
        cnvs().append(" ~("); visitChild(i); cnvs().append(")"); return i;
    }

    @Override public Formula visit_DISJUNCTION(Formula i)
    {
        visitLeft(i); cnvs().append(" || "); visitRight(i); return i;
    }

    @Override public Formula visit_EQUALS(Formula i)
    {
        visitLeft(i); cnvs().append(" == "); visitRight(i); return i;
    }

    @Override public Formula visit_NOT_EQUALS(Formula i)
    {
        visitLeft(i);  cnvs().append(" != "); visitRight(i); return i;
    }

    @Override public Formula visit_LESS_THAN(Formula i)
    {
        visitLeft(i);  cnvs().append(" < "); visitRight(i); return i;
    }

    @Override public Formula visit_GREATER_THAN(Formula i)
    {
        visitLeft(i);  cnvs().append(" > "); visitRight(i); return i;
    }

    @Override public Formula visit_LESS_THAN_OR_EQUAL(Formula i)
    {
        visitLeft(i);  cnvs().append(" <= ");  visitRight(i); return i;
    }

    @Override public Formula visit_GREATER_THAN_OR_EQUAL(Formula i)
    {
        visitLeft(i); cnvs().append(" >= "); visitRight(i); return i;
    }

    @Override public Formula visit_ADDITION(Formula i)
    {
        visitLeft(i); cnvs().append(" + "); visitRight(i); return i;
    }

    @Override public Formula visit_SUBTRACTION(Formula i)
    {
        visitLeft(i);  cnvs().append(" - "); visitRight(i); return i;
    }

    @Override public Formula visit_MULTIPLICATION(Formula i)
    {
        visitLeft(i);  cnvs().append(" * "); visitRight(i); return i;
    }

    @Override public Formula visit_DIVISION(Formula i)
    {
        visitLeft(i);  cnvs().append(" / "); visitRight(i); return i;
    }
}
