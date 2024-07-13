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
        String info = "theory";
        if (thr.isDeployed()) info = "deployed theory";
        cnvs().keywordLeft(info);
        cnvs().space();
        cnvs().id(thr.name()+" "+thr.typeParameters());
        cnvs().space();
        if (thr.isDeployed())
        {
            cnvs().hfil();
            cnvs().keyword(thr.deployedHash());
        }
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
    }

    @Override public void visit_PolymorphicDataType(Theory thr, PolymorphicDataType pdt)
    {
        cnvs().startLine();
        cnvs().keyword("datatype ");
        pdt.formula().toString(cnvs());
        cnvs().endLine();

        cnvs().startAlignments(Canvas.align_2col);

        String id = buildDataTypePartName(pdt);
        cnvs().marker(id);
        if (pdt.hasComment())
        {
            cnvs().acomment(pdt.comment());
        }

        for (Constructor c : pdt.constructorOrdering())
        {
            cnvs().startAlignedLine();
            cnvs().startMath();
            cnvs().append("\\ \\ ");
            cnvs().constructor(c.name());
            cnvs().stopMath();
            cnvs().align();
            cnvs().comment(c.comment());
            cnvs().stopAlignedLine();
        }
        cnvs().stopAlignments();
    }

    @Override public void visit_PolymorphicDataTypesEnd(Theory thr)
    {
    }

    @Override public void visit_OperatorsStart(Theory thr)
    {
    }

    @Override public void visit_Operator(Theory thr, Operator oprt)
    {
        cnvs().startLine();
        cnvs().keyword("operator ");
        cnvs().operator(oprt.name());
        cnvs().endLine();

        String id = buildOperatorPartName(thr, oprt);
        cnvs().marker(id);
        if (oprt.hasComment())
        {
            cnvs().acomment(oprt.comment());
        }

        cnvs().startAlignments(Canvas.align_2col);

/*
        String id = buildOperatorPartName(oprt);

        cnvs().marker(id);

        cnvs().startLine();
        if (oprt.notation() == OperatorNotationType.PREFIX)
        {
            cnvs().keywordLeft("prefix ");
        }
        if (oprt.notation() == OperatorNotationType.INFIX)
        {
            cnvs().keywordLeft("infix ");
        }
        cnvs().keywordLeft("operator ");
        cnvs().id(oprt.name());
        cnvs().endLine();

        if (oprt.hasComment())
        {
            cnvs().acomment(oprt.comment());
        }
*/
        cnvs().stopAlignments();
    }

    @Override public void visit_OperatorsEnd(Theory thr)
    {
    }

    @Override public void visit_AxiomsStart(Theory thr)
    {
        cnvs().startLine();
        cnvs().keyword("theorems");
        cnvs().endLine();

        cnvs().startAlignments(Canvas.align_3col);
    }

    @Override public void visit_Axiom(Theory thr, Axiom axiom)
    {
        cnvs().startAlignedLine();
        cnvs().label(axiom.name());
        cnvs().align();
        cnvs().startMath();
        axiom.writeFormulaStringToCanvas(cnvs());
        cnvs().stopMath();

        stopAlignedLineAndHandlePotentialComment(axiom.comment(), cnvs(), axiom);

    }

    @Override public void visit_AxiomsEnd(Theory thr)
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
