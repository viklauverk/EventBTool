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

import com.viklauverk.common.log.Log;
import com.viklauverk.common.log.LogModule;

public class ImplementationCpp implements Implementation
{
    private static Log log = LogModule.lookup("implementationcpp", ImplementationCpp.class);

    public void generateAddition(RenderFormula render, PlanImplementation plan, Formula i, Formula left, Formula right)
    {
        render.visitLeft(i);
        render.cnvs().append(" + /* PLUSS */ ");
        render.visitRight(i);
    }
}
