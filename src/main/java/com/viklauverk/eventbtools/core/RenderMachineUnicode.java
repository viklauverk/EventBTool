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

public class RenderMachineUnicode extends RenderMachine
{
    @Override
    public void visit_MachineStart(Machine mch)
    {
        cnvs().startLine();
        cnvs().keywordLeft(mch.machineOrRefinement());
        cnvs().space();
        cnvs().id(mch.name());
        cnvs().endLine();

        cnvs().hrule();

        if (mch.hasComment())
        {
            cnvs().acomment(mch.comment());
            cnvs().nl();
        }
    }

    @Override
    public void visit_RefinesStart(Machine mch)
    {
        cnvs().startLine();
        cnvs().keywordLeft("refines");
        cnvs().space();
    }

    @Override
    public void visit_Refines(Machine mch, Machine refines)
    {
        cnvs().id(refines.name());
        cnvs().space();
    }

    @Override
    public void visit_RefinesEnd(Machine mch)
    {
        cnvs().endLine();
    }

    @Override
    public void visit_SeesStart(Machine mch)
    {
        cnvs().startLine();
        cnvs().keywordLeft("sees");
        cnvs().space();
    }

    @Override
    public void visit_Sees(Machine mch, Context sees)
    {
        cnvs().id(sees.name());
        cnvs().space();
    }

    @Override
    public void visit_SeesEnd(Machine mch)
    {
        cnvs().endLine();
    }

    @Override public void visit_HeadingComplete(Machine mch)
    {
    }

    @Override
    public void visit_VariablesStart(Machine mch)
    {
        cnvs().startLine();
        cnvs().keyword("variables");
        cnvs().endLine();

        cnvs().startAlignments(Canvas.align_2col);
    }

    @Override
    public void visit_Variable(Machine mch, Variable variable)
    {
        if (!variable.hasComment() &&
            mch.refines() != null &&
            mch.refines().getVariable(variable.name()) != null)
        {
            // Do not list inherited variables without comments.
            return;
        }

        cnvs().startAlignedLine();
        cnvs().variableDef(variable.name());
        cnvs().align();
        cnvs().comment(variable.comment());
        cnvs().stopAlignedLine();
    }

    @Override
    public void visit_VariablesEnd(Machine mch)
    {
        cnvs().stopAlignments();
    }

    @Override
    public void visit_InvariantsStart(Machine mch)
    {
        cnvs().startLine();
        cnvs().keyword("invariants");
        cnvs().endLine();

        cnvs().startAlignments(Canvas.align_3col);
    }

    @Override
    public void visit_Invariant(Machine mch, Invariant invariant)
    {
        if (invariant.isTheorem())
        {
            cnvs().startAlignedLine();
            cnvs().append("theorem");
            cnvs().align();
            cnvs().label(invariant.name());
            cnvs().stopAlignedLine();
        }
        cnvs().startAlignedLine();
        if (!invariant.isTheorem())
        {
            cnvs().label(invariant.name());
        }
        cnvs().align();
        cnvs().startMath();
        invariant.writeFormulaStringToCanvas(cnvs());
        cnvs().stopMath();

        stopAlignedLineAndHandlePotentialComment(invariant.comment(), cnvs());
    }

    @Override
    public void visit_InvariantsEnd(Machine mch)
    {
        cnvs().stopAlignments();
    }
    @Override
    public void visit_VariantsStart(Machine mch)
    {
        cnvs().startLine();
        cnvs().keyword("variants");
        cnvs().endLine();

        cnvs().startAlignments(Canvas.align_2col);
    }

    @Override
    public void visit_Variant(Machine mch, Variant variant)
    {
        cnvs().startAlignedLine();
        cnvs().startMath();
        variant.writeFormulaStringToCanvas(cnvs());
        cnvs().stopMath();

        stopAlignedLineAndHandlePotentialComment(variant.comment(), cnvs());
    }

    @Override
    public void visit_VariantsEnd(Machine mch)
    {
        cnvs().stopAlignments();
    }


    @Override
    public void visit_EventsStart(Machine mch)
    {
        cnvs().startLine();
        cnvs().keyword("events");
        cnvs().endLine();
    }

    @Override
    public void visit_Event(Machine mch, Event e, String pattern)
    {
        if (e.hasInformation())
        {
            renders().walkEvent(e, pattern);
        }
    }

    @Override
    public void visit_EventsEnd(Machine mch)
    {
    }

    @Override
    public void visit_MachineEnd(Machine mch)
    {
        cnvs().startLine();
        cnvs().keyword("end");
        cnvs().endLine();
    }
}
