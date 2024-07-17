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

public class RenderTheoryTeX extends RenderTheoryUnicode
{
    @Override
    public void renderProofSummary(Theory thr)
    {
        int npa   = thr.numProvedAuto();
        int npmnr = thr.numProvedManualNotReviewed();
        int npmr  = thr.numProvedManualReviewed();
        int nup   = thr.numUnproven();

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
    public void visit_TheoryStart(Theory thr)
    {
        super.visit_TheoryStart(thr);
        boolean add_par = false;
        if (thr.numUnproven() > 0)
        {
            for (ProofObligation po : thr.proofObligationOrdering())
            {
                if (!po.hasProof())
                {
                    cnvs().append("\\noindent\\Unproved\\ \\texttt{\\small "+Util.texSafe(po.name())+"}\\newline ");
                }
            }
            add_par = true;
        }
        if (thr.numProvedManualReviewed() > 0)
        {
            for (ProofObligation po : thr.proofObligationOrdering())
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
    public void visit_PolymorphicDataType(Theory thr, PolymorphicDataType pdt)
    {
        cnvs().append("\\subsection{\\footnotesize ");
        cnvs().startMath();
        pdt.formula().toString(cnvs());
        if (pdt.hasConstructors())
        {
            for (Constructor c: pdt.constructorOrdering())
            {
                cnvs().space();
                c.toString(cnvs());
            }
        }
        cnvs().stopMath();
        cnvs().append("}\n");

        super.visit_PolymorphicDataType(thr, pdt);
    }

    @Override
    public void visit_Operator(Theory thr, Operator oprt)
    {
        cnvs().append("\\subsection{\\footnotesize ");
        cnvs().startMath();
        oprt.formula().toString(cnvs());
        cnvs().stopMath();
        cnvs().append("}\n");

        super.visit_Operator(thr, oprt);
    }

}
