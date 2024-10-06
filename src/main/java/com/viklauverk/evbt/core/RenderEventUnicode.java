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

public class RenderEventUnicode extends RenderEvent
{
    @Override
    public void visit_EventStart(Event eve)
    {
        String id = buildEventPartName(eve);

        cnvs().marker(id);

        cnvs().startLine();
        if (eve.convergence() == Convergence.CONVERGENT)
        {
            cnvs().keywordLeft("convergent ");
        }
        if (eve.convergence() == Convergence.ANTICIPATED)
        {
            cnvs().keywordLeft("anticipated ");
        }
        cnvs().keywordLeft("event ");
        cnvs().id(eve.name());
        cnvs().endLine();

        if (eve.hasComment())
        {
            cnvs().acomment(eve.comment());
        }
    }

    @Override
    public void visit_ExtendsStart(Event eve)
    {
        cnvs().startLine();
        cnvs().keywordLeft("extends");
        cnvs().space();
    }

    @Override
    public void visit_Extend(Event eve, Event extending)
    {
        cnvs().id(extending.name());
        cnvs().space();
    }

    @Override
    public void visit_ExtendsEnd(Event eve)
    {
        cnvs().endLine();
    }

    @Override
    public void visit_RefinesStart(Event eve)
    {
        cnvs().startLine();
        cnvs().keywordLeft("refines");
        cnvs().space();
    }

    @Override
    public void visit_Refine(Event eve, Event refining)
    {
        cnvs().id(refining.name());
        cnvs().space();
    }

    @Override
    public void visit_RefinesEnd(Event eve)
    {
        cnvs().endLine();
    }

    @Override
    public void visit_HeadingComplete(Event eve)
    {
    }

    @Override
    public void visit_ParametersStart(Event eve)
    {
        cnvs().startLine();
        cnvs().keyword("any");
        cnvs().endLine();

        cnvs().startAlignments(Canvas.align_2col);
    }

    @Override
    public void visit_Parameter(Event eve, Variable p)
    {
        cnvs().startAlignedLine();
        cnvs().startMath();
        cnvs().variable(p.name());
        cnvs().stopMath();
        cnvs().align();
        cnvs().comment(p.comment());
        cnvs().stopAlignedLine();
    }

    @Override
    public void visit_ParametersEnd(Event eve)
    {
        cnvs().stopAlignments();
    }

    @Override
    public void visit_GuardsStart(Event eve)
    {
        cnvs().startLine();
        cnvs().keyword("where");
        cnvs().endLine();

        cnvs().startAlignments(Canvas.align_3col);
    }

    @Override
    public void visit_Guard(Event eve, Guard guard)
    {
        cnvs().startAlignedLine();
        cnvs().label(guard.name());
        cnvs().align();
        cnvs().startGuard();
        cnvs().startMath();
        guard.writeFormulaStringToCanvas(cnvs());
        cnvs().stopMath();
        cnvs().stopGuard();

        stopAlignedLineAndHandlePotentialComment(guard.comment(), cnvs(), guard);
    }

    @Override
    public void visit_GuardsEnd(Event eve)
    {
        cnvs().stopAlignments();
    }

    @Override
    public void visit_WitnessesStart(Event eve)
    {
        cnvs().startLine();
        cnvs().keyword("with");
        cnvs().endLine();

        cnvs().startAlignments(Canvas.align_3col);
    }

    @Override
    public void visit_Witness(Event eve, Witness witness)
    {
        cnvs().startAlignedLine();
        cnvs().label(witness.name());
        cnvs().align();
        cnvs().startWitness();
        cnvs().startMath();
        witness.writeFormulaStringToCanvas(cnvs());
        cnvs().stopMath();
        cnvs().stopWitness();

        stopAlignedLineAndHandlePotentialComment(witness.comment(), cnvs(), witness);
    }

    @Override
    public void visit_WitnessesEnd(Event eve)
    {
        cnvs().stopAlignments();
    }

    @Override
    public void visit_ActionsStart(Event eve)
    {
        cnvs().startLine();
        cnvs().keyword("then");
        cnvs().endLine();

        cnvs().startAlignments(Canvas.align_3col);
    }

    @Override
    public void visit_Action(Event eve, Action action)
    {
        cnvs().startAlignedLine();
        cnvs().label(action.name());
        cnvs().align();
        cnvs().startAction();
        cnvs().startMath();
        action.writeFormulaStringToCanvas(cnvs());
        cnvs().stopMath();
        cnvs().stopAction();
        stopAlignedLineAndHandlePotentialComment(action.comment(), cnvs(), action);
    }

    @Override
    public void visit_ActionsEnd(Event eve)
    {
        cnvs().stopAlignments();
    }

    @Override
    public void visit_EventEnd(Event eve)
    {
        cnvs().startLine();
        cnvs().keyword("end");
        cnvs().endLine();
    }
}
