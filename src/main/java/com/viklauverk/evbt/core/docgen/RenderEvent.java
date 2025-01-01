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

package com.viklauverk.evbt.core.docgen;

import com.viklauverk.evbt.core.sys.Action;
import com.viklauverk.evbt.core.sys.Event;
import com.viklauverk.evbt.core.sys.Guard;
import com.viklauverk.evbt.core.sys.Variable;
import com.viklauverk.evbt.core.sys.Witness;

public class RenderEvent extends CommonRenderFunctions
{
    public void visit_EventStart(Event eve) { }

    public void visit_ExtendsStart(Event eve) { }
    public void visit_Extend(Event eve, Event extending) { }
    public void visit_ExtendsEnd(Event eve) { }

    public void visit_RefinesStart(Event eve) { }
    public void visit_Refine(Event eve, Event refining) { }
    public void visit_RefinesEnd(Event eve) { }

    public void visit_HeadingComplete(Event eve) { }

    public void visit_ParametersStart(Event eve) { }
    public void visit_Parameter(Event eve, Variable par) { }
    public void visit_ParametersEnd(Event eve) { }

    public void visit_GuardsStart(Event eve) { }
    public void visit_Guard(Event eve, Guard gua) { }
    public void visit_GuardsEnd(Event eve) { }

    public void visit_WitnessesStart(Event eve) { }
    public void visit_Witness(Event eve, Witness wit) { }
    public void visit_WitnessesEnd(Event eve) { }

    public void visit_ActionsStart(Event eve) { }
    public void visit_Action(Event eve, Action act) { }
    public void visit_ActionsEnd(Event eve) { }

    public void visit_EventEnd(Event eve) { }

    public static String buildEventPartName(Event e)
    {
        return "mch/"+e.machine().name()+"/event/"+e.name();
    }

    public static String buildParameterPartName(Event e, Variable var)
    {
        return "mch/"+e.machine().name()+"/event/"+e.name()+"/parameter/"+var.name();
    }

    public static String buildGuardPartName(Event e, Guard g)
    {
        return "mch/"+e.machine().name()+"/event/"+e.name()+"/guard/"+g.name();
    }

    public static String buildWitnessPartName(Event e, Witness wtn)
    {
        return "mch/"+e.machine().name()+"/event/"+e.name()+"/witness/"+wtn.name();
    }

    public static String buildActionPartName(Event e, Action a)
    {
        return "mch/"+e.machine().name()+"/event/"+e.name()+"/action/"+a.name();
    }

}
