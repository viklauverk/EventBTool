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

public class RenderContextTeX extends RenderContextUnicode
{
    @Override
    public void renderProofSummary(Context ctx)
    {

        int npa   = ctx.numProvedAuto();
        int npmnr = ctx.numProvedManualNotReviewed();
        int npmr  = ctx.numProvedManualReviewed();
        int nup   = ctx.numUnproven();

        if (npa > 0 || npmnr > 0 || npmr > 0 || nup > 0)
        {
            cnvs().append("\\hfil ");
        }
        if (npa > 0)
        {
            cnvs().append("\\ \\ {\\footnotesize "+npa+"}\\ProvedAuto");
        }
        if (npmnr > 0)
        {
            cnvs().append("\\ \\ {\\footnotesize "+npmnr+"}\\ProvedManual");
        }
        if (npmr > 0)
        {
            cnvs().append("\\ \\ {\\footnotesize "+npmr+"}\\Reviewed");
        }
        if (nup > 0)
        {
            cnvs().append("\\ \\ {\\footnotesize "+nup+"}\\Unproved");
        }
    }

    @Override
    public void visit_ContextStart(Context ctx)
    {
        super.visit_ContextStart(ctx);
        boolean add_par = false;
        if (ctx.numUnproven() > 0)
        {
            for (ProofObligation po : ctx.proofObligationOrdering())
            {
                if (!po.hasProof())
                {
                    cnvs().append("\\noindent\\Unproved\\ \\texttt{\\small "+Util.texSafe(po.name())+"}\\newline ");
                }
            }
            add_par = true;
        }
        if (ctx.numProvedManualReviewed() > 0)
        {
            for (ProofObligation po : ctx.proofObligationOrdering())
            {
                if (po.isProvedManualReviewed())
                {
                    cnvs().append("\\noindent\\Reviewed\\ \\texttt{\\small "+Util.texSafe(po.name())+"}\\newline ");
                }
            }
            add_par = true;
        }
        if (add_par)
        {
            cnvs().append("\\par \n");
        }
    }

    @Override
    public void visit_SetsStart(Context ctx)
    {
        cnvs().append("\\subsection{\\footnotesize ");
        for (String sn : ctx.setNames())
        {
            cnvs().set(sn);
            cnvs().append(" ");
        }
        cnvs().append("}\n");
        super.visit_SetsStart(ctx);
    }

    @Override
    public void visit_ConstantsStart(Context ctx)
    {
        cnvs().append("\\subsection{\\footnotesize ");
        for (String sn : ctx.constantNames())
        {
            cnvs().constant(sn);
            cnvs().append(" ");
        }
        cnvs().append("}\n");
        super.visit_ConstantsStart(ctx);
    }
}
