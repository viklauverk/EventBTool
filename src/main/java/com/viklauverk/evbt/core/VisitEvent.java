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

public class VisitEvent
{
    public static void walk(RenderEvent re, Event e, String pattern)
    {
        boolean m = Util.match(RenderEvent.buildEventPartName(e), pattern);

        if (m) re.visit_EventStart(e);

        if (m && e.extended())
        {
            re.visit_ExtendsStart(e);
            re.visit_Extend(e, e.refinesEvents().get(0));
            re.visit_ExtendsEnd(e);
        }
        else
        if (m && e.hasRefines())
        {
            re.visit_RefinesStart(e);
            for (Event r : e.refinesEvents())
            {
                re.visit_Refine(e, r);
            }
            re.visit_RefinesEnd(e);
        }

        if (m) re.visit_HeadingComplete(e);

        if (e.hasParameters())
        {
            if (m) re.visit_ParametersStart(e);
            for (Variable par : e.parameterOrdering())
            {
                boolean pp =  Util.match(RenderEvent.buildParameterPartName(e, par), pattern);
                if (pp) re.visit_Parameter(e, par);
            }
            if (m) re.visit_ParametersEnd(e);
        }

        if (e.hasGuards())
        {
            if (m) re.visit_GuardsStart(e);
            for (Guard gua : e.guardOrdering())
            {
                boolean gg =  Util.match(RenderEvent.buildGuardPartName(e, gua), pattern);
                if (gg) re.visit_Guard(e, gua);
            }
            if (m) re.visit_GuardsEnd(e);
        }

        if (e.hasWitnesses())
        {
            if (m) re.visit_WitnessesStart(e);
            for (Witness wit : e.witnessOrdering())
            {
                boolean ww =  Util.match(RenderEvent.buildWitnessPartName(e, wit), pattern);
                if (ww) re.visit_Witness(e, wit);
            }
            if (m) re.visit_WitnessesEnd(e);
        }

        if (e.hasActions())
        {
            if (m) re.visit_ActionsStart(e);
            for (Action act : e.actionOrdering())
            {
                boolean aa =  Util.match(RenderEvent.buildActionPartName(e, act), pattern);
                if (aa) re.visit_Action(e, act);
            }
            if (m) re.visit_ActionsEnd(e);
        }

        if (m) re.visit_EventEnd(e);
    }
}
