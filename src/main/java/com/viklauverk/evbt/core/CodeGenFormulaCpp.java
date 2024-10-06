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

package com.viklauverk.evbt.core;

import com.viklauverk.evbt.core.Formula;

import java.util.List;
import java.util.LinkedList;

public class CodeGenFormulaCpp extends CodeGenFormula
{
    CodeGenFormulaCpp(CodeGenCpp codegen, PlanImplementation plan)
    {
        super(codegen, plan);
    }

    @Override public Formula visit_SET_SYMBOL(Formula i)
    {
        String name = Symbols.name(i.intData());
        if (name.equals("String"))
        {
            cnvs().append("std::string");
        }
        else
        {
            cnvs().set(name); return i;
        }
        return i;
    }

    @Override public Formula visit_CONSTANT_SYMBOL(Formula i)
    {
        String name = Symbols.name(i.intData());
        if (name.equals("TRUE"))
        {
            cnvs().append("true");
        }
        else
        if (name.equals("FALSE"))
        {
            cnvs().append("false");
        }
        else
        if (name.equals("EmptyString"))
        {
            cnvs().append("\"\"");
        }
        else
        {
            cnvs().variable(name);
        }
        return i;
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

    @Override public Formula visit_BECOME_IN(Formula i)
    {
        visitLeft(i); cnvs().append(" = ");
        if (i.right().is(Node.ENUMERATED_SET)) {
            cnvs().append(i.right().child(0).toString());
        }
        else
        if (i.right().is(Node.NAT_SET)) {
            cnvs().append("4711");
        }
        return i;
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
        visitLeft(i);  cnvs().append(" + "); visitRight(i); return i;
        /*
        Implementation imp = plan().getImplementation(i);

        imp.generateAddition(this, plan(), i, i.left(), i.right());
        */
        //return i;
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

    @Override public Formula visit_FUNC_APP(Formula f)
    {
        // Try the common special cases.
        Formula ff = super.visit_FUNC_APP(f);

        if (ff != null) return ff;

        // Plain func application, not compilable.
        visitLeft(f); cnvs().append("("); visitRight(f); cnvs().append(")");
        return f;
    }

    @Override public Formula visit_SET_COMPREHENSION(Formula f)
    {
        //cnvs().append("{"); visitChildNum(i, 0); cnvs().append(c("·", ".")); visitChildNum(i, 1); cnvs().append("|"); visitChildNum(i, 2); cnvs().append("}"); return i;
        cnvs().append("<SET COMPREHENSION "+f+">");
        return f;
    }
}
