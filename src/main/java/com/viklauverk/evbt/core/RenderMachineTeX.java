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

public class RenderMachineTeX extends RenderMachineUnicode
{
    @Override
    public void renderProofSummary(Machine mch)
    {

        int npa   = mch.numProvedAuto();
        int npmnr = mch.numProvedManualNotReviewed();
        int npmr  = mch.numProvedManualReviewed();
        int nup   = mch.numUnproven();

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
    public void visit_MachineStart(Machine mch)
    {
        super.visit_MachineStart(mch);
        boolean add_par = false;
        if (mch.numUnproven() > 0)
        {
            for (ProofObligation po : mch.proofObligationOrdering())
            {
                if (!po.hasProof())
                {
                    cnvs().append("\\noindent\\Unproved\\ \\texttt{\\small "+Util.texSafe(po.name())+"}\\newline ");
                }
            }
            add_par = true;
        }
        if (mch.numProvedManualReviewed() > 0)
        {
            for (ProofObligation po : mch.proofObligationOrdering())
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
    public void visit_VariablesStart(Machine mch)
    {
        cnvs().append("\\subsection{\\footnotesize ");
        for (String v : mch.variableNames())
        {
            Variable variable = mch.getVariable(v);
            if (!variable.hasComment() &&
                mch.refines() != null &&
                mch.refines().getVariable(variable.name()) != null)
            {
                // Do not list inherited variables without comments.
                continue;
            }

            cnvs().variableDef(v);
            cnvs().append(" ");
        }
        cnvs().append("}\n");
        super.visit_VariablesStart(mch);
    }

    @Override
    public void visit_EventsStart(Machine mch)
    {
        // We override the unicode rendered because
        // each event gets its own subsection written
        // by DocGenTeX.
    }

    @Override
    public void visit_MachineEnd(Machine mch)
    {
        // We override the unicode rendered because
        // we do not write the final END, it just
        // looks confusing in the tex document.
    }

}
