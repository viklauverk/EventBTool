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

public class VisitContext
{
    /** Walk the Event-B context ctx and invoke the visitor functions in rc
        for each part of the ctx. Also only visit parts that matches the pattern.
    */
    public static void walk(RenderContext rc, Context ctx, String pattern)
    {
        boolean m = Util.match(RenderContext.buildContextPartName(ctx), pattern);
        if (m) rc.visit_ContextStart(ctx);

        if (m && ctx.hasExtend())
        {
            rc.visit_ExtendsStart(ctx);
            for (Context c : ctx.extendsContexts())
            {
                rc.visit_Extend(ctx, c);
            }
            rc.visit_ExtendsEnd(ctx);
        }

        if (m) rc.visit_HeadingComplete(ctx);

        if (ctx.hasSets())
        {
            boolean s = Util.match(RenderContext.buildSetPartName(ctx, null), pattern);

            if (s) rc.visit_SetsStart(ctx);
            for (CarrierSet set : ctx.setOrdering())
            {
                boolean ss = Util.match(RenderContext.buildSetPartName(ctx, set), pattern);
                if (ss)
                {
                    rc.visit_Set(ctx, set);
                }
            }
            if (s) rc.visit_SetsEnd(ctx);
        }

        if (ctx.hasConstants())
        {
            boolean c = Util.match(RenderContext.buildConstantPartName(ctx, null), pattern);

            if (c) rc.visit_ConstantsStart(ctx);
            for (Constant constant : ctx.constantOrdering())
            {
                boolean cc =  Util.match(RenderContext.buildConstantPartName(ctx, constant), pattern);
                if (cc) rc.visit_Constant(ctx, constant);
            }
            if (c) rc.visit_ConstantsEnd(ctx);
        }

        if (ctx.hasAxioms())
        {
            boolean a = Util.match(RenderContext.buildAxiomPartName(ctx, null), pattern);

            if (a) rc.visit_AxiomsStart(ctx);
            for (Axiom axi : ctx.axiomOrdering())
            {
                boolean aa =  Util.match(RenderContext.buildAxiomPartName(ctx, axi), pattern);
                if (aa) rc.visit_Axiom(ctx, axi);
            }
            if (a) rc.visit_AxiomsEnd(ctx);
        }

        if (m) rc.visit_ContextEnd(ctx);
    }
}
