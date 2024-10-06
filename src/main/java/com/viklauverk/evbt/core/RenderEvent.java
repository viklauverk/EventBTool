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

package com.viklauverk.evbt.core;

import java.util.List;
import java.util.LinkedList;

import com.viklauverk.evbt.core.Formula;

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

    protected static String buildEventPartName(Event e)
    {
        return "mch/"+e.machine().name()+"/event/"+e.name();
    }

    protected static String buildParameterPartName(Event e, Variable var)
    {
        return "mch/"+e.machine().name()+"/event/"+e.name()+"/parameter/"+var.name();
    }

    protected static String buildGuardPartName(Event e, Guard g)
    {
        return "mch/"+e.machine().name()+"/event/"+e.name()+"/guard/"+g.name();
    }

    protected static String buildWitnessPartName(Event e, Witness wtn)
    {
        return "mch/"+e.machine().name()+"/event/"+e.name()+"/witness/"+wtn.name();
    }

    protected static String buildActionPartName(Event e, Action a)
    {
        return "mch/"+e.machine().name()+"/event/"+e.name()+"/action/"+a.name();
    }

}
