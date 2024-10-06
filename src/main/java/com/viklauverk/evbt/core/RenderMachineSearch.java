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

import com.viklauverk.evbt.core.Formula;

import java.util.List;
import java.util.LinkedList;

public class RenderMachineSearch extends RenderMachine
{
    @Override
    public void visit_MachineStart(Machine mch)
    {
        renders().search().addPart(buildMachinePartName(mch));
    }

    @Override
    public void visit_Variable(Machine mch, Variable variable)
    {
        renders().search().addPart(buildMachineVariablePartName(mch, variable));
    }

    @Override
    public void visit_Invariant(Machine mch, Invariant invariant)
    {
        renders().search().addPart(buildMachineInvariantPartName(mch, invariant));
    }

    @Override
    public void visit_Variant(Machine mch, Variant variant)
    {
        renders().search().addPart(buildMachineVariantPartName(mch, variant));
    }

    @Override
    public void visit_Event(Machine mch, Event e, String pattern)
    {
        if (e.hasInformation())
        {
            renders().walkEvent(e, pattern);
        }
    }
}
