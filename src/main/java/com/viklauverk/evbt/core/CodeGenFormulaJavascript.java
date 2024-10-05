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

public class CodeGenFormulaJavascript extends CodeGenFormula
{
    CodeGenFormulaJavascript(CodeGenJavascript codegen, PlanImplementation plan)
    {
        super(codegen, plan);
    }

    @Override public Formula visit_BECOME_EQ(Formula f)
    {
        // Try the common special cases.
        Formula ff = super.visit_BECOME_EQ(f);

        if (ff != null) return ff;

        // Plain assignment.
        visitLeft(f); cnvs().append(" = "); visitRight(f);
        return f;
    }

    @Override public Formula visit_VARIABLE_SYMBOL(Formula i)
    {
        String symbol = Symbols.name(i.intData());
        Variable variable = symbols().getVariable(symbol);
        if (variable == null)
        {
            System.out.println("Could not find \""+symbol+"\"");
            symbols().print();
        }
        ImplType type = variable.implType();

        if (variable.isParameter())
        {
            cnvs().append(Symbols.name(i.intData())); return i;
        }
        else
        {
            cnvs().append("this."+Symbols.name(i.intData())); return i;
        }
    }

    @Override public Formula visit_CONSTANT_SYMBOL(Formula i)
    {
        String symbol = Symbols.name(i.intData());
        Constant constant = symbols().getConstant(symbol);
        if (constant == null)
        {
            System.out.println("Could not find \""+symbol+"\"");
        }
        ImplType type = constant.implType();
        if (type != null && type.isCarrierSet())
        {
            cnvs().append("this."+type+"_"+constant.name()); return i;
        }
        else
        {
            cnvs().append("this."+constant.name()); return i;
        }
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

    @Override public Formula visit_EQUIVALENCE(Formula i)
    {
        visitLeft(i); visitRight(i); return i;
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

    @Override public Formula visit_MEMBERSHIP(Formula i)
    {
        Formula left = i.left();
        Formula right = i.right();
        boolean handled = false;
        if (left.is(Node.VARIABLE_SYMBOL))
        {
            if (right.is(Node.NAT_SET))
            {
                cnvs().append("typeof("); visitLeft(i); cnvs().append(") === 'number'");
                // Do not visit right.
                handled = true;
            }
        }
        if (!handled) {
            visitLeft(i); cnvs().append(":"); visitRight(i);
        }
        return i;
    }
}
