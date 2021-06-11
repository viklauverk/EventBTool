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

public class RenderContextUnicode extends RenderContext
{
    public void renderProofSummary(Context ctx)
    {
    }

    @Override public void visit_ContextStart(Context ctx)
    {
        cnvs().startLine();
        cnvs().keywordLeft("context");
        cnvs().space();
        cnvs().id(ctx.name());
        renderProofSummary(ctx);
        cnvs().endLine();

        cnvs().hrule();

        if (ctx.hasComment())
        {
            cnvs().acomment(ctx.comment());
            cnvs().nl();
        }
    }

    @Override public void visit_ExtendsStart(Context ctx)
    {
        cnvs().startLine();
        cnvs().keywordLeft("extends");
        cnvs().space();
    }

    @Override public void visit_Extend(Context ctx, Context ext)
    {
        cnvs().id(ext.name());
        cnvs().space();
    }

    @Override public void visit_ExtendsEnd(Context ctx)
    {
        cnvs().endLine();
    }

    @Override public void visit_HeadingComplete(Context ctx)
    {
    }

    @Override public void visit_SetsStart(Context ctx)
    {
        cnvs().startLine();
        cnvs().keyword("sets");
        cnvs().endLine();

        cnvs().startAlignments(Canvas.align_2col);
    }

    @Override public void visit_Set(Context ctx, CarrierSet set)
    {
        cnvs().startAlignedLine();
        cnvs().set(set.name());
        cnvs().align();
        cnvs().comment(set.comment());
        cnvs().stopAlignedLine();
    }

    @Override public void visit_SetsEnd(Context ctx)
    {
        cnvs().stopAlignments();
    }

    @Override public void visit_ConstantsStart(Context ctx)
    {
        cnvs().startLine();
        cnvs().keyword("constants");
        cnvs().endLine();

        cnvs().startAlignments(Canvas.align_2col);
    }

    @Override public void visit_Constant(Context ctx, Constant constant)
    {
        cnvs().startAlignedLine();
        cnvs().startMath();
        cnvs().constant(constant.name());
        cnvs().stopMath();
        cnvs().align();
        cnvs().comment(constant.comment());
        cnvs().stopAlignedLine();
    }

    @Override public void visit_ConstantsEnd(Context ctx)
    {
        cnvs().stopAlignments();
    }

    @Override public void visit_AxiomsStart(Context ctx)
    {
        cnvs().startLine();
        cnvs().keyword("axioms");
        cnvs().endLine();

        cnvs().startAlignments(Canvas.align_3col);
    }

    @Override public void visit_Axiom(Context ctx, Axiom axiom)
    {
        cnvs().startAlignedLine();
        cnvs().label(axiom.name());
        cnvs().align();
        cnvs().startMath();
        axiom.writeFormulaStringToCanvas(cnvs());
        cnvs().stopMath();
        stopAlignedLineAndHandlePotentialComment(axiom.comment(), cnvs());
    }

    @Override public void visit_AxiomsEnd(Context ctx)
    {
        cnvs().stopAlignments();
    }

    @Override public void visit_TheoremsStart(Context ctx)
    {
        cnvs().startLine();
        cnvs().keyword("theorem");
        cnvs().endLine();

        cnvs().startAlignments(Canvas.align_3col);
    }

    @Override public void visit_Theorem(Context ctx, Theorem theorem)
    {
        cnvs().startAlignedLine();
        cnvs().label(theorem.name());
        cnvs().align();
        cnvs().startMath();
        theorem.writeFormulaStringToCanvas(cnvs());
        cnvs().stopMath();
        stopAlignedLineAndHandlePotentialComment(theorem.comment(), cnvs());
    }

    @Override public void visit_TheoremsEnd(Context ctx)
    {
        cnvs().stopAlignments();
    }

    @Override public void visit_ContextEnd(Context ctx)
    {
        cnvs().startLine();
        cnvs().keyword("end");
        cnvs().endLine();
    }

}
