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

package com.viklauverk.evbt.core;

import java.util.List;
import java.util.LinkedList;

import com.viklauverk.evbt.core.Formula;

public class RenderMachine extends CommonRenderFunctions
{
    public void visit_MachineStart(Machine mch) { }

    public void visit_RefinesStart(Machine mch) { }
    public void visit_Refines(Machine mch, Machine refines) { }
    public void visit_RefinesEnd(Machine mch) { }

    public void visit_SeesStart(Machine mch) { }
    public void visit_Sees(Machine mch, Context sees) { }
    public void visit_SeesEnd(Machine mch) { }

    public void visit_HeadingComplete(Machine mch) { }

    public void visit_VariablesStart(Machine mch) { }
    public void visit_Variable(Machine mch, Variable variable) { }
    public void visit_VariablesEnd(Machine mch) { }

    public void visit_InvariantsStart(Machine mch) { }
    public void visit_Invariant(Machine mch, Invariant invariant) { }
    public void visit_InvariantsEnd(Machine mch) { }

    public void visit_VariantsStart(Machine mch) { }
    public void visit_Variant(Machine mch, Variant variant) { }
    public void visit_VariantsEnd(Machine mch) { }

    public void visit_EventsStart(Machine mch) { }
    public void visit_Event(Machine mch, Event e, String pattern) { }
    public void visit_EventsEnd(Machine mch) { }

    public void visit_MachineEnd(Machine mch) { }

    protected static String buildMachinePartName(Machine mch)
    {
        return "mch/"+(mch!=null?mch.name():"");
    }

    protected static String buildMachineVariablePartName(Machine mch, Variable var)
    {
        return "mch/"+mch.name()+"/variable/"+(var!=null?var.name():"");
    }

    protected static String buildMachineInvariantPartName(Machine mch, Invariant inv)
    {
        return "mch/"+mch.name()+"/invariant/"+(inv!=null?inv.name():"");
    }

    protected static String buildMachineVariantPartName(Machine mch, Variant var)
    {
        return "mch/"+mch.name()+"/variant/"+(var!=null?var.name():"");
    }

}
