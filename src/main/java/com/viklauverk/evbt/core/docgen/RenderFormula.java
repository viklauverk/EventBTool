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

import java.util.HashMap;
import java.util.Map;

import com.viklauverk.evbt.common.log.Log;
import com.viklauverk.evbt.common.log.LogModule;
import com.viklauverk.evbt.core.console.Canvas;
import com.viklauverk.evbt.core.sys.Formula;
import com.viklauverk.evbt.core.visitors.WalkFormula;

public class RenderFormula extends WalkFormula
{
    private static Log log = LogModule.lookup("renderformula", RenderFormula.class);

    private static Map<Formula,Boolean> new_line_before_ = new HashMap<>();
    private static Map<Formula,Boolean> new_line_after_ = new HashMap<>();

    private Canvas canvas_ = null;
    private boolean add_types_ = false;
    private boolean add_metas_ = false;

    RenderFormula(Canvas canvas)
    {
        canvas_ = canvas;
    }

    public Canvas cnvs() { return canvas_; }

    public void enterNode(Formula f) {}
    public void exitNode(Formula f) {}

    public void addTypes()
    {
        add_types_ = true;
    }

    public boolean addingTypes()
    {
        return add_types_;
    }

    public void addMetas()
    {
        add_metas_ = true;
    }

    public boolean addingMetas()
    {
        return add_metas_;
    }

    public static void addNewLineHintBefore(Formula f)
    {
        log.trace("add new line hint before: %s", f);
        new_line_before_.put(f, true);
    }

    public static void addNewLineHintAfter(Formula f)
    {
        log.trace("add new line hint after: %s", f);
        new_line_after_.put(f, true);
    }

    public void insertNewLineBefore(Formula i)
    {
        cnvs().newlineInFormula();
    }

    public void insertNewLineAfter(Formula i)
    {
        cnvs().newlineInFormula();
    }

    public static boolean shouldAddNewLineAfter(Formula f)
    {
        Boolean b = new_line_after_.get(f);
        if (b == null || b == false) return false;
        return true;
    }

    public static boolean shouldAddNewLineBefore(Formula f)
    {
        Boolean b = new_line_before_.get(f);
        if (b == null || b == false) return false;
        return true;
    }

    void checkNewLineBefore(Formula i)
    {
        if (shouldAddNewLineBefore(i))
        {
            insertNewLineBefore(i);
        }
    }

    void checkNewLineAfter(Formula i)
    {
        if (shouldAddNewLineAfter(i))
        {
            insertNewLineAfter(i);
        }
    }

}
