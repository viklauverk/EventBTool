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

package com.viklauverk.eventbtools.core;

import java.util.List;
import java.util.LinkedList;

import com.viklauverk.eventbtools.core.Formula;

public class RenderOperator extends CommonRenderFunctions
{
    public void visit_OperatorStart(Operator oprt) { }

    public void visit_HeadingComplete(Operator oprt) { }

    public void visit_OperatorEnd(Operator oprt) { }

    protected String buildOperatorPartName(Operator oprt)
    {
        return oprt.theory().name()+"/"+oprt.name();
    }
}
