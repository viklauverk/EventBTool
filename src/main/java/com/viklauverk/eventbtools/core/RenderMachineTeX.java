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

public class RenderMachineTeX extends RenderMachineUnicode
{
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
