/*
 Copyright (C) 2021-2024 Viklauverk AB

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

public class RenderTheoryUnicode extends RenderTheory
{
    public void renderProofSummary(Theory thr)
    {
    }

    @Override public void visit_TheoryStart(Theory thr)
    {
        cnvs().startLine();
        cnvs().keywordLeft("theory");
        cnvs().space();
        cnvs().id(thr.name());
        renderProofSummary(thr);
        cnvs().endLine();

        cnvs().hrule();

        if (thr.hasComment())
        {
            cnvs().acomment(thr.comment());
            cnvs().nl();
        }
    }

    @Override public void visit_ImportsStart(Theory thr)
    {
        cnvs().startLine();
        cnvs().keywordLeft("imports");
        cnvs().space();
    }

    @Override public void visit_Import(Theory thr, Theory ext)
    {
        cnvs().id(ext.name());
        cnvs().space();
    }

    @Override public void visit_ImportsEnd(Theory thr)
    {
        cnvs().endLine();
    }

    @Override public void visit_HeadingComplete(Theory thr)
    {
    }

    @Override public void visit_PolymorphicDataTypesStart(Theory thr)
    {
        cnvs().startLine();
        cnvs().keyword("datatypes");
        cnvs().endLine();

        cnvs().startAlignments(Canvas.align_2col);
    }

    @Override public void visit_PolymorphicDataType(Theory thr, PolymorphicDataType pdt)
    {
        renders().walkPolymorphicDataType(pdt, "");
    }

    @Override public void visit_PolymorphicDataTypesEnd(Theory thr)
    {
        cnvs().stopAlignments();
    }

    @Override public void visit_OperatorsStart(Theory thr)
    {
        cnvs().startLine();
        cnvs().keyword("operators");
        cnvs().endLine();

        cnvs().startAlignments(Canvas.align_2col);
    }

    @Override public void visit_Operator(Theory thr, Operator oprt)
    {
        cnvs().startAlignedLine();
        cnvs().startMath();
        cnvs().constant(oprt.name());
        cnvs().stopMath();
        cnvs().align();
        cnvs().comment(oprt.comment());
        cnvs().stopAlignedLine();
    }

    @Override public void visit_OperatorsEnd(Theory thr)
    {
        cnvs().stopAlignments();
    }

    @Override public void visit_TheoryEnd(Theory thr)
    {
        cnvs().startLine();
        cnvs().keyword("end");
        cnvs().endLine();
    }

}
