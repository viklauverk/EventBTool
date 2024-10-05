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

public class RenderEventSearch extends RenderEvent
{
    @Override
    public void visit_EventStart(Event eve)
    {
        renders().search().addPart(buildEventPartName(eve));
    }

    @Override
    public void visit_Parameter(Event eve, Variable prm)
    {
        renders().search().addPart(buildParameterPartName(eve, prm));
    }

    @Override
    public void visit_Guard(Event eve, Guard grd)
    {
        renders().search().addPart(buildGuardPartName(eve, grd));
    }

    @Override
    public void visit_Witness(Event eve, Witness wtn)
    {
        renders().search().addPart(buildWitnessPartName(eve, wtn));
    }

    @Override
    public void visit_Action(Event eve, Action act)
    {
        renders().search().addPart(buildActionPartName(eve, act));
    }


}
