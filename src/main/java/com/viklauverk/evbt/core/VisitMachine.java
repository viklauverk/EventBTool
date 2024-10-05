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

public class VisitMachine
{
    public static void walk(RenderMachine v, Machine mch, String pattern)
    {
        boolean m = Util.match(RenderMachine.buildMachinePartName(mch), pattern);

        if (m) v.visit_MachineStart(mch);

        if (m && mch.hasRefines())
        {
            v.visit_RefinesStart(mch);
            v.visit_Refines(mch, mch.refines());
            v.visit_RefinesEnd(mch);
        }

        if (m && mch.hasContexts())
        {
            v.visit_SeesStart(mch);
            for (Context c : mch.contextOrdering())
            {
                v.visit_Sees(mch, c);
            }
            v.visit_SeesEnd(mch);
        }

        if (m) v.visit_HeadingComplete(mch);

        if (mch.hasVariables())
        {
            boolean vs = Util.match(RenderMachine.buildMachineVariablePartName(mch, null), pattern);

            if (vs) v.visit_VariablesStart(mch);
            for (Variable var : mch.variableOrdering())
            {
                boolean vv = Util.match(RenderMachine.buildMachineVariablePartName(mch, var), pattern);
                if (vv) v.visit_Variable(mch, var);
            }
            if (vs) v.visit_VariablesEnd(mch);
        }

        if (mch.hasInvariants())
        {
            boolean i = Util.match(RenderMachine.buildMachineInvariantPartName(mch, null), pattern);

            if (i) v.visit_InvariantsStart(mch);
            for (Invariant inv : mch.invariantOrdering())
            {
                boolean ii = Util.match(RenderMachine.buildMachineInvariantPartName(mch, inv), pattern);
                if (ii) v.visit_Invariant(mch, inv);
            }
            if (i) v.visit_InvariantsEnd(mch);
        }

        if (mch.hasVariants())
        {
            boolean vs = Util.match(RenderMachine.buildMachineVariantPartName(mch, null), pattern);

            if (vs) v.visit_VariantsStart(mch);
            for (Variant var : mch.variantOrdering())
            {
                boolean vv = Util.match(RenderMachine.buildMachineVariantPartName(mch, var), pattern);
                if (vv) v.visit_Variant(mch, var);
            }
            if (vs) v.visit_VariantsEnd(mch);
        }

        if (mch.hasEvents())
        {
            boolean e = Util.match("mch/"+mch.name()+"/event/", pattern);

            if (e) v.visit_EventsStart(mch);
            for (Event eve : mch.eventOrdering())
            {
                v.visit_Event(mch, eve, pattern);
            }
            if (e) v.visit_EventsEnd(mch);
        }

        if (m) v.visit_MachineEnd(mch);
    }

}
