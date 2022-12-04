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

package com.viklauverk.eventbtools.core;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

import com.viklauverk.eventbtools.core.Formula;

public class RenderContext extends CommonRenderFunctions
{
    public void visit_ContextStart(Context ctx) { }

    public void visit_ExtendsStart(Context ctx) { }
    public void visit_Extend(Context ctx, Context ext) { }
    public void visit_ExtendsEnd(Context ctx) { }

    public void visit_HeadingComplete(Context ctx) { }

    public void visit_SetsStart(Context ctx) { }
    public void visit_Set(Context ctx, CarrierSet set) { }
    public void visit_SetsEnd(Context ctx) { }

    public void visit_ConstantsStart(Context ctx) { }
    public void visit_Constant(Context ctx, Constant cnst) { }
    public void visit_ConstantsEnd(Context ctx) { }

    public void visit_AxiomsStart(Context ctx) { }
    public void visit_Axiom(Context ctx, Axiom axiom) { }
    public void visit_AxiomsEnd(Context ctx) { }

    public void visit_ContextEnd(Context ctx) { }

    protected String buildContextPartName(Context ctx)
    {
        return ctx.name();
    }

    protected String buildContextSetsPartName(Context ctx)
    {
        return ctx.name()+"/sets";
    }

    protected String buildContextSetPartName(Context ctx, CarrierSet set)
    {
        return ctx.name()+"/constant/"+set.name();
    }

    protected String buildContextConstantsPartName(Context ctx)
    {
        return ctx.name()+"/constants";
    }

    protected String buildContextConstantPartName(Context ctx, Constant con)
    {
        return ctx.name()+"/constant/"+con.name();
    }


}
